package org.athens.jobs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.apache.log4j.Logger;
import org.athens.common.ApplicationConstants;
import org.athens.dao.impl.KrnwhDaoImpl;
import org.athens.dao.impl.KrnwhLogDaoImpl;
import org.athens.domain.Krnwh;
import org.athens.domain.KrnwhJobSettings;
import org.athens.domain.KrnwhLog;
import org.athens.domain.KrnwhJobStats;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import java.util.concurrent.TimeUnit;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**Im going to rename all quartz job classes**/

@DisallowConcurrentExecution
public class BaseKrnwhJob implements Job {

    final static Logger log = Logger.getLogger(BaseKrnwhJob.class);

    private String authenticationToken = "";

    private int totalCount     = 0;
    private int totalSaved     = 0;
    private int totalError     = 0;
    private int totalFound     = 0;
    private int totalProcessed = 0;


    private String report;
    private JobKey jobKey;

    private KrnwhLog krnwhLog;

    private KrnwhDaoImpl krnwhDao;
    private KrnwhLogDaoImpl krnwhLogDao;
    private KrnwhJobSettings krnwhJobSettings;
    private KrnwhJobStats quartzJobStats;


    public BaseKrnwhJob(String jobName, String report){
        log.info("Initializing " + jobName);
        this.report = report;
        this.jobKey = new JobKey(jobName, ApplicationConstants.ATHENS_GROUP);
    }


    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            setLocalDefined(context);
            resetQuartzJobStats();
            getSetKrnwhQuartzJobLog();

            performKronosAuthentication();

            log.info(this.jobKey.getName());
            /**
            for(int n = 0; n < 932; n++) {
                quartzJobStats.setCount(n);
                TimeUnit.SECONDS.sleep(7);
            }
             **/



             log.info("savedKrnwhLog : " + krnwhLog.getId());

             log.info("apiKey: " + krnwhJobSettings.getApiKey());


             processReportDataFromRequest();


        } catch (Exception e) {
            log.info("something went wrong setting up quartz job");
            e.printStackTrace();
        }
    }


    public void performKronosAuthentication(){

        JsonObject credentials = getAuthenticationCredentials();

        WebResource resource = Client.create(new DefaultClientConfig())
                .resource(ApplicationConstants.KRONOS_LOGIN_URI);

        WebResource.Builder builder = resource.accept("application/json");
        builder.type("application/json");
        builder.header("api-key", krnwhJobSettings.getApiKey());

        ClientResponse response = builder.post(ClientResponse.class, credentials.toString());

        String jsonOutput = response.getEntity(String.class);

        if (response.getStatus() != 200) {
            log.info(response.toString());
            throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
        }

        parseSetAuthenticationToken(jsonOutput);
    }


    private void parseSetAuthenticationToken(String jsonOutput){
        JsonParser jsonParser = new JsonParser();
        JsonObject tokenObj = (JsonObject) jsonParser.parse(jsonOutput);

        this.authenticationToken = tokenObj.get(ApplicationConstants.KRONOS_TOKEN_LOOKUP).toString();
        this.authenticationToken = authenticationToken.replaceAll("^\"|\"$", "");
    }


    private JsonObject getAuthenticationCredentials(){
        JsonObject innerObject = new JsonObject();
        innerObject.addProperty("username", krnwhJobSettings.getUsername());
        innerObject.addProperty("password", krnwhJobSettings.getPassword());
        innerObject.addProperty("company", krnwhJobSettings.getCompany());


        JsonObject jsonObject = new JsonObject();
        jsonObject.add("credentials", innerObject);

        return jsonObject;
    }


    public void processReportDataFromRequest(){
        System.out.println("running report using jersey ...");

        String url = ApplicationConstants.KRONOS_BASE_REPORT_URI + this.report;

        DefaultClientConfig client = new DefaultClientConfig();
        WebResource resource = Client.create(client)
                .resource(url);

        WebResource.Builder builder = resource.accept("text/csv");
        log.info("setting bearer token " + token);
        builder.header("Authentication", "Bearer " + token);

        ClientResponse response  = builder.get(ClientResponse.class);
        String csvData = response.getEntity(String.class);

        //log.info(csvData);
        readCsvDataSetTotal(csvData.toString());
        readCsvDataString(csvData.toString());
    }

