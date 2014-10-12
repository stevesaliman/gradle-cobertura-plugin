package net.saliman.gradle.plugin.cobertura.testclient.dao;

/**
 * This class is used to simulate saving something to a database. The intent
 * is to give Cobertura something to ignore so we can test exclusion patterns.
 */
public class CalculatorDao {
	public static void storeCalculation(int num1, int num2, String operator) {
		System.out.println("Applying " + operator + " to " + num1 + " and " + num2);
	}
}