package net.saliman.gradle.plugin.cobertura

import org.apache.commons.io.FileUtils
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

import java.nio.file.Files

import static groovy.io.FileType.FILES

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
	 * If the user changes the Cobertura version, we need to re-instrument.
	 */
	@Input
	def getCoberturaVersion() {
		configuration.coberturaVersion
	}

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
			if ( f.isDirectory() ) {
				// Copy directories from main source to instrumented path
				project.copy {
					from f
					into outputClassesDir
				}
			} else {
				// add files to the instrumented dir list.
				instrumentDirs << f.path
			}
		}
		// add the instrumented dir to the list.
		instrumentDirs << outputClassesDir.path

		runner.withClasspath(classpath.files).instrument(configuration,
				null,
				getDestinationDir()?.path,
				instrumentDirs as List)

		// Delete any classes from the instrumented classes that are no
		// different from the original class file.
		project.files(classesDirs).each { File f ->
			deleteSameFiles(f, outputClassesDir)
		}
	}

	/**
	 * Helper method to find the files that are the same in the given
	 * directories, after instrumentation is done.
	 * @param inputDir a directory containing uninstrumented classes.
	 * @param outputDir a directory containing instrumented classes.
	 */
	def deleteSameFiles(inputDir, outputDir) {
		def outputPath = outputDir.toPath()
		def inputFiles = findFiles(inputDir)
		def identicalFiles = findFiles(outputDir, { f ->
			// When we decide to drop Gradle 4 support, we can use the easier
			// def relativePath = outputDir.relativePath(f)
			def relativePath = outputPath.relativize(f.toPath()).toString()
			inputFiles.contains(relativePath) && isSameFile(inputDir, outputDir, relativePath)
		})

		identicalFiles.each { f ->
			Files.deleteIfExists(outputDir.toPath().resolve(f))
		}
	}

	/**
	 * Helper method that finds all the files in a directory that meet a given
	 * condition, or all files in the directory if no condition is given.
	 * @param dir the directory to search.
	 * @param condition the optional condition to check.
	 * @return a collection of files, relative to the directory, that meet the
	 * condition.
	 */
	def findFiles(File dir, condition = null) {

		def files = []

		if (dir.exists()) {
			def dirPath = dir.toPath()
			dir.eachFileRecurse(FILES) { f ->
				if (condition == null || condition(f)) {
					// When we decide to drop Gradle 4 support, we can use the easier
					// def relativePath = dir.relativePath(f)
					def relativePath = dirPath.relativize(f.toPath()).toString()
					files << relativePath
				}
			}
		}

		return files
	}

	/**
	 * Helper method to determine if a given file is the same in 2 directories.
	 * @param dirA The first directory to check
	 * @param dirB The second directory to check
	 * @param relativeFile the file to check, as a relative path.  This file
	 *        is assumed to exist in both directories.
	 * @return whether or not the file is the same in the 2 directories.
	 */
	def isSameFile(dirA, dirB, relativeFile) {
		def fileA = new File(dirA, relativeFile)
		def fileB = new File(dirB, relativeFile)
		return FileUtils.contentEquals(fileA, fileB)
	}

}
