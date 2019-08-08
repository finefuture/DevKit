package io.github.finefuture.devKit.core.spi;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@see <a href="https://github.com/alibaba/Sentinel">Sentinel</a>}
 *
 * @author Eric Zhao
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SpiOrder {

    int LOWEST_PRECEDENCE = Integer.MAX_VALUE;

    int HIGHEST_PRECEDENCE = Integer.MIN_VALUE;

    int value() default LOWEST_PRECEDENCE;
}
