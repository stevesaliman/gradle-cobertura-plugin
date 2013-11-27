package net.saliman.gradle.plugin.cobertura

import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection
import org.junit.After
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.fail

/**
 * Created with IntelliJ IDEA.
 * User: steve
 * Date: 11/18/13
 * Time: 7:52 PM
 * To change this template use File | Settings | File Templates.
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


	@Test
	void execute() {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
		ByteArrayOutputStream errorStream = new ByteArrayOutputStream()
		connection.newBuild()
							.withArguments("-m", "-b", "build.gradle")
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
		assertExecuted(":buildSrc:test")
		assertUpToDate(":buildSrc:assemble")
		assertSkipped(":calculator:clean")
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
