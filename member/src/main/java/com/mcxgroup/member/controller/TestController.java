package com.mcxgroup.member.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//Cloud的原始的规范，自动刷新配置中心的数据，与nacos无关
@RefreshScope
public class TestController {
    @Value("${test.nacos}")
    private String nacosTest;

    @GetMapping("/1")
    public String get(){
        return "hello"+nacosTest;
    }


}
