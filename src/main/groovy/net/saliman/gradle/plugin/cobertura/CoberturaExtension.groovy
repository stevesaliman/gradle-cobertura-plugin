package net.saliman.gradle.plugin.cobertura

import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.TaskCollection
import org.gradle.api.tasks.testing.Test

/**
 * Extension class for configuring the Cobertura plugin.  Most of the properties
 * in this extension match options in Cobertura itself.
 */
class CoberturaExtension {
	static ENCODING_UNDEFINED = 'undefined'

	/**
	 * Version of cobertura to use for the plugin. Defaults to 2.1.1
	 */
	String coberturaVersion = '2.1.1'

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
	 * The character encoding to use when generating coverage reports.  If no
	 * encoding is specified, the plugin uses the system default.
	 */
	// For some reason, Gradle doesn't seem to like nulls in @Input properties.
	String coverageEncoding = ENCODING_UNDEFINED

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
	 * The classpath when instrumenting.
	 * Defaults to 
	 * project.sourceSets.main.output.classesDir,
	 * project.sourceSets.main.compileClasspath,
	 */
	FileCollection auxiliaryClasspath

	/**
	 * Whether or not to ignore trivial methods like simple getters and setters.
	 * Available in in Cobertura 2.0.0 and later.  Private to force use of the
	 * setter method.
	 */
	private boolean coverageIgnoreTrivial = false;

	/**
	 * List of fully qualified annotation names that, if present on a method,
	 * will cause it to be ignored by Cobertura for coverage purposes.
	 * Available in Cobertura 2.0.0 and later. Private to force use of the
	 * setter method
	 */
	private List<String> coverageIgnoreMethodAnnotations = []

	/**
	 * Closure that returns the tasks that produce the classes that need to be
	 * instrumented.  The default is the "classes" task.
	 */
	private Closure coverageClassesTasksSpec

	/**
	 * Closure that returns all the tasks in the project that test the code of
	 * interest.  The default is all tasks of type "Test".
	 */
	private Closure coverageTestTasksSpec

  // -----------------------------------------------------------------------
	// properties used for coverage checks.  Many of them are private to force
	// using the "set" methods which check the value range.

	/**
	 * The minimum acceptable branch coverage rate needed by each class. This
	 * should be an integer value between 0 and 100.
	 */
	private Integer coverageCheckBranchRate

	/**
	 * The minimum acceptable line coverage rate needed by each class. This should
	 * be an integer value between 0 and 100.
	 */
	private Integer coverageCheckLineRate

	/**
	 * The minimum acceptable average branch coverage rate needed by each package.
	 * This should be an integer value between 0 and 100.
	 */
	private Integer coverageCheckPackageBranchRate

	/**
	 * The minimum acceptable average line coverage rate needed by each package.
	 * This should be an integer value between 0 and 100.
	 */
	private Integer coverageCheckPackageLineRate

	/**
	 * The minimum acceptable average branch coverage rate needed by the project
	 * as a whole. This should be an integer value between 0 and 100.
	 */
	private Integer coverageCheckTotalBranchRate

	/**
	 * The minimum acceptable average line coverage rate needed by the project as
	 * a whole. This should be an integer value between 0 and 100.
	 */
	private Integer coverageCheckTotalLineRate

	/**
	 * For finer grained control, you can optionally specify minimum branch and
	 * line coverage rates for individual classes using any number of regular
	 * expressions. Each expression is a map with 3 keys:
	 * <ul>
	 * <li><b>regex</b> - a regular expression identifying classes classes that
	 * need special rates</li>
	 * <li><b>branchRate</b> - the branch rate for the selected classes</li>
	 * <li><b>lineRate</b> - the line rate for the selected classes</b>
	 * </ul>
	 * The branch and line rates need to be numbers from 0 to 100.
	 * Example:
	 * <pre>
      coverageCheckRegexes = [
	      [ regex: 'com.example.reallyimportant.*', branchRate: 80, lineRate: 90 ],
	      [ regex: 'com.example.boringcode.*', branchRate: 40, lineRate: 30 ]
	    ]
   * </pre><p>
	 */
	private List<Map> coverageCheckRegexes = []

	/**
	 * Should the build fail if the minimum coverage rates are not met?
	 */
	boolean coverageCheckHaltOnFailure = false

