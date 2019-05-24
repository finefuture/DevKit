package io.github.dynacache.core;

import com.github.benmanes.caffeine.cache.CacheLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cache Loader
 *
 * @author longqiang
 */
public class CacheLoad implements CacheLoader<KeyAndArgs, Object> {

    private static final Logger log = LoggerFactory.getLogger(CacheLoad.class);

    @Override
    public Object load(KeyAndArgs key) {
        try {
            return key.invoke();
        } catch (Throwable throwable) {
            log.error("[CacheLoad] load cache error, key:{}, exception:{}", key, throwable);
            return null;
        }
    }
}
