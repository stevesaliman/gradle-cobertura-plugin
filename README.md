News
----
###November 22, 2013 (Still in development):
*Note:* This plugin will not work with Cobertura 2.0.4 snapshots at this time.
All of the Cobertura classes that are called by this plugin have been renamed
by the Cobertura team.  I'm currently working with the Cobertura team on this
issue.

A special thank you to John Engelman for his help with the 2.2.0 release.

The biggest changes in this release are the behavior of the ```cobertura```
task, the addition of the ```checkCoverage``` task, and support for merging
datafiles before generating coverage reports. See the [CHANGELOG]
(http://github.com/stevesaliman/gradle-cobertura-plugin/blob/master/CHANGELOG)
for the full details, but the main thing is that applying the cobertura plugin
to a project no longer changes anything in the task graphs of other projects
in a multi-project build.

There were also a lot of options added to the ```cobertura``` extension, so
you may want to have a look there as well.

###October 27, 2013:

*Note:* If you have been using the ```coverageDatafile``` property in your
```cobertura``` block, you'll need to change it to ```coverageOutputDatafile```,
and you will probably want to add a ```coverageInputDatafile``` as well.

Version 2.1.0 Has several important changes.
See the [CHANGELOG]
(http://github.com/stevesaliman/gradle-cobertura-plugin/blob/master/CHANGELOG)
for the full details, but there 4 main changes that are important:

1. The ```cobertura``` task works a little differently than it did before. See
the Usage section for more details.

2. There is a new ```coberturaReport``` task that allows finer control over
which tests actually run.  See the Usage section for more details

3. Instrumentation now happens only when it needs to, such as when the source
code changes, or one of the options in the ```cobertura``` block of your
build.gradle changes.

4. The plugin now has better support for projects that use Groovy and Scala,
but the groovy and scala plugins need to be applied before the cobertura plugin.

###September 2013:
Version 2.0.0 only works with Gradle 1.7 and newer.  If you are on an older
version of gradle, you should use the latest 1.x release of this plugin.

Version 2.0.0 uses new features of Gradle 1.7 and removed deprecation warnings.
It also changed the dependencies slightly so that running ```gradle cobertura```
executes all the tests in a multi-project build, similar to what 
```gradle test``` does.

Version 1.2.0 Added support for Cobertura 2.0, which introduced some new
features.  Best among them are 2 new options, ```ignoreTrivial``` and
```ignoreMethodAnnotation```, each of which are described in the usage section
below. It also fixes some new issues found in multi-project builds.

Introduction
------------

This plugin was inspired by the Cobertura plugin by valkolovos and jvanderpol.
This plugin is an improvement over the the original in a few important ways.

- The biggest difference is that this fork of the plugin runs a Cobertura 
coverage report even if tests fail.  If there are multiple test tasks, it will
run the cobertura reports after the last test task that ran. Note that if
there is a test failure other test tasks won't necessarily run.  This is
consistent with Gradle's behavior when running multiple testing tasks.

- Per http://forums.gradle.org/gradle/topics/is_the_new_plugin_extension_approach_the_way_to_go,
I've replaced conventions with extensions.

- This plugin supports Cobertura's coverage check and merge functions.

- Version 2.0 works with Gradle 1.7 and above.  Version 1.2 works with Gradle
1.0 through 1.6.  They both take advantage of features introduced in Cobertura
version 2.0.

- I've worked a lot with build lifecycle to make sure that things only happen
if they need to happen, and when they need to happen.  For example, we only
instrument code if the user wanted to generate coverage reports, and then it
instruments right before the tests run so that time is not spent instrumenting
if the build fails due to some earlier error.

- This plugin is published and available on Maven Central, separating use of
the plugin from the source tree on GitHub.

- Most importantly, this plugin is clearly licensed as an Apache 2.0 licensed
project so users can use this plugin as part of any project they are building.

Todo:
-----

This is still a work in progress.  If anyone would like to help out, here are a
few things I'm still trying to accomplish.

- This plugin needs some robust unit tests.  The testclient directory has a
little java project that I use to manually test different scenarios, but we
could really use some proper unit tests.

- Did I mention testing? :-)  As issues are resolved, it would great if I could
have unit tests that made sure that things fixed for prior issues are still
fixed.  This is becoming more important as I do more work with multi project
builds and multi language projects.

Usage
-----
Add the following to your build.gradle file.

```groovy
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath "net.saliman:gradle-cobertura-plugin:2.1.0"
    }
}
apply plugin: 'cobertura'
```

There are sensible defaults for most things, but if needed, you can change some
of the properties to get different behavior.  The complete set is in the
CoberturaExtensions.groovy file, but the most common are:

- cobertura.coverageIgnoreTrivial = *true|false*: New in version 2.0, this
switch tells Cobertura to ignore simple getters and setters.

- cobertura.coverageIgnoreMethodAnnotations = *annotations*: New in version
2.0, this is an array of fully qualified annotation names.  Any method
annotated with an annotation in this array will be ignored by Cobertura.

- cobertura.coverageInputDataFile = *file*: the file object referring to the
.ser file to create during instrumentation.

- cobertura.coverageOutputDataFile = *file*: the file object referring to the
.ser file to use and modify during tests. This is not the same as the
coverageInputDatafile.  The former is generated by instrument task.  The
outputDataFile is a copy of the inputDataFile made before each build that is
used by (and modified by) tests to keep track of what was covered.  This needs
to be a copy so that the inputDataFile remains unchanged - otherwise, Gradle
will think that instrumentation will need to be done every time, which takes
longer than just copying the existing file.

- cobertura.coverageReportDir = *dir*: the file object representing the
directory where coverage reports should be built.

- cobertura.coverageExcludes = *regexes*: an array of regular expressions 
representing classes you want to exclude.  Cobertura will compare these against
the fully qualified filenames of your classes as they will exist in your 
instrumented classes directory, so you'll probably want to have ```.*``` at
the start of the expression.  Slashes in filenames will be replaced with dots,
so you can specify package names in the regex.  For example, 
```cobertura.coverageExcludes = ['.*net.saliman.someapp.logger.*'] ``` would 
exclude any classes in the 'net.saliman.someapp.logger' package.

Extension properties are changed in the ```cobertura``` block in your 
build.gradle file.

There are 3 tasks you can use to have Cobertura check coverage and generate
reports:

1. The ```coberturaReport``` task will cause instrumentation to happen before
tests are run, and a coverage report to be generated after tests are run, but
it will not cause any tests to run.  Tests will need to be supplied to the
Gradle command line separately.  This task can be used in a parent project of
a multi-project build to create a merged report of all child project code and
coverage.  To create a merged report, just set the ```coverageMergeDatafiles```
extension property to the locations of the child project output ser files, and
make the test task of the parent project dependent on the child project testing
tasks.  The change in task dependencies is important because Gradle doesn't
guarantee that tasks in child projects will run before tasks in the parent
project.  We need to make sure we don't merge datafiles until all datafiles in
child projects have been generated.

2. The ```cobertura``` task does all the things ```coberturaReport``` does,
but it causes all tasks of type "Test" in the applying project to be run before
the coverage report is generated. The idea is that if you want to see how well
your code is covered, you'd want to know the overall coverage, after all tests
are run.  If I'm wrong about that, you can always use ```-x someTask``` or the
```coberturaReport``` task to more precisely control what tests actually get
run.

3. The ```checkCoverage``` task does all the things ```coberturaReport``` does,
but adds a test coverage check to the build.  It optionally fails the build if
the coverage levels are not sufficient.  See the documentation in the
```CoberturaExtension``` class for more details.

If you have a multi-project build, and you need to have classes from more than
one of them, you'll need to add some code to the coverage block of your project
similar to the following:

```groovy
cobertura {
   rootProject.subprojects.each {
     coverageDirs << file("${it.name}/build/classes/main")
   }
}
```

This assumes that each child project is in a directory underneath the main 
project directory.  If this is not the case, the argument to ```file``` will
need to be modified accordingly.

Building
--------
To build from source:

    ./gradlew install

This will create a local jar and put it in your local maven repository. you can
reference it in your builds like this:

    buildscript {
        repositories {
            mavenLocal()
        }
        dependencies {
            classpath 'net.saliman:gradle-cobertura-plugin:2.2.0-SNAPSHOT'
        }
    }
