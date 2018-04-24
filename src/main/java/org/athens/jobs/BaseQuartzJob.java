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
import org.athens.dao.impl.KronosQuartzIngestLogDaoImpl;
import org.athens.domain.KronosQuartzIngestLog;
import org.athens.domain.KronosQuartzJobStats;
import org.athens.domain.KronosWorkHour;
import org.athens.domain.KronosQuartzJobSettings;
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
import org.joda.time.LocalTime;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.core.io.ClassPathResource;

/**Im going to rename all quartz job classes**/

@DisallowConcurrentExecution
public class BaseQuartzJob implements Job {

    final static Logger log = Logger.getLogger(BaseQuartzJob.class);

    private String authenticationToken = "";

    private long timeStarted = 0;

    private int totalLines     = 0;
    private int totalSaved     = 0;
    private int totalError     = 0;
    private int totalFound     = 0;
    private int totalProcessed = 0;

    private String report;
    private JobKey jobKey;

    private String jobDescription;
    private boolean passedIteration = false;

    private KronosQuartzIngestLog kronosIngestLog;

    private KronosWorkHourDaoImpl kronosWorkHourDao;
    private KronosQuartzIngestLogDaoImpl kronosIngestLogDao;
    private KronosQuartzJobSettings kronosWorkHourJobSettings;
    private KronosQuartzJobStats kronosQuartzJobStats;


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
             kronosQuartzJobStats.setCount(n);
             TimeUnit.SECONDS.sleep(7);
             }**/

            setLocalDefined(context);
            resetQuartzJobStats();
            setQuartzJobStatsStatus();
            getSetTimeStarted();
            clearExistingLogs();
            getSetKrnwhQuartzJobLog();

            //performKronosAuthentication();
            //performKronosReportRequestProcess();
            readCsvDataFileProcess();

        } catch (Exception e) {
            if(kronosIngestLog != null){
                kronosIngestLog.setKstatus(ApplicationConstants.ERROR_STATUS);
                kronosIngestLogDao.save(kronosIngestLog);
            }
            log.info("something went wrong setting up quartz job");
            e.printStackTrace();
        }
    }


    private void readCsvDataFileProcess(){
        try {
            ClassPathResource crp = new ClassPathResource("data.csv");
            File file = crp.getFile();
            String csvData = new String(Files.readAllBytes(file.toPath()));
            readKronosCsvDataSetTotalAmount(csvData);
            readKronosCsvDataSave(csvData);
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    private void performKronosAuthentication(){
        JsonObject credentials = getAuthenticationCredentials();

        WebResource resource = Client.create(new DefaultClientConfig())
                .resource(ApplicationConstants.KRONOS_LOGIN_URI);

        WebResource.Builder builder = resource.accept("application/json");
        builder.type("application/json");
        builder.header("api-key", kronosWorkHourJobSettings.getApiKey());

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
        innerObject.addProperty("username", kronosWorkHourJobSettings.getUsername());
        innerObject.addProperty("password", kronosWorkHourJobSettings.getPassword());
        innerObject.addProperty("company", kronosWorkHourJobSettings.getCompany());

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
            totalLines = 0;
            int count = 0;
            while ((line = br.readLine()) != null) {
                if(count != 0) totalLines++;
                count++;
            }

            kronosQuartzJobStats.setTotal(totalLines);
            kronosQuartzJobStats.setStatus(ApplicationConstants.RUNNING_STATUS);

            updateQuartzIngestLog(ApplicationConstants.RUNNING_STATUS);
            
        }catch (Exception e){
            log.warn("issue setting");
        }
    }


    private void readKronosCsvDataSave(String csvData){
        String line = "";
        getSetRunningTime();
        InputStream is = new ByteArrayInputStream(csvData.getBytes());
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            int n = 0;//not me
            while ((line = br.readLine()) != null) {

                if(n != 0) {

                    String[] kronosPunchData = line.split(ApplicationConstants.CSV_DELIMETER);

                    /**
                    String startTime = getWorkHourTime(kronosPunchData, true);
                    String endTime = getWorkHourTime(kronosPunchData, false);

                    if(!startTime.equals("")){
                        KronosWorkHour kronosWorkHourStart = getSetKronosWorkHourFromData(kronosPunchData, startTime);
                        checkExistingKronosWorkHourPersist(kronosWorkHourStart);
                    }

                    if(!endTime.equals("")){
                        KronosWorkHour kronosWorkHourEnd = getSetKronosWorkHourFromData(kronosPunchData, endTime);
                        checkExistingKronosWorkHourPersist(kronosWorkHourEnd);
                    }
                     **/

                    if(kronosPunchData.length > 8 && kronosPunchData[8] != null) {
                        BigDecimal startDate = getPunchDateDataTimeFile(kronosPunchData[8]);

                        if (startDate != null
                                //&& startDate.compareTo(new BigDecimal("20180410000000")) < 0
                                ) {
                            KronosWorkHour kronosWorkHourStart = getSetKronosWorkHourFromDataFile(kronosPunchData, startDate);
                            checkExistingKronosWorkHourPersist(kronosWorkHourStart);
                        }
                    }

                    if(kronosPunchData.length > 9 && kronosPunchData[9] != null) {
                        BigDecimal endDate = getPunchDateDataTimeFile(kronosPunchData[9]);

                        if (endDate != null
                                //&& endDate.compareTo(new BigDecimal("20180410000000")) < 0
                                ) {
                            KronosWorkHour kronosWorkHourEnd = getSetKronosWorkHourFromDataFile(kronosPunchData, endDate);
                            checkExistingKronosWorkHourPersist(kronosWorkHourEnd);
                        }
                    }
                    totalProcessed++;
                    kronosQuartzJobStats.setProcessed(totalProcessed);

                }

                getSetRunningTime();

                kronosQuartzJobStats.setErrored(totalError);
                updateQuartzIngestLog(ApplicationConstants.RUNNING_STATUS);

                n++;
            }

        } catch (Exception e) {
            totalError++;
            e.printStackTrace();
        }

        updateQuartzIngestLog(ApplicationConstants.COMPLETE_STATUS);

        getSetRunningTime();

    }

    //TODO
    private BigDecimal getPunchDateDataTimeFile(String rawDateTime) throws ParseException {
        String cleanDate = cleanupString(rawDateTime);
        BigDecimal punchDate = null;

        if (!cleanDate.equals("")) {
            punchDate = new BigDecimal(cleanDate + "00");
        }

        return punchDate;
    }


    //TODO
    private KronosWorkHour getSetKronosWorkHourFromDataFile(String[] kronosPunchData, BigDecimal punchDate) {

        KronosWorkHour kronosWorkHour = new KronosWorkHour();

        try {

            String employeeIdString = cleanupString(kronosPunchData[0]);
            String badgeIdString = cleanupString(kronosPunchData[6]);
            //String employeeStatus = cleanupString(kronosPunchData[5]);
            String employeeStatus = "q";

            if (employeeIdString == null || employeeIdString.equals("")) employeeIdString = "0";
            if (badgeIdString == null || badgeIdString.equals("")) badgeIdString = "0";

            if (employeeStatus.equals("Active")) employeeStatus = "A";
            if (employeeStatus.equals("LOA")) employeeStatus = "L";
            if (!employeeStatus.equals("L") && !employeeStatus.equals("A")) employeeStatus = "O";

            BigDecimal employeeId = new BigDecimal(employeeIdString);
            BigDecimal badgeId = new BigDecimal(badgeIdString);

            kronosWorkHour.setFpempn(employeeId);
            kronosWorkHour.setFppunc(punchDate);
            kronosWorkHour.setFptype(employeeStatus);
            kronosWorkHour.setFpbadg(badgeId);

            kronosWorkHour.setFpfkey("");//*
            kronosWorkHour.setFppcod(new BigDecimal(0));//*
            kronosWorkHour.setFstatus("q");//*

            kronosWorkHour.setKrnlogid(kronosIngestLog.getId());

        }catch (Exception e) {

            e.printStackTrace();
        }

        return kronosWorkHour;
    }



    private KronosWorkHour getSetKronosWorkHourFromData(String[] kronosPunchData, String time) {

        KronosWorkHour kronosWorkHour = new KronosWorkHour();

        try {
            BigDecimal punchDate = getFormattedPunchDate(kronosPunchData[ApplicationConstants.KRONOS_DATE_STAMP_COLUMN], kronosPunchData, time);

            if(punchDate != null) {

                String employeeIdString = cleanupString(kronosPunchData[ApplicationConstants.KRONOS_EMPLOYEE_ID_COLUMN]);
                String badgeIdString = cleanupString(kronosPunchData[ApplicationConstants.KRONOS_BADGE_ID_COLUMN]);
                String employeeStatus = cleanupString(kronosPunchData[ApplicationConstants.KRONOS_EMPLOYEE_STATUS_COLUMN]);

                if (employeeIdString == null || employeeIdString.equals("")) employeeIdString = "0";
                if (badgeIdString == null || badgeIdString.equals("")) badgeIdString = "0";

                if (employeeStatus.equals("Active")) employeeStatus = "A";
                if (employeeStatus.equals("LOA")) employeeStatus = "L";
                if (!employeeStatus.equals("L") && !employeeStatus.equals("A")) employeeStatus = "O";

                BigDecimal employeeId = new BigDecimal(employeeIdString);
                BigDecimal badgeId = new BigDecimal(badgeIdString);

                kronosWorkHour.setFpempn(employeeId);
                kronosWorkHour.setFppunc(punchDate);
                kronosWorkHour.setFptype(employeeStatus);
                kronosWorkHour.setFpbadg(badgeId);

                kronosWorkHour.setFpfkey("");//*
                kronosWorkHour.setFppcod(new BigDecimal(0));//*
                kronosWorkHour.setFstatus("q");//*

                kronosWorkHour.setKrnlogid(kronosIngestLog.getId());

            }

        }catch (Exception e) {

            e.printStackTrace();
        }

        return kronosWorkHour;
    }




    private void checkExistingKronosWorkHourPersist(KronosWorkHour kronosWorkHour){
        try {

            if(kronosWorkHour.getFppunc() != null &&
                    ((kronosWorkHour.getFpbadg().compareTo(new BigDecimal(0)) != 0) ||
                            (kronosWorkHour.getFpempn().compareTo(new BigDecimal(0)) != 0))) {

                //if (totalProcessed % 2 == 0) kronosWorkHour.setFstatus("aa");

                KronosWorkHour existingKronosWorkHour = getExistingKronosWorkHour(kronosWorkHour);

                if (existingKronosWorkHour != null) {
                    totalFound++;
                    log.info(this.jobKey.getName() + ": found: " + totalFound +  ", processed: " + totalProcessed);
                    kronosQuartzJobStats.setFound(totalFound);
                }

                if (existingKronosWorkHour == null) {
                    KronosWorkHour skronosWorkHour = kronosWorkHourDao.save(kronosWorkHour);
                    totalSaved++;
                    log.info(this.jobKey.getName() + ": saved: " + totalSaved + ", processed: " + totalProcessed);
                    log.info(this.jobKey.getName() + " : " + skronosWorkHour.toString());
                    kronosQuartzJobStats.setSaved(totalSaved);
                }
            }

        } catch (Exception e) {
            kronosQuartzJobStats.addAuditDetails(kronosWorkHour);
            totalError++;
            log.warn("error");
            e.printStackTrace();
        }

    }


    private KronosWorkHour getExistingKronosWorkHour(KronosWorkHour kronosWorkHour){

        int value = 0;
        String kronosIdentifier = "";

        KronosWorkHour existingKronosWorkHour = null;

        if(kronosWorkHour.getFpbadg().compareTo(new BigDecimal(0)) != 0  &&
                kronosWorkHour.getFpempn().compareTo(new BigDecimal(0)) != 0){

            log.info("find by both");
            existingKronosWorkHour = kronosWorkHourDao.findByPunchBadgeIdEmployeeId(kronosWorkHour.getFppunc(), kronosWorkHour.getFpbadg(), kronosWorkHour.getFpempn());
            kronosIdentifier = kronosWorkHour.getFppunc().toString() + kronosWorkHour.getFpbadg().toString() + kronosWorkHour.getFpempn().toString();

        }else if(kronosWorkHour.getFpbadg().compareTo(new BigDecimal(0)) != 0 &&
                kronosWorkHour.getFpempn().compareTo(new BigDecimal(0)) == 0){

            log.info("find by badge id");
            existingKronosWorkHour = kronosWorkHourDao.findByPunchBadgeId(kronosWorkHour.getFppunc(), kronosWorkHour.getFpbadg());
            kronosIdentifier = kronosWorkHour.getFppunc().toString() + kronosWorkHour.getFpbadg().toString();

        }else if(kronosWorkHour.getFpempn().compareTo(new BigDecimal(0)) != 0 &&
                kronosWorkHour.getFpbadg().compareTo(new BigDecimal(0)) == 0){

            existingKronosWorkHour = kronosWorkHourDao.findByPunchEmployeeId(kronosWorkHour.getFppunc(), kronosWorkHour.getFpempn());
            log.info("find by employee id");
            kronosIdentifier = kronosWorkHour.getFppunc().toString() + kronosWorkHour.getFpempn().toString();

        }

        if(kronosQuartzJobStats.getExistsMap().containsKey(kronosIdentifier)){
            value = kronosQuartzJobStats.getExistsMap().get(kronosIdentifier);
        }

        if(existingKronosWorkHour == null){
            kronosIdentifier = "";
        }

        if(existingKronosWorkHour != null && !kronosIdentifier.equals("")){
            value++;
            kronosQuartzJobStats.setExistsMapValue(kronosIdentifier, value);
        }

        return existingKronosWorkHour;

    }



    private void getSetKrnwhQuartzJobLog(){
        BigDecimal dateTime = getLogDateTimeFormatted();
        log.info("executing report : " + dateTime.toString());

        KronosQuartzIngestLog existingIngestLog = kronosIngestLogDao.findByDate(dateTime);

        if (existingIngestLog != null) {
            this.kronosIngestLog = existingIngestLog;
        }else{
            KronosQuartzIngestLog nKronosIngestLog = new KronosQuartzIngestLog();
            nKronosIngestLog.setKtype(jobDescription);
            nKronosIngestLog.setKstatus(ApplicationConstants.STARTED_STATUS);
            nKronosIngestLog.setKtot(new BigDecimal(0));
            nKronosIngestLog.setKproc(new BigDecimal(0));
            nKronosIngestLog.setKadtcnt(new BigDecimal(0));
            nKronosIngestLog.setKaudit(ApplicationConstants.EMPTY_AUDIT);
            nKronosIngestLog.setKdate(dateTime);
            this.kronosIngestLog = kronosIngestLogDao.save(nKronosIngestLog);
        }
        kronosQuartzJobStats.setKronosIngestId(kronosIngestLog.getId());
    }


    private String getAuditJsonRepresentation(){
        Gson gsonObj =  new GsonBuilder().setPrettyPrinting().create();
        return gsonObj.toJson(kronosQuartzJobStats.getAuditDetails());
    }



    private BigDecimal getFormattedPunchDate(String rawDate, String[] kronosPunchData, String time) throws ParseException {
        String cleanDate = cleanupString(rawDate);

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date date = sdf.parse(cleanDate);

        DateFormat sdf2 = new SimpleDateFormat("MM/dd/yyyy");

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(sdf2.format(date));
        stringBuffer.append(time);

        String dateTimeString = stringBuffer.toString();

        DateFormat sdf3 = new SimpleDateFormat("MM/dd/yyyy hh:mmaa");
        Date dateTime = sdf3.parse(dateTimeString);

        DateFormat sdf4 = new SimpleDateFormat("yyyyMMddHHmmss");
        String fullDateTime = sdf4.format(dateTime);

        return new BigDecimal(fullDateTime);
    }


    private String getWorkHourTime(String[] kronosPunchData, boolean workHourStart) {
        StringBuffer timeBuffer = new StringBuffer();

        String time = cleanupString(kronosPunchData[ApplicationConstants.KRONOS_WORK_HOUR_START_COLUMN]);

        if (!workHourStart) {
            time = cleanupString(kronosPunchData[ApplicationConstants.KRONOS_WORK_HOUR_END_COLUMN]);
        }

        if(!time.equals("")) {
            timeBuffer.append(" ");
            timeBuffer.append(time);
            timeBuffer.append("m");
        }

        return timeBuffer.toString();
    }


    private BigDecimal getLogDateTimeFormatted(){
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String fDate = dateFormat.format(date);
        return new BigDecimal(fDate);
    }


    private void updateQuartzIngestLog(String status){
        kronosIngestLog.setKtot(new BigDecimal(totalLines));
        kronosIngestLog.setKadtcnt(new BigDecimal(totalError));
        kronosIngestLog.setKproc(new BigDecimal(totalProcessed));
        kronosIngestLog.setKaudit(getAuditJsonRepresentation());
        kronosIngestLog.setKstatus(status);
        kronosIngestLogDao.update(kronosIngestLog);//TODO:uncomment
        kronosQuartzJobStats.setStatus(status);
        getSetRunningTime();
    }


    private void clearExistingLogs(){
        List<KronosQuartzIngestLog> kronosIngestLogs = kronosIngestLogDao.findAllByStatus(ApplicationConstants.RUNNING_STATUS);
        for(KronosQuartzIngestLog kronosIngestLog : kronosIngestLogs){
            log.info(kronosIngestLog);
            kronosIngestLog.setKstatus(ApplicationConstants.INTERRUPTED_STATUS);
            kronosIngestLogDao.updateStatus(kronosIngestLog);//TODO: uncomment
        }
    }


    private void resetQuartzJobStats(){
        this.kronosQuartzJobStats.setTotal(0);
        this.kronosQuartzJobStats.setSaved(0);
        this.kronosQuartzJobStats.setFound(0);
        this.kronosQuartzJobStats.setErrored(0);
        this.kronosQuartzJobStats.setProcessed(0);
        this.kronosQuartzJobStats.setStatus(null);
        this.kronosQuartzJobStats.setKronosIngestId(new BigDecimal(0));
    }


    private void setLocalDefined(JobExecutionContext context) throws SchedulerException {
        JobDetail jobDetail = context.getScheduler().getJobDetail(this.jobKey);
        this.kronosWorkHourDao = (KronosWorkHourDaoImpl) jobDetail.getJobDataMap().get(ApplicationConstants.KRNWH_DAO_LOOKUP);
        this.kronosIngestLogDao = (KronosQuartzIngestLogDaoImpl) jobDetail.getJobDataMap().get(ApplicationConstants.KRNWH_LOG_DAO_LOOKUP);
        this.kronosWorkHourJobSettings = (KronosQuartzJobSettings) jobDetail.getJobDataMap().get(ApplicationConstants.KRNWH_JOB_SETTINGS_LOOKUP);
        this.kronosQuartzJobStats = (KronosQuartzJobStats) jobDetail.getJobDataMap().get(ApplicationConstants.QUARTZ_JOB_STATS_LOOKUP);
        this.jobDescription = jobDetail.getJobDataMap().get(ApplicationConstants.ATHENS_QUARTZ_JOB_DESCRIPTION_LOOKUP).toString();
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
        kronosQuartzJobStats.setRunningTime(runningTime);
    }


    private void getSetTimeStarted(){
        LocalTime localTime = new LocalTime();
        kronosQuartzJobStats.setTimeStarted(localTime.toString());
        timeStarted = System.nanoTime();
        getSetRunningTime();
    }


    private void setQuartzJobStatsStatus(){
        kronosQuartzJobStats.setStatus(ApplicationConstants.STARTED_STATUS);
    }

    private String cleanupString(String raw){
        return raw.replaceAll("^\"|\"$", "");
    }

}
