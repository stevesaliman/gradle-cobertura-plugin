package com.orbitz.gradle.cobertura.tasks


import org.gradle.api.internal.ConventionTask;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Optional 
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

class GenerateCoverageReportTask extends ConventionTask {
    
    String format
    @InputFile
    @Optional
    File datafile
    @OutputDirectory
    File reportdir
    Set<File> srcDirs
    def runner
    
    @TaskAction
    def generateCoverageReport() {
        runner.generateCoverageReport getDatafile().path, getReportdir().path, getFormat(), getSrcDirs().collect { it.path }
    }
}
