package net.saliman.gradle.plugin.cobertura

import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection
import org.junit.After
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.fail

/**
 * This class tries to do actual builds under various circumstances to determine
 * which tasks actually run from run to run.
 *
 * @author Steven C. Saliman
 */
class CoberturaPluginExecutionTest {
	ProjectConnection connection
	String[] stdout
	String[] stderr

	@Before
	void setUp() {
		connection = GradleConnector.newConnector()
						.forProjectDirectory(new File("testclient/calculator"))
						.connect()
	}

	@After
	void tearDown() {
		if ( connection != null ) {
			System.out.println("Closing connection")
			connection.close()
		}
	}

	/**
	 * Helper task to start with a clean slate.
	 */
	void executeClean() {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
		ByteArrayOutputStream errorStream = new ByteArrayOutputStream()
		connection.newBuild()
							.withArguments("-b", "build.gradle")
							.forTasks("clean")
							.setStandardOutput(outputStream)
							.setStandardError(errorStream)
							.run()
		outputStream.close()
		errorStream.close()
		String s = outputStream.toString()
		if ( s != null ) {
			stdout = s.split('\n')
		}
		s = errorStream.toString()
		if ( s != null ) {
			stderr = s.split('\n')
		}
		System.out.println("here")
	}

	/**
	 * Execute tests without specifying any cobertura tasks.  This should cause
	 * tests to execute, but no cobertura related tasks.
	 */
	@Test
	void executeTestNoCobrtura() {
		executeClean();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
		ByteArrayOutputStream errorStream = new ByteArrayOutputStream()
		connection.newBuild()
						.withArguments("-b", "build.gradle", "-x", "testDivide")
						.forTasks("test")
						.setStandardOutput(outputStream)
						.setStandardError(errorStream)
						.run()
		outputStream.close()
		errorStream.close()
		String s = outputStream.toString()
		if ( s != null ) {
			stdout = s.split('\n')
		}
		s = errorStream.toString()
		if ( s != null ) {
			stderr = s.split('\n')
		}
		assertSkipped(":calculator:instrument")
		assertSkipped(":calculator:copyCoberturaDatafile")
		assertExecuted(":calculator:test")
		assertSkipped(":calculator:generateCoberturaReport")
		assertSkipped(":calculator:performCoverageCheck")
		System.out.println("here")
	}

	/**
	 * Execute the cobertura task.  This should cause tests to run.  It should
	 * also cause instrumentation and report generation, but not coverage
	 * checking.
	 */
	@Test
	void executeCobertura() {
		executeClean();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
		ByteArrayOutputStream errorStream = new ByteArrayOutputStream()
		connection.newBuild()
						.withArguments("-b", "build.gradle", "-x", "testDivide")
						.forTasks("cobertura")
						.setStandardOutput(outputStream)
						.setStandardError(errorStream)
						.run()
		outputStream.close()
		errorStream.close()
		String s = outputStream.toString()
		if ( s != null ) {
			stdout = s.split('\n')
		}
		s = errorStream.toString()
		if ( s != null ) {
			stderr = s.split('\n')
		}
		assertExecuted(":calculator:instrument")
		assertExecuted(":calculator:copyCoberturaDatafile")
		assertExecuted(":calculator:test")
		assertExecuted(":calculator:generateCoberturaReport")
		assertSkipped(":calculator:performCoverageCheck")
		System.out.println("here")
	}

	/**
	 * Execute the cobertura in a dry run.  Nothing should run in this case.
	 */
	@Test
	void executeCoberturaDryRun() {
		executeClean();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
		ByteArrayOutputStream errorStream = new ByteArrayOutputStream()
		connection.newBuild()
						.withArguments("-m", "-b", "build.gradle")
						.forTasks("cobertura")
						.setStandardOutput(outputStream)
						.setStandardError(errorStream)
						.run()
		outputStream.close()
		errorStream.close()
		String s = outputStream.toString()
		if ( s != null ) {
			stdout = s.split('\n')
		}
		s = errorStream.toString()
		if ( s != null ) {
			stderr = s.split('\n')
		}
		assertSkipped(":calculator:instrument")
		assertSkipped(":calculator:copyCoberturaDatafile")
		assertSkipped(":calculator:test")
		assertSkipped(":calculator:generateCoberturaReport")
		assertSkipped(":calculator:performCoverageCheck")
		System.out.println("here")
	}

