package net.saliman.gradle.plugin.cobertura

import net.saliman.gradle.plugin.cobertura.util.ChildFirstUrlClassLoader

import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

/**
 * Wrapper for Cobertura's main classes.
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
		// TODO: if 2.0.4, call InstrumentMain.main
		executeCobertura("net.sourceforge.cobertura.instrument.Main", "main", false, args)
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
		// TODO if 2.0.4, call ReportMain.main
		executeCobertura("net.sourceforge.cobertura.reporting.Main", "main", false, args)
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

		// TODO: branch on version and call CheckCoverageMain.checkCoverage
		executeCobertura("net.sourceforge.cobertura.check.Main", "main", true, args)
	}

	def mergeCoverageReports(CoberturaExtension configuration) {
		List<String> args = new ArrayList<String>()
		if ( configuration.coverageOutputDatafile != null ) {
			args.add("--datafile")
			args.add(configuration.coverageOutputDatafile.path)
		}
		if ( configuration.coverageMergeDatafiles != null ) {
			for ( File f : configuration.coverageMergeDatafiles ) {
				args.add(f.path)
			}
		}

		// TODO: branch on version and call MergeMain.main
		executeCobertura("net.sourceforge.cobertura.merge.Main", "main", false, args)

	}
	/**
	 * Execute the Cobertura method that does the required work. This will replace
	 * the class loader with a child-first class loader to make sure that we get
	 * versions of classes that Cobertura expects over the ones that the
	 * application uses.  It also optionally use a SecurityManager to trap
	 * SecurityExceptions thrown by the Cobertura checkCoverage code.
	 * @param className the name of the class with the method we are executing.
	 * @param methodName the name of the method to execute.
	 * @param useSecurityManager whether or not we need to use a security manager
	 * @param args the arguments to pass to the method.
	 * @return the exit code of the invoked method or 0 if everything ran well.
	 */
	private executeCobertura(String className, String methodName,
	                        boolean useSecurityManager, List<String> args) {
		// We need to replace the classloader for the thread with one that finds
		// Cobertura's dependencies first.
		ClassLoader prevCl = Thread.currentThread().getContextClassLoader();

		if ( classpath ) {
			def urls = classpath.collect { it.toURI().toURL() }
			ClassLoader cl = new ChildFirstUrlClassLoader(urls as URL[], prevCl)
			Thread.currentThread().setContextClassLoader(cl);
		}

		def SecurityManager oldSm = System.getSecurityManager()
		CoberturaSecurityManager sm = new CoberturaSecurityManager(oldSm)
		def exitStatus = 0

		try {
			Class mainClass = Thread.currentThread().getContextClassLoader().loadClass(className)
			Method mainMethod = mainClass.getMethod(methodName, String[])
			if ( useSecurityManager ) {
				System.setSecurityManager(sm)
			}
			exitStatus = mainMethod.invoke(null, [args as String[]] as Object[])
		} catch (Exception e) {
			if ( !isSecurityException(e) ) {
				e.printStackTrace()
				throw e
			}
		} finally {
			// Restore the classLoader
			Thread.currentThread().setContextClassLoader(prevCl);
			if ( useSecurityManager ) {
				System.setSecurityManager(oldSm)
				exitStatus = sm.exitStatus
			} else {
				exitStatus = -1
			}
		}
		return exitStatus
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
