Introduction
------------

This plugin was inspired by the Cobertura plugin by valkolovos and jvanderpol.
This plugin is an improvement over the the original in a few important ways.

- The biggest difference is that the plugin now runs a Cobertura coverage
report even if tests fail.  If there are multiple test tasks, it will run the
cobertura reports after the last test task that ran.

- Per http://forums.gradle.org/gradle/topics/is_the_new_plugin_extension_approach_the_way_to_go,
I've replaced conventions with extensions.

- Works with Gradle 1.0 and 1.1 without deprecation warnings.

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

- I've tried to get the instrument task to perform up to date checking.  I only
want to instrument if the main source changed, or if we are missing the .ser
file, but tests change the .ser file, so we get instrumentation every time.  It
doesn't run at all if we've had a successful test run without cobertura.  Since
tests change the .ser file, it may be that having instrumentation run every
time the plugin runs might be a desirable thing.

- I'd like to have the coverage reports only run if the source or the tests have
changed, but I haven't started that yet.

Usage
-----
Add the following to your build.gradle file.

```groovy
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath "net.saliman:gradle-cobertura-plugin:1.0.0"
    }
}
apply plugin: 'cobertura'
```

There are sensible defaults for most things, but if needed, you can change some
of the properties to get different behavior.  The complete set is in the
CoberturaExtensions.groovy file, but the two most common are:

- cobertura.coverageDataFile = *file*: the file object referring to the .ser
file to create

- cobertura.coverageReportDir = *dir*: the file object representing the
directory where coverage reports should be built.

To get a Cobertura coverage report, simply execute the cobertura task.  The
plugin will make the cobertura task dependent on any Test tasks your project
has, and will run them before running the actual report.

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
            classpath 'net.saliman:gradle-cobertura-plugin:1.0.0'
        }
    }

