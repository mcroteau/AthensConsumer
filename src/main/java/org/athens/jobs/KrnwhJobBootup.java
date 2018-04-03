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

import javax.naming.ldap.PagedResultsControl;


public class KrnwhJobBootup {

    final static Logger log = Logger.getLogger(KrnwhJobBootup.class);

    private KrnwhDaoImpl krnwhDao;
    private KrnwhLogDaoImpl krnwhLogDao;
    private KrnwhJobSettings krnwhJobSettings;


    public KrnwhJobBootup(KrnwhLogDaoImpl krnwhLogDao, KrnwhDaoImpl krnwhDao, KrnwhJobSettings krnwhJobSettings){
        log.info("about to setup krnwh reports.. .");
        this.krnwhDao = krnwhDao;
        this.krnwhLogDao = krnwhLogDao;
        this.krnwhJobSettings = krnwhJobSettings;
        initializeQuartzJobs();
    }


    public void initializeQuartzJobs() {
        initializeQuartzJob(ApplicationConstants.ATHENS_DAILY_KRNWH_JOB, ApplicationConstants.ATHENS_GROUP, ApplicationConstants.QUARTZ_DAILY_JOB_EXPRESSION);
        initializeQuartzJob(ApplicationConstants.ATHENS_WEEKLY_KRNWH_JOB, ApplicationConstants.ATHENS_GROUP, ApplicationConstants.QUARTZ_WEEKLY_JOB_EXPRESSION);
    }


    private void initializeQuartzJob(String name, String group, String expression) {
        try {
            JobDetail job = JobBuilder.newJob(KrnwhDailyJob.class)
                    .withIdentity(name, group).build();

            job.getJobDataMap().put("krnwhDao", krnwhDao);
            job.getJobDataMap().put("krnwhLogDao", krnwhLogDao);
            job.getJobDataMap().put("krnwhJobSettings", krnwhJobSettings);

            Trigger trigger = TriggerBuilder
                    .newTrigger()
                    .withIdentity(ApplicationConstants.ATHENS_QUARTZ_TRIGGER, ApplicationConstants.ATHENS_GROUP)
                    .withSchedule(
                            CronScheduleBuilder.cronSchedule(expression))
                    .build();

            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start();
            scheduler.scheduleJob(job, trigger);

            if (name.equals(ApplicationConstants.ATHENS_DAILY_KRNWH_JOB)) {
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
