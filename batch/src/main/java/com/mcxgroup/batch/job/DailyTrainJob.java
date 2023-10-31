package com.mcxgroup.batch.job;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.MDC;

/**
 * @ClassName DailyTrainJob
 * @Description
 * @Author McXen@2023/10/31
 **/
@Slf4j
public class DailyTrainJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        MDC.put("LOG_ID", System.currentTimeMillis() + RandomUtil.randomString(3));
        log.info("开始生成每日的车次数据");
        log.info("生成每日的车次数据结束");
    }
}
