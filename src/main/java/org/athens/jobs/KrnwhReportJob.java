package org.athens.jobs;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.apache.log4j.Logger;
import org.athens.domain.KRNWH;
import org.athens.domain.KrnwhLog;
import org.athens.common.ApplicationConstants;
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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.DisallowConcurrentExecution;

import java.util.concurrent.TimeUnit;

@DisallowConcurrentExecution
public class KrnwhReportJob implements Job {

	final static Logger log = Logger.getLogger(KrnwhReportJob.class);

	private String token = "";

	private KrnwhDaoImpl krnwhDao;

	private KrnwhLogDaoImpl krnwhLogDao;

	private KrnwhJobSettings krnwhJobSettings;



	public void execute(JobExecutionContext context) throws JobExecutionException {

		try {

			//if(!currentlyRunning(context)) {

				DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
				Date date = new Date();
				String formattedDate = dateFormat.format(date);

				log.info("executing report : " + formattedDate.toString());

				JobKey jobKey = new JobKey("krnwhJob", "atns");
				JobDetail jobDetail = context.getScheduler().getJobDetail(jobKey);

				setKrnwhReportResources(jobDetail);
			TimeUnit.MINUTES.sleep(1);
				/**
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

				KrnwhLog krnwhLog = new KrnwhLog();
				krnwhLog.setKstatus(ApplicationConstants.STARTED_STATUS);
				krnwhLog.setKtot(new BigDecimal(0));
				krnwhLog.setKadtcnt(new BigDecimal(0));
				krnwhLog.setKaudit(ApplicationConstants.EMPTY_AUDIT);
				krnwhLog.setKdate(new BigDecimal(formattedDate));
				KrnwhLog savedKrnwhLog = krnwhLogDao.save(krnwhLog);

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

				processReportDataFromRequest(savedKrnwhLog);
				**/
		//}

		}catch (Exception e){
			log.warn("log error..");
			//TODO: log error
		}

	}



	public void processReportDataFromRequest(KrnwhLog savedKrnwhLog){
		System.out.println("running report using jersey ...");

		String url = "https://secure4.saashr.com/ta/rest/v1/report/saved/70165985";

		DefaultClientConfig client = new DefaultClientConfig();
		WebResource resource = Client.create(client)
				.resource(url);

		WebResource.Builder builder = resource.accept("text/csv");
		log.info("setting bearer token " + token);
		builder.header("Authentication", "Bearer " + token);

		ClientResponse response  = builder.get(ClientResponse.class);
		String csvData = response.getEntity(String.class);

		//log.info(csvData);
		readCsvDataString(csvData.toString(), savedKrnwhLog);
	}




	public void readCsvDataString(String csvData, KrnwhLog savedKrnwhLog){
		String line = "";
		int count = 0;
		int errorCount = 0;

		InputStream is = new ByteArrayInputStream(csvData.getBytes());

		try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

			while ((line = br.readLine()) != null) {

				if(count != 0) {

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

					log.info(date2.toString() + " : " + date.toString());
					System.out.println(line);

					String empIdS = punchData[0].replaceAll("^\"|\"$", "");
					String type = punchData[2].replaceAll("^\"|\"$", "");
					String clockS = punchData[3].replaceAll("^\"|\"$", "");
					String badgeIdS = punchData[4].replaceAll("^\"|\"$", "");

					if (type == "Active") type = "A";
					if (type == "LOA") type = "L";
					if (type != "L" && type != "A") type = "O";


					if (empIdS.equals("")) empIdS = "0";
					if (badgeIdS.equals("")) badgeIdS = "0";

					//log.info("punchData [empId= " + empIdS + " , badgeId=" + badgeIdS + "]");

					BigDecimal badgeId = new BigDecimal(badgeIdS);
					BigDecimal empId = new BigDecimal(empIdS);
					BigDecimal punch = new BigDecimal(date2);

					//krnwh.getFpempn(), krnwh.setFppunc(), krnwh.setFptype(),
					//krnwh.getFpclck(), krnwh.setFpbadg(), krnwh.setFpfkey(),
					//krnwh.getFppcod(), krnwh.setFstatus()

					KRNWH krnwh = new KRNWH();
					krnwh.setFpempn(empId);
					krnwh.setFppunc(punch);
					krnwh.setFptype(type);
					krnwh.setFpclck(clockS);
					krnwh.setFpbadg(badgeId);
					krnwh.setFpfkey("843");//*
					krnwh.setFppcod(new BigDecimal(0));//*
					krnwh.setFstatus("m");
					krnwh.setKrnlogid(savedKrnwhLog.getId());

					if (count == 3) krnwh.setFstatus("aa");

					log.info(krnwh.toString());

					try {

						if(count == 19){

							break;
						}
						processPersistence(krnwh);

					} catch (Exception e) {
						errorCount++;
						//
						log.warn("error");
					}

				}

				count++;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		log.info("count: " + count);
		log.info("error count: " + errorCount);

	}


	public void setKrnwhReportResources(JobDetail jobDetail){
		KrnwhDaoImpl krnwhDao = (KrnwhDaoImpl)jobDetail.getJobDataMap().get("krnwhDao");
		this.krnwhDao = krnwhDao;

		KrnwhLogDaoImpl krnwhLogDao = (KrnwhLogDaoImpl)jobDetail.getJobDataMap().get("krnwhLogDao");
		this.krnwhLogDao = krnwhLogDao;

		KrnwhJobSettings krnwhJobSettings = (KrnwhJobSettings)jobDetail.getJobDataMap().get("krnwhJobSettings");
		this.krnwhJobSettings = krnwhJobSettings;
	}


	public void processPersistence(KRNWH krnwh){
		if(krnwh.getFppunc() != null) {
			KRNWH skrnwh = krnwhDao.save(krnwh);
			if (skrnwh == null) {
				log.warn("error saving" + krnwh.getFppunc());
				//errorCount++;
			}
		}
	}

	public boolean currentlyRunning(JobExecutionContext context){
		try {
			Scheduler scheduler = new StdSchedulerFactory().getScheduler();
			List<JobExecutionContext> runningJobs = scheduler.getCurrentlyExecutingJobs();
			for (JobExecutionContext jobCtx : runningJobs) {
				String thisJobName = jobCtx.getJobDetail().getKey().getName();
				String thisGroupName = jobCtx.getJobDetail().getKey().getGroup();
				if ("krnwhJob".equalsIgnoreCase(thisJobName) && "atns".equalsIgnoreCase(thisGroupName)
					//&& !jobCtx.getFireTime().equals(context.getFireTime())
						) {
					if(jobCtx.getJobDetail().getJobDataMap().getBooleanFromString("running")){
						log.info("running detail..");
						jobCtx.getJobDetail().getJobDataMap().put("running", false);
					}else{
						log.info("not running detail...");
						jobCtx.getJobDetail().getJobDataMap().put("running", true);
					}
					log.info("instance running...");
					return true;
				}
			}
			log.info("not running...");
		}catch (Exception e){
			log.warn("something went wrong currently running?");
			return false;
		}
		return false;
	}

}