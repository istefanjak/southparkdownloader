package com.istef.southpark.requests;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.istef.southpark.exception.SubtitleParserException;
import com.istef.southpark.util.Builder;
import com.istef.southpark.util.Function;

public class SubtitleParser implements Cancelable {

	private List<String> subtitleUris;
	private CloseableHttpClient client;
	private Cancelable runningParser;
	private boolean onGoingCancel;

	public SubtitleParser(CloseableHttpClient client, List<String> subtitleList) {
		this.subtitleUris = subtitleList;
		this.client = client;
	}

	public boolean isOnGoingCancel() {
		return onGoingCancel;
	}
	
	public void parse(Path subtitlePath) throws SubtitleParserException {
		try {
			if (subtitlePath.getParent() != null)
				Files.createDirectories(subtitlePath.getParent());

			int subCnt = 1;
			ConcurrentSubtitleParser par = new ConcurrentSubtitleParser(client, subtitleUris);
			runningParser = par;
			List<String> result = par.getBodies();
			for (String res : result) {
				Path subtitlePathNew = Function.addToFileName(subtitlePath, subCnt);
				Files.writeString(subtitlePathNew, xmlToSrt(res));
				subCnt++;
			}

		} catch (Exception e) {
			if (!onGoingCancel)
				throw new SubtitleParserException(e);
		}

	}

	public static String xmlToSrt(String xml) throws SubtitleParserException {
		try {
			Document doc = Jsoup.parse(xml);

			StringBuilder out = new StringBuilder();

			int titleCnt = 1;
			Elements ps = doc.select("p");
			for (Element p : ps) {
				String begin = p.attr("begin").replace(".", ",");
				String end = p.attr("end").replace(".", ",");

				Element span = p.selectFirst("span");
				String text = span.html().replace("<br>", System.lineSeparator()).replace("<br/>",
						System.lineSeparator());

				out.append(String.valueOf(titleCnt)).append(System.lineSeparator()).append(begin).append(" --> ")
						.append(end).append(System.lineSeparator()).append(text).append(System.lineSeparator())
						.append(System.lineSeparator()).toString();

				titleCnt++;
			}
			return out.toString();

		} catch (Exception e) {
			throw new SubtitleParserException(e);
		}
	}

	private static class ConcurrentSubtitleParser implements Cancelable {
		private CloseableHttpClient client;
		private List<String> uris;
		private List<HttpGet> runningHttpGets;

		private ConcurrentSubtitleParser(CloseableHttpClient client, List<String> uris) {
			this.client = client;
			this.uris = uris;
			runningHttpGets = new ArrayList<>();
		}

		private class CallableTask implements Callable<String> {
			private String uri;

			public CallableTask(String uri) {
				this.uri = uri;
			}

			@Override
			public String call() throws Exception {
				HttpGet get = Builder.getHttpGet(uri);;
				runningHttpGets.add(get);
				String responseBody;
				try (CloseableHttpResponse response = client.execute(get)) {
					responseBody = EntityUtils.toString(response.getEntity());
				}
				return responseBody;
			}
		}

		private List<String> getBodies() throws Exception {
			ExecutorService executors = Executors.newFixedThreadPool(uris.size());
			List<Future<String>> futures = new ArrayList<>();
			for (String uri : uris) {
				futures.add(executors.submit(new CallableTask(uri)));
			}
			List<String> ret = new ArrayList<>(uris.size());
			for (Future<String> f : futures) {
				ret.add(f.get(30, TimeUnit.SECONDS));
			}
			executors.shutdown();
			return ret;
		}

		@Override
		public void cancel() {
			for (HttpGet h : runningHttpGets)
				if (h != null)
					h.cancel();
		}

	}

	@Override
	public void cancel() {
		onGoingCancel = true;
		if (runningParser != null)
			runningParser.cancel();
	}
}
