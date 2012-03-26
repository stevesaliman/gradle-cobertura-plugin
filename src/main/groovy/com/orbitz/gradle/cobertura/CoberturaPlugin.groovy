package com.orbitz.gradle.cobertura

import java.lang.reflect.Constructor;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.internal.AsmBackedClassGenerator;
import org.gradle.api.tasks.testing.Test;

import com.orbitz.gradle.cobertura.tasks.GenerateCoverageReportTask;
import com.orbitz.gradle.cobertura.tasks.InstrumentCodeAction;

/**
 * Provides Cobertura coverage for Test tasks.
 * 
 * Copyright 2011 Orbitz, LLC
 */
class CoberturaPlugin implements Plugin<Project> {
    Project project
    
    def void apply(Project project) {
        this.project = project
        project.apply plugin: 'java'
        project.extensions.coberturaRunner = new CoberturaRunner()
        
        project.convention.plugins.cobertura = new CoberturaConvention(project);
        if (!project.configurations.asMap['cobertura']) {
            project.configurations.add('cobertura') {
                extendsFrom project.configurations['testCompile']
            }
            project.dependencies {
                cobertura "net.sourceforge.cobertura:cobertura:${project.coberturaVersion}"
            }
        }

        project.tasks.withType(GenerateCoverageReportTask).whenTaskAdded {
            configureCoverageReportTask it
        }
        GenerateCoverageReportTask coverageReport = project.tasks.add(name: 'cobertura', dependsOn: [ 'coberturaXml' ], type: GenerateCoverageReportTask)
        coverageReport.description = "Generate Cobertura code coverage report"
        coverageReport.group = "Report"
        GenerateCoverageReportTask xmlCoverageReport = project.tasks.add(name: 'coberturaXml', dependsOn: [ 'cleanTest', 'test' ], type: GenerateCoverageReportTask) {
            format = 'xml'
        }
        configureTestTask()
        project.dependencies.add('testRuntime', "net.sourceforge.cobertura:cobertura:${project.coberturaVersion}")
    }
    
    private def configureTestTask() {
        AsmBackedClassGenerator generator = new AsmBackedClassGenerator()
        Class<? extends InstrumentCodeAction> instrumentClass = generator.generate(InstrumentCodeAction)
        Constructor<InstrumentCodeAction> constructor = instrumentClass.getConstructor(Project.class)
        
        InstrumentCodeAction instrument = constructor.newInstance(project)
        instrument.runner = project.coberturaRunner
        instrument.conventionMapping.map('datafile') {
            project.coverageDatafile
        }
        instrument.conventionMapping.map('classesDirs') {
            project.files(project.coverageDirs).files
        }
        instrument.conventionMapping.map('includes') {
            project.coverageIncludes as Set
        }
        instrument.conventionMapping.map('excludes') {
            project.coverageExcludes as Set
        }
        project.gradle.taskGraph.whenReady {
            if (project.gradle.taskGraph.allTasks.find { it instanceof GenerateCoverageReportTask } != null) {
                project.tasks.withType(Test).each { Test test ->
                    test.systemProperties.put('net.sourceforge.cobertura.datafile', project.coverageDatafile)
                    test.classpath += project.configurations['cobertura']
                    test.doFirst instrument
                }
            }
        }
    }
    
    private def configureCoverageReportTask(GenerateCoverageReportTask coverageReport) {
        coverageReport.runner = project.coberturaRunner
        coverageReport.conventionMapping.map('datafile') {
            project.coverageDatafile
        }
        coverageReport.conventionMapping.map('srcDirs') {
            project.files(project.coverageSourceDirs).files
        }
        coverageReport.conventionMapping.map('format') {
            project.coverageFormat
        }
        coverageReport.conventionMapping.map('reportdir') {
            project.coverageReportDir
        }
    }

}