public void readCsvDataSetTotal(String csvData){
    String line = "";
    InputStream is = new ByteArrayInputStream(csvData.getBytes());
    try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
        int totalCount = 0;
        while ((line = br.readLine()) != null) {
            totalCount++;
        }
        quartzJobStats.setTotal(totalCount);
    }catch (Exception e){

    }
}


    public void readCsvDataString(String csvData){
        String line = "";

        InputStream is = new ByteArrayInputStream(csvData.getBytes());

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            while ((line = br.readLine()) != null) {

                if(totalCount != 0) {

                    String[] punchData = line.split(",");

                    String punchDate = punchData[1].replaceAll("^\"|\"$", "");

                    String format = "MM/dd/yyyy HH:mm'a'";
                    if (punchDate.contains("p")) {
                        format = "MM/dd/yyyy HH:mm'p'";
                    }

                    SimpleDateFormat sdf = new SimpleDateFormat(format);
                    Date date = sdf.parse(punchDate);

                    DateFormat sdff = new SimpleDateFormat("yyyyMMddHHmmss");
                    String date2 = sdff.format(date);

                    //log.info(date2.toString() + " : " + date.toString());
                    //System.out.println(line);

                    String empIdS = punchData[0].replaceAll("^\"|\"$", "");
                    String badgeIdS = punchData[4].replaceAll("^\"|\"$", "");
                    String type = punchData[2].replaceAll("^\"|\"$", "");
                    String clockS = punchData[3].replaceAll("^\"|\"$", "");

                    if (type == "Active") type = "A";
                    if (type == "LOA") type = "L";
                    if (type != "L" && type != "A") type = "O";


                    if (empIdS.equals("")) empIdS = "0";
                    if (badgeIdS.equals("")) badgeIdS = "0";

                    //log.info("punchData [empId= " + empIdS + " , badgeId=" + badgeIdS + "]");

                    BigDecimal badgeId = new BigDecimal(badgeIdS);
                    BigDecimal empId = new BigDecimal(empIdS);
                    BigDecimal punch = new BigDecimal(date2);

                    Krnwh krnwh = new Krnwh();
                    krnwh.setFpempn(empId);
                    krnwh.setFppunc(punch);
                    krnwh.setFptype(type);
                    krnwh.setFpclck(clockS);

                    krnwh.setFpbadg(badgeId);
                    krnwh.setFpfkey("");//*
                    krnwh.setFppcod(new BigDecimal(0));//*
                    krnwh.setFstatus("h");//*

                    krnwh.setKrnlogid(krnwhLog.getId());

                    Krnwh existingKrnwh = null;

                    if(krnwh.getFpbadg().compareTo(new BigDecimal(0)) != 0  &&
                            krnwh.getFpempn().compareTo(new BigDecimal(0)) != 0){

                        //log.info("find by both");
                        existingKrnwh = krnwhDao.findByPunchBadgeIdEmployeeId(krnwh.getFppunc(), krnwh.getFpbadg(), krnwh.getFpempn());
                        if(existingKrnwh != null){
                            String key = krnwh.getFppunc().toString() + krnwh.getFpbadg().toString() + krnwh.getFpempn().toString();

                            int value = 0;
                            if(foundMap.containsKey(key)){
                                value =foundMap.get(key);
                            }

                            value++;
                            foundMap.put(key, value);
                        }
                    }else if(krnwh.getFpbadg().compareTo(new BigDecimal(0)) != 0 &&
                            krnwh.getFpempn().compareTo(new BigDecimal(0)) == 0){

                        //log.info("find by badge id");
                        existingKrnwh = krnwhDao.findByPunchBadgeId(krnwh.getFppunc(), krnwh.getFpbadg());
                        if(existingKrnwh != null){
                            String key = krnwh.getFppunc().toString() + krnwh.getFpbadg().toString();
                            int value = 0;
                            if(foundMap.containsKey(key)){
                                value =foundMap.get(key);
                            }
                            value++;
                            foundMap.put(key, value);
                        }

                    }else if(krnwh.getFpempn().compareTo(new BigDecimal(0)) != 0 &&
                            krnwh.getFpbadg().compareTo(new BigDecimal(0)) == 0){

                        existingKrnwh = krnwhDao.findByPunchEmployeeId(krnwh.getFppunc(), krnwh.getFpempn());
                        //log.info("find by employee id");
                        if(existingKrnwh != null){
                            String key = krnwh.getFppunc().toString() + krnwh.getFpempn().toString();
                            int value = 0;
                            if(foundMap.containsKey(key)){
                                value =foundMap.get(key);
                            }
                            value++;
                            foundMap.put(key, value);
                        }

                    }


                    if (totalCount == 3) krnwh.setFstatus("aa");

                    //log.info(krnwh.toString());

                    try {

                        if(existingKrnwh == null) {
                            Krnwh skrnwh=krnwhDao.save(krnwh);
                            log.info(this.jobKey.getName() + ": saved: " + totalSaved + ", count: " + totalCount);
                            quartzJobStats.setSaved(totalSaved);
                        }else{
                            totalFound++;
                            log.info(this.jobKey.getName() + ": found: " + totalFound +  ", count: " + totalCount);
                            quartzJobStats.setFound(found);
                        }

                        if(totalCount %50==0){
                            Gson g =  new GsonBuilder().setPrettyPrinting().create();
                            String js = g.toJson(foundMap);
                            System.out.println(js);
                        }


                    } catch (Exception e) {
                        totalError;
                        log.warn("error");
                        e.printStackTrace();
                    }

                }

                totalCount++;

                quartzJobStats.setCount(count);
            }

        } catch (Exception e) {
            totalError++;
            e.printStackTrace();
        }

        log.info("count: " + count);
        log.info("error count: " + totalError);
        log.info("totalSaved: " + totalSaved);
        log.info("found : " + found);

        Gson gsonObj =  new GsonBuilder().setPrettyPrinting().create();
        String jsonStr = gsonObj.toJson(foundMap);
        System.out.println(jsonStr);

        krnwhLog.setKtot(new BigDecimal(totalSaved));
        krnwhLog.setKadtcnt(new BigDecimal(totalError));
        krnwhLog.setKstatus(ApplicationConstants.COMPLETE_STATUS);
        krnwhLogDao.update(krnwhLog);

    }



    public void processPersistence(Krnwh krnwh){
    }

    public void getSetKrnwhQuartzJobLog(){
        BigDecimal dateTime = getLogDateTimeFormatted();
        log.info("executing report : " + dateTime.toString());

        KrnwhLog existingKrnwhLog = krnwhLogDao.findByDate(dateTime);

        if (existingKrnwhLog != null) {
            this.krnwhLog = existingKrnwhLog;
        }else{
            KrnwhLog nkrnwhLog = new KrnwhLog();
            nkrnwhLog.setKstatus(ApplicationConstants.STARTED_STATUS);
            nkrnwhLog.setKtot(new BigDecimal(0));
            nkrnwhLog.setKadtcnt(new BigDecimal(0));
            nkrnwhLog.setKaudit(ApplicationConstants.EMPTY_AUDIT);
            nkrnwhLog.setKdate(new BigDecimal(dateTime));
            this.krnwhLog = krnwhLogDao.save(nkrnwhLog);
        }
    }


    public BigDecimal getLogDateTimeFormatted(){
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String fDate = dateFormat.format(date);
        return new BigDecimal(fDate);
    }



    public void resetQuartzJobStats(){
        this.quartzJobStats.setTotal(0);
        this.quartzJobStats.setSaved(0);
        this.quartzJobStats.setCount(0);
        this.quartzJobStats.setFound(0);
        this.quartzJobStats.setErrored(0);
    }


    public void setLocalDefined(JobExecutionContext context) throws SchedulerException {
        JobDetail jobDetail = context.getScheduler().getJobDetail(this.jobKey);
        this.krnwhDao = (KrnwhDaoImpl) jobDetail.getJobDataMap().get(ApplicationConstants.KRNWH_DAO_LOOKUP);
        this.krnwhLogDao = (KrnwhLogDaoImpl) jobDetail.getJobDataMap().get(ApplicationConstants.KRNWH_LOG_DAO_LOOKUP);
        this.krnwhJobSettings = (KrnwhJobSettings) jobDetail.getJobDataMap().get(ApplicationConstants.KRNWH_JOB_SETTINGS_LOOKUP);
        this.quartzJobStats  = (KrnwhJobStats) jobDetail.getJobDataMap().get(ApplicationConstants.QUARTZ_JOB_STATS_LOOKUP);
    }


    public boolean currentlyRunning(JobExecutionContext context){
        try {
            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            List<JobExecutionContext> runningJobs = scheduler.getCurrentlyExecutingJobs();
            for (JobExecutionContext jobCtx : runningJobs) {
                String thisJobName = jobCtx.getJobDetail().getKey().getName();
                String thisGroupName = jobCtx.getJobDetail().getKey().getGroup();
                if (this.jobKey.getName().equalsIgnoreCase(thisJobName) && this.jobKey.getGroup().equalsIgnoreCase(thisGroupName)
                    //&& !jobCtx.getFireTime().equals(context.getFireTime())
                        ) {
                    return true;
                }
            }
        }catch (Exception e){
            log.warn("something went wrong currently running?");
            return false;
        }
        log.info("not running...");
        return false;
    }

}
