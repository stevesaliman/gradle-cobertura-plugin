package com.orbitz.gradle.cobertura

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * Gradle task that instruments java sources for Cobertura coverage reports.
 * <p>
 * We only want to instrument classes if the user has elected to run the
 * "cobertura" task created by the plugin.  We do this by looking for the
 * presence of a runner when we try to instrument.
 */
class InstrumentTask extends DefaultTask {
	File destinationDir
	CoberturaExtension configuration
	def runner

//	@InputFiles
	def getClassesDirs() {
		configuration.coverageDirs
	}

//	@OutputFile
	def getDatafile () {
		configuration.coverageDatafile
	}

	@TaskAction
	def instrument() {
		// Only do the instrumentation if the plugin gave us a runner when the
		// task graph was ready.  That is how we'll detect that the user wanted to
		// generate coverage reports.
		if ( runner != null ) {
			println "Instrumenting..."
			// Before we instrument, copy from the main source to the instrumented path.
			// Doing this here means we only need to do it when we need to instrument.
			def instrumentDirs = [] as Set
			project.files(project.sourceSets.main.output.classesDir.path).each { File f ->
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

			runner.instrument null, getDatafile().path, getDestinationDir()?.path,
							configuration.coverageIgnores as List,
							configuration.coverageIncludes as List,
							configuration.coverageExcludes as List, instrumentDirs as List
		} else {
			println "Skipping instrumentation..."
		}
	}
}
