package net.saliman.gradle.plugin.cobertura

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.testing.Test

import org.gradle.api.execution.TaskExecutionGraph

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
		println "Applying cobertura plugin..."
		project.apply plugin: 'java'
		project.extensions.coberturaRunner = new CoberturaRunner()

		project.extensions.create('cobertura', CoberturaExtension, project)
		if (!project.configurations.asMap['cobertura']) {
			project.configurations.add('cobertura') {
				extendsFrom project.configurations['testCompile']
			}
			project.dependencies {
				cobertura "net.sourceforge.cobertura:cobertura:${project.extensions.cobertura.coberturaVersion}"
			}
		}

		//Add an instrument task, but don't have it do anything yet because we
		// don't know if we need to run cobertura yet. We need to process all new
		// tasks as they are added, so we can't use withType.
		project.tasks.add(name: 'instrument', type: InstrumentTask, {configuration = project.extensions.cobertura})
		project.tasks.add(name: 'cobertura', type:  DefaultTask)
		Task coberturaTask = project.tasks.getByName("cobertura")
		coberturaTask.setDescription("Generate Cobertura coverage reports after running tests.")
		project.tasks.all { task ->
			if ( task instanceof  Test ) {
				println "Changing dependencies for task ${task.name}"
				task.dependsOn 'instrument'
				coberturaTask.dependsOn task
			}
		}

		project.dependencies.add('testRuntime', "net.sourceforge.cobertura:cobertura:${project.extensions.cobertura.coberturaVersion}")
		// If we need to run cobertura, fix tests, set instrument's runner...
		project.gradle.taskGraph.whenReady { graph ->
			if (graph.allTasks.find { it.name == "cobertura" } != null) {
				println "Enhancing test tasks for Cobertura"
				InstrumentTask instrumentTask = graph.allTasks.find { it.name == "instrument" }
				configureInstrumentation(project, graph, instrumentTask)
				// We want to generate a report if we're in the cobertura task, or if
				// we're about to fail a test task.  We need to use afterSuite instead
				// of doLast because doLast won't run if a test fails
				graph.afterTask() { task, state ->
					if ( (task.name == "cobertura" ||
									(task instanceof Test && state.failure != null)) ) {
						project.coberturaRunner.generateCoverageReport project.extensions.cobertura.coverageDatafile.path, project.extensions.cobertura.coverageReportDir.path, project.extensions.cobertura.coverageFormat, project.files(project.extensions.cobertura.coverageSourceDirs).files.collect { it.path }
					}
				}
				// Fix the classpath of any test task we are actually running.
				project.tasks.withType(Test).each { Test test ->
					test.systemProperties.put('net.sourceforge.cobertura.datafile', project.extensions.cobertura.coverageDatafile)
					test.classpath += project.configurations['cobertura']
					fixTestClasspath(project, test)
				}
			}
		}
	}

	/**
	 * Configure a test task.  remove source dirs and add the instrumented dir
	 * @param test the test task to fix
	 */
	def fixTestClasspath(Project project, Task test) {
		def instrumentDirs = [] as Set
		project.files(project.sourceSets.main.output.classesDir.path).each { File f ->
			if (f.isDirectory()) {
				test.classpath = test.classpath - project.files(f)
			}
		}
		test.classpath = test.classpath + project.files("${project.buildDir}/instrumented_classes")
	}

	/**
	 * Configure the instrumentation task to actually instrument code.  When the
	 * instrumentation task is first created, it doesn't know if the user is
	 * going to be running coverage reports or not.  This helper method will be
	 * called once the plugin determines that instrumentation is actually needed.
	 * @param project the project we're dealing with.
	 * @param graph the task graph being executed.
	 * @param instrumentTask the instrumentation task to configure.
	 */
	def configureInstrumentation(Project project, TaskExecutionGraph graph, InstrumentTask instrumentTask) {
		// We tell the instrumentation task to do its thing by setting a runner,
		// but before we do, make sure the user hasn't done a "-x instrument" for
		// some bizarre reason.
		if (graph.allTasks.find { it.name == "instrument" } != null) {
			instrumentTask.runner = project.coberturaRunner
		}
	}
}