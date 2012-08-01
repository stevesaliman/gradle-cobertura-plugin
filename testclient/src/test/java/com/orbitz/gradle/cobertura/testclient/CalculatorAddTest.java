package com.orbitz.gradle.cobertura.testclient;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test the add function.  We expect it to run first.
 *
 * @author Steven C. Saliman
 */
public class CalculatorAddTest {
	private Calculator calculator = new Calculator();

	@Test
	public void add() {
		assertEquals(4, calculator.add(2, 2));
	 }
}
