package io.github.dynacache.service;

import io.github.dynacache.core.Cache;
import io.github.dynacache.core.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author longqiang
 */
@Service
@Cacheable("lo")
public class TService {

    @Cache(key = "loj", configKey = "loj")
    public Integer loj(int bound)  {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new Random().nextInt(bound);
    }

}
