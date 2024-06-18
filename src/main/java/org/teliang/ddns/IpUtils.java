package org.teliang.ddns;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Function;

public class IpUtils {
	public static String getCurrentIp() {
		String currentIp;
		var url = "http://api.ipify.org";
		
		HttpClient client = HttpClientSingleton.getClientInstance();

		// Create a GET HttpRequest
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
		// Send the request and get the response
		currentIp = client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body)
				.thenApply(Function.identity()).join();

		return currentIp;
	}


}
