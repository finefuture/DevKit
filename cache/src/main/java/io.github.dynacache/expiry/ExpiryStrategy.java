package io.github.dynacache.expiry;

import com.github.benmanes.caffeine.cache.Expiry;
import io.github.dynacache.core.KeyAndArgs;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Cache expiry strategy
 *
 * @author longqiang
 */
public class ExpiryStrategy implements Expiry<KeyAndArgs, Object> {

    ExpiryProvider expiryProvider;

    public ExpiryStrategy() {
        ServiceLoader<ExpiryProvider> loader = ServiceLoader.load(ExpiryProvider.class);
        Iterator<ExpiryProvider> iterator = loader.iterator();
        if (iterator.hasNext()) {
            expiryProvider = iterator.next();
        }
    }

    @Override
    public long expireAfterCreate(@NonNull KeyAndArgs key, @NonNull Object value, long currentTime) {
        return expiryProvider.getCreateExpire(key.getConfigKey());
    }

    @Override
    public long expireAfterUpdate(@NonNull KeyAndArgs key, @NonNull Object value, long currentTime, @NonNegative long currentDuration) {
        return expiryProvider.getUpdateExpire(key.getConfigKey(), currentDuration);
    }

    @Override
    public long expireAfterRead(@NonNull KeyAndArgs key, @NonNull Object value, long currentTime, @NonNegative long currentDuration) {
        return expiryProvider.getReadExpire(key.getConfigKey(), currentDuration);
    }
}
