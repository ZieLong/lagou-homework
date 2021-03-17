package com.lagou.controller;

import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wz
 * @date 2021/3/15
 */
@RestController
public class TestController {

    @RequestMapping("/app/test")
    public String test() {
        System.out.println("-------hello------");
        return "hello";
    }
}
