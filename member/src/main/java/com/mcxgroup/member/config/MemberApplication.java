package com.mcxgroup.member.config;


import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

@SpringBootApplication
@ComponentScan("com.mcxgroup")
//Scan不加的扫描不到隔壁目录的TestController
@MapperScan("com.mcxgroup.member.mapper")
//@Configuration
public class MemberApplication {
    private static final Logger LOG = LoggerFactory.getLogger(MemberApplication.class);

    //主项目
    public static void main(String[] args) {

//        SpringApplication.run(MemberApplication.class, args);
        SpringApplication app = new SpringApplication(MemberApplication.class);
        Environment env = app.run(args).getEnvironment();
        LOG.info("member模块启动成功");
        LOG.info("测试地址: \thttp://127.0.0.1:{}{}/1", env.getProperty("server.port"), env.getProperty("server.servlet.context-path"));
    }
}
