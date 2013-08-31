package net.saliman.gradle.plugin.cobertura

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.UnknownConfigurationException
import org.gradle.api.tasks.testing.Test

import org.gradle.api.invocation.Gradle

/**
 * Provides Cobertura coverage for Test tasks.
 *
 * This plugin will create 2 tasks.
 *
 * The first is a "cobertura" task that users may call to generate coverage
 * reports.  The plugin will make the cobertura task depend on all test tasks,
 * but it won't actually do any work because the cobertura task won't run if
 * there are any test failures.
 *
 * The second task is an "instrument" task, that will instrument the Java source
 * code.  Users won't call it directly, but the plugin will make all test
 * tasks depend on it so that instrumentation only happens once, and only if
 * the task graph has the "cobertura task"
 *
 * This plugin also defines a "cobertura" extension with properties that are
 * used to configure the operation of the plugin and its tasks.
 *
 * The plugin runs cobertura coverage reports for sourceSets.main.  A project
 * might have have multiple artifacts that use different parts of the code, and
 * there may be different test tasks that test different parts of the source,
 * but there is almost always only one main source set.
 *
 * Most of the magic of this plugin happens not at apply time, but when the
 * task graph is ready.  If the graph contains the "cobertura" task, it will
 * make sure the instrument task is configured to do actual work, and it will
 * enhance each test task so that it runs the cobertura report before it fails
 * the build.
 *
 */
class CoberturaPlugin implements Plugin<Project> {

	def void apply(Project project) {

		project.logger.info("Applying cobertura plugin to $project")
		project.apply plugin: 'java'
		project.extensions.coberturaRunner = new CoberturaRunner()

		project.extensions.create('cobertura', CoberturaExtension, project)
		if (!project.configurations.asMap['cobertura']) {
			project.configurations.create('cobertura') {
				extendsFrom project.configurations['testCompile']
			}
			project.dependencies {
				cobertura "net.sourceforge.cobertura:cobertura:${project.extensions.cobertura.coberturaVersion}"
			}
		}

		// Add an instrument task, but don't have it do anything yet because we
		// don't know if we need to run cobertura yet. We need to process all new
		// tasks as they are added, so we can't use withType.
		project.tasks.create(name: 'instrument', type: InstrumentTask, {configuration = project.extensions.cobertura})
		InstrumentTask instrumentTask = project.tasks.getByName("instrument")
		instrumentTask.setDescription("Instrument code for Cobertura coverage reports")
		instrumentTask.runner = project.extensions.coberturaRunner
		// instrumentation needs to depend on compiling.
		Task compileTask = project.tasks.getByName("compileJava")
		instrumentTask.dependsOn compileTask

		// Create the report task that does the actual work of generating the
		// reports.
		project.tasks.create(name: 'coberturaReport', type: ReportTask, {configuration = project.extensions.cobertura})
		ReportTask reportTask = project.tasks.getByName("coberturaReport")
		reportTask.setDescription("Helper task that does the actual Cobertura report generation")
		reportTask.runner = project.extensions.coberturaRunner

		// Create the cobertura task itself.  This is the task users will call.
		project.tasks.create(name: 'cobertura', type:  DefaultTask)
		Task coberturaTask = project.tasks.getByName("cobertura")
		coberturaTask.setDescription("Generate Cobertura coverage reports after running tests.")

		// The cobertura task needs to depend on all of the test tasks, and test
		// tasks need to be finalized by the report task.
		project.tasks.all { task ->
			if ( task instanceof  Test ) {
				project.logger.info("Changing dependencies for task ${task.project}:${task.name}")
				task.dependsOn 'instrument'
				coberturaTask.dependsOn task
				task.finalizedBy reportTask
			}
		}

		project.gradle.rootProject.rootProject.getTasksByName('test', true).each { t ->
			if ( t != null ) {
				coberturaTask.dependsOn t
			}
		}

		project.dependencies.add('testRuntime', "net.sourceforge.cobertura:cobertura:${project.extensions.cobertura.coberturaVersion}")

		registerTaskFixupListener(project.gradle)

	}

	/**
	 * Register a listener with Gradle.  When gradle is ready to run tasks, it
	 * will call our listener.  If the coberrura task is in our graph, the
	 * listener will fix the classpaths of all the test tasks that we are actually
	 * running.
	 *
	 * @param gradle the gradle instance running the plugin.
	 */
	private void registerTaskFixupListener(Gradle gradle) {
		// If we need to run cobertura, fix test classpaths, and set them to
		// generate reports on failure.  If not, disable instrumentation.
		// "whenReady()" is a global event, so closure should be registered exactly
		// once for single and multi-project builds.
		if ( !gradle.ext.has('coberturaPluginListenerRegistered') ) {
			gradle.ext.coberturaPluginListenerRegistered = true
			gradle.taskGraph.whenReady { graph ->
				if (graph.allTasks.find { it.name == "cobertura" } != null) {
					// We're running cobertura, so fix the classpath of any test task we
					// are actually running.
					graph.allTasks.findAll { it instanceof Test}.each { Test test ->
						try {
							Configuration config = test.project.configurations['cobertura']
							test.systemProperties.put('net.sourceforge.cobertura.datafile', test.project.extensions.cobertura.coverageDatafile)
							test.classpath += config
							fixTestClasspath(test)
						} catch (UnknownConfigurationException e) {
							// Eat this. It just means we have a multi-project build, and
							// there is test in a project that doesn't have cobertura applied.
						}
					}
				} else {
					// We're not running cobertura, so disable all instrument and report
					// tasks.
					graph.allTasks.findAll { it instanceof InstrumentTask}.each {
						it.enabled = false
					}
					graph.allTasks.findAll { it instanceof ReportTask}.each {
						it.enabled = false
					}
				}
			}
		}
	}

	/**
	 * Configure a test task.  remove source dirs and add the instrumented dir
	 * @param test the test task to fix
	 */
	def fixTestClasspath(Task test) {
		def project = test.project
		project.files(project.sourceSets.main.output.classesDir.path).each { File f ->
			if (f.isDirectory()) {
				test.classpath = test.classpath - project.files(f)
			}
		}
		test.classpath = project.files("${project.buildDir}/instrumented_classes") + test.classpath
	}
}
