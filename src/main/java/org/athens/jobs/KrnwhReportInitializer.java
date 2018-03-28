package org.athens.jobs;

import org.apache.log4j.Logger;

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


public class KrnwhReportInitializer {

    final static Logger log = Logger.getLogger(KrnwhReportInitializer.class);

    private KrnwhDaoImpl krnwhDao;

    private KrnwhLogDaoImpl krnwhLogDao;

    private KrnwhJobSettings krnwhJobSettings;

    public KrnwhReportInitializer(KrnwhLogDaoImpl krnwhLogDao, KrnwhDaoImpl krnwhDao, KrnwhJobSettings krnwhJobSettings){
        log.info("about to setup krnwh report...");
        this.krnwhDao = krnwhDao;
        this.krnwhLogDao = krnwhLogDao;
        this.krnwhJobSettings = krnwhJobSettings;
        initializeReportQuartzJob();
    }

    public void initializeReportQuartzJob() {
        try {

            //log.info(krnwhLogDao.list.jsp(10, 0));

            JobDetail job = JobBuilder.newJob(KrnwhReportJob.class)
                    .withIdentity("krnwhJob", "atns").build();

            job.getJobDataMap().put("krnwhDao", krnwhDao);
            job.getJobDataMap().put("krnwhLogDao", krnwhLogDao);
            job.getJobDataMap().put("krnwhJobSettings", krnwhJobSettings);

            Trigger trigger = TriggerBuilder
                    .newTrigger()
                    .withIdentity("krnTrigger", "atns")
                    .withSchedule(
                            CronScheduleBuilder.cronSchedule("1 */3 * * * ?"))
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