	// ------------------------------------------------------------------------
	// Properties used for merging reports
	/**
	 * A list of data files to merge into a single data file to produce a merged
	 * report.  If set, each of the datafiles in this list will be merged into
	 * a the single datafile, specified by {@link #coverageReportDatafile},
	 * before generating a coverage report.
	 */
	List<File> coverageMergeDatafiles

	/**
	 * Path to the data file to use when generating reports tests. Most users
	 * won't need to change this property.  Defaults to
	 * ${project.buildDir.path}/cobertura/cobertura.ser. The only time this should
	 * be changed is when users are merging datafiles and
	 * {@link #coverageMergeDatafiles} contains the default datafile.
	 */
	File coverageReportDatafile

	/**
	 * Constructor for the extension.  It needs a project handle to set the
	 * coverageDirs sensible defaults.
	 * @param project the Gradle project that owns the extension.
	 */
	CoberturaExtension(Project project) {
		project.logger.info "Creating cobertura extension for project ${project.name}"
		coverageDirs = [project.sourceSets.main.output.classesDir.path]
		coverageInputDatafile = new File("${project.buildDir.path}/cobertura", 'coberturaInput.ser')
		coverageOutputDatafile = new File("${project.buildDir.path}/cobertura", 'cobertura.ser')
		coverageReportDatafile = new File("${project.buildDir.path}/cobertura", 'cobertura.ser')
		coverageReportDir = new File("${project.reporting.baseDir.path}/cobertura")

		// Set the ausxiliaryClasspath to defaults. This is the classpath cobertura uses for
		// resolving classes while instrumenting
		auxiliaryClasspath = project.files project.sourceSets.main.output.classesDir
		auxiliaryClasspath = auxiliaryClasspath.plus(project.sourceSets.main.compileClasspath)

		coverageSourceDirs = project.sourceSets.main.java.srcDirs

		// By default instrumentation depends on the "classes" task
		coverageClassesTasksSpec = {
			project.tasks.matching { it.name == 'classes' }
		}

		// By default the "cobertura" task depends on all test tasks
		coverageTestTasksSpec = {
			project.tasks.withType(Test)
		}
	}

	// Accessors used to check things before they or set, or provide access to
	// private attributes.

	/**
	 * @return whether or not we should ignore trivial methods.
	 */
	boolean getCoverageIgnoreTrivial() {
		return coverageIgnoreTrivial
	}

	/**
	 * Whether or not to ignore trivial methods like simple getters and setters.
	 * Available in in Cobertura 2.0.0 and later.  Attempting to set this
	 * property when using Cobertura 1.x will result in an error.
	 * @param ignoreTrivial whether or not we should ignore trivial methods.
	 */
	void setCoverageIgnoreTrivial(boolean ignoreTrivial) {
		if ( coberturaVersion == null || coberturaVersion.startsWith("1") ) {
			throw new IllegalArgumentException("cobertura-plugin: Setting the coverageIgnoreTrivial property requires cobertura 2.0.0 or later")
		}
		coverageIgnoreTrivial = ignoreTrivial
	}

	/**
	 * @return the names of annotations used to flag methods to be ignored.
	 */
	List<String> getCoverageIgnoreMethodAnnotations() {
		return coverageIgnoreMethodAnnotations
	}

	/**
	 * List of fully qualified annotation names that, if present on a method,
	 * will cause it to be ignored by Cobertura for coverage purposes.
	 * Available in Cobertura 2.0.0 and later.  Attempting to set this property
	 * when using Cobertura 1.x will result in an error.
	 * @param ignoreMethodAnnotations the names of annotations used to flag
	 *        methods to be ignored
	 */
	void setCoverageIgnoreMethodAnnotations(List<String> ignoreMethodAnnotations) {
		if ( coberturaVersion == null || coberturaVersion.startsWith("1") ) {
			throw new IllegalArgumentException("cobertura-plugin: Setting the coverageIgnoreMethodAnnotations property requires cobertura 2.0.0 or later")
		}
		coverageIgnoreMethodAnnotations = ignoreMethodAnnotations
	}

	/**
	 * @return the tasks that produce the classes that need to be instrumented
	 * for coverage reports, as returned by the coverageClassTasks closure
	 */
	TaskCollection getCoverageClassesTasks() {
		coverageClassesTasksSpec()
	}

