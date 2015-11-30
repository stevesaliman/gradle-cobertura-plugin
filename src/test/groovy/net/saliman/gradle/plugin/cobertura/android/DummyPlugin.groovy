package net.saliman.gradle.plugin.cobertura.android
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test

class DummyPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        // Setup android extension
        project.extensions.create("android", DummyExtension.class)

        // Setup default debug and flavored compile tasks
        project.task("compileDebugJavaWithJavac")
        project.task("compileFlavor1DebugJavaWithJavac")

        // Setup default debug and flavored test tasks
        project.tasks.create("testDebugUnitTest", Test)
        project.tasks.create("testFlavor1DebugUnitTest", Test)
    }
}
