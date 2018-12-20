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
		// TODO: Figure out how to verify that the correct default for srcDirs was
		// passed to the runner.
		System.out.println("here")
	}

	// TODO: Test that we can override the srcDirs when we have something in the
	// CoberturaExtension.

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
		return !stdout.any {
			it.contains("$task SKIPPED") || it.contains("$task UP-TO-DATE")
		}
	}

	def assertSkipped(String task) {
		if ( stdout == null || stdout.length < 1 ) {
			fail "Standard Output not set. Did Gradle run?"
		}
		return stdout.any {
			it.contains("$task SKIPPED")
		}
	}

	def assertUpToDate(String task) {
		if ( stdout == null || stdout.length < 1 ) {
			fail "Standard Output not set. Did Gradle run?"
		}
		return stdout.any {
			it.contains("$task UP-TO-DATE")
		}
	}
}