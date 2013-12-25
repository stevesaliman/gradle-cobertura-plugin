package net.saliman.gradle.plugin.cobertura.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

/**
 * This class is a <b>ClassLoader</b> that loads classes before delegating to
 * a parent.
 * <p>
 * The normal behavior for a Java ClassLoader is to delegate to parent class
 * loaders and only trying to load a class if the parent couldn't find it.
 * This causes problems with Cobertura instrumentation, where we need to
 * have Cobertura's dependencies ahead of application dependencies.  The normal
 * parent-first approach can cause problems with instrumentation when Cobertura
 * needs a different version of a library.  For example, Cobertura uses a newer
 * version of ASM than Hibernate does, so if the app uses Hibernate, we need to
 * make sure Cobertura's version of ASM is used at instrument time.
 * <p>
 * We can't simply make a {@code URLClassLoader} with no parent, because then
 * instrumentation would fail on things like log4j, is not a dependency of
 * Cobertura, but is referenced in the code being instrumented.
 * <p>
 * This class needs to be a Java class.  For some reason, when it is implemented
 * as a Groovy class, I get stack overflow problems.  I have no idea why.  I
 * also have no idea why the compilation fails when the Java class is in the
 * same package as the Groovy classes that use it.
 *
 * @author Steven C. Saliman
 */
public class ChildFirstUrlClassLoader extends URLClassLoader {

	private ClassLoader system;

	public ChildFirstUrlClassLoader(URL[] classpath, ClassLoader parent) {
		super(classpath, parent);
		system = getSystemClassLoader();
	}

	@Override
	protected synchronized Class<?> loadClass(String name, boolean resolve)
			throws ClassNotFoundException {
		// First, check if the class has already been loaded
		Class<?> c = findLoadedClass(name);
		if (c == null) {
			if (system != null) {
				try {
					// checking system: jvm classes, endorsed, cmd classpath, etc.
					c = system.loadClass(name);
				}
				catch (ClassNotFoundException ignored) {
				}
			}
			if (c == null) {
				try {
					// checking local
					c = findClass(name);
				} catch (ClassNotFoundException e) {
					// checking parent
					// This call to loadClass may eventually call findClass again, in case the parent doesn't find anything.
					c = super.loadClass(name, resolve);
				}
			}
		}
		if (resolve) {
			resolveClass(c);
		}
		return c;
	}

	@Override
	public URL getResource(String name) {
		URL url = null;
		if (system != null) {
			url = system.getResource(name);
		}
		if (url == null) {
			url = findResource(name);
			if (url == null) {
				// This call to getResource may eventually call findResource again, in case the parent doesn't find anything.
				url = super.getResource(name);
			}
		}
		return url;
	}

	@Override
	public Enumeration<URL> getResources(String name) throws IOException {
		/**
		 * Similar to super, but local resources are enumerated before parent resources
		 */
		Enumeration<URL> systemUrls = null;
		if (system != null) {
			systemUrls = system.getResources(name);
		}
		Enumeration<URL> localUrls = findResources(name);
		Enumeration<URL> parentUrls = null;
		if (getParent() != null) {
			parentUrls = getParent().getResources(name);
		}
		final List<URL> urls = new ArrayList<URL>();
		if (systemUrls != null) {
			while(systemUrls.hasMoreElements()) {
				urls.add(systemUrls.nextElement());
			}
		}
		if (localUrls != null) {
			while (localUrls.hasMoreElements()) {
				urls.add(localUrls.nextElement());
			}
		}
		if (parentUrls != null) {
			while (parentUrls.hasMoreElements()) {
				urls.add(parentUrls.nextElement());
			}
		}
		return new Enumeration<URL>() {
			Iterator<URL> iter = urls.iterator();

			public boolean hasMoreElements() {
				return iter.hasNext();
			}
			public URL nextElement() {
				return iter.next();
			}
		};
	}

	@Override
	public InputStream getResourceAsStream(String name) {
		URL url = getResource(name);
		try {
			return url != null ? url.openStream() : null;
		} catch (IOException e) {
			// Nothing we can do here.
		}
		return null;
	}

}
