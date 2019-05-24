package io.github.dynacache.core;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.github.benmanes.caffeine.cache.LoadingCache;
import io.github.dynacache.expiry.ExpiryStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

/**
 * Caffeine Manager
 * The primary responsibility is to create a cache manager and obtain cached values.
 *
 * @author longqiang
 */
public final class CaffeineManager {

    private static final Logger logger = LoggerFactory.getLogger(CaffeineManager.class);
    private static final Map<String, AsyncLoadingCache<KeyAndArgs, Object>> ASYNC_CACHE_MAP = new ConcurrentHashMap<>(16);
    private static final Map<String, LoadingCache<KeyAndArgs, Object>> SYNC_CACHE_MAP = new ConcurrentHashMap<>(16);
    private static final Expiry<KeyAndArgs, Object> EXPIRY = new ExpiryStrategy();
    private static final CacheLoad CACHE_LOAD = new CacheLoad();

    private static LoadingCache<KeyAndArgs, Object> buildSync(Cacheable cacheable) {
        Caffeine<KeyAndArgs, Object> builder = newBuilder(cacheable);
        if (cacheable.softValues()) {
            builder.softValues();
        }
        if (cacheable.weakKeys()) {
            builder.weakKeys();
        }
        if (cacheable.weakValues()) {
            builder.weakValues();
        }
        return builder.build(CACHE_LOAD);
    }

    private static AsyncLoadingCache<KeyAndArgs, Object> buildAsync(Cacheable cacheable) {
        Caffeine<KeyAndArgs, Object> builder = newBuilder(cacheable);
        return builder.buildAsync(CACHE_LOAD);
    }

    private static Caffeine<KeyAndArgs, Object> newBuilder(Cacheable cacheable) {
        Caffeine<KeyAndArgs, Object> builder = Caffeine.newBuilder()
                                                        .expireAfter(EXPIRY);
        if (cacheable.initialCapacity() > 0) {
            builder.initialCapacity(cacheable.initialCapacity());
        }
        if (cacheable.maximumSize() > 0) {
            builder.maximumSize(cacheable.maximumSize());
        }
        if (cacheable.maximumWeight() > 0) {
            builder.maximumWeight(cacheable.maximumWeight());
        }
        return builder;
    }

    private static AsyncLoadingCache<KeyAndArgs, Object> getOrCreateAsyncCache(Object target) {
        Cacheable cacheable = target.getClass().getAnnotation(Cacheable.class);
        return ASYNC_CACHE_MAP.computeIfAbsent(cacheable.value(), k -> buildAsync(cacheable));
    }

    private static LoadingCache<KeyAndArgs, Object> getOrCreateSyncCache(Object target) {
        Cacheable cacheable = target.getClass().getAnnotation(Cacheable.class);
        return SYNC_CACHE_MAP.computeIfAbsent(cacheable.value(), k -> buildSync(cacheable));
    }

    public static <T> T get(KeyAndArgs key) {
        if (key.isAsync()) {
            return getAsync(key);
        }
        return getSync(key);
    }

    @SuppressWarnings("unchecked")
    private static <T> T getAsync(KeyAndArgs key) {
        try {
            return (T) getOrCreateAsyncCache(key.getTarget()).get(key).get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("[LocalCache] 异步执行异常: key:{}, exception:{}", key, e);
            return null;
        } catch (ClassCastException e) {
            logger.error("[LocalCache] 类型转换异常,获取了错误的缓存: key:{}, exception:{}", key, e);
            return null;
        } catch (Exception e) {
            logger.error("[LocalCache] 获取缓存异常: key:{}, exception:{}", key, e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T getSync(KeyAndArgs key) {
        try {
            return (T) getOrCreateSyncCache(key.getTarget()).get(key);
        } catch (ClassCastException e) {
            logger.error("[LocalCache] 类型转换异常,获取了错误的缓存: key:{}, exception:{}", key, e);
            return null;
        } catch (Exception e) {
            logger.error("[LocalCache] 获取缓存异常: key:{}, exception:{}", key, e);
            return null;
        }
    }

}
