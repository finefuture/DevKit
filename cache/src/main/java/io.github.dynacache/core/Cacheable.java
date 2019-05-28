package io.github.dynacache.core;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * cache annotation of class type,
 * the value corresponds to the cache manager,
 * Other values correspond to the creation parameters of the cache manager.
 * Asynchronous caching does not support weak and soft references.
 * The same value corresponds to different cache manager between asynchronous and non-asynchronous.
 *
 * @author longqiang
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Cacheable {

    String value();

    int initialCapacity() default 16;

    long maximumSize() default Short.MAX_VALUE;

    long maximumWeight() default -1;

    boolean weakKeys() default false;

    boolean weakValues() default false;

    boolean softValues() default false;

    /**
     * long Time in nanos
     */
    long refreshAfterWrite() default -1;

    boolean recordStats() default false;

}
