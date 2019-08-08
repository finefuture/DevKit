package io.github.finefuture.dynamic.cache.expiry;

/**
 * Expiry Provider SPI
 *
 * @author longqiang
 */
public interface ExpiryProvider {

    String CREATE_EXPIRE = "-create-expire";
    String UPDATE_EXPIRE = "-update-expire";
    String READ_EXPIRE = "-read-expire";
    String DEFAULT_CREATE_EXPIRE = "default-create-expire";

    /**
     * Get expiration time after cache creation through configuration center
     *
     * @param configKey the key of config center
     * @return long Time in nanos
     */
    long getCreateExpire(String configKey);

    /**
     * Get expiration time after cache modification through configuration center
     *
     * @param configKey the key of config center
     * @param currentDuration Residual expiration time
     * @return long Time in nanos
     */
    long getUpdateExpire(String configKey, long currentDuration);

    /**
     * Getting the expiration time after cache reading through the configuration center
     *
     * @param configKey the key of config center
     * @param currentDuration Residual expiration time
     * @return long Time in nanos
     */
    long getReadExpire(String configKey, long currentDuration);

}
