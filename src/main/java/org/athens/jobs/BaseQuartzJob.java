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
import org.athens.dao.impl.QuartzIngestLogDaoImpl;
import org.athens.domain.QuartzIngestLog;
import org.athens.domain.KronosWorkHour;
import org.athens.domain.QuartzJobSettings;
import org.athens.domain.QuartzJobStats;
import org.quartz.*;

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
import java.util.concurrent.TimeUnit;
import org.joda.time.LocalTime;

/**Im going to rename all quartz job classes**/

@DisallowConcurrentExecution
public class BaseQuartzJob implements Job {

    final static Logger log = Logger.getLogger(BaseQuartzJob.class);

    private String authenticationToken = "";

    private long timeStarted = 0;

    private int totalCount     = 0;
    private int totalSaved     = 0;
    private int totalError     = 0;
    private int totalFound     = 0;
    private int totalProcessed = 0;


    private String report;
    private JobKey jobKey;

    private QuartzIngestLog kronosIngestLog;

    private KronosWorkHourDaoImpl krnwhDao;
    private QuartzIngestLogDaoImpl krnwhLogDao;
    private QuartzJobSettings krnwhJobSettings;
    private QuartzJobStats quartzJobStats;


    public BaseQuartzJob(String jobName, String report){
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
            setQuartzJobStatsStatus();
            getSetTimeStarted();
            clearExistingLogs();
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


    private void performKronosAuthentication(){
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
        getSetRunningTime();
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


    private void performKronosReportRequestProcess(){
        String uri = ApplicationConstants.KRONOS_BASE_REPORT_URI + this.report;

        DefaultClientConfig client = new DefaultClientConfig();
        WebResource resource = Client.create(client)
                .resource(uri);

        WebResource.Builder builder = resource.accept("text/csv");
        log.info("setting bearer token " + this.authenticationToken);
        builder.header("Authentication", "Bearer " + this.authenticationToken);

        ClientResponse response  = builder.get(ClientResponse.class);
        String csvData = response.getEntity(String.class);

        getSetRunningTime();
        readKronosCsvDataSetTotalAmount(csvData.toString());
        readKronosCsvDataSave(csvData.toString());
    }


    private void readKronosCsvDataSetTotalAmount(String csvData){
        String line = "";
        InputStream is = new ByteArrayInputStream(csvData.getBytes());
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            totalCount = 0;
            while ((line = br.readLine()) != null) {
                totalCount++;
            }

            quartzJobStats.setTotal(totalCount);
            quartzJobStats.setStatus(ApplicationConstants.RUNNING_STATUS);

            updateQuartzIngestLog(ApplicationConstants.RUNNING_STATUS);
        }catch (Exception e){
            log.warn("issue setting");
        }
    }


    private void readKronosCsvDataSave(String csvData){
        String line = "";


        InputStream is = new ByteArrayInputStream(csvData.getBytes());
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            while ((line = br.readLine()) != null) {

                if(totalProcessed != 0) {

                    String[] kronosPunchData = line.split(ApplicationConstants.CSV_DELIMETER);

                    KronosWorkHour kronosWorkHour = getSetKronosWorkHourFromData(kronosPunchData);

                    KronosWorkHour existingKronosWorkHour = getExistingKronosWorkHour(kronosWorkHour);

                    if (totalProcessed % 2 == 0) kronosWorkHour.setFstatus("aa");

                    try {

                        if(existingKronosWorkHour != null) {
                            totalFound++;
                            log.info(this.jobKey.getName() + ": found: " + totalFound +  ", processed: " + totalProcessed);
                            quartzJobStats.setFound(totalFound);
                        }

                        if(existingKronosWorkHour == null){
                            //KronosWorkHour skrnwh = krnwhDao.save(kronosWorkHour);
                            totalSaved++;
                            log.info(this.jobKey.getName() + ": saved: " + totalSaved + ", processed: " + totalProcessed);
                            quartzJobStats.setSaved(totalSaved);
                        }

                    } catch (Exception e) {
                        quartzJobStats.addAuditDetails(kronosWorkHour);
                        totalError++;
                        log.warn("error");
                        e.printStackTrace();
                    }
                }

                getSetRunningTime();

                totalProcessed++;
                quartzJobStats.setProcessed(totalProcessed);

                updateQuartzIngestLog(ApplicationConstants.RUNNING_STATUS);
            }

        } catch (Exception e) {
            totalError++;
            e.printStackTrace();
        }

        updateQuartzIngestLog(ApplicationConstants.COMPLETE_STATUS);

        getSetRunningTime();

    }


