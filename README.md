News
----
If you are running a version prior to Release 1.0.3, you should update to a 
newer version, as version 1.0.3 fixes a classpath issue that prevented reports
from generating correctly on the first run after a "clean"

Version 1.1.0 adds support for multiple report formats (Thank you aartiPI).
The default behavior is still to generate html reports, but you can change this
behavior by assigning a value to ```coverageFormats``` in the cobertura 
configuration block.  There is a slight backwards compatibility issue for 
anyone who overrode the default format in version 1.0.3 and earlier. To fix it,
simply change ```coverageFormat``` to ```coverageFormats``` and change the 
value to a String array.

Version 1.1.1 adds support for Java 1.5, with thanks to trnl.

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

- I'd like to have the coverage reports only run if the source or the tests have
changed, but I haven't started that yet. Instrumentation would only need to happen if coverage reports are requested, and are not up to date.

Usage
-----
Add the following to your build.gradle file.

```groovy
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath "net.saliman:gradle-cobertura-plugin:1.1.1"
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

Extension properties are changed in the ```cobertura``` block in your 
build.gradle file.

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
            classpath 'net.saliman:gradle-cobertura-plugin:1.1.1'
        }
    }

