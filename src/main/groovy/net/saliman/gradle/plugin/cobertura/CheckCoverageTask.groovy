package net.saliman.gradle.plugin.cobertura

import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.TaskAction
import org.gradle.tooling.BuildException

/**
 * Gradle task that does the actual work of generating the Cobertura coverage
 * reports.
 * <p>
 * The plugin will add this task as finalizer of test tasks, but it will only
 * be enabled if the user intends to generate a report via the
 * {@code cobertura} or {@code coberturaReport) tasks.
 */
class CheckCoverageTask extends DefaultTask {
	static final String NAME = 'checkCoverage'
	File destinationDir
	CoberturaExtension configuration
	CoberturaRunner runner
	Configuration classpath
	boolean coverageCheckFailed = false

	@TaskAction
	def checkCoverage() {
		project.logger.info("${path} - Checking coverage...")
		def exitStatus = runner.withClasspath(classpath.files).checkCoverage(configuration)

		if (exitStatus != null && exitStatus != 0) {
			if (configuration.coverageCheckHaltOnFailure) {
				throw new BuildException("checkCoverage: Tests failed to meet minimum coverage levels.", null)
			}
			coverageCheckFailed = true;
		} else {
			project.logger.info("${path}: Tests met minimum coverage levels.")
		}
	}
}