    private void updateQuartzIngestLog(String status){
        kronosIngestLog.setKtot(new BigDecimal(totalSaved));
        kronosIngestLog.setKadtcnt(new BigDecimal(totalError));
        kronosIngestLog.setKproc(new BigDecimal(totalProcessed));
        kronosIngestLog.setKaudit(getAuditJsonRepresentation());
        kronosIngestLog.setKstatus(status);
        krnwhLogDao.update(kronosIngestLog);//TODO:uncomment
        getSetRunningTime();
    }


    private KronosWorkHour getExistingKronosWorkHour(KronosWorkHour kronosWorkHour){

        int value = 0;
        String kronosIdentifier = "";

        KronosWorkHour existingKronosWorkHour = null;

        if(kronosWorkHour.getFpbadg().compareTo(new BigDecimal(0)) != 0  &&
                kronosWorkHour.getFpempn().compareTo(new BigDecimal(0)) != 0){

            log.info("find by both");
            existingKronosWorkHour = krnwhDao.findByPunchBadgeIdEmployeeId(kronosWorkHour.getFppunc(), kronosWorkHour.getFpbadg(), kronosWorkHour.getFpempn());
            kronosIdentifier = kronosWorkHour.getFppunc().toString() + kronosWorkHour.getFpbadg().toString() + kronosWorkHour.getFpempn().toString();

        }else if(kronosWorkHour.getFpbadg().compareTo(new BigDecimal(0)) != 0 &&
                kronosWorkHour.getFpempn().compareTo(new BigDecimal(0)) == 0){

            log.info("find by badge id");
            existingKronosWorkHour = krnwhDao.findByPunchBadgeId(kronosWorkHour.getFppunc(), kronosWorkHour.getFpbadg());
            kronosIdentifier = kronosWorkHour.getFppunc().toString() + kronosWorkHour.getFpbadg().toString();

        }else if(kronosWorkHour.getFpempn().compareTo(new BigDecimal(0)) != 0 &&
                kronosWorkHour.getFpbadg().compareTo(new BigDecimal(0)) == 0){

            existingKronosWorkHour = krnwhDao.findByPunchEmployeeId(kronosWorkHour.getFppunc(), kronosWorkHour.getFpempn());
            log.info("find by employee id");
            kronosIdentifier = kronosWorkHour.getFppunc().toString() + kronosWorkHour.getFpempn().toString();

        }

        if(quartzJobStats.getExistsMap().containsKey(kronosIdentifier)){
            value = quartzJobStats.getExistsMap().get(kronosIdentifier);
        }

        if(existingKronosWorkHour == null){
            kronosIdentifier = "";
        }

        if(existingKronosWorkHour != null && !kronosIdentifier.equals("")){
            value++;
            quartzJobStats.setExistsMapValue(kronosIdentifier, value);
        }

        return existingKronosWorkHour;

    }


    private String getAuditJsonRepresentation(){
        Gson gsonObj =  new GsonBuilder().setPrettyPrinting().create();
        return gsonObj.toJson(quartzJobStats.getAuditDetails());
    }


