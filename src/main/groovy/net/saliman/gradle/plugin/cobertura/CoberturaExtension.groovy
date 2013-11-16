package net.saliman.gradle.plugin.cobertura

import org.gradle.api.Project
import org.gradle.api.plugins.GroovyBasePlugin
import org.gradle.api.plugins.scala.ScalaBasePlugin
import org.gradle.api.tasks.TaskCollection
import org.gradle.api.tasks.testing.Test;

/**
 * Extension class for configuring the Cobertura plugin.  Most of the properties
 * in this extension match options in Cobertura itself.
 */
class CoberturaExtension {

	/**
	 * Version of cobertura to use for the plugin. Defaults to 2.0.3
	 */
	String coberturaVersion = '2.0.3'

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

	CoberturaRunner runner = new CoberturaRunner()

	/**
	 * Constructor for the extension.  It needs a project handle to set the
	 * coverageDirs sensible defaults.
	 * @param project the Gradle project that owns the extension.
	 */
	CoberturaExtension(Project project) {
		project.logger.info "creating extension"
		coverageDirs = [project.sourceSets.main.output.classesDir.path]
		coverageInputDatafile = new File("${project.buildDir.path}/cobertura", 'coberturaInput.ser')
		coverageOutputDatafile = new File("${project.buildDir.path}/cobertura", 'cobertura.ser')
		coverageReportDir = new File("${project.reporting.baseDir.path}/cobertura")
		// The cobertura plugin causes the java plugin to be included.  Also, the
		// groovy and scala plugins extend the java plugin.  This means that the
		// java source directories will always be defined.
		coverageSourceDirs = project.sourceSets.main.java.srcDirs

		// By default instrumentation depends on the "classes" task
		coverageClassesTasksSpec = {
			project.tasks.matching { it.name == 'classes' }
		}

		// By default the "cobertura" task depends on all test tasks
		coverageTestTasksSpec = {
			project.tasks.withType(Test)
		}

		//Using plugins.withType allows the container to be updated whenever the plugin is applied
		// Look for Groovy
		project.plugins.withType(GroovyBasePlugin).whenPluginAdded {
			coverageSourceDirs += project.sourceSets.main.groovy.srcDirs
		}
		// Look for Scala
		project.plugins.withType(ScalaBasePlugin).whenPluginAdded {
			coverageSourceDirs += project.sourceSets.main.scala.srcDirs
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
}
