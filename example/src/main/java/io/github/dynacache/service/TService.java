package io.github.dynacache.service;

import io.github.dynacache.core.Cache;
import io.github.dynacache.core.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author longqiang
 */
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
