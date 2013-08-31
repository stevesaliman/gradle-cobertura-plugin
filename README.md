News
----
Version 2.0.0 only works with Gradle 1.7 and newer.  If you are on an older
version of gradle, you should use the latest 1.x release of this plugin.

Version 2.0.0 uses new features of Gradle 1.7 and removes deprecation warnings.
It also changes the dependencies slightly so that running ```gradle cobertura```
executes all the tests in a multi-project build, similar to what 
```gradle test``` does.

Version 1.2.0 Added support for Cobertura 2.0, which introduced some new
features.  Best among them are 2 new options, ```ignoreTrivial``` and
```ignoreMethodAnnotation```, each of which are described in the usage section
below. It also fixes some new issues found in multi-project builds.

Version 1.1.2 Fixed some issues with multi project builds, with thanks to 
detlef-brendle.

Version 1.1.1 added support for Java 1.5, with thanks to trnl.

Version 1.1.0 added support for multiple report formats (Thank you aartiPI).
The default behavior is still to generate html reports, but you can change this
behavior by assigning a value to ```coverageFormats``` in the cobertura 
configuration block.  There is a slight backwards compatibility issue for 
anyone who overrode the default format in version 1.0.3 and earlier. To fix it,
simply change ```coverageFormat``` to ```coverageFormats``` and change the 
value to a String array.


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

- Works with Gradle 1.0 and above.

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

- I'd like to have the coverage reports only run if the source or the tests have
changed, but I haven't started that yet. Instrumentation would only need to happen if coverage reports are requested, and are not up to date.

- Did I mention testing? :-)  As issues are resolved, it would great if I could
have unit tests that made sure that things fixed for prior issues are still
fixed.

Usage
-----
Add the following to your build.gradle file.

```groovy
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath "net.saliman:gradle-cobertura-plugin:1.2.0"
    }
}
apply plugin: 'cobertura'
```

There are sensible defaults for most things, but if needed, you can change some
of the properties to get different behavior.  The complete set is in the
CoberturaExtensions.groovy file, but the two most common are:

- cobertura.coverageIgnoreTrivial = *true|false*: New in version 2.0, this
switch tells Cobertura to ignore simple getters and setters.

- cobertura.coverageIgnoreMethodAnnotations = *annotations*: New in version
2.0, this is an array of fully qualified annotation names.  Any method
annotated with an annotation in this array will be ignored by Cobertura.

- cobertura.coverageDataFile = *file*: the file object referring to the .ser
file to create

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

To get a Cobertura coverage report, simply execute the ```cobertura``` task. 
The plugin will make the cobertura task dependent on any Test tasks your 
project has, any ```test``` tasks in related projects, and will run them all 
before running the actual report. The only difference in the tests that run
in the ```test``` and ```cobertura``` tasks is that the ```cobertura``` task
runs *all* test tasks in the current project.  The ```test``` task doesn't.

If you have a multi-project build, and you need to have classes from more than
one of them, you'll need to add some code to the coverage block of your project
similar to the following:

```groovy
cobertura {
   rootProject.subprojects.each {
   coverageDirs << file("${it.name}/build/classes/main")
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
            classpath 'net.saliman:gradle-cobertura-plugin:2.0.0'
        }
    }

