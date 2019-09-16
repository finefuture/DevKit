package io.github.finefuture.dynamic.bean;

import io.github.finefuture.devkit.core.spi.SpiLoader;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ReflectiveMethodInvocation;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author longqiang
 * @version 1.0
 */
public class DrsoMethodInterceptor implements MethodInterceptor, Serializable {

    private static final long serialVersionUID = -5178689205648376475L;

    private DefaultListableBeanFactory beanFactory;

    private ConfigService configService;

    private Set<String> factoryBeanSet;

    DrsoMethodInterceptor(DefaultListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        this.configService = SpiLoader.loadHighestPriorityInstance(ConfigService.class);
        this.factoryBeanSet = new LinkedHashSet<>();
    }

    Set<String> getFactoryBeanSet() {
        return factoryBeanSet;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        String[] factoryBeanName = beanFactory.getBeanNamesForType(((ReflectiveMethodInvocation) invocation).getProxy().getClass());
        factoryBeanSet.addAll(Arrays.asList(factoryBeanName));
        DynamicBean dynamicBean = method.getAnnotation(DynamicBean.class);
        String beanName = dynamicBean.beanName().isEmpty() ? resolveBeanName(method) : dynamicBean.beanName();
        configService.addChangeListener(dynamicBean, beanName, invocation, beanFactory);
        return invocation.proceed();
    }

    private String resolveBeanName(Method method) {
        return method.getName();
    }

}
