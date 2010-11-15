package org.gradle.api.tasks.cobertura

import org.gradle.api.internal.ConventionMapping;

import org.gradle.api.internal.ConventionAwareHelper;
import org.gradle.api.internal.ConventionMapping;

import org.gradle.api.Action
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.internal.IConventionAware;

class InstrumentCodeAction implements Action<Task>, IConventionAware {
    
    File datafile
    File destinationDir
    Set<File> classesDirs
    Set<String> includes
    Set<String> excludes
    def runner
    private ConventionMapping conventionMapping
    
    def InstrumentCodeAction(Project project) {
        conventionMapping = new ConventionAwareHelper(this, project.getConvention())
    }
    
    void execute(Task task) {
        def instrumentDirs = [] as Set
        getClassesDirs().each { File f ->
            if (f.isDirectory()) {
                task.classpath = task.classpath - task.project.files(f) + task.project.files("${task.project.buildDir}/instrumented_classes")
                task.project.copy {
                    into "${task.project.buildDir}/instrumented_classes"
                    from f
                }
                instrumentDirs << ("${task.project.buildDir}/instrumented_classes" as String)
            } else {
                instrumentDirs << f.path
            }
        }
        runner.instrument null, getDatafile().path, getDestinationDir()?.path, null, getIncludes() as List,
                getExcludes() as List, instrumentDirs as List
    }
    
    void setConventionMapping(ConventionMapping mapping) {
        this.conventionMapping = mapping
    }
    
    ConventionMapping getConventionMapping() {
        return conventionMapping
    }
    
    
}
