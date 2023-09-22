package com.mcxgroup.member.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.mcxgroup")
//Scan不加的扫描不到隔壁目录的TestController
@Slf4j
public class MemberApplication {

    //主项目
    public static void main(String[] args) {
        SpringApplication.run(MemberApplication.class, args);
        log.info("member模块启动成功");
    }
}
