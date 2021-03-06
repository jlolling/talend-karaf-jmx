package de.jlo.talendcomp.karaf.jmx;

import static org.junit.Assert.assertTrue;

import java.lang.management.MemoryUsage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.management.ObjectInstance;
import javax.management.ObjectName;

import org.junit.Test;

public class TestCollectMetrics {
	
	@Test
	public void testConnect() throws Exception {
		String host = "dwhtalendjobtest02.gvl.local";
		int jmxPort = 44444;
		int jstatdPort = 1099;
		String karafInstance = "trun";
		String user = "karaf";
		String passwd = "karaf";
		KarafClient c = new KarafClient();
		c.setJmxUser(user);
		c.setJmxPassword(passwd);
		c.setKarafRemoteJmxUrl(host, jmxPort, karafInstance, jstatdPort);
		c.connect();
		assertTrue(true);
		// check memory usage
		MemoryUsage meminfo = c.getMemoryInfo();
		System.out.println("max=" + meminfo.getMax() + " used=" + meminfo.getUsed());
	}

	@Test
	public void testQueryObjectNames() throws Exception {
		String host = "dwhtalendjobtest02.gvl.local";
		int jmxPort = 44444;
		int jstatdPort = 1099;
		String karafInstance = "trun";
		String user = "karaf";
		String passwd = "karaf";
		KarafClient c = new KarafClient();
		c.setJmxUser(user);
		c.setJmxPassword(passwd);
		c.setKarafRemoteJmxUrl(host, jmxPort, karafInstance, jstatdPort);
		c.connect();
		ObjectName pattern = new ObjectName("org.apache.cxf:type=Metrics.Server,*");
		System.out.println(pattern.isPropertyListPattern());
		Set<ObjectInstance> result = c.getmBeanServerConnection().queryMBeans(pattern /*new ObjectName("org.apache.cxf.bus.id=*")*/, null);
		for (ObjectInstance oi : result) {
			if ("com.codahale.metrics.JmxReporter$JmxTimer".equals(oi.getClassName())) {
				ObjectName on = oi.getObjectName();
				System.out.println(on.getKeyProperty("bus.id"));
			}
		}
		assertTrue(true);
	}
	
	@Test
	public void testSetupCXFMetricObjectNames() throws Exception {
		String host = "dwhtalendjobtest02.gvl.local";
		int jmxPort = 44444;
		int jstatdPort = 1099;
		String karafInstance = "trun";
		String user = "karaf";
		String passwd = "karaf";
		KarafClient c = new KarafClient();
		c.setJmxUser(user);
		c.setJmxPassword(passwd);
		c.setKarafRemoteJmxUrl(host, jmxPort, karafInstance, jstatdPort);
		c.connect();
		CXFMetricsCollector coll = new CXFMetricsCollector(c);
		coll.setArtifactPattern("core_api|beat17");
		coll.setupCXFTotalsMetricObjectNames();
		assertTrue(true);
	}

	@Test
	public void testListServiceMetrics() throws Exception {
		String host = "dwhtalendjobtest02.gvl.local";
		int jmxPort = 44444;
		int jstatdPort = 1099;
		String karafInstance = "trun";
		String user = "karaf";
		String passwd = "karaf";
		KarafClient c = new KarafClient();
		c.setJmxUser(user);
		c.setJmxPassword(passwd);
		c.setKarafRemoteJmxUrl(host, jmxPort, karafInstance, jstatdPort);
		c.connect();
		CXFMetricsCollector coll = new CXFMetricsCollector(c);
		coll.setInterval(5);
		coll.setArtifactPattern("core_api|beat17");
		coll.setupCXFTotalsMetricObjectNames();
		System.out.println("\n\n#########################");
		int count = 0;
		while (coll.next()) {
			List<ServiceMetric> metrics = coll.fetchServiceMetrics();
			for (ServiceMetric m : metrics) {
				System.out.println(m);
			}
			if (++count == 100) {
				break;
			}
			System.out.println(new SimpleDateFormat("HH:mm:ss").format(new Date()));
			System.out.println("#########################");
		}
		assertTrue(true);
	}

	@Test
	public void testListSystemMetrics() throws Exception {
		String host = "talendjob01.gvl.local";
		int jmxPort = 44444;
		int jstatdPort = 1099;
		String karafInstance = "trun";
		String user = "karaf";
		String passwd = "karaf";
		KarafClient c = new KarafClient();
		c.setJmxUser(user);
		c.setJmxPassword(passwd);
		c.setKarafRemoteJmxUrl(host, jmxPort, karafInstance, jstatdPort);
		c.connect();
		JVMMetricsCollector coll = new JVMMetricsCollector(c);
		System.out.println("\n\n#########################");
		for (int i = 0; i < 1000; i++) {
			SystemMetrics metrics = coll.fetchSystemMetrics();
			System.out.println(metrics);
			Thread.sleep(1000);
		}
		assertTrue(true);
	}
}
