Changes for 3.0.1
=================
- Fixed an issue in newer versions of gradle caused by the movement of the 
  DeprecationLogger class to another package (Issue #168)
  
Changes for 3.0.0
=================
- Dropped support for Versions of Java older than version 8.

- Dropped support for Cobertura 1.x

- Added support for Gradle 6.x, with thanks to Roberto Perez Alcolea (Issues
  #164 and #165).
  
Changes for 2.6.1
=================
- This is a minor release that tries to make the plugin work better with other
  coverage tools like Scoverage by deleting classes from the
  instrumented_classes directory that didn't actually change. (Issue #160)
  
Changes for 2.6.0
=================
- The plugin now supports Gradle 5.1, with thanks to Roberto Alcolea (@rpalcolea)

Changes for 2.5.4
=================
- Fixed issues introduced by earlier refactorings (Issue #146)
- Fixed a bug that was preventing Cobertura tasks from running on Android 
  projects (Issue #148)

Changes for 2.5.3
=================
- Fixed Android Tools 3 support with thanks to Eduardo Giménez (@edu-gimenez)

Changes for 2.5.2
=================
- Added support for Android Tools 3 and Kotlin with thanks to Christoph Walcher 
  (@wiomoc)
  
Changes for 2.5.1
=================
- Added `NoClassDefFoundErrors` to the exceptions I'm catching in the 
  `ChildFirstUrlClassLoader`, thanks to @boschi's suggestion in Issue #89.

Changes for 2.5.0
=================
Prior to version 4.0, Gradle put the compiled class files from Java, Groovy, and
Scala sources into the same directory (`build/classes/main`),  Starting with 
Gradle 4.0, each language gets its own directory (`build/classes/java/main`, 
`build/classes/groovy/main`, etc.) for compiled classes.  The Cobertura plugin
now adds all of the correct directories based on which version of Gradle you are
running, and which plugins have been applied.

Changes for 2.4.0
=================
- Changed the way auxiliaryClasspath is built.  In prior versions, it was 
  necessary to re-specify the defaults when adding to the auxiliaryClasspath
  (Issue #116 and Issue #124).  Now, the plugin adds the value of 
  auxiliaryClasspath in the ```cobertura``` closure to the defaults, with 
  thanks to @Frank667
- Updated the plugin to build with Gradle 3.2 and bumped the Gradle wrapper.
  
Changes for 2.3.2
=================
- Added the 3 user selectable tasks to the Cobertura group so they show up
  when you do a ```gradle tasks``` (Issue #103)
  
- Added the ability to apply the plugin from ```settings.gradle``` and 
  ```init.gradle``` (Issue #109)
  
Changes for 2.3.1
=================
- Fixed a classpath issue with Android projects, with thanks to Adam Peck
  (@dampcake). (Issue #101)
  
Changes for 2.3.0
=================
- Added support for Android projects, with much thanks to Gautam Korlam 
  (@kageiit) (Issue #90)
  
Changes for 2.2.9
=================
- Fixed unit tests so that they run and pass (Issue #79)

Changes for 2.2.8
=================
- Fixed a bug in ```coberturaCheck``` that was causing all builds to fail when
  the ```coverageCheckHaltOnFailure``` option was set (Issue #74)
  
Changes for 2.2.7
=================
- Fixed a bug that was preventing the ```coberturaCheck``` task from running
  (Issue #70)

- Updated the default version of Cobertura to 2.1.1, which should resolve
  resolve several issues relating to Java 8 and ASM.

- Bumped the Gradle wrapper to version 2.3.

Changes for 2.2.6
=================
- Fixed the issue with the ```coberturaCheck``` task that was causing it to 
  skip instrumentation (Issue #63)
  
- Fixed dry-run behavior (Issue #64)

Changes for 2.2.5
=================
- Added support for Cobertura's ```encoding``` option when generating reports
  (Issue #43)

- Added the cobertura version to the up-to-date checks so that we re-instrument
  if the version changes (Issue #52)

- Fixed the way the plugin tells Cobertura where source directories are.  The
  coverageSourceDirs argument always allowed users to configure this setting, 
  but the default (srcSets.main.java.srcDirs) was set at apply time, so any
  changes made to source sets in build.gradle was being ignored.  The default
  is now handled at execution time (Issue #53)

- The plugin is now available on the Gradle Plugin Portal (Issue #54)

- The plugin can now be applied by 2 names, 'cobertura', and
  'net.saliman.cobertura' (Issue #59)


Changes for 2.2.4
=================
- Added support for customizing the auxiliary classpath, with thanks to Harald
  Schmitt (Issue #24)

Changes for 2.2.3
=================
- Added support for the Gradle dashboard, with thanks to vyazelenko (Issues #45
  and #46)

Changes for 2.2.2
=================
- Fixed the Auxiliary classpath on Windows boxes. (Issue #39)

- Fixed (again) the classpath issue that was leading to ASM conflicts.  The
  plugin now uses its own child-first classloader to make sure we get the
  versions Cobertura needs ahead of the classes used by the application.

Changes for 2.2.1
=================
- Fixed the operation of the coverageClassesTask and coverageTestTask closures.
  (Issue #38)

Changes for 2.2.0
=================
- One of the biggest changes in this release is the behavior of the "cobertura"
  task.  In 2.1.0, it was made to run all the tests in the applying project, as
  well as tests named "test" in other parts of a multi-project build.  This
  produced undesirable coupling between projects, so this has been removed in
  2.2.0.  The "cobertura" task now runs only the tests in project. (issue #31)

- The exact tasks that get run by the "cobertura" task can be configured by
  setting the coverageTestTasks closure.  Ths no-arg closure returns the
  collection of tasks that should run before the coverage report is generated.

- Users who need to use older versions of Cobertura can now do so without the
  Cobertura libraries getting in the way. Some options, such as
  coverageIgnoreTrivial only work in Cobertura 2.0, so attempting to set those
  properties in the cobertura block of your build.gradle will result in an
  error.

- Groovy and Scala plugins may now be applied before or after the cobertura
  plugin. (issue #35 and issue #37)

- If the coberturaReport is in the task graph, the up-to-date status of all
  tests is set to false (issue #33)

- A Cobertura Check task has been implemented (issue #29) to check test coverage
  levels.

- Support for merging datafiles before generating reports (Issue #10).  This has
  the most value in the parent project of a multi project build.  When this
  happens, the parent project will need to declare a test task dependency on
  all the child project testing tasks to make sure the reports are accurate.

- I've added more tests, and hope to add even more soon.

Changes for 2.1.0
=================
- Added the coberturaReport task to generate coverage reports. (issue #20)

- The cobertura task now depends on tasks of type test in the applying project,
  and tasks named test in child projects (but not all tasks of type test in the
  child project).  It no longer runs tests in parent projects, sibling projects
  or tests in child projects that are not named "test". (issue #14)

- The coverageSourceDirs extension property now looks for scala and groovy code
  by default. (issues #7 and #22)

- Instrumentation now happens only when source code has changed or the cobertura
  configuration has changed. (issue #23)

- Instrumentation now depends on the classes task so that changes to Groovy
  or Scala source code triggers re-instrumentation. (issue #15)

- The plugin adjusts dependencies for test tasks added after the plugin is
  applied (issue #21)

Changes for 2.0.0
=================
- Updated the plugin to work with Gradle 1.7.  Dropped support for prior
  versions of Gradle.

Changes for 1.2.0
=================
- Added support for Cobertura 2.0 and its extra configuration options.

- Fixed bug that caused the configuration of test tasks to fail on sub-projects
  that don't have the coberura plugin applied. (issue #8)
