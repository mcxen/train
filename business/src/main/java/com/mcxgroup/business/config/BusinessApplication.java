package com.mcxgroup.business.config;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@ComponentScan("com.mcxgroup")
//Scan不加的扫描不到隔壁目录的TestController
@MapperScan("com.mcxgroup.*.mapper")
//@Configuration
@Slf4j
@EnableFeignClients("com.mcxgroup.business.feign")
@EnableCaching
public class BusinessApplication {

    //主项目
    public static void main(String[] args) {

//        SpringApplication.run(MemberApplication.class, args);
        SpringApplication app = new SpringApplication(BusinessApplication.class);
        Environment env = app.run(args).getEnvironment();
        log.info("Buisiness模块启动成功");
        log.info("测试地址: \thttp://127.0.0.1:{}{}/hello", env.getProperty("server.port"), env.getProperty("server.servlet.context-path"));
        //
//        initFlowRules();
//        log.info("》》》》已经定义sentinel限流规则《《《");
    }

//    //手动增加sentinel限制流量
//    private static void  initFlowRules(){
//        List<FlowRule> rules = new ArrayList<>();
//        FlowRule rule = new FlowRule();
//        rule.setResource("doConfirm");
//        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
//        rule.setCount(1);
//        rules.add(rule);
//        FlowRuleManager.loadRules(rules);
//    }
}
