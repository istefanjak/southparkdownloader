package com.istef.southpark.requests;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import com.google.gson.Gson;
import com.istef.southpark.exception.BadStatusException;
import com.istef.southpark.exception.MgidParserException;
import com.istef.southpark.model.Episode;
import com.istef.southpark.model.Episode.SegmentRef;
import com.istef.southpark.model.Segment;
import com.istef.southpark.model.json.FeedInfo;
import com.istef.southpark.model.json.SegmentInfo;
import com.istef.southpark.util.Builder;

public class MgidParser implements Cancelable {

	public static class MgidParserModel {
		private List<String> m3u8s_Bodies;
		private Episode episode;

		public MgidParserModel(List<String> m3u8s_Bodies, Episode episode) {
			this.m3u8s_Bodies = m3u8s_Bodies;
			this.episode = episode;
		}

		public List<String> getM3u8s_Bodies() {
			return m3u8s_Bodies;
		}

		public Episode getEpisode() {
			return episode;
		}
	}

	private static final String EPISODE_URI_FORMAT = "https://media.mtvnservices.com/pmt/e1/access/index.html?uri=%s&configtype=edge";
	private String mgid;
	private CloseableHttpClient client;
	private HttpGet runningHttpGet;
	private Cancelable runningParser;

	public MgidParser(CloseableHttpClient client, String mgid) {
		this.client = client;
		this.mgid = mgid;
	}

	private void handleStatus(int status) throws BadStatusException {
		if (status != 200)
			throw new BadStatusException("Status code " + status);
	}

	public MgidParserModel parse() throws MgidParserException {
		try {
			runningHttpGet = Builder.getHttpGet(String.format(EPISODE_URI_FORMAT, mgid));
			String responseBody;
			try (CloseableHttpResponse response = client.execute(runningHttpGet)) {
				responseBody = EntityUtils.toString(response.getEntity());
				handleStatus(response.getCode());
			}
			if (responseBody != null) {
				FeedInfo feedInfoJSON = new Gson().fromJson(responseBody, FeedInfo.class);
				Episode episode = new Episode(feedInfoJSON);

				ConcurrentSegmentParser concurrentSegmentParser = new ConcurrentSegmentParser(client,
						episode.getSegments());
				runningParser = concurrentSegmentParser;
				List<String> ret = concurrentSegmentParser.getBodies();
				return new MgidParserModel(ret, episode);
			}
			throw new MgidParserException("responseBody null");

		} catch (Exception e) {
			throw new MgidParserException(e);
		}
	}

	private static class ConcurrentSegmentParser implements Cancelable {
		private CloseableHttpClient client;
		private List<SegmentRef> segments;
		private List<HttpGet> runningHttpGets;
		private List<Future<String>> futures;
		private ExecutorService executors;

		private ConcurrentSegmentParser(CloseableHttpClient client, List<SegmentRef> segments) {
			this.client = client;
			this.segments = segments;
			runningHttpGets = new ArrayList<>();
			futures = new ArrayList<>();
		}

		private class CallableTask implements Callable<String> {
			private String uri;

			public CallableTask(String uri) {
				this.uri = uri;
			}

			@Override
			public String call() throws Exception {
				HttpGet get = Builder.getHttpGet(uri);
				runningHttpGets.add(get);
				String responseBody;
				try (CloseableHttpResponse response = client.execute(get)) {
					responseBody = EntityUtils.toString(response.getEntity());
				}
				if (responseBody != null) {
					SegmentInfo segmentInfoJSON = new Gson().fromJson(responseBody, SegmentInfo.class);
					Segment segment = new Segment(segmentInfoJSON);
					
					get = Builder.getHttpGet(segment.getUri());
					runningHttpGets.add(get);
					responseBody = null;
					try (CloseableHttpResponse response = client.execute(get)) {
						responseBody = EntityUtils.toString(response.getEntity());
					}
					if (responseBody != null) {
						return responseBody;
					}
				}
				return null;
			}
		}

		private List<String> getBodies() throws Exception {
			List<String> uris = segments.stream().map(e -> e.getUri()).collect(Collectors.toList());
			executors = Executors.newFixedThreadPool(uris.size());
			for (String uri : uris) {
				futures.add(executors.submit(new CallableTask(uri)));
			}
			List<String> ret = new ArrayList<>(uris.size());
			for (Future<String> f : futures) {
				ret.add(f.get(10, TimeUnit.SECONDS));
			}
			executors.shutdown();
			return ret;
		}

		@Override
		public void cancel() {
			for (HttpGet h : runningHttpGets) {
				if (h != null)
					h.cancel();	
			}
			for (Future<String> f : futures) {
				f.cancel(true);
			}
			executors.shutdownNow();
		}

	}

	@Override
	public void cancel() {
		if (runningParser != null)
			runningParser.cancel();

		if (runningHttpGet != null)
			runningHttpGet.cancel();
	}

}
