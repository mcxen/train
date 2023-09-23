package com.mcxgroup.member.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/1")
    public String get(){
        return "hello";
    }


}
