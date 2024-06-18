package org.teliang.ddns.service;

import java.util.Map.Entry;

public interface DNSService {
	Entry<String, String> getRecordIdAndIp();

	void addRecord();

	void updateRecord(Entry<String, String> entry);
}
