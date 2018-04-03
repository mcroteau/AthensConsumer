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
public class KrnwhDailyJob implements Job {

	final static Logger log = Logger.getLogger(KrnwhDailyJob.class);

	private String token = "";

	private int totalSaved = 0;

	private int errorCount = 0;

	private KrnwhLog krnwhLog;

	private KrnwhDaoImpl krnwhDao;

	private KrnwhLogDaoImpl krnwhLogDao;

	private KrnwhJobSettings krnwhJobSettings;

	private Map<String, Integer> foundMap = new HashMap<String, Integer>();

	public void execute(JobExecutionContext context) throws JobExecutionException {

		try {

			//if(!currentlyRunning(context)) {

				DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
				Date date = new Date();
				String formattedDate = dateFormat.format(date);

				log.info("executing report : " + formattedDate.toString());

				JobKey jobKey = new JobKey("krnwhJob", "atns");
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
		//}

		}catch (Exception e){
			log.warn("log error..");
			//TODO: log error
		}

	}



	public void processReportDataFromRequest(){
		System.out.println("running report using jersey ...");

		String url = "https://secure4.saashr.com/ta/rest/v1/report/saved/70184453";

		DefaultClientConfig client = new DefaultClientConfig();
		WebResource resource = Client.create(client)
				.resource(url);

		WebResource.Builder builder = resource.accept("text/csv");
		log.info("setting bearer token " + token);
		builder.header("Authentication", "Bearer " + token);

		ClientResponse response  = builder.get(ClientResponse.class);
		String csvData = response.getEntity(String.class);

		//log.info(csvData);
		readCsvDataString(csvData.toString());
	}




	public void readCsvDataString(String csvData){
		String line = "";
		int count = 0;
		int found = 0;

		InputStream is = new ByteArrayInputStream(csvData.getBytes());

		try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

			while ((line = br.readLine()) != null) {

				if(count != 0) {//TODO: remo

					//log.info("\n\n");

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

					//krnwh.getFpempn(), krnwh.setFppunc(), krnwh.setFptype(),
					//krnwh.getFpclck(), krnwh.setFpbadg(), krnwh.setFpfkey(),
					//krnwh.getFppcod(), krnwh.setFstatus()

					Krnwh krnwh = new Krnwh();
					krnwh.setFpempn(empId);
					//if(count % 2 ==0) krnwh.setFpempn(new BigDecimal("0"));
					krnwh.setFppunc(punch);
					krnwh.setFptype(type);
					krnwh.setFpclck(clockS);

					krnwh.setFpbadg(badgeId);
					//if(count % 3 ==0) krnwh.setFpbadg(new BigDecimal("0"));
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


					//if (count == 3) krnwh.setFstatus("aa");

					//log.info(krnwh.toString());

					try {

						if(existingKrnwh == null) {
							processPersistence(krnwh);
							log.info("saved: " + totalSaved + ", count: " + count);
						}else{
							found++;
							log.info("found: " + found +  ", count: " + count);
						}

						if(count %50==0){
							Gson g =  new GsonBuilder().setPrettyPrinting().create();
							String js = g.toJson(foundMap);
							System.out.println(js);
						}


					} catch (Exception e) {
						errorCount++;
						log.warn("error");
						e.printStackTrace();
					}

				}

				count++;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		log.info("count: " + count);
		log.info("error count: " + errorCount);
		log.info("totalSaved: " + totalSaved);
		log.info("found : " + found);

		Gson gsonObj =  new GsonBuilder().setPrettyPrinting().create();
		String jsonStr = gsonObj.toJson(foundMap);
		System.out.println(jsonStr);

		krnwhLog.setKtot(new BigDecimal(totalSaved));
		krnwhLog.setKadtcnt(new BigDecimal(errorCount));
		krnwhLog.setKstatus(ApplicationConstants.COMPLETE_STATUS);
		krnwhLogDao.update(krnwhLog);

	}



	public void processPersistence(Krnwh krnwh){
		if(krnwh.getFppunc() != null) {
			Krnwh skrnwh = krnwhDao.save(krnwh);
			if (skrnwh != null) {
				totalSaved++;
				//log.info("saved : " + krnwh.getFppunc());
			}else{
				log.warn("error saving : " + krnwh.getFppunc());
				errorCount++;
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