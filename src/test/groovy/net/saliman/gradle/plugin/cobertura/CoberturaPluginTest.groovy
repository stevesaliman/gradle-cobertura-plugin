package net.saliman.gradle.plugin.cobertura

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.assertTrue
import static org.junit.Assert.assertNotNull
import org.gradle.api.DefaultTask;

/**
 * Test case for Cobertura plugin
 * <p>
 * This is far, far from being complete.  At the moment, I just see if the
 * 4 tasks I expect are actually present.
 */
class CoberturaPluginTest {
	def project = ProjectBuilder.builder().build()
	def plugin = new CoberturaPlugin()

	@Test
	void canApplyPlugin() {
		project.apply plugin: 'cobertura'
		assertTrue("Project is missing plugin", project.plugins.hasPlugin(CoberturaPlugin))
		def task = project.tasks.findByName("instrument")
		assertNotNull("Project is missing instrument task", task)
		assertTrue("Instrument task is the wrong type", task instanceof InstrumentTask)
		task = project.tasks.findByName("generateCoberturaReport")
		assertNotNull("Project is missing generateCoberturaReport task", task)
		assertTrue("GenerateCoberturaReport task is the wrong type", task instanceof GenerateReportTask)
		task = project.tasks.findByName("coberturaReport")
		assertNotNull("Project is missing coberturaReport task", task)
		assertTrue("CoberturaReport task is the wrong type", task instanceof DefaultTask)
		task = project.tasks.findByName("cobertura")
		assertNotNull("Project is missing cobertura task", task)
		assertTrue("cobertura task is the wrong type", task instanceof DefaultTask)
		assertNotNull("We're missing the configuration", project.configurations.asMap['cobertura'])
        task = project.tasks.findByName("coberturaCheck")
        assertNotNull("Project is missing coberturaCheck task", task)
        assertTrue("Check task is the wrong type", task instanceof CheckTask)
    }
}
