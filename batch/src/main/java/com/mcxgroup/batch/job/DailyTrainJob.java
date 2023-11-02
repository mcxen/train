package com.mcxgroup.batch.job;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.mcxgroup.batch.feign.BusinessFeign;
import com.mcxgroup.common.resp.CommonResp;
import jakarta.annotation.Resource;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Date;

/**
 * @ClassName DailyTrainJob
 * @Description
 * @Author McXen@2023/11/2
 **/
public class DailyTrainJob implements Job {
    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainJob.class);
    @Resource
    BusinessFeign businessFeign;
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // 增加日志流水号
        MDC.put("LOG_ID", System.currentTimeMillis() + RandomUtil.randomString(3));
        LOG.info("生成15天后的车次数据开始");
        // 计算15天后的日期
        Date date = new Date();
        DateTime dateTime = DateUtil.offsetDay(date, 15);
        Date offsetDate = dateTime.toJdkDate();
        // 通过 feign 调用 business 的服务
        CommonResp<Object> commonResp = businessFeign.genDaily(offsetDate);
        LOG.info("生成15天后的车次数据结束，结果：{}", commonResp);
    }
}
