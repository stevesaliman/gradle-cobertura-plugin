package net.saliman.gradle.plugin.cobertura.testclient.calculator;

import net.saliman.gradle.plugin.cobertura.testclient.logger.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.Exception;
import java.lang.Process;
import java.lang.RuntimeException;
import java.lang.System;
import java.net.URL;
import java.util.Properties;
import net.saliman.gradle.plugin.cobertura.testclient.dao.CalculatorDao;

/**
 * Just a simple little calculator that we can use to test some things.
 *
 * @author Steven C. Saliman
 */
public class Calculator {
	private Properties properties;

	public Calculator() {
		if ( properties != null ) {
			return;
		}
		properties = new Properties();
		try {
			InputStream file = this.getClass().getResourceAsStream("/test.properties");
			properties.load(file);
		} catch (Exception e) {
			System.out.println("test.properties not found");
			throw new RuntimeException(e);
		}

	}
	public int add(int x, int y) {
		Logger.log("Adding for " + properties.get("author"));
		CalculatorDao.storeCalculation(x, y, "add");
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
