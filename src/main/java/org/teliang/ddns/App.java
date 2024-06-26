package org.teliang.ddns;

import java.util.Map;
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
	@Option(names = { "--hostNames" }, required = true, description = "hostNames")
	String hostNames;
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
		config.setHostNames(hostNames.split(","));

		DNSService dnsService = new NamesiloServiceImp(config);
		while (true) {
			String currentIp = IpUtils.getCurrentIp();
			logger.info("get current ip : {}", currentIp);
			config.setCurrentIp(currentIp);

			Map<String, Entry<String, String>> map = dnsService.getAllRecords();
			for (var hostName : config.getHostNames()) {
				String fullDomain = "";
				if (hostName != null && !hostName.isEmpty()) {
					fullDomain = hostName + "." + config.getDomain();
				} else {
					fullDomain = config.getDomain();
				}
				Entry<String, String> entry = map.get(fullDomain);
				if (entry == null) {
					dnsService.addRecord(hostName);
				} else if (!Objects.equals(currentIp, entry.getValue())) {
					dnsService.updateRecord(entry, hostName);
				} else {
					logger.info("fullDomain: {} ,current ip equals dns ip!", fullDomain);
				}
			}

			try {
				Thread.sleep(config.getExecuteFixTime());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
