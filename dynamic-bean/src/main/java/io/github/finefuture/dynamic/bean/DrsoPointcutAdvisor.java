package io.github.finefuture.dynamic.bean;

import io.github.finefuture.devKit.core.spi.SpiLoader;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.lang.reflect.Method;

/**
 * Spring PointcutAdvisor
 *
 * @author longqiang
 */
public class DrsoPointcutAdvisor extends DefaultPointcutAdvisor {

    private DefaultListableBeanFactory beanFactory;

    private ConfigService configService;

    public DrsoPointcutAdvisor(DefaultListableBeanFactory beanFactory) {
        setOrder(HIGHEST_PRECEDENCE);
        this.beanFactory = beanFactory;
        this.configService = SpiLoader.loadHighestPriorityInstance(ConfigService.class);
    }

    @Override
    public Pointcut getPointcut() {
        return new AnnotationMatchingPointcut(null, DynamicBean.class);
    }

    @Override
    public Advice getAdvice() {
        return (MethodInterceptor) invocation -> {
            Method method = invocation.getMethod();
            DynamicBean dynamicBean = method.getAnnotation(DynamicBean.class);
            String beanName = dynamicBean.beanName().isEmpty() ? resolveBeanName(method) : dynamicBean.beanName();
            configService.addChangeListener(dynamicBean, beanName, invocation, beanFactory);
            return invocation.proceed();
        };
    }

    private String resolveBeanName(Method method) {
        return method.getName();
    }
}
