package net.saliman.gradle.plugin.cobertura.testclient.logger;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LoggerTest {

	@Test
	public void getLinesWritten() {
		Logger logger = Logger.getInstance();
		assertEquals(0, logger.getLinesWritten());
		Logger.log("Something");
		assertEquals(1, logger.getLinesWritten());
	}

}
