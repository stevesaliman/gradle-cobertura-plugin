package net.saliman.gradle.plugin.cobertura

import groovy.io.FileType;
import net.saliman.gradle.plugin.cobertura.CoberturaRunner
import org.junit.Test

import static org.junit.Assert.assertEquals;

/**
 * Test class for Cobertura Runner.  We just wrap The Cobertura java class,
 * so this doesn't have to do much, just call it.
 */
class CoberturaRunnerTest {

	@Test
	public void instrument() throws Exception {
		File tmpFile = File.createTempFile("cobertura", "test");
		tmpFile.deleteOnExit();
		File destDir = new File(tmpFile.getParentFile(), "cobertura_test");
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		CoberturaRunner runner = new CoberturaRunner();
		runner.instrument("build/classes/main", "${destDir.absolutePath}/cobertura.ser", destDir.absolutePath, null, null, null, ['.']);
		File classesDir = new File('build/classes/main')
		int count = 0
		classesDir.eachFileMatch FileType.FILES, ~/.*\.class/, { count++ }
		int instrumentedCount = 0
		destDir.eachFileMatch FileType.FILES, ~/.*\.class/, { instrumentedCount++ }
		assertEquals(count, instrumentedCount)
		destDir.deleteDir()
	}
}
