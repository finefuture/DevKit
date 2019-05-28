# Dynacache 动态缓存

## 0. 概述

Dynacache是动态缓存的一种实现，通过SPI机制可以制定特殊的过期策略。

## 1. 如何使用

### 1.1 如何引入maven依赖

```xml
<dependency>
    <groupId>io.github.finefuture</groupId>
    <artifactId>cache</artifactId>
    <version>1.0.1.RELEASE</version>
</dependency>
```

### 1.2 如何使用

#### 1.2.1 使用前提

##### Apollo配置中心
 
1. 首先需要启动apollo配置中心,集群或单机   
2. 在业务项目启动时加入jvm参数：-Ddev_meta=http://localhost:10005 -Denv=DEV -Dapp.id=10000021312
3. 在apollo配置中心跟上面指定的env、appId对应的配置页，在namespace为application下添加相关的缓存过期时间配置项

jvm启动参数说明:

| 参数 | 作用 |
|--------|--------|
|`-Ddev_meta=http://localhost:10005`|指定apollo的config server地址|
|`-Denv=DEV`|指定使用的apollo环境|
|`-Dapp.id=10000021312`|指定使用的apollo appId|
apollo缓存配置项说明:前缀就是@Cache注解的configKey,默认是""

| 后缀 | 作用 |
|--------|--------|
|`-create-expire`|缓存在创建之后指定过期时间|
|`-update-expire`|缓存在更新之后指定过期时间|
|`-read-expire`|缓存在读取之后指定过期时间|

| 特殊配置项 | 作用 |
|--------|--------|
|`default-create-expire`|缓存创建之后默认的过期时间|

##### 其他配置中心(比如Nacos等)自行配置，跟上述apollo配置中心类似



#### 1.2.2 SpringBoot环境

```java
@Service
@Cacheable("lo")
public class TService {

    @Cache(key = "loj1", configKey = "loj1")
    public CompletableFuture<Integer> lojAsync1(int bound)  {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return CompletableFuture.completedFuture(new Random().nextInt(bound));
    }

    @Cache(key = "loj2", configKey = "loj2")
    public Object lojAsync2(int bound)  {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new Random().nextInt(bound);
    }

    @Cache(key = "loj3", configKey = "loj3", isAsync = false)
    public Integer lojSync(int bound)  {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new Random().nextInt(bound);
    }

}

@RestController
@RequestMapping("test")
public class Controller {

    @Autowired
    TService tService;

    @GetMapping("async1")
    public Integer test1(int bound) throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> loj = tService.lojAsync1(bound);
        while (!loj.isDone()) {
            //you can do something before future is done
        }
        return loj.get();
    }

    @GetMapping("async2")
    public Integer test2(int bound) throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> loj = (CompletableFuture<Integer>) tService.lojAsync2(bound);
        while (!loj.isDone()) {
            //you can do something before future is done
        }
        return loj.get();
    }

    @GetMapping("sync1")
    public Integer test3(int bound) {
        return tService.lojSync(bound);
    }

}
```

如上代码，将@Cacheable注解写在有@Service或者@Configuration等Spring将实例注入容器注解过的类上，然后将@Cache写在该类中的方法上，这样就可以使用了

#### 1.2.3 非SpringBoot环境

```java
@Cacheable("lo")
public class TService {

    @Cache(key = "loj1", configKey = "loj1")
    public CompletableFuture<Integer> lojAsync1(int bound)  {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return CompletableFuture.completedFuture(new Random().nextInt(bound));
    }

    @Cache(key = "loj2", configKey = "loj2")
    public Object lojAsync2(int bound)  {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new Random().nextInt(bound);
    }

    @Cache(key = "loj3", configKey = "loj3", isAsync = false)
    public Integer lojSync(int bound)  {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new Random().nextInt(bound);
    }

}

public class Test{
    
    private TService tService = CglibCacheProxy.proxy(TService.class);
    
    @Test
    public void testCache() {
        long startTime;
        for (int i = 0; i < 5; i++) {
            startTime = System.currentTimeMillis();
            System.out.println(tService.loj(10));
            System.out.println("耗时:" + (System.currentTimeMillis() - startTime));
        } 
        try {
            TimeUnit.SECONDS.sleep(2); // 模拟超时
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 5; i++) {
            startTime = System.currentTimeMillis();
            System.out.println(tService.loj(10));
            System.out.println("耗时:" + (System.currentTimeMillis() - startTime));
        }
    }
}
```

## 2. 详解

### 2.1 @Cacheable 与 @Cache

#### 2.1.1 @Cacheable

这个注解是用来创建Caffeine缓存的,当有两个相同value值的@Cacheable注解存在与两个不同的类上时,如果类中方法上的@Cache注解有非异步与异步的情况下,是会创建两个Caffeine缓存的,如果都统一是一种,那么只需要在一个@Cacheable中指定需要特殊化创建的Caffeine缓存,另一个或多个只需要引用那个value即可.

#### 2.1.2 @Cache

这个注解是用来指定缓存对应的key,还有对应配置中心的配置key前缀的,以及指定该缓存的加载是通过同步的方式还是异步的方式进行加载的

### 2.2 关于异步

Dynacache 指定异步有两种方式：

(1). 使用CompletableFuture作为返回类型,这种做法的好处是使用起来方便,但是缺点是会多创建一个CompletableFuture,当然还有其他缺点,示例代码如下:
```text
@Cache(key = "loj1", configKey = "loj1")
public CompletableFuture<Integer> lojAsync1(int bound)  {
    try {
        TimeUnit.SECONDS.sleep(2);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    return CompletableFuture.completedFuture(new Random().nextInt(bound));
}
```
(2). 使用Object作为返回类型,这种做法的好处是只需要做一次强制类型转换就行了,缺点也很明显,调用方需要显式的强制转换为CompletableFuture
```text
@Cache(key = "loj2", configKey = "loj2")
public Object lojAsync2(int bound)  {
    try {
        TimeUnit.SECONDS.sleep(2);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    return new Random().nextInt(bound);
}
```
