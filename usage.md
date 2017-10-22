Applying the plugin
===================
To use the plugin with Gradle 2.1 or later, add the following to your 
build.gradle file.

```groovy
plugins {
  id 'net.saliman.cobertura' version '2.5.1'
}
```

To use the plugin with Gradle 2.0 or older, or to use a snapshot release of the
plugin, add the following to build.gradle:

```groovy
buildscript {
    repositories {
        mavenCentral()
        // The next repo is only needed while using SNAPSHOT versions
        maven { url "https://oss.sonatype.org/content/repositories/snapshots" }
    }
    dependencies {
        classpath "net.saliman:gradle-cobertura-plugin:2.5.1"
    }
}
apply plugin: 'net.saliman.cobertura'
```

If you are using this plugin on a java, groovy, or scala project, it is probably
best to apply it after those plugins.  If you are using this plugin on an 
android project, you must apply it after the android plugin.

If you are using this plugin on a project that does *not* use slf4j, and you
run into ```ClassNotFoundException``` issues, you will need to add a version 
of the slf4j api to the testRuntime dependencies, for example:
```groovy
dependencies {
  testRuntime "org.slf4j:slf4j-api:1.7.10"
}
```

The cobertura plugin can also be applied in ```settings.gradle``` and 
```init.gradle```.  In both cases, this will have the effect of applying the 
plugin to all projects in the build, so use this option carefully.

Tasks
=====
The Cobertura plugin will create 3 tasks you can use to have Cobertura check
coverage and generate reports:

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

2. The ```cobertura``` task is intended to be a convenience task runs all of the 
unit tests and generate a coverage report.  It does all the things
```coberturaReport``` does, but after causing all tasks of type "Test" in the
applying project to run before the coverage report is generated. The idea is 
that if you want to see how well your code is covered, you'd want to know the 
overall coverage, after all tests are run.  If I'm wrong about that, you can 
always use ```-x someTask``` or the ```coberturaReport``` task to more 
precisely control what tests actually get run.

3. The ```coberturaCheck``` task does all the things ```coberturaReport``` does,
but adds a test coverage check to the build.  It optionally fails the build if
the coverage levels are not sufficient.  See the documentation in the
```CoberturaExtension``` class for more details. Like the ```coberturaReport```
task, ```coberturaCheck``` will not cause any tests to run.

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

Configuration
=============

The behavior of this plugin is controlled by setting various options in the
```cobertura``` block of your build.gradle file. There are sensible defaults for
most options, but you can fine tune the behavior of this plugin with the 
following options:

- ```coberturaVersion = <version>```: The version of Cobertura that will be
  used to run the coverage reports.  The default is 2.1.1.

- ```auxiliaryClasspath = <FileCollection>```: You can add files and directories
  to the classpath that Cobertura uses while instrumenting your classes. The
  plugin will always include certain directories, based on the type of project.
  Java projects will always include project.sourceSets.main.output.classesDirs +
  project.sourceSets.main.compileClasspath. Android projects will always include 	
  ${project.buildDir.path}/intermediates/classes/${classesDir} + 
  project.configurations.getByName("compile") + 
  project.configurations.getByName("${androidVariant}Compile"))). 
  There is no need to include them again.

- ```coverageDirs = [ <dirnames> ]```: An array of directories under the base
  directory containing classes to be instrumented.  The default is the names
  of each directory in [ project.sourceSets.main.classesDirs ]

- ```coverageInputDatafile = <file>```: The file object referring to the
  .ser file to create during instrumentation.

- ```coverageOutputDatafile = <file>```: The file object referring to
  the .ser file to use and modify during tests. This is not the same as the
  coverageInputDatafile.  The former is generated by instrument task.  The
  outputDatafile is a copy of the inputDatafile made before each build that is
  used by (and modified by) tests to keep track of what was covered.  This needs
  to be a copy so that the inputDatafile remains unchanged - otherwise, Gradle
  will think that instrumentation will need to be done every time, which takes
  longer than just copying the existing file.

- ```coverageReportDir = <dir>```: The file object representing the
  directory where coverage reports should be built.

- ```coverageFormats = [ <formats> ]```: Tells the plugin what report formats
  should be used.  Cobertura supports 'html' and 'xml'.  The default is html.

- ```coverageEncoding```: The file encoding Cobertura should use when it 
  generates coverage reports, such as 'UTF-8'.  There is no default for this
  optional setting.  If no encoding is specified, Cobertura will use the 
  default encoding of the OS.
  
- ```coverageSourceDirs = <set of directories>```: Tells the plugin where to
  look for source files to instrument and include in reports.  By default,
  the plugin will include project.sourceSets.main.java.srcDirs,
  project.sourceSets.main.groovy.srcDirs, and project.sourceSets.main.scala.srcDirs

