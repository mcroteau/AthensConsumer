package org.athens.jobs;

import org.apache.log4j.Logger;
import org.athens.common.ApplicationConstants;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class KrnwhWeeklyJob extends BaseKrnwhJob {

    final static Logger log = Logger.getLogger(KrnwhDailyJob.class);

    public KrnwhWeeklyJob(){
        super(ApplicationConstants.ATHENS_WEEKLY_QUARTZ_JOB);
        log.info("initialized krnwh weekly job...");
    }

}
