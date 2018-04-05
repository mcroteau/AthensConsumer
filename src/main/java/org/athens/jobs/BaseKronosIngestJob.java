package org.athens.jobs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.apache.log4j.Logger;
import org.athens.common.ApplicationConstants;
import org.athens.dao.impl.KronosWorkHourDaoImpl;
import org.athens.dao.impl.KronosIngestLogDaoImpl;
import org.athens.domain.KronosIngestLog;
import org.athens.domain.KronosWorkHour;
import org.athens.domain.KronosWorkHourSettings;
import org.athens.domain.KronosQuartzJobStats;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**Im going to rename all quartz job classes**/

@DisallowConcurrentExecution
public class BaseKronosIngestJob implements Job {

    final static Logger log = Logger.getLogger(BaseKronosIngestJob.class);

    private String authenticationToken = "";

    private int totalCount     = 0;
    private int totalSaved     = 0;
    private int totalError     = 0;
    private int totalFound     = 0;
    private int totalProcessed = 0;


    private String report;
    private JobKey jobKey;

    private KronosIngestLog kronosIngestLog;

    private KronosWorkHourDaoImpl krnwhDao;
    private KronosIngestLogDaoImpl krnwhLogDao;
    private KronosWorkHourSettings krnwhJobSettings;
    private KronosQuartzJobStats quartzJobStats;


    public BaseKronosIngestJob(String jobName, String report){
        log.info("Initializing " + jobName);
        this.report = report;
        this.jobKey = new JobKey(jobName, ApplicationConstants.ATHENS_GROUP);
    }


    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            /**
             for(int n = 0; n < 932; n++) {
             quartzJobStats.setCount(n);
             TimeUnit.SECONDS.sleep(7);
             }**/

            setLocalDefined(context);
            resetQuartzJobStats();
            getSetKrnwhQuartzJobLog();

            log.info("running " + this.jobKey.getName() + "... log:"+ kronosIngestLog.getId());

