package io.github.finefuture.dynamic.bean;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.util.Objects;
import java.util.Set;

/**
 * Spring PointcutAdvisor
 *
 * @author longqiang
 */
public class DrsoPointcutAdvisor extends DefaultPointcutAdvisor {

    private static final long serialVersionUID = 3581110452529715632L;

    private DefaultListableBeanFactory beanFactory;

    private DrsoPointcut drsoPointcut;

    private DrsoMethodInterceptor interceptor;

    DrsoPointcutAdvisor(DefaultListableBeanFactory beanFactory) {
        setOrder(0);
        this.beanFactory = beanFactory;
        this.drsoPointcut = new DrsoPointcut();
        this.interceptor = new DrsoMethodInterceptor(beanFactory);
        DrsoConfigChangeListener.initBeanMap(beanFactory);
    }

    Set<String> getFactoryBeanSet() {
        return interceptor.getFactoryBeanSet();
    }

    @Override
    public Pointcut getPointcut() {
        return drsoPointcut;
    }

    @Override
    public Advice getAdvice() {
        return interceptor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DrsoPointcutAdvisor)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        DrsoPointcutAdvisor that = (DrsoPointcutAdvisor) o;
        return Objects.equals(beanFactory, that.beanFactory) &&
                Objects.equals(drsoPointcut, that.drsoPointcut) &&
                Objects.equals(interceptor, that.interceptor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), beanFactory, drsoPointcut, interceptor);
    }
}
