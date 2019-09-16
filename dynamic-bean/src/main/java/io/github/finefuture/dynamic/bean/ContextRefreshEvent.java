package io.github.finefuture.dynamic.bean;

import org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author longqiang
 * @version 1.0
 */
public class ContextRefreshEvent implements ApplicationListener<ContextRefreshedEvent> {

    private DefaultListableBeanFactory beanFactory;

    private DrsoPointcutAdvisor drso;

    public ContextRefreshEvent(DefaultListableBeanFactory beanFactory, DrsoPointcutAdvisor drso) {
        this.beanFactory = beanFactory;
        this.drso = drso;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        beanFactory.destroySingleton("drso");
        beanFactory.removeBeanDefinition("drso");
        initAdvisor();
        drso.getFactoryBeanSet().forEach(beanName -> {
            beanFactory.destroySingleton(beanName);
            beanFactory.getBean(beanName);
        });
    }

    private void initAdvisor() {
        Class<AbstractAdvisorAutoProxyCreator> clazz = AbstractAdvisorAutoProxyCreator.class;
        Map<String, ? extends AbstractAdvisorAutoProxyCreator> advisorBeans = beanFactory.getBeansOfType(clazz);
        advisorBeans.forEach((BiConsumer<String, AbstractAdvisorAutoProxyCreator>) (s, advisorAutoProxyCreator) -> {
            Method initBeanFactoryMethod = ReflectionUtils.findMethod(clazz, "initBeanFactory", ConfigurableListableBeanFactory.class);
            ReflectionUtils.makeAccessible(initBeanFactoryMethod);
            ReflectionUtils.invokeMethod(initBeanFactoryMethod, advisorAutoProxyCreator, beanFactory);
        });
    }

}
