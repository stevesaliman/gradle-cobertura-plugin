package net.saliman.gradle.plugin.cobertura

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue

/**
 * Some basic tests for the {@link CoberturaExtension} class.  The extension
 * is basically a container for properties, but we need to make sure some
 * checks happen correctly.
 *
 * @author Steven C. Saliman
 */
class CoberturaExtensionTest {
	def project = ProjectBuilder.builder().build()
	CoberturaExtension extension

	/**
	 * Set up for each test. We need to make sure the java plugin is applied, or
	 * we'll have problems when we make the extension and there are no source
	 * sets.
	 */
	@Before
	void setUp() {
		project.apply plugin: 'java'
		extension = new CoberturaExtension(project)
	}

	/**
	 * Try setting a value for coverageIgnores when we can't determine what
	 * version of Cobertura we have.  Expect an error.
	 */
	@Test(expected=IllegalArgumentException)
	void ignoreTrivialNullCobertura() {
		extension.coberturaVersion = null;
		extension.setCoverageIgnoreTrivial(true);
	}

	/**
	 * Try setting a value for coverageIgnores when we have an old version of
	 * Cobertura.Expect an error.
	 */
	@Test(expected=IllegalArgumentException)
	void ignoreTrivialOldCobertura() {
		extension.coberturaVersion = "1";
		extension.setCoverageIgnoreTrivial(true);
	}

	/**
	 * Try setting a value for coverageIgnores when we have the required version
	 * of Cobertura. We know we're good if we don't get an error.
	 */
	@Test
	void ignoreTrivial() {
		extension.coberturaVersion = "2.0.0";
		extension.setCoverageIgnoreTrivial(true);
		assertTrue("Failed to set the ignoreTrivial flag", extension.getCoverageIgnoreTrivial())
	}


	/**
	 * Try setting a value for coverageIgnoreMethodAnnotations when we can't
	 * determine what version of Cobertura we have.  Expect an error.
	 */
	@Test(expected=IllegalArgumentException)
	void ignoreMethodAnnotationsNullCobertura() {
		extension.coberturaVersion = null;
		extension.setCoverageIgnoreMethodAnnotations(new ArrayList<String>());
	}

	/**
	 * Try setting a value for coverageIgnoreMethodAnnotations when we have an
	 * old version of Cobertura.Expect an error.
	 */
	@Test(expected=IllegalArgumentException)
	void ignoreMethodAnnotationsOldCobertura() {
		extension.coberturaVersion = "1";
		extension.setCoverageIgnoreMethodAnnotations(new ArrayList<String>());
	}

	/**
	 * Try setting a value for coverageIgnoreMethodAnnotations when we have the
	 * required version of Cobertura. We know we're good if we don't get an error.
	 */
	@Test
	void ignoreMethodAnnotations() {
		extension.coberturaVersion = "2.0.0";
		extension.setCoverageIgnoreMethodAnnotations(new ArrayList<String>());
		assertNotNull("Failed to set the coverageIgnoreMethodAnnotations", extension.getCoverageIgnoreTrivial())
	}
}
