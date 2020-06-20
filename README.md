Usage
=====
See the [Usage](http://github.com/stevesaliman/gradle-cobertura-plugin/blob/master/usage.md)
page for complete details on how to use this plugin.

News
====
### June 20, 2020
Version 3.0.1-SNAPSHOT has been published to the Maven Snapshot repository, 
which should solve issues running with Gradle 6.2+.  I appreciate any feedback 
on how this new version runs in any version of Gradle.

### November 10, 2019
Version 3.0.0 of the Cobertura plugin now supports Gradle 6.0, with thanks to
Roberto Perez Alcolea.  This release no longer supports or works on older 
versions of Java and Gradle.  Users will need to update to at least Java 8 and
Gradle 5.1

### March 23, 2019
Version 2.6.1 is a minor release that is meant to get the Cobertura plugin 
working better with the Scoverage Scala plugin.  Thank you to Eyal Roth
(@eyalroth) for his suggestions and code examples.

### January 5, 2019
Version 2.6.0 supports Gradle 5.1, with thanks to Roberto Alcolea (@rpalcolea)

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
		classpath 'net.saliman:gradle-cobertura-plugin:3.0.0'
	}
}
```
