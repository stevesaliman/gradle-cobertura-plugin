News
----
*October 27, 2013:*
*Note:* If you have been using the ```coverageDatafile``` property in your
```cobertura``` block, you'll need to change it to ```coverageOutputDatafile```,
and you will probably want to add a ```coverageInputDatafile``` as well.

Version 2.1.0 Has several important changes.
See http://github.com/stevesaliman/gradle-cobertura-plugin/blob/master/CHANGELOG
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

*September 2013:*
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
there is a test failure in a project that is part of a multi-project build, 
the tests in other projects won't necessarily run.  This is consistent with
Gradle's behavior in the ```test``` task.

- Per http://forums.gradle.org/gradle/topics/is_the_new_plugin_extension_approach_the_way_to_go,
I've replaced conventions with extensions.

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
little java project that I use to manually test different scenarios  (the test
client needs a buildSrc link back to the plugin source), but we could really use
some proper unit tests.

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

For projects that use Groovy or Scala, those plugins should be applied before
the Cobertura plugin, or the Groovy and Scala source code won't be available in
the generated reports.

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

There are 2 tasks you can use to get Cobertura coverage reports:

1. The ```coberturaReport``` task will cause instrumentation to happen before
tests are run, and a coverage report to be generated after tests are run, but
it will not cause any tests to run.  Tests will need to be supplied to the
Gradle command line separately.

2. The ```cobertura``` task is meant to be a shortcut to
```gradle test coverageReport```, with one exception.  By default, the
```test``` task runs all tasks named "test" from the current directory, down.
the ```cobertura``` task adds all tasks of type "Test" in the applying project.
The idea is that if you want to see how well your code is covered, you'd want
to know the overall coverage, after all tests are run.  If I'm wrong about that,
you can always use ```-x someTask``` or the ```coberturaReport``` task to more
precisely control what tests actually get run.

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
            classpath 'net.saliman:gradle-cobertura-plugin:2.1.0'
        }
    }
