package org.athens.jobs;

import org.apache.log4j.Logger;

import org.athens.common.ApplicationConstants;
import org.athens.domain.KronosQuartzJobStats;
import org.athens.domain.KronosQuartzJobSettings;
import org.quartz.JobDetail;
import org.quartz.JobBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.CronScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;

import org.athens.dao.impl.KronosWorkHourDaoImpl;
import org.athens.dao.impl.KronosQuartzIngestLogDaoImpl;


public class KronosJobsBootup {

    final static Logger log = Logger.getLogger(KronosJobsBootup.class);


    private KronosWorkHourDaoImpl kronosWorkHourDao;
    private KronosQuartzIngestLogDaoImpl kronosIngestLogDao;
    private KronosQuartzJobSettings kronosWorkHourJobSettings;
    private KronosQuartzJobStats dailyKronosQuartzJobStats;
    private KronosQuartzJobStats weeklyKronosQuartzJobStats;




    public KronosJobsBootup(KronosQuartzIngestLogDaoImpl kronosIngestLogDao, KronosWorkHourDaoImpl kronosWorkHourDao, KronosQuartzJobSettings kronosWorkHourJobSettings, KronosQuartzJobStats dailyKronosQuartzJobStats, KronosQuartzJobStats weeklyKronosQuartzJobStats){
        log.info("about to setup kronosWorkHour reports.. .");
        this.kronosWorkHourDao = kronosWorkHourDao;
        this.kronosIngestLogDao = kronosIngestLogDao;
        this.kronosWorkHourJobSettings = kronosWorkHourJobSettings;
        this.dailyKronosQuartzJobStats = dailyKronosQuartzJobStats;
        this.weeklyKronosQuartzJobStats = weeklyKronosQuartzJobStats;
        initializeQuartzJobs();
    }


    public void initializeQuartzJobs() {
        initializeQuartzJob(KronosWorkHourDailyJob.class, ApplicationConstants.ATHENS_DAILY_QUARTZ_JOB, ApplicationConstants.ATHENS_QUARTZ_DAILY_TRIGGER, ApplicationConstants.DAILY_JOB_QUARTZ_EXPRESSION, ApplicationConstants.ATHENS_DAILY_QUARTZ_JOB_DESCRIPTION, dailyKronosQuartzJobStats);
        initializeQuartzJob(KronosWorkHourWeeklyJob.class, ApplicationConstants.ATHENS_WEEKLY_QUARTZ_JOB, ApplicationConstants.ATHENS_QUARTZ_WEEKLY_TRIGGER, ApplicationConstants.WEEKLY_JOB_QUARTZ_EXPRESSION, ApplicationConstants.ATHENS_WEEKLY_QUARTZ_JOB_DESCRIPTION, weeklyKronosQuartzJobStats);
    }


    private void initializeQuartzJob(Class clazz, String name, String triggerName, String expression, String description, KronosQuartzJobStats kronosQuartzJobStats) {
        try {
            JobDetail job = JobBuilder.newJob(clazz)
                    .withIdentity(name, ApplicationConstants.ATHENS_GROUP).build();

            job.getJobDataMap().put(ApplicationConstants.KRNWH_DAO_LOOKUP, kronosWorkHourDao);
            job.getJobDataMap().put(ApplicationConstants.KRNWH_LOG_DAO_LOOKUP, kronosIngestLogDao);
            job.getJobDataMap().put(ApplicationConstants.KRNWH_JOB_SETTINGS_LOOKUP, kronosWorkHourJobSettings);
            job.getJobDataMap().put(ApplicationConstants.QUARTZ_JOB_STATS_LOOKUP, kronosQuartzJobStats);
            job.getJobDataMap().put(ApplicationConstants.ATHENS_QUARTZ_JOB_DESCRIPTION_LOOKUP, description);

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
            log.info("something went wrong setting up kronosWorkHour job");
            e.printStackTrace();
        }
    }

}
