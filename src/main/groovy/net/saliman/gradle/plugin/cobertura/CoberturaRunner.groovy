package net.saliman.gradle.plugin.cobertura

import java.lang.reflect.Method


/**
 * Wrapper for Cobertura's main class.
 */
public class CoberturaRunner {

    private Set<File> classpath

    public CoberturaRunner withClasspath(Set<File> classpath) {
        return new CoberturaRunner(classpath: classpath)
    }

    public void instrument(String basedir, String datafile, String destination, List<String> ignore,
            List<String> includeClasses, List<String> excludeClasses,
            boolean ignoreTrivial, List<String> ignoreMethodAnnotations, String auxiliaryClasspath,
            List<String> instrument) {
        List<String> args = new ArrayList<String>()
        /*
         * cobertura will ignore excludes if there are no includes specified, so
         * if excludes have been specified but includes haven't, put a default
         * include in the list
         */
        if (excludeClasses != null && excludeClasses.size() > 0 && (includeClasses == null || includeClasses.size() == 0)) {
            includeClasses = new ArrayList<String>(1)
            includeClasses.add(".*")
        }
        if (basedir != null && !basedir.equals("")) {
            args.add("--basedir")
            args.add(basedir)
        }
        if (datafile != null && !datafile.equals("")) {
            args.add("--datafile")
            args.add(datafile)
        }
        if (destination != null && !destination.equals("")) {
            args.add("--destination")
            args.add(destination)
        }
        if (ignore != null) {
            for (String s : ignore) {
                args.add("--ignore")
                args.add(s)
            }
        }
        if (includeClasses != null) {
            for (String s : includeClasses) {
                args.add("--includeClasses")
                args.add(s)
            }
        }
        if (excludeClasses != null) {
            for (String s : excludeClasses) {
                args.add("--excludeClasses")
                args.add(s)
            }
        }
	    if ( ignoreTrivial ) {
		    args.add("--ignoreTrivial")
	    }

	    if ( ignoreMethodAnnotations != null ) {
		    for (String s : ignoreMethodAnnotations ) {
			    args.add("--ignoreMethodAnnotation")
			    args.add(s)
		    }
	    }

	    args.add("--auxClasspath")
	    args.add(auxiliaryClasspath)
//	    <path id="cobertura.auxpath">
//	    <pathelement path="${classpath}"/>
//	    <fileset dir="lib">
//	    <include name="**/*.jar"/>
//	    </fileset>
//	    <pathelement location="classes"/>
//	    </path>

        args.addAll(instrument)
        executeMain("net.sourceforge.cobertura.instrument.Main", args)
    }

    public void generateCoverageReport(String datafile, String destination, String format,
            List<String> sourceDirectories) throws Exception {
        List<String> args = new ArrayList<String>()
        args.add("--datafile")
        args.add(datafile)
        args.add("--format")
        args.add(format)
        args.add("--destination")
        args.add(destination)
        args.addAll(sourceDirectories)
        executeMain("net.sourceforge.cobertura.reporting.Main", args)
    }

    private void executeMain(String className, List<String> args) {
        ClassLoader cl = this.getClass().getClassLoader()
        if (classpath) {
            cl = new URLClassLoader(classpath.collect { it.toURI().toURL() } as URL[])
        }
        Class mainClass = cl.loadClass(className)
        Method mainMethod = mainClass.getMethod("main", String[])
        mainMethod.invoke(null, [args as String[]] as Object[])
    }
}
