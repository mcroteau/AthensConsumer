package org.athens.jobs;

import org.apache.log4j.Logger;
import org.athens.common.ApplicationConstants;
import org.quartz.DisallowConcurrentExecution;

@DisallowConcurrentExecution
public class KronosWorkHourWeeklyJob extends BaseQuartzJob {

    final static Logger log = Logger.getLogger(KronosWorkHourWeeklyJob.class);

    public KronosWorkHourWeeklyJob(){
        super(ApplicationConstants.ATHENS_WEEKLY_QUARTZ_JOB, ApplicationConstants.KRONOS_WEEKLY_REPORT);
    }

}
