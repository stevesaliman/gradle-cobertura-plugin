package net.saliman.gradle.plugin.cobertura.testclient.util;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class StringUtilTest {
	@Test
	public void isUsAscii() {
	  assertTrue(StringUtil.isUsAscii("some text"));
	}
}
