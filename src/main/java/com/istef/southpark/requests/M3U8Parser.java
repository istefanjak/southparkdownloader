package com.istef.southpark.requests;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import com.istef.southpark.exception.M3U8ParserException;
import com.istef.southpark.model.listener.CentileListener;
import com.istef.southpark.util.Builder;
import com.istef.southpark.util.Crypto;
import com.istef.southpark.util.Function;

import io.lindstrom.m3u8.model.MasterPlaylist;
import io.lindstrom.m3u8.model.MediaPlaylist;
import io.lindstrom.m3u8.model.MediaSegment;
import io.lindstrom.m3u8.model.Resolution;
import io.lindstrom.m3u8.parser.MasterPlaylistParser;
import io.lindstrom.m3u8.parser.MediaPlaylistParser;
import io.lindstrom.m3u8.parser.PlaylistParserException;

public class M3U8Parser implements Cancelable {
	public static class Centile {
		private double centile;
		
		public Centile(double centile) {
			if (centile >= 1.)
				centile = 1.;
			this.centile = centile;
		}

		public double get() {
			return centile;
		}

		@Override
		public String toString() {
			return String.format("%.2f%%", centile*100);
		}
	}

	private CloseableHttpClient client;
	private List<String> m3u8s_Bodies;
	private HttpGet runningHttpGet;
	private boolean ongoingCancel;

	public M3U8Parser(CloseableHttpClient client, List<String> m3u8s_Bodies) {
		this.client = client;
		this.m3u8s_Bodies = m3u8s_Bodies;
	}
	
	public boolean isOngoingCancel() {
		return ongoingCancel;
	}

	public List<Resolution> getResolutions() throws PlaylistParserException {
		MasterPlaylistParser playlistParser = new MasterPlaylistParser();
		MasterPlaylist playlist = playlistParser.readPlaylist(m3u8s_Bodies.get(0));
		return playlist.variants().stream().map(var -> var.resolution().get()).collect(Collectors.toList());
	}

	public void parse(Path videoPath, Resolution resolution, CentileListener centileListener) throws M3U8ParserException {
		int actCnt = 1;
		try {
			if (videoPath.getParent() != null)
				Files.createDirectories(videoPath.getParent());
				
			BigDecimal centileCnt = BigDecimal.valueOf(0.);
			if (centileListener != null) {
				centileListener.onProgressChanged(new Centile(0.));
			}
			for (String playlistBodyStr : m3u8s_Bodies) {
				MasterPlaylistParser playlistParser = new MasterPlaylistParser();
				MasterPlaylist playlist = playlistParser.readPlaylist(playlistBodyStr);

				String uri = playlist.variants().stream().filter(p -> {
					Resolution predicateRes = p.resolution().get();
					return predicateRes.width() == resolution.width() && predicateRes.height() == resolution.height();
				}).findFirst().get().uri();
				runningHttpGet = Builder.getHttpGet(uri);
				String responseBody;
				try (CloseableHttpResponse response = client.execute(runningHttpGet)) {
					responseBody = EntityUtils.toString(response.getEntity());
				}
				if (ongoingCancel) return;
				if (responseBody != null) {
					MediaPlaylistParser mediaPlaylistParser = new MediaPlaylistParser();
					MediaPlaylist mediaPlaylist = mediaPlaylistParser.readPlaylist(responseBody);

					List<MediaSegment> mediaSegments = mediaPlaylist.mediaSegments();
					String keyUri = mediaSegments.get(0).segmentKey().get().uri().get();
					String keyIV = mediaSegments.get(0).segmentKey().get().iv().get();

					runningHttpGet = Builder.getHttpGet(keyUri);
					byte[] key;
					try (CloseableHttpResponse response = client.execute(runningHttpGet)) {
						key = EntityUtils.toByteArray(response.getEntity());
					}
					if (ongoingCancel) return;
					if (key != null) {
						byte[] iv = Crypto.getBytesFromHexString(keyIV.substring(2));

						Path videoPathNew = Function.addToFileName(videoPath, actCnt);
						try (FileOutputStream fos = new FileOutputStream(videoPathNew.toString())) {
							
							for (int i = 0; i < mediaSegments.size(); i++) {
								String downloadUri = mediaSegments.get(i).uri();

								runningHttpGet = Builder.getHttpGet(downloadUri);
								byte[] encryptedData;
								try (CloseableHttpResponse response = client.execute(runningHttpGet)) {
									encryptedData = EntityUtils.toByteArray(response.getEntity());
								}
								if (ongoingCancel) return;
								if (encryptedData != null) {
									byte[] decryptedData = Crypto.decrypt(key, iv, encryptedData);

									fos.write(decryptedData);
									centileCnt = centileCnt.add(BigDecimal.valueOf(1.).divide(BigDecimal.valueOf(mediaSegments.size()).multiply(BigDecimal.valueOf(m3u8s_Bodies.size())), MathContext.DECIMAL128));
									if (centileListener != null) {
										centileListener.onProgressChanged(new Centile(centileCnt.doubleValue()));
									}
								}

							}
							actCnt++;
						}

					}
				}
			}

		} catch (Exception e) {
			if (!ongoingCancel)
				throw new M3U8ParserException(e);
		}
	}

	public void parse(Path videoPath, Resolution resolution) throws M3U8ParserException {
		parse(videoPath, resolution, null);
	}

	@Override
	public void cancel() {
		ongoingCancel = true;
		if(runningHttpGet != null) {
			runningHttpGet.cancel();
		}
	}

}
