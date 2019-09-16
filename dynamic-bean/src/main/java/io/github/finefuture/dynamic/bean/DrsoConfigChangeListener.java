package io.github.finefuture.dynamic.bean;

import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.util.CollectionUtils;
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

    private DefaultListableBeanFactory beanFactory;

    private Field dependentBeanMap;

    private Field dependenciesForBeanMap;

    DrsoConfigChangeListener(DynamicBean dynamicBean, String beanName, MethodInvocation invocation, DefaultListableBeanFactory beanFactory) {
        this.dynamicBean = dynamicBean;
        this.beanName = beanName;
        this.invocation = invocation;
        this.beanFactory = beanFactory;
        initBeanMap();
    }

    private void initBeanMap() {
        if (dynamicBean.cascade()) {
            this.dependentBeanMap = ReflectionUtils.findField(DefaultSingletonBeanRegistry.class, "dependentBeanMap");
            this.dependenciesForBeanMap = ReflectionUtils.findField(DefaultSingletonBeanRegistry.class, "dependenciesForBeanMap");
            ReflectionUtils.makeAccessible(dependentBeanMap);
            ReflectionUtils.makeAccessible(dependenciesForBeanMap);
        }
    }

    @Override
    public void onChange(ConfigChangeEvent changeEvent) {
        if (!beanFactory.containsBean(beanName)) {
            return;
        }
        for (String key : dynamicBean.keys()) {
            if (changeEvent.isChanged(key)) {
                Map<String, Set<String>> dependentBeanMapCopy = new ConcurrentHashMap<>(256);
                Map<String, Set<String>> dependenciesForBeanMapInstanceCopy = new ConcurrentHashMap<>(256);
                copyBeanMap(dependentBeanMapCopy, dependenciesForBeanMapInstanceCopy);
                Object bean = beanFactory.getBean(beanName);
                beanFactory.destroySingleton(beanName);
                try {
                    invocation.proceed();
                } catch (Throwable throwable) {
                    LOGGER.error("Dynamic replacement bean failed, exception:{}", throwable);
                    beanFactory.registerSingleton(beanName, bean);
                }
                resetBeanMap(dependentBeanMapCopy, dependenciesForBeanMapInstanceCopy);
                break;
            }
        }
    }

    private void copyBeanMap(Map<String, Set<String>> dependentBeanMapCopy, Map<String, Set<String>> dependenciesForBeanMapInstanceCopy) {
        if (dynamicBean.cascade() && Objects.nonNull(dependentBeanMapCopy) && Objects.nonNull(dependenciesForBeanMapInstanceCopy)) {
            Map<String, Set<String>> dependentBeanMapInstance = (Map) ReflectionUtils.getField(dependentBeanMap, beanFactory);
            Map<String, Set<String>> dependenciesForBeanMapInstance = (Map) ReflectionUtils.getField(dependenciesForBeanMap, beanFactory);
            dependentBeanMapCopy.putAll(dependentBeanMapInstance);
            dependenciesForBeanMapInstanceCopy.putAll(dependenciesForBeanMapInstance);
        }
    }

    private void resetBeanMap(Map<String, Set<String>> dependentBeanMapCopy, Map<String, Set<String>> dependenciesForBeanMapInstanceCopy) {
        if (dynamicBean.cascade() && !CollectionUtils.isEmpty(dependentBeanMapCopy)
                && !CollectionUtils.isEmpty(dependenciesForBeanMapInstanceCopy)) {
            ReflectionUtils.setField(dependentBeanMap, beanFactory, dependentBeanMapCopy);
            ReflectionUtils.setField(dependenciesForBeanMap, beanFactory, dependenciesForBeanMapInstanceCopy);
        }
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
                Objects.equals(beanName, that.beanName) &&
                Objects.equals(beanFactory, that.beanFactory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dynamicBean, beanName, beanFactory);
    }

}
