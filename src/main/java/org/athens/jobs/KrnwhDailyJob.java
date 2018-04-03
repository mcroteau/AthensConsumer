package org.athens.jobs;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.apache.log4j.Logger;
import org.athens.common.ApplicationConstants;
import org.athens.domain.Krnwh;
import org.athens.domain.KrnwhLog;
import org.quartz.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.athens.domain.KrnwhJobSettings;
import org.athens.dao.impl.KrnwhDaoImpl;
import org.athens.dao.impl.KrnwhLogDaoImpl;

import org.quartz.impl.StdSchedulerFactory;
import org.quartz.DisallowConcurrentExecution;

import java.util.Map;
import java.util.HashMap;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


@DisallowConcurrentExecution
public class KrnwhDailyJob extends BaseKrnwhJob {

	final static Logger log = Logger.getLogger(KrnwhDailyJob.class);

	public KrnwhDailyJob(){
		super(ApplicationConstants.ATHENS_DAILY_QUARTZ_JOB, ApplicationConstants.KRONOS_DAILY_REPORT);
		log.info("initialized krnwh daily job ...");
	}

}