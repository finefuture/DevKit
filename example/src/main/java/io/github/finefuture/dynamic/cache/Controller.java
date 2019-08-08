package io.github.finefuture.dynamic.cache;

import io.github.finefuture.dynamic.cache.service.TService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author longqiang
 * @version 1.0
 * @description TODO
 * @date 2019/5/22 14:49
 * @modifiedBy
 */
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
