package net.saliman.gradle.plugin.cobertura.testclient.calculator;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test the subtract function.  We expect it to run last.
 *
 * @author Steven C. Saliman
 */
public class CalculatorSubtractTest {
	private Calculator calculator = new Calculator();

	@Test
	public void subtract() {
		assertEquals(2, calculator.subtract(4, 2));
	 }
}
