package net.saliman.gradle.plugin.cobertura

import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * Gradle task that instruments java sources for Cobertura coverage reports.
 * <p>
 * The plugin will always add this task as a dependency of the
 * copyCoberturaDatafiletask, but we only want to instrument classes if the user
 * has elected to generate Cobertura reports for the build.  The plugin will
 * register a listener to figure out if this task should be enabled or not.
 * <p>
 * If this task is enabled, we only need to run it if the source code changed,
 * or if the user changed any of the options that effect what should be included
 * in the report, such as the coverageIgnoreTrivial option..
 */
class InstrumentTask extends DefaultTask {
	static final String NAME = 'instrument'
	File destinationDir
	CoberturaExtension configuration
	CoberturaRunner runner
	Configuration classpath

	/**
	 * If the included classes change, we need to re-instrument
	 */
	@Input
	def getIncludes() {
		configuration.coverageIncludes
	}

	/**
	 * If the excluded classes change, we need to re-instrument
	 */
	@Input
	def getExcludes() {
		configuration.coverageExcludes
	}

	/**
	 * If the user changes the things to ignore, we need to re-instrument.
	 */
	@Input
	def getIgnores() {
		configuration.coverageIgnores
	}

	/**
	 * If the user changes whether or not to ignore trivial, we'll need to
	 * re-instrument
	 */
	@Input
	def getIgnoreTrivial() {
		configuration.coverageIgnoreTrivial
	}

	/**
	 * If the user changes the annotation used to ignore methods, we'll need to
	 * re-instrument.
	 */
	@Input
	def getIgnoreMethodAnnotations() {
		configuration.coverageIgnoreMethodAnnotations
	}

	/**
	 * If the compiled class files from our main source changes, we'll need to
	 * re-instrument.
	 */
	@InputFiles
	def getClassesDirs() {
		configuration.coverageDirs
	}

	/**
	 * If the auxiliary classpath changes, we'll need to
	 * re-instrument.
	 */
	@InputFiles
	def getAuxiliaryClasspath() {
		configuration.auxiliaryClasspath
	}

	/**
	 * The output file from this task is named inputDatafile because it is the
	 * input input for the task that copies the ser file, and is input for the
	 * tests.
	 */
	@OutputFile
	def getInputDatafile() {
		configuration.coverageInputDatafile
	}

	/**
	 * If the instrumented class files change (or go missing), we need to
	 * re-instrument
	 */
	@OutputDirectory
	def getOutputClassesDir() {
		return new File("${project.buildDir}/instrumented_classes")
	}

	@TaskAction
	def instrument() {
		project.logger.info("${path} - Instrumenting code...")
		// When Cobertura instruments code, it appears to use some of what is
		// already in the .ser file, if it exists, so the first thing we need to
		// do is get rid of the old .ser file.  Otherwise, changing ignoreTrivial
		// from false to true will have no effect on reports.
		configuration.coverageInputDatafile.delete()
		// Before we instrument, copy from the main source to the instrumented path.
		// Doing this here means we only need to do it when we need to instrument.
		def instrumentDirs = [] as Set
		project.files(classesDirs).each { File f ->
			if (f.isDirectory()) {
				// Copy directories from main source to instrumented path
				project.copy {
					from f
					into "${project.buildDir}/instrumented_classes"
				}
			} else {
				// add files to the instrumented dir list.
				instrumentDirs << f.path
			}
		}
		// add the instrumented dir to the list.
		instrumentDirs << ("${project.buildDir}/instrumented_classes" as String)

		runner.withClasspath(classpath.files).instrument null, configuration.coverageInputDatafile.path, getDestinationDir()?.path,
						configuration.coverageIgnores as List,
						configuration.coverageIncludes as List,
						configuration.coverageExcludes as List,
						configuration.coverageIgnoreTrivial as boolean,
						configuration.coverageIgnoreMethodAnnotations as List,
						configuration.auxiliaryClasspath.getAsPath(),
						instrumentDirs as List
	}
}
