package io.github.dynacache.core;

import org.springframework.cglib.proxy.MethodProxy;

/**
 * Cglib extensions for caching keys
 *
 * @author longqiang
 */
public class CglibKeyAndArgs extends KeyAndArgs {

    private MethodProxy proxy;

    private CglibKeyAndArgs(String key, Object[] args, String configKey, Object target, MethodProxy proxy, boolean isAsync) {
        super(key, args, configKey, target, isAsync);
        this.proxy = proxy;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public MethodProxy getProxy() {
        return proxy;
    }

    @Override
    public Object invoke() throws Throwable {
        return proxy.invokeSuper(getTarget(), getArgs());
    }

    @Override
    public String toString() {
        return "CglibKeyAndArgs{" +
                super.toString() +
                "proxy=" + proxy +
                '}';
    }

    public static class Builder extends KeyAndArgs.Builder<Builder> {

        private MethodProxy proxy;

        public Builder setProxy(MethodProxy proxy) {
            this.proxy = proxy;
            return this;
        }

        @Override
        public CglibKeyAndArgs build() {
            return new CglibKeyAndArgs(key, args, configKey, target, proxy, isAsync);
        }

        @Override
        public CglibKeyAndArgs buildWithAnnotation(Cache cache) {
            return new CglibKeyAndArgs(cache.key(), args, cache.configKey(), target, proxy, cache.isAsync());
        }
    }

}
