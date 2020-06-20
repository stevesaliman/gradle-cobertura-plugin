package net.saliman.gradle.plugin.cobertura
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.reporting.Reporting
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.reflect.Instantiator
import org.gradle.internal.Factory
import org.gradle.util.SingleMessageLogger

import javax.inject.Inject

/**
 * Gradle task that does the actual work of generating the Cobertura coverage
 * reports.
 * <p>
 * The plugin will add this task as finalizer of test tasks, but it will only
 * be enabled if the user intends to generate a report via the
 * {@code cobertura} or {@code coberturaReport) tasks.
 * <p>
 * This task does not declare inputs or outputs because we want it to run every
 * time (when it is enabled).  This is because while the code and tests may not
 * have changed, the actual tests run from build to build may have.
 */
class GenerateReportTask extends DefaultTask implements Reporting<CoberturaReports> {
	static final String NAME = 'generateCoberturaReport'
	@Internal
	CoberturaExtension configuration
	@Internal
	CoberturaRunner runner
	@Classpath
	Configuration classpath
	@Nested
	private final CoberturaReportsImpl reports

	@Inject
	GenerateReportTask(Instantiator instantiator) {
		reports = createReports(instantiator, this)
		// Never consider this up to date.  We might be executing different tests
		// from run to run.
		outputs.upToDateWhen { false }
	}

	private CoberturaReportsImpl createReports(Instantiator instantiator, Task task) {
		return SingleMessageLogger.whileDisabled(new Factory<CoberturaReportsImpl>() {
			@Override
			CoberturaReportsImpl create() {
				return instantiator.newInstance(CoberturaReportsImpl, task)
			}
		})
	}

	@Internal
	@Override
	CoberturaReports getReports() {
		reports
	}

	@Override
	CoberturaReports reports(Closure closure) {
		// return reports(new ClosureBackedAction<CoberturaReports>(closure))
		reports.configure(closure)
	}

	// The reports method uses "super" instead of "extends", but changing it
	// here breaks the build for reasons I'm still trying to figure out.
	// Until then, we need this method, but we can't use the Override
	// annotation.
//	@Override
	CoberturaReports reports(Action<? extends CoberturaReports> configureAction) {
	  configureAction.execute(reports)
		return reports
	}

	/**
	 * If the user changes the file encoding, we need to re-generate the
	 * report.
	 */
	@Input
	def getCoverageEncoding() {
		configuration.coverageEncoding
	}

	/**
	 * Call Cobertura to generate the coverage reports.  The last argument to
	 * Cobertura is a list of source directories.  Users can manually configure
	 * this list via the coverageSourceDirs extension property, but if they don't,
	 * the default is applied here.  The default is the value of
	 * sourceSets.main.java.srcDirs, plus sourceSets.main.groovy.srcDirs and
	 * sourceSets.main.scala.srcDirs, if the groovy or scala plugins have been
	 * applied.  Normally, we'd set this default at apply time, but in this case
	 * we can't because we won't know where the user wants source code until
	 * configuration time.
	 */
	@TaskAction
	def generateReports() {
		// If the user specified merge files, than do a merge before generating
		// reports.
		if ( configuration.coverageMergeDatafiles != null &&
		configuration.coverageMergeDatafiles.size() > 0 ) {
			project.logger.info("${path} - Merging datafiles...")
			runner.withClasspath(classpath.files).mergeCoverageReports(configuration)
		}

		Set<File> sourceDirs = configuration.coverageSourceDirs
		if (sourceDirs == null) {
			if (CoberturaPlugin.isAndroidProject(project)) {
				sourceDirs = project.android.sourceSets.main.java.srcDirs
			} else {
				sourceDirs = project.sourceSets.main.java.srcDirs
				if (project.sourceSets.main.hasProperty('groovy')) {
					sourceDirs += project.sourceSets.main.groovy.srcDirs
				}
				if (project.sourceSets.main.hasProperty('scala')) {
					sourceDirs += project.sourceSets.main.scala.srcDirs
				}
			}
		}
		project.logger.info("${path} - Generating reports...")
		// Generate a report for each provided format
		for (format in configuration.coverageFormats) {
			runner.withClasspath(classpath.files).generateCoverageReport(
							configuration,
							format,
							project.files(sourceDirs).files.collect { it.path })
		}
	}
}
