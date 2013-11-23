package net.saliman.gradle.plugin.cobertura

import java.lang.reflect.InvocationTargetException
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
		if ( excludeClasses != null && excludeClasses.size() > 0 && (includeClasses == null || includeClasses.size() == 0) ) {
			includeClasses = new ArrayList<String>(1)
			includeClasses.add(".*")
		}
		if ( hasLength(basedir) ) {
			args.add("--basedir")
			args.add(basedir)
		}
		if ( hasLength(datafile) ) {
			args.add("--datafile")
			args.add(datafile)
		}
		if ( hasLength(destination) ) {
			args.add("--destination")
			args.add(destination)
		}
		if ( ignore != null ) {
			for ( String s : ignore ) {
				args.add("--ignore")
				args.add(s)
			}
		}
		if ( includeClasses != null ) {
			for ( String s : includeClasses ) {
				args.add("--includeClasses")
				args.add(s)
			}
		}
		if ( excludeClasses != null ) {
			for ( String s : excludeClasses ) {
				args.add("--excludeClasses")
				args.add(s)
			}
		}
		if ( ignoreTrivial ) {
			args.add("--ignoreTrivial")
		}

		if ( ignoreMethodAnnotations != null ) {
			for ( String s : ignoreMethodAnnotations ) {
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

	public int checkCoverage(CoberturaExtension configuration) throws Exception {
		List<String> args = new ArrayList<String>()
		args.add("--datafile")
		args.add(configuration.coverageOutputDatafile.path)

		if ( configuration.coverageCheckBranchRate != null ) {
			args.add("--branch")
			args.add(configuration.coverageCheckBranchRate.toString())
		}

		if ( configuration.coverageCheckLineRate != null ) {
			args.add("--line")
			args.add(configuration.coverageCheckLineRate.toString())
		}

		if ( configuration.coverageCheckPackageBranchRate != null ) {
			args.add("--packagebranch")
			args.add(configuration.coverageCheckPackageBranchRate.toString())
		}

		if ( configuration.coverageCheckPackageLineRate != null ) {
			args.add("--packageline")
			args.add(configuration.coverageCheckPackageLineRate.toString())
		}

		if ( configuration.coverageCheckTotalBranchRate != null ) {
			args.add("--totalbranch")
			args.add(configuration.coverageCheckTotalBranchRate.toString())
		}

		if ( configuration.coverageCheckTotalLineRate != null ) {
			args.add("--totalline")
			args.add(configuration.coverageCheckTotalLineRate.toString())
		}

		if ( configuration.coverageCheckRegexes != null ) {
			for ( Map map : configuration.coverageCheckRegexes ) {
				args.add("--regex")
				args.add("${map.regex}:${map.branchRate}:${map.lineRate}")
			}
		}

		executeMain("net.sourceforge.cobertura.check.Main", args)
	}

	private int executeMain(String className, List<String> args) {
		ClassLoader cl = this.class.classLoader
		if ( classpath ) {
			cl = new URLClassLoader(classpath.collect { it.toURI().toURL() } as URL[], cl)
		}

		def SecurityManager oldSm = System.getSecurityManager()
		CoberturaSecurityManager sm = new CoberturaSecurityManager(oldSm)

		Class mainClass = cl.loadClass(className)
		Method mainMethod = mainClass.getMethod("main", String[])
		try {
			System.setSecurityManager(sm)
			mainMethod.invoke(null, [args as String[]] as Object[])
		} catch (Exception e) {
			if ( !isSecurityException(e) ) {
				e.printStackTrace()
				throw e
			}
		} finally {
			System.setSecurityManager(oldSm)
		}
		return sm.exitStatus
	}

	/**
	 * helper method to check to see if a given exception is a SecurityException,
	 * or the cause of an InvocationTargetException.  We need to check both
	 * because we're invoking the Cobertura Main class via reflection, which will
	 * wrap exceptions thrown by the invoked code.
	 * @param e the exception to check.
	 * @return {@code true} if the underlying cause of the given exception is
	 * a SecurityException
	 */
	boolean isSecurityException(e) {
		if ( SecurityException.class.isAssignableFrom(e.class) ) {
			return true
		}
		if ( !InvocationTargetException.class.isAssignableFrom(e.class) ) {
			return false;
		}
		def cause = e.targetException
		if ( cause == null ) {
			return false;
		}
		return SecurityException.class.isAssignableFrom(cause.class)
	}

	boolean hasLength(String s) {
		return (s != null && s.length() > 0)
	}
}