	/**
	 * Set the closure that will be used to determine what tasks need to be
	 * complete before we can instrument classes for coverage. Used to override
	 * the default closure, which returns the "classes" task.
	 * @param c a no-argument closure that returns a {@code TaskCollection}.
	 */
	void coverageClassesTasks(Closure c) {
		coverageClassesTasksSpec = c
	}

	/**
	 * @return the test tasks that should run, as returned by the
	 * coverageTestTask closure
	 */
	TaskCollection getCoverageTestTasks() {
		coverageTestTasksSpec()
	}

	/**
	 * Set the closure that will be used to determine what tests should be run
	 * when the {@code cobertura} task is invoked.  Used to override the default
	 * closure, which returns all tasks of type {@code Test}
	 * @param c a no-argument closure that returns a {@code TaskCollection}.
	 */
	void coverageTestTasks(Closure c) {
		coverageTestTasksSpec = c
	}
	/**
	 * @return The minimum acceptable branch coverage rate needed by each class.
	 */
	Integer getCoverageCheckBranchRate() {
		return coverageCheckBranchRate
	}

	/**
	 * Set the minimum acceptable branch coverage rate needed by each class. This
	 * needs to be an integer value between 0 and 100.
	 * @param coverageCheckBranchRate the rate to set
	 * @throws IllegalArgumentException if the rate is not between 0 and 100.
	 */
	void setCoverageCheckBranchRate(Integer coverageCheckBranchRate) {
		if ( coverageCheckBranchRate != null ) {
			if ( coverageCheckBranchRate < 0 || coverageCheckBranchRate > 100 ) {
				throw new IllegalArgumentException("coverageCheckBranchRate must be between 0 and 100")
			}
		}
		this.coverageCheckBranchRate = coverageCheckBranchRate
	}

	/**
	 * @return The minimum acceptable line coverage rate needed by each class.
	 */
	Integer getCoverageCheckLineRate() {
		return coverageCheckLineRate
	}

	/**
	 * Set the minimum acceptable line coverage rate needed by each class. This
	 * needs to be an integer value between 0 and 100.
	 * @param coverageCheckLineRate the rate to set
	 * @throws IllegalArgumentException if the rate is not between 0 and 100.
	 */
	void setCoverageCheckLineRate(Integer coverageCheckLineRate) {
		if ( coverageCheckLineRate != null ) {
			if ( coverageCheckLineRate < 0 || coverageCheckLineRate > 100 ) {
				throw new IllegalArgumentException("coverageCheckLineRate must be between 0 and 100")
			}
		}
		this.coverageCheckLineRate = coverageCheckLineRate
	}

	/**
	 * @return The minimum acceptable package branch coverage rate needed by each
	 * class.
	 */
	Integer getCoverageCheckPackageBranchRate() {
		return coverageCheckPackageBranchRate
	}

	/**
	 * Set the minimum acceptable package branch coverage rate needed by each
	 * class. This needs to be an integer value between 0 and 100.
	 * @param coverageCheckPackageBranchRate the rate to set
	 * @throws IllegalArgumentException if the rate is not between 0 and 100.
	 */
	void setCoverageCheckPackageBranchRate(Integer coverageCheckPackageBranchRate) {
		if ( coverageCheckPackageBranchRate != null ) {
			if ( coverageCheckPackageBranchRate < 0 || coverageCheckPackageBranchRate > 100 ) {
				throw new IllegalArgumentException("coverageCheckPackageBranchRate must be between 0 and 100")
			}
		}
		this.coverageCheckPackageBranchRate = coverageCheckPackageBranchRate
	}

	/**
	 * @return The minimum acceptable package line coverage rate needed by each
	 * class.
	 */
	Integer getCoverageCheckPackageLineRate() {
		return coverageCheckPackageLineRate
	}

	/**
	 * Set the minimum acceptable package line coverage rate needed by each class.
	 * This needs to be an integer value between 0 and 100.
	 * @param coverageCheckPackageLineRate the rate to set
	 * @throws IllegalArgumentException if the rate is not between 0 and 100.
	 */
	void setCoverageCheckPackageLineRate(Integer coverageCheckPackageLineRate) {
		if ( coverageCheckPackageLineRate != null ) {
			if ( coverageCheckPackageLineRate < 0 || coverageCheckPackageLineRate > 100 ) {
				throw new IllegalArgumentException("coverageCheckPackageLineRate must be between 0 and 100")
			}
		}
		this.coverageCheckPackageLineRate = coverageCheckPackageLineRate
	}

