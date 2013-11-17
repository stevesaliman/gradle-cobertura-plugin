package net.saliman.gradle.plugin.cobertura

import java.security.Permission;

/**
 * Security manager to use when running Cobertura classes directly.
 * <p>
 * The Main class that implements Cobertura's coverage check functionality
 * uses {@code System.exit} when it is done which is fine for a standalone
 * utility, but bad in terms of using it from within another program like
 * a build tool.
 * <p>
 * This implementation denies access to the System.exit method, but saves
 * the exit status so that the caller can decide if the build needs to be
 * failed or not.
 *
 * @author Steven C. Saliman
 */
public class CoberturaSecurityManager extends SecurityManager {
  int exitStatus = 0
	SecurityManager delegate = null

	/**
	 * Make a new SecurityManager that delegates to another SecurityManager for
	 * all things except System.exit calls.  The delegate should be set to
	 * whatever security manager was in place before setting this one.
	 * @param oldSecurityManager the security manager to use as a delegate.
	 *
	 */
	CoberturaSecurityManager(SecurityManager delegate) {
		this.delegate = delegate
	}

	/**
	 * Throws a SecurityException because we don't want Cobertura trying to exit
	 * the VM.  Saves the exit status first so that callers can find out what
	 * the exit code would have been.
	 * @param exitStatus the exit status
	 */
	@Override
	public void checkExit(int exitStatus) {
		this.exitStatus = exitStatus;
		throw new SecurityException();
	}

	/**
	 * Throws a SecurityException if the requested access, specified by the given
	 * permission, is not permitted based on the security policy currently in
	 * effect.
	 * <p>
	 * This implementation permits everything, unless there is a delegate, in
	 * which case it does whatever the delegate requests.
	 * @param perm the requested permission
	 */
	@Override
	void checkPermission(Permission perm) {
		if ( delegate != null ) {
			delegate.checkPermission(perm)
		}
	}

	/**
	 * Throws a SecurityException if the requested access, specified by the given
	 * permission, is not permitted based on the security policy currently in
	 * effect.
	 * <p>
	 * This implementation permits everything, unless there is a delegate, in
	 * which case it does whatever the delegate requests.
	 * @param perm the requested permission
	 * @param context a system dependent security context.
	 */
	@Override
	void checkPermission(Permission perm, Object context) {
		if ( delegate != null ) {
			delegate.checkPermission(perm, context)
		}
	}
}
