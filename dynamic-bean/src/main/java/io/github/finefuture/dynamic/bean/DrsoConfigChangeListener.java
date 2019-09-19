package io.github.finefuture.dynamic.bean;

import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import io.github.finefuture.devkit.core.time.Time;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author longqiang
 * @version 1.0
 */
public class DrsoConfigChangeListener implements ConfigChangeListener, Serializable {

    private static final long serialVersionUID = 1937043421879826293L;

    private static final Logger LOGGER = LoggerFactory.getLogger(DrsoConfigChangeListener.class);

    private DynamicBean dynamicBean;

    private String beanName;

    private MethodInvocation invocation;

    private static DefaultListableBeanFactory beanFactory;

    private static Map<String, Set<String>> dependentBeanMapInstance;

    private static Map<String, Set<String>> dependenciesForBeanMapInstance;

    DrsoConfigChangeListener(DynamicBean dynamicBean, String beanName, MethodInvocation invocation) {
        this.dynamicBean = dynamicBean;
        this.beanName = beanName;
        this.invocation = invocation;
    }

    @SuppressWarnings("unchecked")
    static void initBeanMap(DefaultListableBeanFactory beanFactory) {
        DrsoConfigChangeListener.beanFactory = beanFactory;
        Field dependentBeanMap = ReflectionUtils.findField(DefaultSingletonBeanRegistry.class, "dependentBeanMap");
        Field dependenciesForBeanMap = ReflectionUtils.findField(DefaultSingletonBeanRegistry.class, "dependenciesForBeanMap");
        ReflectionUtils.makeAccessible(dependentBeanMap);
        ReflectionUtils.makeAccessible(dependenciesForBeanMap);
        dependentBeanMapInstance = (Map) ReflectionUtils.getField(dependentBeanMap, beanFactory);
        dependenciesForBeanMapInstance = (Map) ReflectionUtils.getField(dependenciesForBeanMap, beanFactory);
    }

    @Override
    public void onChange(ConfigChangeEvent changeEvent) {
        if (!beanFactory.containsBean(beanName)) {
            return;
        }
        for (String key : dynamicBean.keys()) {
            if (changeEvent.isChanged(key)) {
                synchronized (dependentBeanMapInstance) {
                    long start = Time.currentElapsedTime();
                    long cur = start;
                    long end = start + dynamicBean.waitTime();
                    while (!ReferenceCounter.canReplace(dynamicBean) && cur < end) {
                        try {
                            dependentBeanMapInstance.wait(end - cur);
                        } catch (InterruptedException e) {
                            LOGGER.error("Configuration change notification interrupted, exception:{}", e);
                            Thread.currentThread().interrupt();
                        }
                        cur = Time.currentElapsedTime();
                    }
                    drso();
                    ReferenceCounter.refreshed(beanName);
                    ReferenceCounter.initComplete(dynamicBean);
                    dependentBeanMapInstance.notifyAll();
                }
                break;
            }
        }
    }

    private void drso() {
        Map<String, Set<String>> dependentBeanMapCopy = new ConcurrentHashMap<>(dependentBeanMapInstance);
        Map<String, Set<String>> dependenciesForBeanMapInstanceCopy = new ConcurrentHashMap<>(dependenciesForBeanMapInstance);
        Object bean = beanFactory.getBean(beanName);
        beanFactory.destroySingleton(beanName);
        try {
            invocation.proceed();
        } catch (Throwable throwable) {
            LOGGER.error("Dynamic replacement bean failed, exception:{}", throwable);
            beanFactory.registerSingleton(beanName, bean);
        }
        dependentBeanMapInstance.putAll(dependentBeanMapCopy);
        dependenciesForBeanMapInstance.putAll(dependenciesForBeanMapInstanceCopy);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DrsoConfigChangeListener)) {
            return false;
        }
        DrsoConfigChangeListener that = (DrsoConfigChangeListener) o;
        return Objects.equals(dynamicBean, that.dynamicBean) &&
                Objects.equals(beanName, that.beanName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dynamicBean, beanName);
    }

}
