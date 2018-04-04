package org.athens.jobs;

import org.apache.log4j.Logger;
import org.athens.common.ApplicationConstants;

import org.quartz.DisallowConcurrentExecution;


@DisallowConcurrentExecution
public class KrnwhDailyJob extends BaseKrnwhJob {

	final static Logger log = Logger.getLogger(KrnwhDailyJob.class);

	public KrnwhDailyJob(){
		super(ApplicationConstants.ATHENS_DAILY_QUARTZ_JOB, ApplicationConstants.KRONOS_DAILY_REPORT);
	}

}