package io.github.dynacache;

import io.github.dynacache.service.TService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping
    public Integer test(int bound) {
        return tService.loj(bound);
    }

}
