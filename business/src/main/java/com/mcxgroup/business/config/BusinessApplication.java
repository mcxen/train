package com.mcxgroup.business.config;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

@SpringBootApplication
@ComponentScan("com.mcxgroup")
//Scan不加的扫描不到隔壁目录的TestController
@MapperScan("com.mcxgroup.*.mapper")
//@Configuration
@Slf4j
@EnableFeignClients("com.mcxgroup.business.feign")
public class BusinessApplication {

    //主项目
    public static void main(String[] args) {

//        SpringApplication.run(MemberApplication.class, args);
        SpringApplication app = new SpringApplication(BusinessApplication.class);
        Environment env = app.run(args).getEnvironment();
        log.info("Buisiness模块启动成功");
        log.info("测试地址: \thttp://127.0.0.1:{}{}/hello", env.getProperty("server.port"), env.getProperty("server.servlet.context-path"));
    }
}
