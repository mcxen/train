package com.mcxgroup.batch.controller;

import com.mcxgroup.batch.feign.BusinessFeign;
import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    private static final Logger LOG = LoggerFactory.getLogger(TestController.class);

    @Resource
    BusinessFeign businessFeign;
    @GetMapping("/hello")
    public String get(){
        String s = businessFeign.hello();
        System.out.println("s = " + s);
        LOG.info(s);
        return "Hello Batch"+s;
    }

}
