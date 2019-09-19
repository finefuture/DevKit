package io.github.finefuture.dynamic.bean;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author longqiang
 * @version 1.0
 */
public class DrsoPointcut implements Pointcut, Serializable {

    private static final long serialVersionUID = -1845292410328616601L;

    private static Set<Class<?>> factoryBeanSet = new LinkedHashSet<>();

    @Override
    public ClassFilter getClassFilter() {
        return DrsoClassFilter.INSTANCE;
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return DrsoMethodMatcher.INSTANCE;
    }

    private enum DrsoClassFilter implements ClassFilter {
        /**
         * singleton filter
         */
        INSTANCE;

        @Override
        public boolean matches(Class<?> clazz) {
            Class<?> superclass = clazz.getSuperclass();
            return superclass.isAnnotationPresent(Configuration.class) && factoryBeanSet.add(superclass);
        }
    }

    private enum DrsoMethodMatcher implements MethodMatcher {
        /**
         * singleton matcher
         */
        INSTANCE;

        @Override
        public boolean matches(Method method, Class<?> targetClass) {
            if (method.isAnnotationPresent(DynamicBean.class)) {
                return true;
            }
            Method specificMethod = AopUtils.getMostSpecificMethod(method, targetClass);
            return (specificMethod != method && specificMethod.isAnnotationPresent(DynamicBean.class));
        }

        @Override
        public boolean isRuntime() {
            return false;
        }

        @Override
        public boolean matches(Method method, Class<?> targetClass, Object... args) {
            throw new UnsupportedOperationException("This is not a runtime method matcher");
        }
    }
}
