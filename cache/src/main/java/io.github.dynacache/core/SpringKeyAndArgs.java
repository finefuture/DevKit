package io.github.dynacache.core;

import org.aopalliance.intercept.MethodInvocation;

/**
 * Spring extensions for caching keys
 *
 * @author longqiang
 */
public class SpringKeyAndArgs extends KeyAndArgs {

    private MethodInvocation proxy;

    private SpringKeyAndArgs(String key, Object[] args, String configKey, Object target, MethodInvocation proxy, boolean isAsync) {
        super(key, args, configKey, target, isAsync);
        this.proxy = proxy;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public MethodInvocation getProxy() {
        return proxy;
    }

    @Override
    public Object invoke() throws Throwable {
        return proxy.proceed();
    }

    @Override
    public String toString() {
        return "SpringKeyAndArgs{" +
                super.toString() +
                "proxy=" + proxy +
                '}';
    }

    public static class Builder extends KeyAndArgs.Builder<Builder> {

        private MethodInvocation proxy;

        public Builder setProxy(MethodInvocation proxy) {
            this.proxy = proxy;
            return this;
        }

        @Override
        public SpringKeyAndArgs build() {
            return new SpringKeyAndArgs(key, args, configKey, target, proxy, isAsync);
        }

        @Override
        public SpringKeyAndArgs buildWithAnnotation(Cache cache) {
            return new SpringKeyAndArgs(cache.key(), args, cache.configKey(), target, proxy, cache.isAsync());
        }
    }
}
