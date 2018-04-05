package org.athens.jobs;

import org.apache.log4j.Logger;
import org.athens.common.ApplicationConstants;
import org.quartz.DisallowConcurrentExecution;

@DisallowConcurrentExecution
public class KrnwhWeeklyJob extends BaseKronosIngestJob {

    final static Logger log = Logger.getLogger(KrnwhWeeklyJob.class);

    public KrnwhWeeklyJob(){
        super(ApplicationConstants.ATHENS_WEEKLY_QUARTZ_JOB, ApplicationConstants.KRONOS_WEEKLY_REPORT);
    }

}
