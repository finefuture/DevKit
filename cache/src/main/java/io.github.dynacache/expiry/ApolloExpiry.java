package io.github.dynacache.expiry;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.concurrent.TimeUnit;

/**
 * Apollo Expiry
 *
 * @author longqiang
 */
public class ApolloExpiry implements ExpiryProvider {

    private static final Config CONFIG = ConfigService.getAppConfig();

    @Override
    public long getCreateExpire(String configKey) {
        String expireTime = CONFIG.getProperty(configKey + CREATE_EXPIRE, null);
        if (expireTime == null) {
            expireTime = CONFIG.getProperty(DEFAULT_CREATE_EXPIRE, null);
        }
        return TimeUnit.SECONDS.toNanos(NumberUtils.toLong(expireTime));
    }

    @Override
    public long getUpdateExpire(String configKey, long currentDuration) {
        String expireTime = CONFIG.getProperty(configKey + UPDATE_EXPIRE, null);
        return expireTime == null ? currentDuration : TimeUnit.SECONDS.toNanos(NumberUtils.toLong(expireTime));
    }

    @Override
    public long getReadExpire(String configKey, long currentDuration) {
        String expireTime = CONFIG.getProperty(configKey + READ_EXPIRE, null);
        return expireTime == null ? currentDuration : TimeUnit.SECONDS.toNanos(NumberUtils.toLong(expireTime));
    }

}
