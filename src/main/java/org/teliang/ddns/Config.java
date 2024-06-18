package org.teliang.ddns;

public class Config {
	private String domain;
	private String type;
	private String key;
	private long executeFixTime;
	private String currentIp;

	public String getCurrentIp() {
		return currentIp;
	}

	public void setCurrentIp(String currentIp) {
		this.currentIp = currentIp;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public long getExecuteFixTime() {
		return executeFixTime;
	}

	public void setExecuteFixTime(long executeFixTime) {
		this.executeFixTime = executeFixTime;
	}

}