- ```coverageIncludes = [ <regexes> ]```: An array of regular expressions
  representing classes you want to include.  Cobertura will compare these
  against the fully qualified filenames of your classes as they will exist in
  your instrumented classes directory, so you'll probably want to have ```.*```
  at the start of the expression.  Slashes in filenames will be replaced with
  dots, so you can specify package names in the regex.  For example,
  ```coverageIncludes = ['.*net.saliman.someapp.logger.*'] ``` would include
  any classes in the 'net.saliman.someapp.logger' package. By default, all
  classes in the source directories are included.

- ```coverageExcludes = [ <regexes> ]```: An array of regular expressions
  representing classes you want to *exclude*.  Cobertura will compare these
  against the fully qualified filenames of your classes as they will exist in
  your instrumented classes directory, so you'll probably want to have ```.*```
  at the start of the expression.  Slashes in filenames will be replaced with
  dots, so you can specify package names in the regex.  For example,
  ```coverageExcludes = ['.*net.saliman.someapp.logger.*'] ``` would exclude any
  classes in the 'net.saliman.someapp.logger' package.  By default, no classes
  are excluded.

- ```coverageIgnores = [ <regexes> ]```: An array of regular expressions
  representing patterns within the source code that should be ignored, such as
  logging statements.  This applies to code within a source file not to files
  themselves.  see ```coverageIncludes``` for file level inclusion.

- ```coverageIgnoreTrivial = <true|false>```: New in version 2.0, this switch
  tells Cobertura to ignore simple getters and setters.  The default is false.

- ```coverageIgnoreMethodAnnotations = [ <annotations> ]```: New in version
  2.0, this is an array of fully qualified annotation names.  Any method
  annotated with an annotation in this array will be ignored by Cobertura.

- ```coverageClassesTasks = <Closure>```: Allows users to override the list of
  tasks that the plugin expects to produce compiled classes that need to be
  instrumented.  The given closure must return a list of Gradle tasks.  The
  default is to use the "classes" task.

- ```coverageTestTasks = <Closure>```: Allows users to override the list of
  tasks that the plugin expects to be tests.  The given closure must return a
  list of Gradle tasks.  The default is to use all tasks of type "Test".

- ```coverageCheckBranchRate = <percent>```: The minimum acceptable branch
  coverage rate needed by each class. This should be an integer value between 0
  and 100.  Used when running the ```coberturaCheck``` task.

- ```coverageCheckLineRate = <percent>```: The minimum acceptable line coverage
  rate needed by each class. This should be an integer value between 0 and 100.
  Used when running the ```coberturaCheck``` task.

- ```coverageCheckPackageBranchRate = <percent>```: The minimum acceptable
  average branch coverage rate needed by each package. This should be an integer
  value between 0 and 100.  Used by the ```coberturaCheck```task.

- ```coverageCheckPackageLineRate = <percent>```: The minimum acceptable average
  line coverage rate needed by each package. This should be an integer value
  between 0 and 100.  Used by the ```coberturaCheck``` task.

- ```coverageCheckTotalBranchRate = <percent>```: The minimum acceptable average
  branch coverage rate needed by the project as a whole. This should be an
  integer value between 0 and 100. Used by the ```coberturaCheck``` task.

- ```coverageCheckTotalLineRate = <percent>```:	The minimum acceptable average
  line coverage rate needed by the project as a whole. This should be an integer
  value between 0 and 100.  Used by the ```coberturaCheck``` task.

- ```coverageCheckRegexes = [ <regexes> ]```: For finer grained control, you can
  optionally specify minimum branch and line coverage rates for individual
  classes using any number of regular expressions. Each expression is a map with
  3 keys like this:
  ```
  coverageCheckRegexes = [
    [ regex: 'com.example.reallyimportant.*', branchRate: 80, lineRate: 90 ],
    [ regex: 'com.example.boringcode.*', branchRate: 40, lineRate: 30 ]
  ]
  ```
  
  The keys are:
    - ```regex```: A regular expression identifying classes classes that need
      special rates
    - ```branchRate```: The branch rate for the selected classes
    - ```lineRate```: The line rate for the selected classes

- ```coverageCheckHaltOnFailure = <true|false>```: Whether or not the
  ```coberturaCheck``` should fail the build if the minimum coverage rates are
  not met.  Defaults to false.

- ```coverageMergeDatafiles = [ <files> ]```: A list of data files to merge into
  a single data file to produce a merged report.  If set, each of the datafiles
  in the given list will be merged into a the single datafile, specified by
  ```coverageReportDatafile```, before generating a coverage report.

- ```coverageReportDatafile = <file>```: Path to the data file to use when
  generating reports tests. Most users won't need to change this property.
  Defaults to project.buildDir.path/cobertura/cobertura.ser. The only time this
  should be changed is when users are merging datafiles and
  ```coverageMergeDatafiles``` contains the default datafile.

- ```androidVariant = <String>```: The variant for android projects. The default is `debug`.
  Running cobertura is only supported on a single test variant.

Troubleshooting
===============

Visit our Wiki for Troubleshooting and Tips from our community.
https://github.com/stevesaliman/gradle-cobertura-plugin/wiki
