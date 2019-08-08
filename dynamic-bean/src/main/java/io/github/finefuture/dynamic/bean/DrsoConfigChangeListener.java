package io.github.finefuture.dynamic.bean;

import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.util.Objects;

/**
 * @author longqiang
 * @version 1.0
 * @date 2019/8/6 16:23
 */
public class DrsoConfigChangeListener implements ConfigChangeListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(DrsoConfigChangeListener.class);

    private DynamicBean dynamicBean;

    private String beanName;

    private MethodInvocation invocation;

    private DefaultListableBeanFactory beanFactory;

    DrsoConfigChangeListener(DynamicBean dynamicBean, String beanName, MethodInvocation invocation, DefaultListableBeanFactory beanFactory) {
        this.dynamicBean = dynamicBean;
        this.beanName = beanName;
        this.invocation = invocation;
        this.beanFactory = beanFactory;
    }

    @Override
    public void onChange(ConfigChangeEvent changeEvent) {
        if (!beanFactory.containsBean(beanName)) {
            return;
        }
        for (String key : dynamicBean.keys()) {
            if (changeEvent.isChanged(key)) {
                Object bean = beanFactory.getBean(beanName);
                beanFactory.destroySingleton(beanName);
                try {
                    invocation.proceed();
                } catch (Throwable throwable) {
                    LOGGER.error("Dynamic replacement bean failed, exception:{}", throwable);
                    beanFactory.registerSingleton(beanName, bean);
                }
                break;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DrsoConfigChangeListener)) return false;
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
