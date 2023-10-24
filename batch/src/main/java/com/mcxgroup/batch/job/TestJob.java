package com.mcxgroup.batch.job;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @ClassName TestJob
 * @Description
 * @Author McXen@2023/10/24
 **/
@Component
@EnableScheduling
public class TestJob {
    @Scheduled(cron = "0/5 * * * * ?")
    public void test(){
        System.out.println(" 定时任务正在进行 .... ");
    }
}
