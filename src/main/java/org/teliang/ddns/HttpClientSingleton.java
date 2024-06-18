package org.teliang.ddns;

import java.net.http.HttpClient;

public class HttpClientSingleton {
	// Create an HttpClient instance
	private static final HttpClient CLIENT = HttpClient.newHttpClient();

	public static HttpClient getClientInstance() {
		return CLIENT;
	}
}
