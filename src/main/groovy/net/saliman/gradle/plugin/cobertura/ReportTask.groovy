package net.saliman.gradle.plugin.cobertura

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.TaskAction

/**
 * Gradle task that creates the actual Cobertura coverage reports.
 * <p>
 * The plugin will add this task as finalizer of test tasks
 */
class ReportTask extends DefaultTask {
	File destinationDir
	CoberturaExtension configuration
	def runner

	@TaskAction
	def instrument() {
		project.logger.info("Instrumenting code...")
		// Generate a report for each provided format
		for ( format in configuration.coverageFormats ) {
			runner.generateCoverageReport(
							configuration.coverageDatafile.path,
							configuration.coverageReportDir.path,
							format,
							project.files(configuration.coverageSourceDirs).files.collect { it.path })
		}
	}
}
