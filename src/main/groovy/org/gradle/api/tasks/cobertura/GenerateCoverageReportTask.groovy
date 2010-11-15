package org.gradle.api.tasks.cobertura

import org.gradle.api.internal.ConventionTask;
import org.gradle.api.tasks.TaskAction;

class GenerateCoverageReportTask extends ConventionTask {
    
    String format
    File datafile
    String reportdir
    Set<File> srcDirs
    def runner
    
    @TaskAction
    def generateCoverageReport() {
        runner.generateCoverageReport getDatafile().path, getReportdir(), getFormat(), getSrcDirs().collect { it.path }
    }
}
