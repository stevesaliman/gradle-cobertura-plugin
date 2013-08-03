package net.saliman.gradle.plugin.cobertura.testclient.calculator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotation that we can put on a method to have it ignored by Cobertura, 
 * assuming that this annotation is passed to Cobertura at run time.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CoverageIgnore {
}
