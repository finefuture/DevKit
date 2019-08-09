package io.github.finefuture.dynamic.bean;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * @author longqiang
 * @version 1.0
 */
public interface ConfigService {

    /**
     * add config change listener
     *
     * @param dynamicBean Annotation of DynamicBean
     * @param beanName spring bean name
     * @param invocation spring aop MethodInvocation
     * @param beanFactory spring bean factory
     */
    void addChangeListener(DynamicBean dynamicBean, String beanName, MethodInvocation invocation, DefaultListableBeanFactory beanFactory);

}
