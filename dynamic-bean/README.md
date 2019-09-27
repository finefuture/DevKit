# dynamic-bean

## 0. 概述

通过修改配置中心配置的诸如Redis、RabbitMQ等中间件的url、password之类的连接信息达到动态重启Spring factory中客户端的效果

## 1. 如何使用

### 1.1 如何引入maven依赖

```xml
<dependency>
    <groupId>io.github.finefuture</groupId>
    <artifactId>dynamic-bean</artifactId>
    <version>1.0.3.RELEASE</version>
</dependency>
```

### 1.2 如何使用

#### 1.2.1 使用前提

##### Apollo配置中心
 
1. 首先需要启动apollo配置中心,集群或单机   
2. 在业务项目启动时加入jvm参数：-Ddev_meta=http://localhost:10005 -Denv=DEV -Dapp.id=10000021312
3. 在apollo配置中心跟上面指定的env、appId对应的配置页，在相应namespace下修改连接信息等配置项

jvm启动参数说明:

| 参数 | 作用 |
|--------|--------|
|`-Ddev_meta=http://localhost:10005`|指定apollo的config server地址|
|`-Denv=DEV`|指定使用的apollo环境|
|`-Dapp.id=10000021312`|指定使用的apollo appId|

以Redis为例

| 配置项 | 值 |
|--------|--------|
|`redis.optional.url`|127.0.0.1:6379|
|`redis.password`|longqiang|


##### 其他配置中心(比如Nacos等)自行配置，跟上述apollo配置中心类似



#### 1.2.2 实例代码(Spring Boot)
使用
```java
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
```

验证
```java
@Service
public class DynamicBeanTestService {

    @Autowired
    private RedissonClient redissonClient;

    public List<String> getNewRedissonAddress() {
        Collection<ClusterNode> nodes = redissonClient.getClusterNodesGroup().getNodes();
        List<String> newRedissonAddresses = Lists.newArrayList();
        nodes.forEach(node -> newRedissonAddresses.add(node.getAddr().toString()));
        return newRedissonAddresses;
    }

}
```


## 2. 详解

### 2.1 @DynamicBean 注解详解
```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DynamicBean {

    String[] keys();

    String beanName() default "";

    String namespace() default "application";

    boolean resetArguments() default false;

    String[] dependsOn() default {};

    long waitTime() default 1000;

}
```
- keys:配置中心关注的配置项
- beanName:Spring BeanFactory中的bean名称, 默认方法名
- namespace:配置项所在的namespace
- resetArguments:是否重置方法参数(无方法参数可以不用理会), Spring在加载bean的时候会缓存方法参数, 不重置会导致无法引用到Spring BeanFactory中最新的bean实例
- dependsOn:重启客户端所依赖的其它重启完成的bean, 可以保证Spring BeanFactory重启客户端时加载bean的顺序
- waitTime:针对dependsOn参数设置的等待时间, 超过等待时间时将不会保证bean加载的顺序, 单位为毫秒, 默认1秒
