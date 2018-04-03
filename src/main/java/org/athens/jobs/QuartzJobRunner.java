package org.athens.jobs;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.athens.common.ApplicationConstants;
import org.athens.dao.impl.KrnwhDaoImpl;
import org.athens.dao.impl.KrnwhLogDaoImpl;
import org.athens.domain.KrnwhJobSettings;
import org.athens.domain.KrnwhLog;
import org.quartz.JobDetail;
import org.quartz.JobKey;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class QuartzJobRunner {

    public QuartzJobRunner(){

    }

    public void run(){
        //if(!currentlyRunning(context)) {

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        String formattedDate = dateFormat.format(date);

        log.info("executing report : " + formattedDate.toString());

        JobKey jobKey = new JobKey(ApplicationConstants.ATHENS_DAILY_QUARTZ_JOB, ApplicationConstants.ATHENS_QUARTZ_GROUP);
        JobDetail jobDetail = context.getScheduler().getJobDetail(jobKey);

        KrnwhDaoImpl krnwhDao = (KrnwhDaoImpl)jobDetail.getJobDataMap().get("krnwhDao");
        this.krnwhDao = krnwhDao;

        KrnwhLogDaoImpl krnwhLogDao = (KrnwhLogDaoImpl)jobDetail.getJobDataMap().get("krnwhLogDao");
        this.krnwhLogDao = krnwhLogDao;

        KrnwhJobSettings krnwhJobSettings = (KrnwhJobSettings)jobDetail.getJobDataMap().get("krnwhJobSettings");
        this.krnwhJobSettings = krnwhJobSettings;

        //TimeUnit.MINUTES.sleep(1);

        //log.info(krnwhJobSettings.getCompany() + " : " + krnwhJobSettings.getReport() + " : " + krnwhJobSettings.getApiKey());
        KrnwhLog todaysKrnwhLog = krnwhLogDao.findByDate(new BigDecimal(formattedDate));

        if (todaysKrnwhLog != null) {
            todaysKrnwhLog.setKstatus(ApplicationConstants.COMPLETE_STATUS);
            todaysKrnwhLog.setKtot(new BigDecimal(124));
            todaysKrnwhLog.setKaudit("g");
            todaysKrnwhLog.setKadtcnt(new BigDecimal(3));
            todaysKrnwhLog.setKdate(new BigDecimal(20180323));
            krnwhLogDao.update(todaysKrnwhLog);
        }

        KrnwhLog klog = new KrnwhLog();
        klog.setKstatus(ApplicationConstants.STARTED_STATUS);
        klog.setKtot(new BigDecimal(0));
        klog.setKadtcnt(new BigDecimal(0));
        klog.setKaudit(ApplicationConstants.EMPTY_AUDIT);
        klog.setKdate(new BigDecimal(formattedDate));
        KrnwhLog savedKrnwhLog = krnwhLogDao.save(klog);

        krnwhLog = savedKrnwhLog;

        log.info("savedKrnwhLog : " + savedKrnwhLog.getId());
        log.info("apiKey: " + krnwhJobSettings.getApiKey());

        String authuri = "https://secure4.saashr.com/ta/rest/v1/login";

        JsonObject innerObject = new JsonObject();
        innerObject.addProperty("username", krnwhJobSettings.getUsername());
        innerObject.addProperty("password", krnwhJobSettings.getPassword());
        innerObject.addProperty("company", krnwhJobSettings.getCompany());


        JsonObject jsonObject = new JsonObject();
        jsonObject.add("credentials", innerObject);

        WebResource resource = Client.create(new DefaultClientConfig())
                .resource(authuri);

        WebResource.Builder builder = resource.accept("application/json");
        builder.type("application/json");
        builder.header("api-key", krnwhJobSettings.getApiKey());


        ClientResponse cresponse = builder.post(ClientResponse.class, jsonObject.toString());

        String jsonOutput = cresponse.getEntity(String.class);

        if (cresponse.getStatus() != 200) {
            log.info(cresponse.toString());
            throw new RuntimeException("Failed : HTTP error code : " + cresponse.getStatus());
        }

        System.out.println("Token json response from server .... \n");
        //log.info(jsonOutput);

        JsonParser jsonParser = new JsonParser();
        JsonObject tokenObj = (JsonObject) jsonParser.parse(jsonOutput);


        this.token = tokenObj.get("token").toString();
        this.token = token.replaceAll("^\"|\"$", "");

        processReportDataFromRequest();
    }

}
