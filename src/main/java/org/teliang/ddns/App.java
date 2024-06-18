package org.teliang.ddns;

import java.util.Map.Entry;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.teliang.ddns.service.DNSService;
import org.teliang.ddns.service.imp.NamesiloServiceImp;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * ddns
 *
 */
@Command(name = "ddns", version = "ddns 1.0", mixinStandardHelpOptions = true)
public class App implements Runnable {

	private static final Logger logger = LogManager.getLogger(App.class);

	@Option(names = { "--domain" }, required = true, description = "domain")
	String domain;
	@Option(names = { "--type" }, defaultValue = "A", description = "domain type")
	String type;
	@Option(names = { "--key" }, required = true, description = "service api key")
	String key;
	@Option(names = { "--executeFixTime" }, defaultValue = "300", description = "executeFixTime")
	String executeFixTime;

	public static void main(String[] args) {
		int exitCode = new CommandLine(new App()).execute(args);
		System.exit(exitCode);
	}

	@Override
	public void run() {
		Config config = new Config();
		config.setDomain(domain);
		config.setType(type);
		config.setKey(key);
		config.setExecuteFixTime(Long.valueOf(executeFixTime) * 1000);

		DNSService dnsService = new NamesiloServiceImp(config);
		while (true) {
			String currentIp = IpUtils.getCurrentIp();
			logger.info("get current ip : {}", currentIp);
			config.setCurrentIp(currentIp);

			Entry<String, String> entry = dnsService.getRecordIdAndIp();
			if (entry == null) {
				dnsService.addRecord();
			} else if (!Objects.equals(currentIp, entry.getValue())) {
				dnsService.updateRecord(entry);
			} else {
				logger.info("current ip equals dns ip!");
			}

			try {
				Thread.sleep(config.getExecuteFixTime());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
