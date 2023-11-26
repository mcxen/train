package com.mcxgroup.business.config;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.mcxgroup.business.service.TrainSeatService;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@ComponentScan("com.mcxgroup")
//Scan不加的扫描不到隔壁目录的TestController
@MapperScan("com.mcxgroup.*.mapper")
//@Configuration
@EnableFeignClients("com.mcxgroup.business.feign")
@EnableCaching
@EnableAsync//开启异步线程
public class BusinessApplication {

    private static final Logger LOG = LoggerFactory.getLogger(TrainSeatService.class);
    //主项目
    public static void main(String[] args) {

//        SpringApplication.run(MemberApplication.class, args);
        SpringApplication app = new SpringApplication(BusinessApplication.class);
        Environment env = app.run(args).getEnvironment();
        LOG.info("Buisiness模块启动成功");
        LOG.info("测试地址: \thttp://127.0.0.1:{}{}/hello", env.getProperty("server.port"), env.getProperty("server.servlet.context-path"));
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
