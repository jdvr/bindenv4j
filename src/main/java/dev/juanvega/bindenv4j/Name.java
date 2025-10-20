package dev.juanvega.bindenv4j;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Specifies the name of the environment variable to bind to a field.
 *
 * <p>This annotation allows you to map a field to an environment variable with a different name.</p>
 *
 * <p>Example of usage:
 *
 * <pre>{@code
 * public class Config {
 *   @Name("SERVER_HOST")
 *   private String host;
 * }
 * }</pre>
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Name {
    String value() default "";
}
