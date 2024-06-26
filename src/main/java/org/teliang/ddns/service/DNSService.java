package org.teliang.ddns.service;

import java.util.Map;
import java.util.Map.Entry;

public interface DNSService {
	Map<String, Entry<String, String>> getAllRecords();

	void addRecord(String hostName);

	void updateRecord(Entry<String, String> entry, String hostName);
}
