package org.teliang.ddns.service.imp;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.teliang.ddns.Config;
import org.teliang.ddns.HttpClientSingleton;
import org.teliang.ddns.service.AbstractDNSService;
import org.teliang.ddns.service.DNSService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class NamesiloServiceImp extends AbstractDNSService implements DNSService {
	private static final Logger logger = LogManager.getLogger(NamesiloServiceImp.class);

	public NamesiloServiceImp(Config config) {
		super(config);
	}

	@Override
	public void addRecord(String hostName) {
//		add
		var baseUri = "https://www.namesilo.com/api/dnsAddRecord";
		Map<String, String> parameters = new HashMap<String, String>() {
			{
				put("version", "1");
				put("type", "xml");
				put("key", config.getKey());
				put("domain", config.getDomain());
				put("rrtype", config.getType());
				put("rrhost", hostName);
				put("rrvalue", config.getCurrentIp());
				put("rrttl", "3600");
			}
		};

		String url = constructUriWithParams(baseUri, parameters);

		HttpClient client = HttpClientSingleton.getClientInstance();

		// Create a GET HttpRequest
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
		// Send the request and get the response
		client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body).thenAccept(e -> {
			try {
				XmlMapper mapper = new XmlMapper();
				logger.debug("dnsAddRecord response : {}", e);
				Map map = mapper.readValue(e, Map.class);
				if (isSucces(map)) {
					logger.info("dnsAddRecord request is Successful");
				} else {
					logger.error("dnsAddRecord request is not Succes");
				}
			} catch (JsonProcessingException e1) {
				logger.error("dnsAddRecord JsonProcessingException", e);
			}
		}).join();

	}

	@Override
	public void updateRecord(Entry<String, String> entry, String hostName) {
//		update
		var baseUri = "https://www.namesilo.com/api/dnsUpdateRecord";

		Map<String, String> parameters = new HashMap<String, String>() {
			{
				put("version", "1");
				put("type", "xml");
				put("key", config.getKey());
				put("domain", config.getDomain());
				put("rrtype", config.getType());
				put("rrhost", hostName);
				put("rrvalue", config.getCurrentIp());
				put("rrttl", "3600");
				put("rrid", entry.getKey());
			}
		};

		String url = constructUriWithParams(baseUri, parameters);

		HttpClient client = HttpClientSingleton.getClientInstance();

		// Create a GET HttpRequest
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
		// Send the request and get the response
		client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body).thenAccept(e -> {
			try {
				XmlMapper mapper = new XmlMapper();
				logger.debug("dnsUpdateRecord response : {}", e);
				Map map = mapper.readValue(e, Map.class);
				if (isSucces(map)) {
					logger.info("dnsUpdateRecord request is Successful");
				} else {
					logger.error("dnsUpdateRecord request is not Succes");
				}
			} catch (JsonProcessingException e1) {
				logger.error("dnsUpdateRecord JsonProcessingException", e);
			}
		}).join();

	}

	@Override
	public Map<String, Entry<String, String>> getAllRecords() {

		HashMap<String, Entry<String, String>> hashMap = new HashMap<String, Entry<String, String>>();
		var url = "https://www.namesilo.com/api/dnsListRecords?version=1&type=xml&key=%s&domain=%s"
				.formatted(config.getKey(), config.getDomain());

		HttpClient client = HttpClientSingleton.getClientInstance();

		// Create a GET HttpRequest
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
		// Send the request and get the response
		client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body).thenAccept(e -> {
			try {
				XmlMapper mapper = new XmlMapper();
				logger.debug("dnsListRecords response : {}", e);
				Map map = mapper.readValue(e, Map.class);
				if (!isSucces(map)) {
					logger.error("dnsListRecords request is not Succes, response : {}", e);
					return;
				}
				map = (Map) map.get("reply");
				Object resourceRecord = map.get("resource_record");
				if (resourceRecord instanceof List) {
					List<Map<String, String>> list = (List<Map<String, String>>) resourceRecord;
					for (var obj : list) {
						if (config.getType().equals(obj.get("type"))) {
							String recordId = obj.get("record_id");
							String value = obj.get("value");
							SimpleEntry<String, String> simpleEntry = new AbstractMap.SimpleEntry<String, String>(
									recordId, value);
							hashMap.put(obj.get("host"), simpleEntry);
						}
					}
				} else if (resourceRecord instanceof Map) {
					Map<String, String> obj = (Map<String, String>) resourceRecord;
					if (config.getType().equals(obj.get("type"))) {
						String recordId = obj.get("record_id");
						String value = obj.get("value");
						SimpleEntry<String, String> simpleEntry = new AbstractMap.SimpleEntry<>(recordId, value);
						hashMap.put(obj.get("host"), simpleEntry);
					}
				} else {
					logger.info("dnsListRecords resourceRecord -- : {}", resourceRecord);
				}

			} catch (JsonProcessingException e1) {
				logger.error("dnsListRecords JsonProcessingException", e);
			}
		}).join();

		logger.info("get recordId And Ip : {}", hashMap);
		return hashMap;

	}

	private static boolean isSucces(Map map) {
		map = (Map) map.get("reply");

		return Objects.equals("300", map.get("code"));
	}

	private static String constructUriWithParams(String baseUri, Map<String, String> parameters) {
		StringJoiner joiner = new StringJoiner("&");

		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			String encodedKey = URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8);
			String encodedValue = URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8);
			joiner.add(encodedKey + "=" + encodedValue);
		}

		return baseUri + "?" + joiner.toString();
	}
}
