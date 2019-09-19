package io.github.finefuture.dynamic.bean;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author longqiang
 * @version 1.0
 */
public class ReferenceCounter {

    private static final Map<String, DependentInfo> DEPENDENT_INFO = new HashMap<>();

    private static DefaultListableBeanFactory beanFactory;

    static void initBeanFactory(DefaultListableBeanFactory beanFactory) {
        ReferenceCounter.beanFactory = beanFactory;
    }

    static void registerDependentInfo(DynamicBean dynamicBean) {
        String[] dependsOn = dynamicBean.dependsOn();
        for (String beanName : dependsOn) {
            if (beanFactory.containsBean(beanName)) {
                DependentInfo dependentInfo = DEPENDENT_INFO.computeIfAbsent(beanName, DependentInfo::new);
                dependentInfo.registerCount();
            }
        }
    }

    static void refreshed(String beanName) {
        DependentInfo dependentInfo = DEPENDENT_INFO.get(beanName);
        if (Objects.nonNull(dependentInfo)) {
            dependentInfo.setRefreshed(true);
        }
    }

    static void initComplete(DynamicBean dynamicBean) {
        String[] dependsOn = dynamicBean.dependsOn();
        for (String beanName : dependsOn) {
            DependentInfo dependentInfo = DEPENDENT_INFO.get(beanName);
            if (Objects.nonNull(dependentInfo)) {
                dependentInfo.decrementCount();
            }
        }
    }

    static boolean canReplace(DynamicBean dynamicBean) {
        String[] dependsOn = dynamicBean.dependsOn();
        for (String beanName : dependsOn) {
            DependentInfo dependentInfo = DEPENDENT_INFO.get(beanName);
            if (Objects.nonNull(dependentInfo) && !dependentInfo.getRefreshed()) {
                return false;
            }
        }
        return true;
    }

}
