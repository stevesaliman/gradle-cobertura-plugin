package net.saliman.gradle.plugin.cobertura;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.cobertura.instrument.Main;

/**
 * Wrapper for Cobertura's main class.
 */
public class CoberturaRunner {

    public void instrument(String basedir, String datafile, String destination, List<String> ignore,
            List<String> includeClasses, List<String> excludeClasses, List<String> instrument) {
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
        args.addAll(instrument);
        Main.main(args.toArray(new String[args.size()]));
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
