package net.saliman.gradle.plugin.cobertura.testclient.util;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public class StringUtil {
    private static final CharsetEncoder ASCII_ENCODER = Charset.forName("US-ASCII").newEncoder(); // or "ISO-8859-1" for ISO Latin 1

    public static boolean isUsAscii(String input) {
		return ASCII_ENCODER.canEncode(input);
	}
}
