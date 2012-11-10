package net.saliman.gradle.plugin.cobertura.testclient;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test the divide function.  We expect it to run in the middle.
 *
 * @author Steven C. Saliman
 */
public class CalculatorDivideTest {
	private Calculator calculator = new Calculator();

	@Test
	public void divideByZero() {
		assertEquals(2, calculator.divide(4, 0));
	 }
}
