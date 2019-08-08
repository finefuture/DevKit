package io.github.finefuture.dynamic.cache.core;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.core.Ordered;

import java.lang.reflect.Method;

/**
 * Spring PointcutAdvisor
 *
 * @author longqiang
 */
public class CachePointcutAdvisor extends DefaultPointcutAdvisor {

    public CachePointcutAdvisor() {
        setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    @Override
    public Pointcut getPointcut() {
        return new AnnotationMatchingPointcut(Cacheable.class, Cache.class);
    }

    @Override
    public Advice getAdvice() {
        return (MethodInterceptor) invocation -> {
            Method method = invocation.getMethod();
            Cache cache = method.getAnnotation(Cache.class);
            SpringKeyAndArgs keyAndArgs = SpringKeyAndArgs.newBuilder()
                                                            .setArgs(invocation.getArguments())
                                                            .setTarget(invocation.getThis())
                                                            .setProxy(invocation)
                                                            .buildWithAnnotation(cache);
            return CaffeineManager.get(keyAndArgs);
        };
    }
}
