package io.github.finefuture.devkit.example.dynamicbean;

import com.ctrip.framework.apollo.ConfigService;
import io.github.finefuture.dynamic.bean.DynamicBean;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * @author longqiang
 * @version 1.0
 */
@Configuration
public class BeanConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanConfig.class);

    @DynamicBean(keys = {"redis.optional.url", "redis.password"})
    @Bean
    public RedissonClient redissonClient() {
        com.ctrip.framework.apollo.Config appConfig = ConfigService.getAppConfig();
        String redisClusterUrl = appConfig.getProperty("redis.optional.url", "");
        String password = appConfig.getProperty("redis.password", "");
        LOGGER.info("redis cluster url: {}", redisClusterUrl);
        if(StringUtils.isEmpty(redisClusterUrl)){
            return null;
        }
        Config config = new Config();
        ClusterServersConfig clusterServersConfig = config.useClusterServers()
                                                          .setScanInterval(500);
        if (!StringUtils.isEmpty(password)) {
            clusterServersConfig.setPassword(password);
        }
        String[] serverArray = redisClusterUrl.split(",");
        for (String ipPort : serverArray) {
            if(!StringUtils.isEmpty(ipPort)){
                clusterServersConfig.addNodeAddress("redis://"+ipPort);
            }
        }
        RedissonClient redissonClient = null;
        try{
            redissonClient = Redisson.create(config);
        }catch (Exception e){
            LOGGER.error("[BeanConfig] create redisson client failed, {}", e);
        }
        return redissonClient;
    }
}
