package io.github.dynacache.core;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * cache annotation of method.
 * The criteria for determining cache uniqueness are key, configKey, and method parameters.
 * {@linkplain #isAsync() This will determine whether the cache is asynchronous or non-asynchronous}
 *
 * @author longqiang
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Cache {

    String key();

    String configKey() default "";

    boolean isAsync() default true;
}
