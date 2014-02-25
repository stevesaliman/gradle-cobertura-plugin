package net.saliman.gradle.plugin.cobertura

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertNull
import static org.junit.Assert.assertTrue
import static org.junit.Assert.assertEquals

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

	/**
	 * Try setting the coverage check branch rate to a valid number, then to null.
	 * This should be fine.
	 */
	@Test
	void setBranchRateValid() {
		extension.coverageCheckBranchRate = 10
		assertEquals("Failed to set the coverageCheckBranchRate", 10, extension.coverageCheckBranchRate)
		extension.coverageCheckBranchRate = null
		assertNull("Failed to clear the coverageCheckBranchRate", extension.coverageCheckBranchRate)
	}

	/**
	 * Try setting the coverage check branch rate to a number less than 0
	 */
	@Test(expected=IllegalArgumentException)
	void setBranchRateTooLow() {
		extension.coverageCheckBranchRate = -1
	}

	/**
	 * Try setting the coverage check branch rate to a number less than 0
	 */
	@Test(expected=IllegalArgumentException)
	void setBranchRateTooHigh() {
		extension.coverageCheckBranchRate = 101
	}

	/**
	 * Try setting the coverage check line rate to a valid number, then to null.
	 * This should be fine.
	 */
	@Test
	void setLineRateValid() {
		extension.coverageCheckLineRate = 10
		assertEquals("Failed to set the coverageCheckLineRate", 10, extension.coverageCheckLineRate)
		extension.coverageCheckLineRate = null
		assertNull("Failed to clear the coverageCheckLineRate", extension.coverageCheckLineRate)
	}

	/**
	 * Try setting the coverage check line rate to a number less than 0
	 */
	@Test(expected=IllegalArgumentException)
	void setLineRateTooLow() {
		extension.coverageCheckLineRate = -1
	}

	/**
	 * Try setting the coverage check line rate to a number less than 0
	 */
	@Test(expected=IllegalArgumentException)
	void setLineRateTooHigh() {
		extension.coverageCheckLineRate = 101
	}

	/**
	 * Try setting the coverage check package branch rate to a valid number, then
	 * to null. This should be fine.
	 */
	@Test
	void setPackageBranchRateValid() {
		extension.coverageCheckPackageBranchRate = 10
		assertEquals("Failed to set the coverageCheckPackageBranchRate", 10, extension.coverageCheckPackageBranchRate)
		extension.coverageCheckPackageBranchRate = null
		assertNull("Failed to clear the coverageCheckPackageBranchRate", extension.coverageCheckPackageBranchRate)
	}

	/**
	 * Try setting the coverage check package branch rate to a number less than 0
	 */
	@Test(expected=IllegalArgumentException)
	void setPackageBranchRateTooLow() {
		extension.coverageCheckPackageBranchRate = -1
	}

	/**
	 * Try setting the coverage check package branch rate to a number less than 0
	 */
	@Test(expected=IllegalArgumentException)
	void setPackageBranchRateTooHigh() {
		extension.coverageCheckPackageBranchRate = 101
	}

	/**
	 * Try setting the coverage check package line rate to a valid number, then
	 * to null. This should be fine.
	 */
	@Test
	void setPackageLineRateValid() {
		extension.coverageCheckPackageLineRate = 10
		assertEquals("Failed to set the coverageCheckPackageLineRate", 10, extension.coverageCheckPackageLineRate)
		extension.coverageCheckPackageLineRate = null
		assertNull("Failed to clear the coverageCheckPackageLineRate", extension.coverageCheckPackageLineRate)
	}

	/**
	 * Try setting the coverage check package line rate to a number less than 0
	 */
	@Test(expected=IllegalArgumentException)
	void setPackageLineRateTooLow() {
		extension.coverageCheckPackageLineRate = -1
	}

	/**
	 * Try setting the coverage check package line rate to a number less than 0
	 */
	@Test(expected=IllegalArgumentException)
	void setPackageLineRateTooHigh() {
		extension.coverageCheckPackageLineRate = 101
	}

	/**
	 * Try setting the coverage check total branch rate to a valid number, then
	 * to null. This should be fine.
	 */
	@Test
	void setTotalBranchRateValid() {
		extension.coverageCheckTotalBranchRate = 10
		assertEquals("Failed to set the coverageCheckTotalBranchRate", 10, extension.coverageCheckTotalBranchRate)
		extension.coverageCheckTotalBranchRate = null
		assertNull("Failed to clear the coverageCheckTotalBranchRate", extension.coverageCheckTotalBranchRate)
	}

	/**
	 * Try setting the coverage check total branch rate to a number less than 0
	 */
	@Test(expected=IllegalArgumentException)
	void setTotalBranchRateTooLow() {
		extension.coverageCheckTotalBranchRate = -1
	}

	/**
	 * Try setting the coverage check total branch rate to a number less than 0
	 */
	@Test(expected=IllegalArgumentException)
	void setTotalBranchRateTooHigh() {
		extension.coverageCheckTotalBranchRate = 101
	}

	/**
	 * Try setting the coverage check total line rate to a valid number, then to null.
	 * This should be fine.
	 */
	@Test
	void setTotalLineRateValid() {
		extension.coverageCheckTotalLineRate = 10
		assertEquals("Failed to set the coverageCheckTotalLineRate", 10, extension.coverageCheckTotalLineRate)
		extension.coverageCheckTotalLineRate = null
		assertNull("Failed to clear the coverageCheckTotalLineRate", extension.coverageCheckTotalLineRate)
	}

	/**
	 * Try setting the coverage check total line rate to a number less than 0
	 */
	@Test(expected=IllegalArgumentException)
	void setTotalLineRateTooLow() {
		extension.coverageCheckTotalLineRate = -1
	}

	/**
	 * Try setting the coverage check total line rate to a number less than 0
	 */
	@Test(expected=IllegalArgumentException)
	void setTotalLineRateTooHigh() {
		extension.coverageCheckTotalLineRate = 101
	}

	/**
	 * Try setting an empty list of regexes. This is valid.
	 */
	@Test
	void setCoverageCheckRegexesNull() {
		extension.coverageCheckRegexes = null;
	}

	/**
	 * Try setting an empty list of regexes.  This is unusual, but valid.
	 */
	@Test
	void setCoverageCheckRexexesEmpty() {
		extension.coverageCheckRegexes = []
		assertNotNull("Failed to set regex collection", extension.coverageCheckRegexes)
	}

	/**
	 * Try setting the lit of regexes to 2 maps, where all is well.  We'll also
	 * set the branch and line rates to the minimum and maximum allowed values.
	 */
	@Test
	void setCoverageCheckRegexes() {
		extension.coverageCheckRegexes = [
		        [ regex: 'regex one', branchRate: 0, lineRate: 0 ],
						[ regex: 'regex two', branchRate: 100, lineRate: 100 ]
		]
		assertNotNull("Failed to set regex collection", extension.coverageCheckRegexes)
	}

	/**
	 * Try setting regexes when one is missing the regex.  Expect an error.
	 */
	@Test(expected=IllegalArgumentException)
	void setCoverageCheckRegexesNullRegex() {
		extension.coverageCheckRegexes = [
						[ regex: 'regex one', branchRate: 0, lineRate: 0 ],
						[ branchRate: 100, lineRate: 100 ]
		]
	}

	/**
	 * Try setting regexes when one is missing the regex.  Expect an error.
	 */
	@Test(expected=IllegalArgumentException)
	void setCoverageCheckRegexesEmptyRegex() {
		extension.coverageCheckRegexes = [
						[ regex: 'regex one', branchRate: 0, lineRate: 0 ],
						[ regex: '', branchRate: 100, lineRate: 100 ]
		]
	}

	/**
	 * Try setting regexes when one is missing the branch rate.  This an error.
	 */
	@Test(expected=IllegalArgumentException)
	void setCoverageCheckRegexesMissingBranchRate() {
		extension.coverageCheckRegexes = [
						[ regex: 'regex one', branchRate: 0, lineRate: 0 ],
						[ regex: 'regex two', lineRate: 100 ]
		]
	}

	/**
	 * Try setting regexes when a branch rate is not a number.
	 */
	@Test(expected=IllegalArgumentException)
	void setCoverageCheckRegexesCharBranchRate() {
		extension.coverageCheckRegexes = [
						[ regex: 'regex one', branchRate: 0, lineRate: 0 ],
						[ regex: 'regex two', branchRate: 'x', lineRate: 100 ]
		]
	}

	/**
	 * Try setting regexes when a branch rate is too small.
	 */
	@Test(expected=IllegalArgumentException)
	void setCoverageCheckRegexesBranchRateTooSmall() {
		extension.coverageCheckRegexes = [
						[ regex: 'regex one', branchRate: 0, lineRate: 0 ],
						[ regex: 'regex two', branchRate: -1, lineRate: 100 ]
		]
	}
	/**
	 * Try setting regexes when a branch rate is too large.
	 */
	@Test(expected=IllegalArgumentException)
	void setCoverageCheckRegexesBranchRateTooLarge() {
		extension.coverageCheckRegexes = [
						[ regex: 'regex one', branchRate: 0, lineRate: 0 ],
						[ regex: 'regex two', branchRate: 101, lineRate: 100 ]
		]
	}

	// set 2 missing li
	// set 2 char lr
	// set 2 small lr
	// set 2 large lr
	/**
	 * Try setting regexes when one is missing the line rate.  This an error.
	 */
	@Test(expected=IllegalArgumentException)
	void setCoverageCheckRegexesMissingLineRate() {
		extension.coverageCheckRegexes = [
						[ regex: 'regex one', branchRate: 0, lineRate: 0 ],
						[ regex: 'regex two', branchRate: 100 ]
		]
	}

	/**
	 * Try setting regexes when a line rate is not a number.
	 */
	@Test(expected=IllegalArgumentException)
	void setCoverageCheckRegexesCharLineRate() {
		extension.coverageCheckRegexes = [
						[ regex: 'regex one', branchRate: 0, lineRate: 0 ],
						[ regex: 'regex two', branchRate: 100, lineRate: 'x' ]
		]
	}

	/**
	 * Try setting regexes when a line rate is too small.
	 */
	@Test(expected=IllegalArgumentException)
	void setCoverageCheckRegexesLineRateTooSmall() {
		extension.coverageCheckRegexes = [
						[ regex: 'regex one', branchRate: 0, lineRate: 0 ],
						[ regex: 'regex two', branchRate: 100, lineRate: -1 ]
		]
	}
	/**
	 * Try setting regexes when a line rate is too large.
	 */
	@Test(expected=IllegalArgumentException)
	void setCoverageCheckRegexesLineRateTooLarge() {
		extension.coverageCheckRegexes = [
						[ regex: 'regex one', branchRate: 0, lineRate: 0 ],
						[ regex: 'regex two', branchRate: 100, lineRate: 101 ]
		]
	}

	/**
	 * Try setting the to a file collection.
	 * This should be fine.
	 */
	@Test
	void setAuxiliaryClasspathValid() {
		extension.auxiliaryClasspath = project.files('tmp')
		assertEquals("Failed to set the auxiliaryClasspath", project.files('tmp'), extension.auxiliaryClasspath)
	}


}
