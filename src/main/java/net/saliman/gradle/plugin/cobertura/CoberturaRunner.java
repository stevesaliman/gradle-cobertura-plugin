package net.saliman.gradle.plugin.cobertura;

import net.sourceforge.cobertura.instrument.Main;
import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.file.collections.SimpleFileCollection;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.JavaExec;
import org.gradle.tooling.BuildException;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Wrapper for Cobertura's main class.
 */
public class CoberturaRunner {

    public void instrument(String basedir, String datafile, String destination, List<String> ignore,
            List<String> includeClasses, List<String> excludeClasses,
            boolean ignoreTrivial, List<String> ignoreMethodAnnotations, String auxiliaryClasspath,
            List<String> instrument) {
        List<String> args = new ArrayList<String>();
        /*
         * cobertura will ignore excludes if there are no includes specified, so
         * if excludes have been specified but includes haven't, put a default
         * include in the list
         */
        if (excludeClasses != null && excludeClasses.size() > 0 && (includeClasses == null || includeClasses.size() == 0)) {
            includeClasses = new ArrayList<String>(1);
            includeClasses.add(".*");
        }
        if (basedir != null && !basedir.equals("")) {
            args.add("--basedir");
            args.add(basedir);
        }
        if (datafile != null && !datafile.equals("")) {
            args.add("--datafile");
            args.add(datafile);
        }
        if (destination != null && !destination.equals("")) {
            args.add("--destination");
            args.add(destination);
        }
        if (ignore != null) {
            for (String s : ignore) {
                args.add("--ignore");
                args.add(s);
            }
        }
        if (includeClasses != null) {
            for (String s : includeClasses) {
                args.add("--includeClasses");
                args.add(s);
            }
        }
        if (excludeClasses != null) {
            for (String s : excludeClasses) {
                args.add("--excludeClasses");
                args.add(s);
            }
        }
	    if ( ignoreTrivial ) {
		    args.add("--ignoreTrivial");
	    }

	    if ( ignoreMethodAnnotations != null ) {
		    for (String s : ignoreMethodAnnotations ) {
			    args.add("--ignoreMethodAnnotation");
			    args.add(s);
		    }
	    }

	    args.add("--auxClasspath");
	    args.add(auxiliaryClasspath);
//	    <path id="cobertura.auxpath">
//	    <pathelement path="${classpath}"/>
//	    <fileset dir="lib">
//	    <include name="**/*.jar"/>
//	    </fileset>
//	    <pathelement location="classes"/>
//	    </path>

        args.addAll(instrument);
        Main.main(args.toArray(new String[args.size()]));
    }

    public void check(Project project, final String classpath, final List<String> args) {
        final Logger log = project.getLogger();
        List<String> command = new ArrayList<String>() {{
            add("java");
            add("-cp");
            add(classpath);
            add("net.sourceforge.cobertura.check.Main");
            addAll(args);
        }};
        ProcessBuilder pb = new ProcessBuilder(command);
        try {
            Process java = pb.start();
            BufferedReader reader= new BufferedReader(new InputStreamReader(java.getErrorStream()));
            int returnCode = java.waitFor();
            if (returnCode == 0) {
                System.out.println("*** All Coverage Checks Have Passed. ***");
            } else {
                String line;
                while((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
                System.out.println("Coverage check failed. See messages above. [Exit Code = " +  returnCode + "]");
                throw new BuildException("Coverage check failed. See messages above.", null);
            }
        } catch (InterruptedException e) {
            log.error("Coverage check failed. See messages above.");
            throw new BuildException("Coverage check failed. See messages above.", e);
        } catch (IOException e) {
            log.error("Coverage check failed. See messages above.");
            throw new BuildException("Coverage check failed. See messages above.", e);
        }
    }

    public void generateCoverageReport(String datafile, String destination, String format,
            List<String> sourceDirectories) throws Exception {
        List<String> args = new ArrayList<String>();
        args.add("--datafile");
        args.add(datafile);
        args.add("--format");
        args.add(format);
        args.add("--destination");
        args.add(destination);
        args.addAll(sourceDirectories);
        net.sourceforge.cobertura.reporting.Main.main(args.toArray(new String[args.size()]));
    }
}