	/**
	 * @return The minimum acceptable total branch coverage rate needed by each class.
	 */
	Integer getCoverageCheckTotalBranchRate() {
		return coverageCheckTotalBranchRate
	}

	/**
	 * Set the minimum acceptable total branch coverage rate needed by each class.
	 * This needs to be an integer value between 0 and 100.
	 * @param coverageCheckTotalBranchRate the rate to set
	 * @throws IllegalArgumentException if the rate is not between 0 and 100.
	 */
	void setCoverageCheckTotalBranchRate(Integer coverageCheckTotalBranchRate) {
		if ( coverageCheckTotalBranchRate != null ) {
			if ( coverageCheckTotalBranchRate < 0 || coverageCheckTotalBranchRate > 100 ) {
				throw new IllegalArgumentException("coverageCheckTotalBranchRate must be between 0 and 100")
			}
		}
		this.coverageCheckTotalBranchRate = coverageCheckTotalBranchRate
	}

	/**
	 * @return The minimum acceptable total line coverage rate needed by each class.
	 */
	Integer getCoverageCheckTotalLineRate() {
		return coverageCheckTotalLineRate
	}

	/**
	 * Set the minimum acceptable total line coverage rate needed by each class.
	 * This needs to be an integer value between 0 and 100.
	 * @param coverageCheckTotalLineRate the rate to set
	 * @throws IllegalArgumentException if the rate is not between 0 and 100.
	 */
	void setCoverageCheckTotalLineRate(Integer coverageCheckTotalLineRate) {
		if ( coverageCheckTotalLineRate != null ) {
			if ( coverageCheckTotalLineRate < 0 || coverageCheckTotalLineRate > 100 ) {
				throw new IllegalArgumentException("coverageCheckTotalLineRate must be between 0 and 100")
			}
		}
		this.coverageCheckTotalLineRate = coverageCheckTotalLineRate
	}

	List<Map> getCoverageCheckRegexes() {
		return coverageCheckRegexes
	}

	/**
	 * For finer grained control, you can optionally specify minimum branch and
	 * line coverage rates for individual classes using any number of regular
	 * expressions. Each expression is a map with 3 keys:
	 * <ul>
	 * <li><b>regex</b> - a regular expression identifying classes classes that
	 * need special rates</li>
	 * <li><b>branchRate</b> - the branch rate for the selected classes</li>
	 * <li><b>lineRate</b> - the line rate for the selected classes</b>
	 * </ul>
	 * The branch and line rates need to be numbers from 0 to 100.
	 * Example:
	 * <pre>
	   coverageCheckRegexes = [
	     [ regex: 'com.example.reallyimportant.*', branchRate: 80, lineRate: 90 ],
	     [ regex: 'com.example.boringcode.*', branchRate: 40, lineRate: 30 ]
	   ]
	 * </pre>
	 * @param coverageCheckRegexes the expressions to use.
	 * @throws IllegalArgumentException if any of the regular expressions or
	 * coverage rates are missing, or if the rates are outside the valid range.
	 */
	void setCoverageCheckRegexes(List<Map> coverageCheckRegexes) {
		if ( coverageCheckRegexes != null && coverageCheckRegexes.size() > 0 ) {
			for ( Map map : coverageCheckRegexes ) {
				if ( map.regex == null || map.regex.length() < 1 ) {
					throw new IllegalArgumentException("One of the coverageCheckRexexes is missing a regex")
				}
				if ( map.branchRate == null ) {
					throw new IllegalArgumentException("One of the coverageCheckRexexes is missing a branchRate")
				}
				if ( map.lineRate == null ) {
					throw new IllegalArgumentException("One of the coverageCheckRexexes is missing a lineRate")
				}
				if ( map.branchRate < 0 || map.branchRate > 100 ) {
					throw new IllegalArgumentException("${map.branchRate} is an invalid branch rate.  It must be between 0 and 100")
				}
				if ( map.lineRate < 0 || map.lineRate > 100 ) {
					throw new IllegalArgumentException("${map.lineRate} is an invalid line rate.  It must be between 0 and 100")
				}
			}
		}
		this.coverageCheckRegexes = coverageCheckRegexes
	}
}