    private KronosWorkHour getSetKronosWorkHourFromData(String[] kronosPunchData) {

        KronosWorkHour kronosWorkHour = new KronosWorkHour();

        try {

            BigDecimal punchDate = getFormattedPunchDate(kronosPunchData[ApplicationConstants.KRONOS_PUNCH_DATE_COLUMN]);

            String employeeIdString = cleanupString(kronosPunchData[ApplicationConstants.KRONOS_EMPLOYEE_ID_COLUMN]);
            String badgeIdString = cleanupString(kronosPunchData[ApplicationConstants.KRONOS_BADGE_ID_COLUMN]);

            String employeeStatus = cleanupString(kronosPunchData[ApplicationConstants.KRONOS_EMPLOYEE_STATUS_COLUMN]);
            String terminal = cleanupString(kronosPunchData[ApplicationConstants.KRONOS_TERMINAL_COLUMN]);

            if (employeeIdString.equals("")) employeeIdString = "0";
            if (badgeIdString.equals("")) badgeIdString = "0";

            if (employeeStatus.equals("Active")) employeeStatus = "A";
            if (employeeStatus.equals("LOA")) employeeStatus = "L";
            if (!employeeStatus.equals("L") && !employeeStatus.equals("A")) employeeStatus = "O";

            BigDecimal employeeId = new BigDecimal(employeeIdString);
            BigDecimal badgeId = new BigDecimal(badgeIdString);

            kronosWorkHour.setFpempn(employeeId);
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


    private String cleanupString(String raw){
        return raw.replaceAll("^\"|\"$", "");
    }



    private void getSetKrnwhQuartzJobLog(){
        BigDecimal dateTime = getLogDateTimeFormatted();
        log.info("executing report : " + dateTime.toString());

        QuartzIngestLog existingIngestLog = krnwhLogDao.findByDate(dateTime);

        if (existingIngestLog != null) {
            this.kronosIngestLog = existingIngestLog;
        }else{
            QuartzIngestLog nkrnwhLog = new QuartzIngestLog();
            nkrnwhLog.setKstatus(ApplicationConstants.STARTED_STATUS);
            nkrnwhLog.setKtot(new BigDecimal(0));
            nkrnwhLog.setKadtcnt(new BigDecimal(0));
            nkrnwhLog.setKaudit(ApplicationConstants.EMPTY_AUDIT);
            nkrnwhLog.setKdate(dateTime);
            this.kronosIngestLog = krnwhLogDao.save(nkrnwhLog);
        }
        quartzJobStats.setKronosIngestId(kronosIngestLog.getId());
    }


    private BigDecimal getFormattedPunchDate(String unformattedDate) throws ParseException {
        unformattedDate = unformattedDate.replaceAll("^\"|\"$", "");
        String format = "MM/dd/yyyy HH:mm'a'";
        if (unformattedDate.contains("p")) {
            format = "MM/dd/yyyy HH:mm'p'";
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = sdf.parse(unformattedDate);

        DateFormat sdff = new SimpleDateFormat("yyyyMMddHHmmss");
        String formattedDate = sdff.format(date);
        return new BigDecimal(formattedDate);
    }


    private BigDecimal getLogDateTimeFormatted(){
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String fDate = dateFormat.format(date);
        return new BigDecimal(fDate);
    }


    private void clearExistingLogs(){
        List<QuartzIngestLog> kronosIngestLogs = krnwhLogDao.findAllByStatus(ApplicationConstants.RUNNING_STATUS);
        for(QuartzIngestLog kronosIngestLog : kronosIngestLogs){
            log.info(kronosIngestLog);
            kronosIngestLog.setKstatus(ApplicationConstants.INTERRUPTED_STATUS);
            //krnwhLogDao.updateStatus(kronosIngestLog);//TODO: uncomment
        }
    }


    private void resetQuartzJobStats(){
        this.quartzJobStats.setTotal(0);
        this.quartzJobStats.setSaved(0);
        this.quartzJobStats.setFound(0);
        this.quartzJobStats.setErrored(0);
        this.quartzJobStats.setProcessed(0);
        this.quartzJobStats.setStatus(null);
        this.quartzJobStats.setKronosIngestId(new BigDecimal(0));
    }


    private void setLocalDefined(JobExecutionContext context) throws SchedulerException {
        JobDetail jobDetail = context.getScheduler().getJobDetail(this.jobKey);
        this.krnwhDao = (KronosWorkHourDaoImpl) jobDetail.getJobDataMap().get(ApplicationConstants.KRNWH_DAO_LOOKUP);
        this.krnwhLogDao = (QuartzIngestLogDaoImpl) jobDetail.getJobDataMap().get(ApplicationConstants.KRNWH_LOG_DAO_LOOKUP);
        this.krnwhJobSettings = (QuartzJobSettings) jobDetail.getJobDataMap().get(ApplicationConstants.KRNWH_JOB_SETTINGS_LOOKUP);
        this.quartzJobStats  = (QuartzJobStats) jobDetail.getJobDataMap().get(ApplicationConstants.QUARTZ_JOB_STATS_LOOKUP);
    }


    private void getSetRunningTime(){
        long iterationTime = System.nanoTime();
        long difference = iterationTime - timeStarted;
        long seconds = difference / 1000000000;
        long minutes = seconds / 60;
        String runningTime = minutes + " minutes";
        if(seconds % 60 !=0){
            seconds = seconds - (minutes * 60);
            //runningTime = runningTime + " " + seconds;//TODO:synch server side with client timer
        }
        quartzJobStats.setRunningTime(runningTime);
    }


    private void getSetTimeStarted(){
        LocalTime localTime = new LocalTime();
        quartzJobStats.setTimeStarted(localTime.toString());
        timeStarted = System.nanoTime();
        getSetRunningTime();
    }


    private void setQuartzJobStatsStatus(){
        quartzJobStats.setStatus(ApplicationConstants.STARTED_STATUS);
    }

}
