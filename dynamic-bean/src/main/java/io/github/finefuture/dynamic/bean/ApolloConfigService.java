package io.github.finefuture.dynamic.bean;

import com.ctrip.framework.apollo.Config;
import io.github.finefuture.devKit.core.spi.SpiOrder;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * @author longqiang
 * @version 1.0
 * @date 2019/8/8 15:58
 */
@SpiOrder
public class ApolloConfigService implements ConfigService {

    @Override
    public void addChangeListener(DynamicBean dynamicBean, String beanName, MethodInvocation invocation, DefaultListableBeanFactory beanFactory) {
        Config config = com.ctrip.framework.apollo.ConfigService.getConfig(dynamicBean.namespace());
        config.addChangeListener(new DrsoConfigChangeListener(dynamicBean, beanName, invocation, beanFactory));
    }
}
