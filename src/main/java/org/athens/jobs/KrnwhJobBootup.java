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


public class KrnwhJobBootup {

    final static Logger log = Logger.getLogger(KrnwhJobBootup.class);

    private KrnwhDaoImpl krnwhDao;

    private KrnwhLogDaoImpl krnwhLogDao;

    private KrnwhJobSettings krnwhJobSettings;

    public KrnwhJobBootup(KrnwhLogDaoImpl krnwhLogDao, KrnwhDaoImpl krnwhDao, KrnwhJobSettings krnwhJobSettings){
        log.info("about to setup krnwh report...");
        this.krnwhDao = krnwhDao;
        this.krnwhLogDao = krnwhLogDao;
        this.krnwhJobSettings = krnwhJobSettings;
        initializeReportQuartzJob();
    }

    public void initializeReportQuartzJob() {
        try {
            JobDetail job = JobBuilder.newJob(KrnwhDailyJob.class)
                    .withIdentity(ApplicationConstants.ATHENS_DAILY_KRNWH_JOB, ApplicationConstants.ATHENS_GROUP).build();

            job.getJobDataMap().put("krnwhDao", krnwhDao);
            job.getJobDataMap().put("krnwhLogDao", krnwhLogDao);
            job.getJobDataMap().put("krnwhJobSettings", krnwhJobSettings);

            Trigger trigger = TriggerBuilder
                    .newTrigger()
                    .withIdentity("krnTrigger", "atns")
                    .withSchedule(
                            CronScheduleBuilder.cronSchedule("1 */113 * * * ?"))
                    .build();

            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start();
            scheduler.scheduleJob(job, trigger);

            log.info("setup krnw daily...");

            //0 */2 * * * ?

        }catch(Exception e){
            log.info("something went wrong setting up krnwh job");
            e.printStackTrace();
        }
    }

}
