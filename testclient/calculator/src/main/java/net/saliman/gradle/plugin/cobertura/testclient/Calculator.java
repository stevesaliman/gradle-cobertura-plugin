package net.saliman.gradle.plugin.cobertura.testclient;

/**
 * Just a simple little calculator that we can use to test some things.
 *
 * @author Steven C. Saliman
 */
public class Calculator {
	public int add(int x, int y) {
		Logger.log("Adding");
		return x+y;
	}

	public int subtract(int x, int y) {
		return x-y;
	}

	public int divide(int x, int y) {
		// No divide by zero test - on purpose.
		return x/y;
	}

	public int multiply(int x, int y) {
		return x*y;
	}
}
