Usage
=====
See the [Usage]
(http://github.com/stevesaliman/gradle-cobertura-plugin/blob/master/usage.md)
page for complete details on how to use this plugin.

News
====
###March 2, 2015
At long last, Cobertura has released version 2.1, with thanks to Dennis
Lundberg.  As a result, the Gradle Cobertura plugin now uses version 2.1.1 of
Cobertura by default.  This version should play better with Java 8 and fix
several of the issues users were having.
###October 12, 2014
The Cobertura plugin now supports Cobertura 2.1, and is a part of Gradle 2.1's
plugin repository.  See the [Usage] 
(http://github.com/stevesaliman/gradle-cobertura-plugin/blob/master/usage.md)
page for more details.
###June 11, 2014
Gradle has made the first 2.0 release candidate available, and it looks like
the cobertura plugin works fine with Gradle 2.0.
###February 25, 2014:
*Note:* This plugin will not work with Cobertura 2.0.4 snapshots at this time.
All of the Cobertura classes that are called by this plugin have been renamed
by the Cobertura team.  I'm currently working with the Cobertura team on this
issue.

Added suAdded support for customizing the auxiliary classpath, with thanks to
Harald Schmitt

###December 08, 2013:
A special thank you to John Engelman for his help with the 2.2.0 release.

The biggest changes in this release are the behavior of the ```cobertura```
task, the addition of the ```checkCoverage``` task, and support for merging
datafiles before generating coverage reports. See the [CHANGELOG]
(http://github.com/stevesaliman/gradle-cobertura-plugin/blob/master/CHANGELOG.md)
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
(http://github.com/stevesaliman/gradle-cobertura-plugin/blob/master/CHANGELOG.md)
for the full details, but there 4 main changes that are important:

1. The ```cobertura``` task works a little differently than it did before. See
   the [Usage]
(http://github.com/stevesaliman/gradle-cobertura-plugin/blob/master/usage.md)
   page for more details.

2. There is a new ```coberturaReport``` task that allows finer control over
which tests actually run.  See the [Usage]
(http://github.com/stevesaliman/gradle-cobertura-plugin/blob/master/usage.md)
   page for more details

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
============

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
=====

This is still a work in progress.  If anyone would like to help out, here are a
few things I'm still trying to accomplish.

- This plugin needs some robust unit tests.  The testclient directory has a
little java project that I use to manually test different scenarios, but we
could really use some proper unit tests.

- Did I mention testing? :-)  As issues are resolved, it would great if I could
have unit tests that made sure that things fixed for prior issues are still
fixed.  This is becoming more important as I do more work with multi project
builds and multi language projects.

Building
========
To build this plugin from source use the following command:

```
./gradlew install
```

This will create a local jar and put it in your local maven repository. you can
reference it in your builds like this:

```groovy
buildscript {
	repositories {
		mavenLocal()
	}
	dependencies {
		classpath 'net.saliman:gradle-cobertura-plugin:2.2.5'
	}
}
```
