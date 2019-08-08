package io.github.finefuture.dynamic.bean;

import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Registry
 *
 * @author longqiang
 */
@Configuration
@ComponentScan(basePackageClasses = SpringRegistry.class)
public class SpringRegistry {

    @Bean
    public DefaultPointcutAdvisor drso(DefaultListableBeanFactory beanFactory) {
        return new DrsoPointcutAdvisor(beanFactory);
    }

}
