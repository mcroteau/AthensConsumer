package org.athens.jobs;

import org.apache.log4j.Logger;

import org.athens.common.ApplicationConstants;
import org.athens.domain.QuartzJobSettings;
import org.athens.domain.QuartzJobStats;
import org.quartz.JobDetail;
import org.quartz.JobBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.CronScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;

import org.athens.dao.impl.KronosWorkHourDaoImpl;
import org.athens.dao.impl.QuartzIngestLogDaoImpl;


public class KrnwhJobsBootup {

    final static Logger log = Logger.getLogger(KrnwhJobsBootup.class);


    private KronosWorkHourDaoImpl krnwhDao;
    private QuartzIngestLogDaoImpl krnwhLogDao;
    private QuartzJobSettings krnwhJobSettings;
    private QuartzJobStats dailyQuartzJobStats;
    private QuartzJobStats weeklyQuartzJobStats;




    public KrnwhJobsBootup(QuartzIngestLogDaoImpl krnwhLogDao, KronosWorkHourDaoImpl krnwhDao, QuartzJobSettings krnwhJobSettings, QuartzJobStats dailyQuartzJobStats, QuartzJobStats weeklyQuartzJobStats){
        log.info("about to setup krnwh reports.. .");
        this.krnwhDao = krnwhDao;
        this.krnwhLogDao = krnwhLogDao;
        this.krnwhJobSettings = krnwhJobSettings;
        this.dailyQuartzJobStats = dailyQuartzJobStats;
        this.weeklyQuartzJobStats = weeklyQuartzJobStats;
        initializeQuartzJobs();
    }


    public void initializeQuartzJobs() {
        initializeQuartzJob(KronosWorkHourDailyJob.class, ApplicationConstants.ATHENS_DAILY_QUARTZ_JOB, ApplicationConstants.ATHENS_QUARTZ_DAILY_TRIGGER, ApplicationConstants.DAILY_JOB_QUARTZ_EXPRESSION, dailyQuartzJobStats);
        initializeQuartzJob(KronosWorkHourWeeklyJob.class, ApplicationConstants.ATHENS_WEEKLY_QUARTZ_JOB, ApplicationConstants.ATHENS_QUARTZ_WEEKLY_TRIGGER, ApplicationConstants.WEEKLY_JOB_QUARTZ_EXPRESSION, weeklyQuartzJobStats);
    }


    private void initializeQuartzJob(Class clazz, String name, String triggerName, String expression, QuartzJobStats quartzJobStats) {
        try {
            JobDetail job = JobBuilder.newJob(clazz)
                    .withIdentity(name, ApplicationConstants.ATHENS_GROUP).build();

            job.getJobDataMap().put(ApplicationConstants.KRNWH_DAO_LOOKUP, krnwhDao);
            job.getJobDataMap().put(ApplicationConstants.KRNWH_LOG_DAO_LOOKUP, krnwhLogDao);
            job.getJobDataMap().put(ApplicationConstants.KRNWH_JOB_SETTINGS_LOOKUP, krnwhJobSettings);
            job.getJobDataMap().put(ApplicationConstants.QUARTZ_JOB_STATS_LOOKUP, quartzJobStats);

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
