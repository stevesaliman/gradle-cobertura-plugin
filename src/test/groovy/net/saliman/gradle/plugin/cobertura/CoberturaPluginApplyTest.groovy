package net.saliman.gradle.plugin.cobertura

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue
import static org.junit.Assert.fail
/**
 * Test case for things that should happen when the cobertura plugin is applied.
 * <p>
 * This is far, far from being complete.  At the moment, I just see if the
 * 4 tasks I expect are actually present.
 */
class CoberturaPluginApplyTest {
	Project project

	/**
	 * Set up for each test.  Create a new project, but do not apply the plugin
	 * because tests might want to do apply other plugins first.
	 */
	@Before
	void setUp() {
		project = ProjectBuilder.builder().build()
	}

	/**
	 * Apply the plugin and make sure the cobertura task is created with a
	 * dependency on the coberturaReport task.  This task should be enabled.
	 */
	@Test
	void applyPluginCoberturaTask() {
		project.apply plugin: 'cobertura'
		assertTrue("Project is missing plugin", project.plugins.hasPlugin(CoberturaPlugin))
		def task = project.tasks.findByName(CoberturaPlugin.COBERTURA_TASK_NAME)
		assertNotNull("Project is missing cobertura task", task)
		assertTrue("cobertura task is the wrong type", task instanceof DefaultTask)
		assertTrue("cobertura task should be enabled", task.enabled)
		assertTaskDependsOn(task, CoberturaPlugin.COBERTURA_REPORT_TASK_NAME)
		assertTaskDoesNotDependOn(task, CoberturaPlugin.COBERTURA_CHECK_TASK_NAME)
		assertTaskDoesNotDependOn(task, InstrumentTask.NAME)
		assertTaskDoesNotDependOn(task, CopyDatafileTask.NAME)
		assertTaskDoesNotDependOn(task, GenerateReportTask.NAME)
		assertTaskDoesNotDependOn(task, PerformCoverageCheckTask.NAME)
	}

	/**
	 * Apply the plugin and make sure the coberturaReport task is created with no
	 * dependencies and enabled.
	 */
	@Test
	void applyPluginCoberturaReportTask() {
		project.apply plugin: 'cobertura'
		def task = project.tasks.findByName(CoberturaPlugin.COBERTURA_REPORT_TASK_NAME)
		assertNotNull("Project is missing coberturaReport task", task)
		assertTrue("coberturaReport task is the wrong type", task instanceof DefaultTask)
		assertTrue("coberturaReport task should be enabled", task.enabled)
		assertTaskDoesNotDependOn(task, CoberturaPlugin.COBERTURA_TASK_NAME)
		assertTaskDoesNotDependOn(task, CoberturaPlugin.COBERTURA_CHECK_TASK_NAME)
		assertTaskDoesNotDependOn(task, InstrumentTask.NAME)
		assertTaskDoesNotDependOn(task, CopyDatafileTask.NAME)
		assertTaskDoesNotDependOn(task, GenerateReportTask.NAME)
		assertTaskDoesNotDependOn(task, PerformCoverageCheckTask.NAME)
	}

	/**
	 * Apply the plugin and make sure the coberturaReport task is created with no
	 * dependencies and enabled.
	 */
	@Test
	void applyPluginCheckCoverageTask() {
		project.apply plugin: 'cobertura'
		def task = project.tasks.findByName(CoberturaPlugin.COBERTURA_CHECK_TASK_NAME)
		assertNotNull("Project is missing checkCoverage task", task)
		assertTrue("checkCoverage task is the wrong type", task instanceof DefaultTask)
		assertTrue("checkCoverage task should be enabled", task.enabled)
		assertTaskDoesNotDependOn(task, CoberturaPlugin.COBERTURA_TASK_NAME)
		assertTaskDoesNotDependOn(task, CoberturaPlugin.COBERTURA_CHECK_TASK_NAME)
		assertTaskDoesNotDependOn(task, InstrumentTask.NAME)
		assertTaskDoesNotDependOn(task, CopyDatafileTask.NAME)
		assertTaskDoesNotDependOn(task, GenerateReportTask.NAME)
		assertTaskDoesNotDependOn(task, PerformCoverageCheckTask.NAME)
	}

	/**
	 * Apply the plugin and make sure the instrument task is created with no
	 * dependencies on tasks from this plugin.  It should start out disabled.
	 */
	@Test
	void applyPluginInstrumentTask() {
		project.apply plugin: 'cobertura'
		def task = project.tasks.findByName(InstrumentTask.NAME)
		assertNotNull("Project is missing instrument task", task)
		assertTrue("instrument task is the wrong type", task instanceof InstrumentTask)
		assertFalse("instrument task should not be enabled", task.enabled)
		assertTaskDoesNotDependOn(task, CoberturaPlugin.COBERTURA_TASK_NAME)
		assertTaskDoesNotDependOn(task, CoberturaPlugin.COBERTURA_REPORT_TASK_NAME)
		assertTaskDoesNotDependOn(task, CoberturaPlugin.COBERTURA_CHECK_TASK_NAME)
		assertTaskDoesNotDependOn(task, CopyDatafileTask.NAME)
		assertTaskDoesNotDependOn(task, GenerateReportTask.NAME)
		assertTaskDoesNotDependOn(task, PerformCoverageCheckTask.NAME)
	}

