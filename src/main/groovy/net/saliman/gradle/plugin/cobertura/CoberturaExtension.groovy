package net.saliman.gradle.plugin.cobertura


import org.gradle.api.Project;

/**
 * Extension class for configuring the Cobertura plugin.
 */
class CoberturaExtension {

	/**
	 * Directories under the base directory containing classes to be
	 * instrumented. Defaults to [project.sourceSets.main.classesDir.path]
	 */
	List<String> coverageDirs

	/**
	 * Path to data file to use for Cobertura. Defaults to
	 * ${project.buildDir.path}/cobertura/cobertura.ser
	 */
	File coverageDatafile

	/**
	 * Path to report directory for coverage report. Defaults to
	 * ${project.reportsDir.path}/cobertura
	 */
	File coverageReportDir

	/**
	 * Formats of cobertura report. Default is a single report in 'html'
	 * format.
	 */
	Set<String> coverageFormats = ['html']

	/**
	 * Directories of source files to use. Defaults to
	 * project.sourceSets.main.java.srcDirs
	 */
	Set<File> coverageSourceDirs

	/**
	 * List of include patterns
	 */
	List<String> coverageIncludes

	/**
	 * List of exclude patterns
	 */
	List<String> coverageExcludes

	/**
	 * List of ignore patterns
	 */
	List<String> coverageIgnores

	/**
	 * Whether or not to ignore trivial methods like simple getters and setters.
	 */
	boolean coverageIgnoreTrivial = false;

	/**
	 * List of fully qualified annotation names that, if present on a method,
	 * will cause it to be ignored by Cobertura for coverage purposes.
	 */
	List<String> coverageIgnoreMethodAnnotations

	/**
	 * Version of cobertura to use for the plugin. Defaults to 2.0.3
	 */
	String coberturaVersion = '2.0.3'

	private Project project

	/**
	 * Constructor for the extension.  It needs a project handle to set the sets
	 * sensible defaults.
	 * @param project the Gradle project that owns the extension.
	 */
	CoberturaExtension(Project project) {
		project.logger.info "creating extension"
		this.project = project
		coverageDirs = [ project.sourceSets.main.output.classesDir.path ]
		coverageDatafile = new File("${project.buildDir.path}/cobertura", 'cobertura.ser')
		coverageReportDir = new File("${project.reporting.baseDir.path}/cobertura")
		coverageSourceDirs = project.sourceSets.main.java.srcDirs
	}
}
