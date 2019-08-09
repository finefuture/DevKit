package io.github.finefuture.devkit.example.dynamicbean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author longqiang
 * @version 1.0
 * @date 2019/8/9 11:05
 */
@RestController
@RequestMapping("dynamic-bean")
public class DynamicBeanTestController {

    @Autowired
    DynamicBeanTestService testService;

    @GetMapping("test")
    public List<String> test() {
        return testService.getNewRedissonAddress();
    }

}
