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
	 * Path to the data file to produce during instrumentation. This file is used
	 * to determine if instrumentation is up to date, so it cannot be used during
	 * the tests themselves. Defaults to
	 * ${project.buildDir.path}/cobertura/coberturaInput.ser
	 */
	File coverageInputDatafile

	/**
	 * Path to the data file to use during tests.  The contents of this file are
	 * changed by testing, so a new copy is made from the input datafile before
	 * each test run. Defaults to
	 * ${project.buildDir.path}/cobertura/cobertura.ser
	 */
	File coverageOutputDatafile

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
	 * Directories of source files to use. The default is to look for and include
	 * each of the following, if present:
	 * project.sourceSets.main.java.srcDirs,
	 * project.sourceSets.main.groovy.srcDirs,
	 * and project.sourceSets.main.scala.srcDirs
	 */
	Set<File> coverageSourceDirs

	/**
	 * List of include patterns
	 */
	List<String> coverageIncludes = []

	/**
	 * List of exclude patterns
	 */
	List<String> coverageExcludes = []

	/**
	 * List of ignore patterns
	 */
	List<String> coverageIgnores = []

	/**
	 * Whether or not to ignore trivial methods like simple getters and setters.
	 */
	boolean coverageIgnoreTrivial = false;

	/**
	 * List of fully qualified annotation names that, if present on a method,
	 * will cause it to be ignored by Cobertura for coverage purposes.
	 */
	List<String> coverageIgnoreMethodAnnotations = []

    /**
     * branch coverage rate
     */
    String branchCoverage

    /**
     * line coverage rate
     */
    String lineCoverage

    /**
     * package branch coverage  rate
     */
    String packageBranchCoverage

    /**
     * package line coverage rate
     */
    String packageLineCoverage

    /**
     * total line coverage rate
     */
    String totalLineCoverage

    /**
     * total branch coverage rate
     */
    String totalBranchCoverage

    /**
     * regex for minimum coverage rate
     */
    String minCoverageRegex

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
		coverageInputDatafile = new File("${project.buildDir.path}/cobertura", 'coberturaInput.ser')
		coverageOutputDatafile = new File("${project.buildDir.path}/cobertura", 'cobertura.ser')
		coverageReportDir = new File("${project.reporting.baseDir.path}/cobertura")
		// The cobertura plugin causes the java plugin to be included.  Also, the
		// groovy and scala plugins extend the java plugin.  This means that the
		// java source directories will always be defined.
		coverageSourceDirs = project.sourceSets.main.java.srcDirs
		// Look for Groovy
		try {
			coverageSourceDirs += project.sourceSets.main.groovy.srcDirs
		} catch (MissingPropertyException e) {
			// This just means we don't have the groovy plugin.
		}
		// Look for Scala
		try {
  		coverageSourceDirs += project.sourceSets.main.scala.srcDirs
		} catch (MissingPropertyException e) {
			// This just means we don't have the Scala plugin.
		}
	}
}
