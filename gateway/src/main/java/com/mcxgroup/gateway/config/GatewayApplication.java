package com.mcxgroup.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

@SpringBootApplication
@ComponentScan("com.mcxgroup")
//Scan不加的扫描不到隔壁目录的TestController
@Slf4j
public class GatewayApplication {

    //主项目
    public static void main(String[] args) {

//        SpringApplication.run(MemberApplication.class, args);
        SpringApplication app = new SpringApplication(GatewayApplication.class);
        Environment env = app.run(args).getEnvironment();
        log.info("GateWay模块启动成功");
        log.info("网关地址: \thttp://127.0.0.1:{}", env.getProperty("server.port"));
    }
}