            performKronosAuthentication();
            performKronosReportRequestProcess();

        } catch (Exception e) {
            if(kronosIngestLog != null){
                kronosIngestLog.setKstatus(ApplicationConstants.ERROR_STATUS);
                krnwhLogDao.save(kronosIngestLog);
            }
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


    public void performKronosReportRequestProcess(){
        String uri = ApplicationConstants.KRONOS_BASE_REPORT_URI + this.report;

        DefaultClientConfig client = new DefaultClientConfig();
        WebResource resource = Client.create(client)
                .resource(uri);

        WebResource.Builder builder = resource.accept("text/csv");
        log.info("setting bearer token " + this.authenticationToken);
        builder.header("Authentication", "Bearer " + this.authenticationToken);

        ClientResponse response  = builder.get(ClientResponse.class);
        String csvData = response.getEntity(String.class);

        readCsvDataSetTotal(csvData.toString());
        readCsvDataString(csvData.toString());
    }


    public void readCsvDataSetTotal(String csvData){
        String line = "";
        InputStream is = new ByteArrayInputStream(csvData.getBytes());
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            totalCount = 0;
            while ((line = br.readLine()) != null) {
                totalCount++;
            }
            kronosIngestLog.setKtot(new BigDecimal(totalCount));
            kronosIngestLog.setKstatus(ApplicationConstants.RUNNING_STATUS);
            krnwhLogDao.update(kronosIngestLog);

            quartzJobStats.setTotal(totalCount);
            quartzJobStats.setStatus(ApplicationConstants.RUNNING_STATUS);
        }catch (Exception e){
            log.warn("issue setting");
        }
    }


    public void readCsvDataString(String csvData){
        String line = "";

        InputStream is = new ByteArrayInputStream(csvData.getBytes());
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            while ((line = br.readLine()) != null) {

                if(totalProcessed != 0) {

                    String[] kronosPunchData = line.split(",");
                    KronosWorkHour kronosWorkHour = getSetKronosWorkHour(kronosPunchData);

                    KronosWorkHour existingKronosWorkHour = null;

                    if(kronosWorkHour.getFpbadg().compareTo(new BigDecimal(0)) != 0  &&
                            kronosWorkHour.getFpempn().compareTo(new BigDecimal(0)) != 0){

                        log.info("find by both");
                        existingKronosWorkHour = krnwhDao.findByPunchBadgeIdEmployeeId(kronosWorkHour.getFppunc(), kronosWorkHour.getFpbadg(), kronosWorkHour.getFpempn());
                        if(existingKronosWorkHour != null){
                            String key = kronosWorkHour.getFppunc().toString() + kronosWorkHour.getFpbadg().toString() + kronosWorkHour.getFpempn().toString();

                            int value = 0;
                            if(quartzJobStats.getExistsMap().containsKey(key)){
                                value = quartzJobStats.getExistsMap().get(key);
                            }

                            value++;
                            quartzJobStats.setExistsMapValue(key, value);
                        }
                    }else if(kronosWorkHour.getFpbadg().compareTo(new BigDecimal(0)) != 0 &&
                            kronosWorkHour.getFpempn().compareTo(new BigDecimal(0)) == 0){

                        log.info("find by badge id");
                        existingKronosWorkHour = krnwhDao.findByPunchBadgeId(kronosWorkHour.getFppunc(), kronosWorkHour.getFpbadg());
                        if(existingKronosWorkHour != null){
                            String key = kronosWorkHour.getFppunc().toString() + kronosWorkHour.getFpbadg().toString();
                            int value = 0;
                            if(quartzJobStats.getExistsMap().containsKey(key)){
                                value = quartzJobStats.getExistsMap().get(key);
                            }
                            value++;
                            quartzJobStats.setExistsMapValue(key, value);
                        }

                    }else if(kronosWorkHour.getFpempn().compareTo(new BigDecimal(0)) != 0 &&
                            kronosWorkHour.getFpbadg().compareTo(new BigDecimal(0)) == 0){

                        existingKronosWorkHour = krnwhDao.findByPunchEmployeeId(kronosWorkHour.getFppunc(), kronosWorkHour.getFpempn());
                        log.info("find by employee id");
                        if(existingKronosWorkHour != null){
                            String key = kronosWorkHour.getFppunc().toString() + kronosWorkHour.getFpempn().toString();
                            int value = 0;
                            if(quartzJobStats.getExistsMap().containsKey(key)){
                                value = quartzJobStats.getExistsMap().get(key);
                            }
                            value++;
                            quartzJobStats.setExistsMapValue(key, value);
                        }

                    }


                    if (totalProcessed % 3 == 0) kronosWorkHour.setFstatus("aa");

                    //log.info(kronosWorkHour.toString());

                    try {

                        if(existingKronosWorkHour == null) {
                            KronosWorkHour skrnwh = krnwhDao.save(kronosWorkHour);
                            totalSaved++;
                            log.info(this.jobKey.getName() + ": saved: " + totalSaved + ", processed: " + totalProcessed);
                            quartzJobStats.setSaved(totalSaved);
                        }else{
                            totalFound++;
                            log.info(this.jobKey.getName() + ": found: " + totalFound +  ", processed: " + totalProcessed);
                            quartzJobStats.setFound(totalFound);
                        }


                    } catch (Exception e) {
                        quartzJobStats.addAuditDetails(kronosWorkHour);
                        totalError++;
                        log.warn("error");
                        e.printStackTrace();

                    }

                }

                totalProcessed++;
                quartzJobStats.setProcessed(totalProcessed);



                kronosIngestLog.setKadtcnt(new BigDecimal(totalError));
                kronosIngestLog.setKproc(new BigDecimal(totalProcessed));
                krnwhLogDao.update(kronosIngestLog);
            }

        } catch (Exception e) {
            totalError++;
            e.printStackTrace();
        }

        log.info("processed: "   + totalProcessed);
        log.info("error count: " + totalError);
        log.info("totalSaved: "  + totalSaved);
        log.info("found: "       + totalFound);




        kronosIngestLog.setKaudit(getAuditJsonRepresentation());
        kronosIngestLog.setKproc(new BigDecimal(totalProcessed));
        kronosIngestLog.setKtot(new BigDecimal(totalSaved));
        kronosIngestLog.setKadtcnt(new BigDecimal(totalError));
        kronosIngestLog.setKstatus(ApplicationConstants.COMPLETE_STATUS);
        krnwhLogDao.update(kronosIngestLog);

    }


    public String getAuditJsonRepresentation(){
        Gson gsonObj =  new GsonBuilder().setPrettyPrinting().create();
        return gsonObj.toJson(quartzJobStats.getAuditDetails());
    }

    public KronosWorkHour getSetKronosWorkHour(String[] kronosPunchData) {

        KronosWorkHour kronosWorkHour = new KronosWorkHour();

        try {

            BigDecimal punchDate = getFormattedPunchDate(kronosPunchData[ApplicationConstants.KRONOS_PUNCH_DATE_COLUMN]);

            String empIdS = kronosPunchData[0].replaceAll("^\"|\"$", "");
            String badgeIdS = kronosPunchData[4].replaceAll("^\"|\"$", "");

            String employeeStatus = kronosPunchData[2].replaceAll("^\"|\"$", "");
            String terminal = kronosPunchData[3].replaceAll("^\"|\"$", "");

            if (empIdS.equals("")) empIdS = "0";
            if (badgeIdS.equals("")) badgeIdS = "0";

            if (employeeStatus.equals("Active")) employeeStatus = "A";
            if (employeeStatus.equals("LOA")) employeeStatus = "L";
            if (!employeeStatus.equals("L") && !employeeStatus.equals("A")) employeeStatus = "O";


            BigDecimal badgeId = new BigDecimal(badgeIdS);
            BigDecimal empId = new BigDecimal(empIdS);

            kronosWorkHour.setFpempn(empId);
            kronosWorkHour.setFppunc(punchDate);
            kronosWorkHour.setFptype(employeeStatus);
            kronosWorkHour.setFpclck(terminal);

            kronosWorkHour.setFpbadg(badgeId);
            kronosWorkHour.setFpfkey("");//*
            kronosWorkHour.setFppcod(new BigDecimal(0));//*
            kronosWorkHour.setFstatus("h");//*

            kronosWorkHour.setKrnlogid(kronosIngestLog.getId());

        }catch (Exception e) {

            e.printStackTrace();
        }

        return kronosWorkHour;
    }


    public void getSetKrnwhQuartzJobLog(){
        BigDecimal dateTime = getLogDateTimeFormatted();
        log.info("executing report : " + dateTime.toString());

        KronosIngestLog existingIngestLog = krnwhLogDao.findByDate(dateTime);

        if (existingIngestLog != null) {
            this.kronosIngestLog = existingIngestLog;
        }else{
            KronosIngestLog nkrnwhLog = new KronosIngestLog();
            nkrnwhLog.setKstatus(ApplicationConstants.STARTED_STATUS);
            nkrnwhLog.setKtot(new BigDecimal(0));
            nkrnwhLog.setKadtcnt(new BigDecimal(0));
            nkrnwhLog.setKaudit(ApplicationConstants.EMPTY_AUDIT);
            nkrnwhLog.setKdate(dateTime);
            this.kronosIngestLog = krnwhLogDao.save(nkrnwhLog);
        }
    }


    public BigDecimal getFormattedPunchDate(String unformattedDate) throws ParseException {
        unformattedDate = unformattedDate.replaceAll("^\"|\"$", "");
        String format = "MM/dd/yyyy HH:mm'a'";
        if (unformattedDate.contains("p")) {
            format = "MM/dd/yyyy HH:mm'p'";
        }
//they are copying my key strokes Curtis Gray
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = sdf.parse(unformattedDate);

        DateFormat sdff = new SimpleDateFormat("yyyyMMddHHmmss");
        String formattedDate = sdff.format(date);
        return new BigDecimal(formattedDate);
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
        this.quartzJobStats.setFound(0);
        this.quartzJobStats.setErrored(0);
        this.quartzJobStats.setProcessed(0);
        this.quartzJobStats.setStatus(null);
    }


    public void setLocalDefined(JobExecutionContext context) throws SchedulerException {
        JobDetail jobDetail = context.getScheduler().getJobDetail(this.jobKey);
        this.krnwhDao = (KronosWorkHourDaoImpl) jobDetail.getJobDataMap().get(ApplicationConstants.KRNWH_DAO_LOOKUP);
        this.krnwhLogDao = (KronosIngestLogDaoImpl) jobDetail.getJobDataMap().get(ApplicationConstants.KRNWH_LOG_DAO_LOOKUP);
        this.krnwhJobSettings = (KronosWorkHourSettings) jobDetail.getJobDataMap().get(ApplicationConstants.KRNWH_JOB_SETTINGS_LOOKUP);
        this.quartzJobStats  = (KronosQuartzJobStats) jobDetail.getJobDataMap().get(ApplicationConstants.QUARTZ_JOB_STATS_LOOKUP);
        this.quartzJobStats.setStatus(ApplicationConstants.STARTED_STATUS);
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
