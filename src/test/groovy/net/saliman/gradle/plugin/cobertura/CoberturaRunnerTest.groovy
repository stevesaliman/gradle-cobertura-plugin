package net.saliman.gradle.plugin.cobertura

import groovy.io.FileType;
import net.saliman.gradle.plugin.cobertura.CoberturaRunner
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals;

/**
 * Test class for Cobertura Runner.  We just wrap The Cobertura java class,
 * so this doesn't have to do much, just call it.
 */
class CoberturaRunnerTest {
	def runner
	def project = ProjectBuilder.builder().build()

	@Before
	void setUp() {
		project.apply plugin: 'java'
		runner = new CoberturaRunner()
	}

	@Test
	public void instrument() throws Exception {
		File tmpFile = File.createTempFile("cobertura", "test")
		tmpFile.deleteOnExit()
		File destDir = new File(tmpFile.getParentFile(), "cobertura_test")
		if (!destDir.exists()) {
			destDir.mkdirs()
		}
		def configuration = new CoberturaExtension(project)
		configuration.coverageInputDatafile = new File("${destDir.absolutePath}/cobertura.ser")

		runner.instrument(configuration, "build/classes/main", destDir.absolutePath, ['.'])
		File classesDir = new File('build/classes/main')
		int count = 0
		classesDir.eachFileMatch FileType.FILES, ~/.*\.class/, { count++ }
		int instrumentedCount = 0
		destDir.eachFileMatch FileType.FILES, ~/.*\.class/, { instrumentedCount++ }
		assertEquals(count, instrumentedCount)
		destDir.deleteDir()
	}

	@Test
	void compare() {
		assertEquals(-1, runner.compareVersions("1", "2"))
		assertEquals(0, runner.compareVersions("2.0.2", "2.0.2"))
		assertEquals(1, runner.compareVersions("2.0.2", "2.0"))
		assertEquals(-1, runner.compareVersions("3.0", "4.0.1"))
		assertEquals(-1, runner.compareVersions("2.0.4", "2.1"))
		assertEquals(-1, runner.compareVersions("2.01.4", "2.10.4"))
		assertEquals(-1, runner.compareVersions("2.1.4", "2.10.4"))
		assertEquals(-1, runner.compareVersions("2.0.4", "2.0.5-SNAPSHOT"))
	}
}
