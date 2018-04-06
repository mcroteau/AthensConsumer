package org.athens.jobs;

import org.apache.log4j.Logger;
import org.athens.common.ApplicationConstants;

import org.quartz.DisallowConcurrentExecution;


@DisallowConcurrentExecution
public class KronosWorkHourDailyJob extends BaseQuartzJob {

	final static Logger log = Logger.getLogger(KronosWorkHourDailyJob.class);

	public KronosWorkHourDailyJob(){
		super(ApplicationConstants.ATHENS_DAILY_QUARTZ_JOB, ApplicationConstants.KRONOS_DAILY_REPORT);
	}

}