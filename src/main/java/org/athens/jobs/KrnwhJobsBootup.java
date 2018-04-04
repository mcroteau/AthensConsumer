package org.athens.jobs;

import org.apache.log4j.Logger;

import org.athens.common.ApplicationConstants;
import org.athens.domain.KrnwhJobSettings;
import org.quartz.JobDetail;
import org.quartz.JobBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.CronScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;

import org.athens.dao.impl.KrnwhDaoImpl;
import org.athens.dao.impl.KrnwhLogDaoImpl;


public class KrnwhJobsBootup {

    final static Logger log = Logger.getLogger(KrnwhJobsBootup.class);

    private KrnwhDaoImpl krnwhDao;
    private KrnwhLogDaoImpl krnwhLogDao;
    private KrnwhJobSettings krnwhJobSettings;
    private QuartzJobStats quartzJobStats;
    //private Scheduler scheduler;


    public KrnwhJobsBootup(KrnwhLogDaoImpl krnwhLogDao, KrnwhDaoImpl krnwhDao, KrnwhJobSettings krnwhJobSettings, QuartzJobStats quartzJobStats){
        log.info("about to setup krnwh reports.. .");
        this.krnwhDao = krnwhDao;
        this.krnwhLogDao = krnwhLogDao;
        this.krnwhJobSettings = krnwhJobSettings;
        this.quartzJobStats = quartzJobStats;
        initializeQuartzJobs();
    }


    public void initializeQuartzJobs() {
        initializeQuartzJob(KrnwhDailyJob.class, ApplicationConstants.ATHENS_DAILY_QUARTZ_JOB, ApplicationConstants.ATHENS_QUARTZ_DAILY_TRIGGER, ApplicationConstants.DAILY_JOB_QUARTZ_EXPRESSION);
        initializeQuartzJob(KrnwhWeeklyJob.class, ApplicationConstants.ATHENS_WEEKLY_QUARTZ_JOB, ApplicationConstants.ATHENS_QUARTZ_WEEKLY_TRIGGER, ApplicationConstants.WEEKLY_JOB_QUARTZ_EXPRESSION);
    }


    private void initializeQuartzJob(Class clazz, String name, String triggerName, String expression) {
        try {
            JobDetail job = JobBuilder.newJob(clazz)
                    .withIdentity(name, ApplicationConstants.ATHENS_GROUP).build();

            job.getJobDataMap().put("krnwhDao", krnwhDao);
            job.getJobDataMap().put("krnwhLogDao", krnwhLogDao);
            job.getJobDataMap().put("krnwhJobSettings", krnwhJobSettings);
            job.getJobDataMap().put("quartzJobStats", quartzJobStats);
            job.getJobDataMap().put("jobCount", 4);

            Trigger trigger = TriggerBuilder
                    .newTrigger()
                    .withIdentity(triggerName, ApplicationConstants.ATHENS_GROUP)
                    .withSchedule(
                            CronScheduleBuilder.cronSchedule(expression))
                    .build();

            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start();
            scheduler.scheduleJob(job, trigger);

            if(name.equals(ApplicationConstants.ATHENS_DAILY_QUARTZ_JOB)){
                log.info("setup krnw daily...");
            } else {
                log.info("setup krnw weekly...");
            }
        } catch (Exception e) {
            log.info("something went wrong setting up krnwh job");
            e.printStackTrace();
        }
    }
}