	/**
	 * Apply the plugin and make sure the copyCoberturaDatafile task is created
	 * with a dependency on the instrument task.  This task should be disabled.
	 */
	@Test
	void applyPluginCopyDatafileTask() {
		project.apply plugin: 'cobertura'
		def task = project.tasks.findByName(CopyDatafileTask.NAME)
		assertNotNull("Project is missing copyCoberturaDatafile task", task)
		assertTrue("copyCoberturaDatafile task is the wrong type", task instanceof CopyDatafileTask)
		assertFalse("copyCoberturaDatafile task should not be enabled", task.enabled)
		assertTaskDoesNotDependOn(task, CoberturaPlugin.COBERTURA_TASK_NAME)
		assertTaskDoesNotDependOn(task, CoberturaPlugin.COBERTURA_REPORT_TASK_NAME)
		assertTaskDoesNotDependOn(task, CoberturaPlugin.COBERTURA_CHECK_TASK_NAME)
		assertTaskDependsOn(task, InstrumentTask.NAME)
		assertTaskDoesNotDependOn(task, CopyDatafileTask.NAME)
		assertTaskDoesNotDependOn(task, GenerateReportTask.NAME)
		assertTaskDoesNotDependOn(task, PerformCoverageCheckTask.NAME)
	}

	/**
	 * Apply the plugin and make sure the generateReport task is created with no
	 * dependencies on other tasks from this plugin.  It should start out
	 * disabled.
	 */
	@Test
	void applyPluginGenerateReportTask() {
		project.apply plugin: 'cobertura'
		def task = project.tasks.findByName(GenerateReportTask.NAME)
		assertNotNull("Project is missing generateCoberturaReport task", task)
		assertTrue("generateCoberturaReport task is the wrong type", task instanceof GenerateReportTask)
		assertFalse("generateCoberturaReport task should not be enabled", task.enabled)
		assertTaskDoesNotDependOn(task, CoberturaPlugin.COBERTURA_TASK_NAME)
		assertTaskDoesNotDependOn(task, CoberturaPlugin.COBERTURA_REPORT_TASK_NAME)
		assertTaskDoesNotDependOn(task, CoberturaPlugin.COBERTURA_CHECK_TASK_NAME)
		assertTaskDoesNotDependOn(task, InstrumentTask.NAME)
		assertTaskDoesNotDependOn(task, CopyDatafileTask.NAME)
		assertTaskDoesNotDependOn(task, PerformCoverageCheckTask.NAME)
	}

	/**
	 * Apply the plugin and make sure the checkCoverage task is created with a
	 * dependency on the coberturaReport task.  This task should be disabled.
	 */
	@Test
	void applyPluginPerformCoverageCheckTask() {
		project.apply plugin: 'cobertura'
		def task = project.tasks.findByName(PerformCoverageCheckTask.NAME)
		assertNotNull("Project is missing performCoverageCheck task", task)
		assertTrue("performCoverageCheck task is the wrong type", task instanceof PerformCoverageCheckTask)
		assertFalse("performCoverageCheck task should not be enabled", task.enabled)
		assertTaskDoesNotDependOn(task, CoberturaPlugin.COBERTURA_TASK_NAME)
		assertTaskDoesNotDependOn(task, CoberturaPlugin.COBERTURA_REPORT_TASK_NAME)
		assertTaskDoesNotDependOn(task, CoberturaPlugin.COBERTURA_CHECK_TASK_NAME)
		assertTaskDoesNotDependOn(task, InstrumentTask.NAME)
		assertTaskDoesNotDependOn(task, CopyDatafileTask.NAME)
		assertTaskDependsOn(task, GenerateReportTask.NAME)
	}

	@Test
	void applyPluginConfiguration() {
		project.apply plugin: 'cobertura'
		CoberturaExtension configuration = project.extensions.getByName('cobertura')
		assertNotNull("We're missing the configuration", configuration)
		Set srcDirs = configuration.coverageSourceDirs
		assertNotNull("We're missing the srcDirs", srcDirs)
		assertEquals("Wrong number of srcDirs", 1, srcDirs.size())
		assertTrue(configuration.coverageSourceDirs.asList().get(0).path.endsWith("src/main/java"))
	}

	/**
	 * Helper method to make sure a task depends on another task.  This method
	 * will fail a test if the given task does not depend on the given other task.
	 * @param task the task to check
	 * @param dependsOn the name of the task it should depend on
	 */
	private void assertTaskDependsOn(Task task, String dependsOn) {
		if ( task.dependsOn == null ) {
			fail("Task ${task.name} should depend on ${dependsOn}")
		}
		for ( Object o : task.dependsOn ) {
			if ( Task.class.isAssignableFrom(o.class) ) {
				if ( ((Task)o).name == dependsOn ) {
					return; // found the required dependency
				}
			}
		}
		fail("Task ${task.name} should depend on ${dependsOn}")
	}

	/**
	 * Helper method to make sure a task does not depend on another task.  This
	 * method will fail a test if the given task depends on the given other task.
	 * @param task the task to check
	 * @param dependsOn the name of the task it should not depend on
	 */
	private void assertTaskDoesNotDependOn(Task task, String dependsOn) {
		if ( task.dependsOn == null ) {
			return
		}
		for ( Object o : task.dependsOn ) {
			if ( Task.class.isAssignableFrom(o.class) ) {
				if ( ((Task)o).name == dependsOn ) {
					fail("Task ${task.name} should not depend on ${dependsOn}")
				}
			}
		}
	}

}
