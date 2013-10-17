package net.saliman.gradle.plugin.cobertura

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class CheckTask extends DefaultTask {
    static final String NAME = 'coberturaCheck'
    CoberturaExtension configuration
    def runner

    @TaskAction
    def check() {
        def log = project.logger
        log.info('Checking Code Coverage Thresholds...')

        def branchCoverageRate = configuration.branchCoverage
        def lineCoverageRate = configuration.lineCoverage
        def packageBranchCoverageRate = configuration.packageBranchCoverage
        def packageLineCoverageRate = configuration.packageLineCoverage
        def totalBranchCoverageRate = configuration.totalBranchCoverage
        def totalLineCoverageRate = configuration.totalLineCoverage
        def datafile = configuration.coverageOutputDatafile.path
        def regex = configuration.minCoverageRegex

        def args = []
        if (isNotNullOrEmpty(branchCoverageRate)) {
            args << '--branch' <<  branchCoverageRate
        }
        if (isNotNullOrEmpty(datafile)) {
            args << '--datafile' << datafile
        }
        if(isNotNullOrEmpty(lineCoverageRate)) {
            args << '--line' << lineCoverageRate
        }
        if(isNotNullOrEmpty(regex)) {
            args << "--regex" << regex
        }
        if(isNotNullOrEmpty(packageBranchCoverageRate)) {
            args << "--packagebranch" << packageBranchCoverageRate
        }
        if(isNotNullOrEmpty(packageLineCoverageRate)) {
            args << "--packageline" << packageLineCoverageRate
        }
        if(isNotNullOrEmpty(totalBranchCoverageRate)) {
            args << "--totalbranch" << totalBranchCoverageRate
        }
        if(isNotNullOrEmpty(totalLineCoverageRate)) {
            args << "--totalline" << totalLineCoverageRate
        }

        log.info("checkCobertura Task Args = $args")
        def classpath = project.configurations.testRuntime.asPath
        runner.check(project, classpath, args)
    }

    private boolean isNotNullOrEmpty(String data) {
        return !(data == null || data.isEmpty());
    }
}