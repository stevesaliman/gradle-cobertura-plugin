package net.saliman.gradle.plugin.cobertura.android

import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.testfixtures.ProjectBuilder

class DummyExtension {

    SourceSetContainer sourceSets
    Project dummy

    DummyExtension() {
        dummy = ProjectBuilder.builder().build()
        dummy.apply plugin: 'java'
        sourceSets = dummy.sourceSets
    }
}
