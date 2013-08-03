package net.saliman.gradle.plugin.cobertura.testclient.logger;

/**
 * A logger class in a different project.  The code here is not really meant
 * to make sense, just give us something we can test.
 */
public class Logger {
	private static Logger instance;
	private int linesWritten;

	public int getLinesWritten() {
	    return linesWritten;
	}

	public void setLinesWritten(int linesWritten) {
	    this.linesWritten = linesWritten;
	}

	public static Logger getInstance() {
		if ( instance == null ) {
		    instance = new Logger();
		}

	    return instance;
	}

	public static void log(String text) {
		getInstance().setLinesWritten(instance.getLinesWritten() + 1);
		System.out.println(text);

	}
}
  
