package com.istef.southpark.util;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;

public class Builder {
	
	public static CloseableHttpClient getCloseableHttpClient() {
		PoolingHttpClientConnectionManager connPool;
        connPool = new PoolingHttpClientConnectionManager();
        connPool.setMaxTotal(100);
        connPool.setDefaultMaxPerRoute(20);
        return HttpClients.custom()
	            .setConnectionManager(connPool)
	            .setConnectionManagerShared(true).build();
	}
	
	public static HttpGet getHttpGet(String uri) {
		HttpGet get = new HttpGet(uri);
		//get.setHeader(HttpHeaders.HOST, "media.mtvnservices.com");
		//get.setHeader(HttpHeaders.CONNECTION, "keep-alive");
		//get.setHeader(HttpHeaders.CACHE_CONTROL, "max-age=0");
		//get.setHeader("Upgrade-Insecure-Requests", "1");
		//get.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36");
		//get.setHeader(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
		//get.setHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate, br");
		return get;
	}

}
