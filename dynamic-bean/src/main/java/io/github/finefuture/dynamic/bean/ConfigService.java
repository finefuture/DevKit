package io.github.finefuture.dynamic.bean;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * @author longqiang
 * @version 1.0
 * @date 2019/8/8 11:27
 */
public interface ConfigService {

    /**
     * add config change listener
     *
     * @param dynamicBean Annotation of DynamicBean
     * @param beanName spring bean name
     * @param invocation spring aop MethodInvocation
     * @param beanFactory spring bean factory
     * @author longqiang
     * @date 2019/8/8 16:58
     */
    void addChangeListener(DynamicBean dynamicBean, String beanName, MethodInvocation invocation, DefaultListableBeanFactory beanFactory);

}
