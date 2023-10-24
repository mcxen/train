//package com.mcxgroup.batch.config;
//
//import com.mcxgroup.batch.job.QuartzJob;
//import org.quartz.*;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * @ClassName QuartzConfig
// * @Description
// * @Author McXen@2023/10/24
// **/
//@Configuration
//public class QuartzConfig {
//
//    @Bean
//    public JobDetail jobDetail(){
//        //可以起不同的名字，不同的组
//        return JobBuilder.newJob(QuartzJob.class)
//                .withIdentity("QuartzJob","test")
//                .storeDurably()
//                .build();
//    }
//
//    @Bean
//    public Trigger trigger(){
//        return TriggerBuilder.newTrigger()
//                .forJob(jobDetail())
//                .withIdentity("trigger","trigger")
//                .startNow()
//                .withSchedule(CronScheduleBuilder.cronSchedule("*/2 * * * * ?"))
//                .build();
////        不管余数是多少，直接就开始2秒执行执行
//    }
//}
