package net.saliman.gradle.plugin.cobertura

import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.TaskAction

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
class GenerateReportTask extends DefaultTask {
	static final String NAME = 'generateCoberturaReport'
	CoberturaExtension configuration
	CoberturaRunner runner
	Configuration classpath

	@TaskAction
	def generateReports() {
		// If the user specified merge files, than do a merge before generating
		// reports.
		if ( configuration.coverageMergeDatafiles != null &&
		configuration.coverageMergeDatafiles.size() > 0 ) {
			project.logger.info("${path} - Merging datafiles...")
			runner.withClasspath(classpath.files).mergeCoverageReports(configuration)
		}

		project.logger.info("${path} - Generating reports...")
		// Generate a report for each provided format
		for (format in configuration.coverageFormats) {
			runner.withClasspath(classpath.files).generateCoverageReport(
							configuration.coverageReportDatafile.path,
							configuration.coverageReportDir.path,
							format,
							project.files(configuration.coverageSourceDirs).files.collect { it.path })
		}
	}
}
