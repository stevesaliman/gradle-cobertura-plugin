package net.saliman.gradle.plugin.cobertura

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.TaskAction

/**
 * Gradle task that does the actual work of generating the Cobertura coverage
 * reports.
 * <p>
 * The plugin will add this task as finalizer of test tasks
 */
class GenerateReportTask extends DefaultTask {
	File destinationDir
	CoberturaExtension configuration
	def runner

	@TaskAction
	def generateReports() {
		project.logger.info("Generating reports...")
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
