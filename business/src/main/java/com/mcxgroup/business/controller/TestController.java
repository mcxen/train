package com.mcxgroup.business.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @SentinelResource("hello")
    @GetMapping("/hello")
    public String get(){
        return "hello buisiness";
    }

}
