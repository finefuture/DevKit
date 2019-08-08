package io.github.finefuture.dynamic.cache.expiry;

import com.github.benmanes.caffeine.cache.Expiry;
import io.github.finefuture.devKit.core.spi.SpiLoader;
import io.github.finefuture.dynamic.cache.core.KeyAndArgs;
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

    private ExpiryProvider expiryProvider;

    public ExpiryStrategy() {
        expiryProvider = SpiLoader.loadHighestPriorityInstance(ExpiryProvider.class);
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
