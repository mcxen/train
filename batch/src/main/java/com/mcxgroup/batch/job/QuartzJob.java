package com.mcxgroup.batch.job;


import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @ClassName QuartzJob
 * @Description
 * @Author McXen@2023/10/24
 **/
public class QuartzJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("Test Job");
    }
}
