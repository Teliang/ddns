package org.teliang.ddns.service;

import org.teliang.ddns.Config;

public abstract class AbstractDNSService {
	protected Config config;

	protected AbstractDNSService(Config config) {
		this.config = config;
	}

}
