package net.saliman.gradle.plugin.cobertura

import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.tooling.BuildException

/**
 * Gradle task that does the actual work of generating the Cobertura coverage
 * reports.
 * <p>
 * The plugin will add this task as finalizer of test tasks, but it will only
 * be enabled if the user intends to generate a report via the
 * {@code cobertura} or {@code coberturaReport) tasks.
 * <p>
 * This task does not declare inputs or outputs because we want it to run every
 * time (when it is enabled).  This is because while the code and tests may not
 * have changed, the actual tests run from build to build may have.
 */
class PerformCoverageCheckTask extends DefaultTask {
	static final String NAME = 'performCoverageCheck'
	@Internal
	File destinationDir
	@Internal
	CoberturaExtension configuration
	@Internal
	CoberturaRunner runner
	@Classpath
	Configuration classpath
	@Internal
	boolean coverageCheckFailed = false

	PerformCoverageCheckTask() {
		// Never consider this up to date.  We might be executing different tests
		// from run to run.
		outputs.upToDateWhen { false }
	}

	@TaskAction
	def checkCoverage() {
		project.logger.info("${path} - Checking coverage...")
		def exitStatus = runner.withClasspath(classpath.files).checkCoverage(configuration)

		if (exitStatus != null && exitStatus != 0) {
			if (configuration.coverageCheckHaltOnFailure) {
				throw new BuildException("performCoverageCheck: Tests failed to meet minimum coverage levels.", null)
			}
			coverageCheckFailed = true;
		} else {
			project.logger.info("${path}: Tests met minimum coverage levels.")
		}
	}
}
