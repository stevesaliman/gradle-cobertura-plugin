Usage
=====
See the [Usage](http://github.com/stevesaliman/gradle-cobertura-plugin/blob/master/usage.md)
page for complete details on how to use this plugin.

News
====
### December 22, 2017
Version 2.5.3 now supports Android Tools 3.0 and Android Kotlin projects with 
thanks to Christoph Walcher (@wiomoc) and Eduardo Giménez (@edu-gimenez)

### October 22, 2017
Version 2.5.1 works around a bug described in Issue #89

### June 20, 2017
Version 2.5.0 now supports Gradle 4.0.  In particular, the plugin has been 
enhanced to be aware of where Gradle 4.0 is putting class files.  Prior versions
of the plugin will run in Gradle 4.0, but it might not run correctly.  See the
[CHANGELOG](http://github.com/stevesaliman/gradle-cobertura-plugin/blob/master/CHANGELOG.md)
for more details.
### December 22, 2016
Version 2.4.0 now builds under Gradle 3.2.  It also introduces a change to the
way the auxiliaryClasspath is set up.  Previously, if you wanted to add to 
the auxiliaryClasspath, you would need to explicitly set all the elements.  
Starting with version 2.4.0, you only need to specify the things you want to
add to the default auxiliaryClasspath.  Thanks to @Frank667 for the contribution.
### May 26,2016
Version 2.3.2 Groups tasks better, and allows applying the plugin from 
settings.gradle and init.gradle
### March 3, 2016
Version 2.3.1 fixes a minor issue with Android projects, with thanks to Adam
Peck (@dampcake).
### December 27,2015
The Gradle Cobertura plugin now supports Android projects, with thanks to Gautam
Korlam (@kageiit).  It is worth noting that unlike Java projects, where plugins
can be applied in any order, using this plugin on Android projects requires that
the Android plugin be applied *before* the Cobertura plugin.
### March 2, 2015
At long last, Cobertura has released version 2.1, with thanks to Dennis
Lundberg.  As a result, the Gradle Cobertura plugin now uses version 2.1.1 of
Cobertura by default.  This version should play better with Java 8 and fix
several of the issues users were having.  As part of this release, I've bumped
the version of Groovy that the plugin uses.  This can cause issues in Gradle
1.x.  The workaround is to add 
```classpath 'org.codehaus.groovy:groovy-backports-compat23:2.3.5'``` to the
buildscript dependencies.

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
		classpath 'net.saliman:gradle-cobertura-plugin:2.5.3'
	}
}
```
