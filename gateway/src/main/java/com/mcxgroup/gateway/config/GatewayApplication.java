package com.mcxgroup.gateway.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

@SpringBootApplication
@ComponentScan("com.mcxgroup")
//Scan不加的扫描不到隔壁目录的TestController
public class GatewayApplication {
    private static final Logger LOG = LoggerFactory.getLogger(GatewayApplication.class);
    //主项目
    public static void main(String[] args) {

//        SpringApplication.run(MemberApplication.class, args);
        SpringApplication app = new SpringApplication(GatewayApplication.class);
        Environment env = app.run(args).getEnvironment();
        LOG.info("GateWay模块启动成功");
        LOG.info("网关地址: \thttp://127.0.0.1:{}", env.getProperty("server.port"));
    }
}
