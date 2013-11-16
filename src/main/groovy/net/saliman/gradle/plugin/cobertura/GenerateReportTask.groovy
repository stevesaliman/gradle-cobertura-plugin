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
 */
class GenerateReportTask extends DefaultTask {
	static final String NAME = 'generateCoberturaReport'
	File destinationDir
	CoberturaExtension configuration
	CoberturaRunner runner
	Configuration classpath

	@TaskAction
	def generateReports() {
		project.logger.info("Generating reports...")
		// Generate a report for each provided format
		for (format in configuration.coverageFormats) {
			runner.withClasspath(classpath.files).generateCoverageReport(
							configuration.coverageOutputDatafile.path,
							configuration.coverageReportDir.path,
							format,
							project.files(configuration.coverageSourceDirs).files.collect { it.path })
		}
	}
}
