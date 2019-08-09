package io.github.finefuture.devkit.example.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author longqiang
 * @version 1.0
 */
@RestController
@RequestMapping("cache")
public class CacheTestController {

    @Autowired
    CacheTestService testService;

    @GetMapping("async1")
    public Integer test1(int bound) throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> loj = testService.lojAsync1(bound);
        while (!loj.isDone()) {
            //you can do something before future is done
        }
        return loj.get();
    }

    @GetMapping("async2")
    public Integer test2(int bound) throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> loj = (CompletableFuture<Integer>) testService.lojAsync2(bound);
        while (!loj.isDone()) {
            //you can do something before future is done
        }
        return loj.get();
    }

    @GetMapping("sync1")
    public Integer test3(int bound) {
        return testService.lojSync(bound);
    }

}