	/**
	 * Test report generation when we build with the "test" and "coberturaReport"
	 * tasks.  This should do the same thing as running "cobertura" alone.
	 */
	@Test
	void executeTestCoberturaReport() {
		executeClean();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
		ByteArrayOutputStream errorStream = new ByteArrayOutputStream()
		connection.newBuild()
						.withArguments("-b", "build.gradle", "-x", "testDivide")
						.forTasks("test", "coberturaReport")
						.setStandardOutput(outputStream)
						.setStandardError(errorStream)
						.run()
		outputStream.close()
		errorStream.close()
		String s = outputStream.toString()
		if ( s != null ) {
			stdout = s.split('\n')
		}
		s = errorStream.toString()
		if ( s != null ) {
			stderr = s.split('\n')
		}
		assertExecuted(":calculator:instrument")
		assertExecuted(":calculator:copyCoberturaDatafile")
		assertExecuted(":calculator:test")
		assertExecuted(":calculator:generateCoberturaReport")
		assertSkipped(":calculator:performCoverageCheck")
		System.out.println("here")
	}

	/**
	 * Test checking coverage when we build with the "test" and "checkCoverage"
	 * tasks.  This should do the same thing as running "cobertura" alone, but
	 * with the addition of the coverage check task.
	 */
	@Test
	void executeTestCoberturaCheck() {
		executeClean();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
		ByteArrayOutputStream errorStream = new ByteArrayOutputStream()
		connection.newBuild()
						.withArguments("-b", "build.gradle", "-x", "testDivide")
						.forTasks("test", "coberturaCheck")
						.setStandardOutput(outputStream)
						.setStandardError(errorStream)
						.run()
		outputStream.close()
		errorStream.close()
		String s = outputStream.toString()
		if ( s != null ) {
			stdout = s.split('\n')
		}
		s = errorStream.toString()
		if ( s != null ) {
			stderr = s.split('\n')
		}
		assertExecuted(":calculator:instrument")
		assertExecuted(":calculator:copyCoberturaDatafile")
		assertExecuted(":calculator:test")
		assertExecuted(":calculator:generateCoberturaReport")
		assertExecuted(":calculator:performCoverageCheck")
		System.out.println("here")
	}

	/**
	 * Test checking coverage when we build with the "test" and "checkCoverage"
	 * tasks, but we are doing a dry run. No tasks should run.
	 */
	@Test
	void executeTestCoberturaCheckDryRun() {
		executeClean();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
		ByteArrayOutputStream errorStream = new ByteArrayOutputStream()
		connection.newBuild()
						.withArguments("-m", "-b", "build.gradle", "-x", "testDivide")
						.forTasks("test", "coberturaCheck")
						.setStandardOutput(outputStream)
						.setStandardError(errorStream)
						.run()
		outputStream.close()
		errorStream.close()
		String s = outputStream.toString()
		if ( s != null ) {
			stdout = s.split('\n')
		}
		s = errorStream.toString()
		if ( s != null ) {
			stderr = s.split('\n')
		}
		assertSkipped(":calculator:instrument")
		assertSkipped(":calculator:copyCoberturaDatafile")
		assertSkipped(":calculator:test")
		assertSkipped(":calculator:generateCoberturaReport")
		assertSkipped(":calculator:performCoverageCheck")
		System.out.println("here")
	}

	def assertExecuted(String task) {
		if ( stdout == null || stdout.length < 1 ) {
			fail "Standard Output not set. Did Gradle run?"
		}
		for ( String s : stdout ) {
			// if the line is just the task name, it executed.
			if ( s.equals(task) ) {
				return
			}
			// If the line starts with the task name and a space, it was either
			// skipped or up to date.  The assert statement just gives us a nice
			// message.
			if ( s.startsWith("${task} ") ) {
				assertEquals("${task} did not execute.", task, s)
				return  // we know it executed, return.
			}
		}
		fail "${task} was not in the output, so it did not execute."
	}

	def assertSkipped(String task) {
		if ( stdout == null || stdout.length < 1 ) {
			fail "Standard Output not set. Did Gradle run?"
		}
		for ( String s : stdout ) {
			if ( s.startsWith("${task} ") ) {
				// need 2 Groovy Strings...
				assertEquals("${task} did not execute.", "${task} SKIPPED", "${s}")
				return // we know it was skipped, return
			}
		}
		fail "${task} was not in the output, is it a valid task?"
	}

	def assertUpToDate(String task) {
		if ( stdout == null || stdout.length < 1 ) {
			fail "Standard Output not set. Did Gradle run?"
		}
		for ( String s : stdout ) {
			if ( s.startsWith("${task} ") ) {
				// Need 2 Groovy strings...
				assertEquals("${task} did not execute.", "${task} UP-TO-DATE", "${s}")
				return // we know it was skipped, return
			}
		}
		fail "${task} was not in the output, is it a valid task?"
	}
}
