package io.github.dynacache.core;

import com.google.common.base.Preconditions;

import java.util.Arrays;
import java.util.Objects;

/**
 * Cache key
 *
 * @author longqiang
 */
public class KeyAndArgs {

    private String key;

    private Object[] args;

    private String configKey;

    private Object target;

    private boolean isAsync;

    protected KeyAndArgs(String key, Object[] args, String configKey, Object target, boolean isAsync) {
        this.key = key;
        this.args = args;
        this.configKey = configKey;
        this.target = target;
        this.isAsync = isAsync;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getKey() {
        return key;
    }

    public Object[] getArgs() {
        return args;
    }

    public String getConfigKey() {
        return configKey;
    }

    public Object getTarget() {
        return target;
    }

    public boolean isAsync() {
        return isAsync;
    }

    public Object invoke() throws Throwable {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KeyAndArgs)) {
            return false;
        }
        KeyAndArgs that = (KeyAndArgs) o;
        return Objects.equals(getKey(), that.getKey()) &&
                Arrays.equals(getArgs(), that.getArgs()) &&
                Objects.equals(getConfigKey(), that.getConfigKey());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getKey(), getConfigKey());
        result = 31 * result + Arrays.hashCode(getArgs());
        return result;
    }

    @Override
    public String toString() {
        return "KeyAndArgs{" +
                "key='" + key + '\'' +
                ", args=" + Arrays.toString(args) +
                ", configKey='" + configKey + '\'' +
                ", target=" + target +
                ", isAsync=" + isAsync +
                '}';
    }

    protected static class Builder<T extends Builder<T>>{

        protected String key;

        protected Object[] args;

        protected String configKey;

        protected Object target;

        protected boolean isAsync = true;

        public T setKey(String key) {
            this.key = Preconditions.checkNotNull(key);
            return (T) this;
        }

        public T setArgs(Object[] args) {
            this.args = Preconditions.checkNotNull(args);
            return (T) this;
        }

        public T setConfigKey(String configKey) {
            this.configKey = configKey;
            return (T) this;
        }

        public T setTarget(Object target) {
            this.target = target;
            return (T) this;
        }

        public T setAsync(boolean isAsync) {
            this.isAsync = isAsync;
            return (T) this;
        }

        protected KeyAndArgs build() {
            return new KeyAndArgs(key, args, configKey, target, isAsync);
        }

        protected KeyAndArgs buildWithAnnotation(Cache cache) {
            return new KeyAndArgs(cache.key(), args, cache.configKey(), target, cache.isAsync());
        }
    }
}
