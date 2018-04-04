package org.athens;
import org.apache.log4j.Logger;

import com.sun.jersey.api.client.config.DefaultClientConfig;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import org.athens.domain.Krnwh;
import org.athens.domain.Krnwh;
import org.athens.domain.KrnwhLog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.math.BigDecimal;

import org.athens.dao.impl.KrnwhDaoImpl;
import org.athens.dao.impl.KrnwhLogDaoImpl;
import org.athens.common.ApplicationConstants;

import java.util.List;


public class ApplicationRunner {

	private String token = "";

	@Value("${api.key}")
	private String apiKey;

	@Value("${api.company}")
	private String company;

	@Value("${api.username}")
	private String username;

	@Value("${api.password}")
	private String password;

	@Value("${krnwh.report}")
	private String report;

	
	@Autowired
	private KrnwhDaoImpl dao;

	@Autowired
	private KrnwhLogDaoImpl logDao;


	final static Logger log = Logger.getLogger(ApplicationRunner.class);

	public ApplicationRunner(){}


	public void run(){

		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		String formattedDate = dateFormat.format(date);
		log.info(formattedDate.toString());

		KrnwhLog todaysKrnwhLog = logDao.findByDate(new BigDecimal(formattedDate));

		if(todaysKrnwhLog != null){
			todaysKrnwhLog.setKstatus(ApplicationConstants.COMPLETE_STATUS);
			todaysKrnwhLog.setKtot(new BigDecimal(124));
			todaysKrnwhLog.setKaudit("g");
			todaysKrnwhLog.setKadtcnt(new BigDecimal(3));
			todaysKrnwhLog.setKdate(new BigDecimal(20180323));
			logDao.update(todaysKrnwhLog);
		}

		KrnwhLog krnwhLog = new KrnwhLog();
		krnwhLog.setKstatus(ApplicationConstants.STARTED_STATUS);
		krnwhLog.setKtot(new BigDecimal(0));
		krnwhLog.setKadtcnt(new BigDecimal(0));
		krnwhLog.setKaudit(ApplicationConstants.EMPTY_AUDIT);
		krnwhLog.setKdate(new BigDecimal(formattedDate));
		KrnwhLog savedKrnwhLog = logDao.save(krnwhLog);

		log.info("savedKrnwhLog : " + savedKrnwhLog.getId());

		
		List<KrnwhLog> krnwhLogs = logDao.list(10, 0);
		
		log.info("krnwhLogs : " + krnwhLogs.size());
		
		for (KrnwhLog klog : krnwhLogs) {
			log.info(klog);
		}

		return;
		/**
		log.info("apiKey: " + apiKey);
		String authuri = "https://secure4.saashr.com/ta/rest/v1/login";


		JsonObject innerObject = new JsonObject();
		innerObject.addProperty("username", username);
		innerObject.addProperty("password", password);
		innerObject.addProperty("company", company);


		JsonObject jsonObject = new JsonObject();
		jsonObject.add("credentials", innerObject);

		WebResource resource = Client.create(new DefaultClientConfig())
			.resource(authuri);
		            
		WebResource.Builder builder = resource.accept("application/json");
		builder.type("application/json");
		builder.header("api-key", apiKey);

	
    	ClientResponse cresponse  = builder.post(ClientResponse.class, jsonObject.toString());

		String jsonOutput = cresponse.getEntity(String.class);

		if (cresponse.getStatus() != 200) {
		   log.info(cresponse.toString());
		   throw new RuntimeException("Failed : HTTP error code : "
				+ cresponse.getStatus());
		}

		System.out.println("Token json response from server .... \n");
		log.info(jsonOutput);

		JsonParser jsonParser = new JsonParser();
		JsonObject tokenObj = (JsonObject)jsonParser.parse(jsonOutput);

		
		this.token = tokenObj.get("token").toString();
		this.token = token.replaceAll("^\"|\"$", "");
		
		processReportDataFromRequest();
		 **/
	}



	public void processReportDataFromRequest(){
		System.out.println("running report using jersey ...");

		String url = "https://secure4.saashr.com/ta/rest/v1/report/saved/70165985";
		
		DefaultClientConfig client = new DefaultClientConfig();
		WebResource resource = Client.create(client)
				.resource(url);

		WebResource.Builder builder = resource.accept("text/csv");
		log.info("setting bearer token " + token);
		//builder.header("Accept", "application/xml");
		builder.header("Authentication", "Bearer " + token);

        ClientResponse response  = builder.get(ClientResponse.class);
		String csvData = response.getEntity(String.class);

		log.info(csvData);
        readCsvDataString(csvData.toString());
	}




	public void readCsvDataString(String csvData){
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

					Krnwh krnwh = new Krnwh();
					krnwh.setFpempn(empId);
					krnwh.setFppunc(punch);
					krnwh.setFptype(type);
					krnwh.setFpclck(clockS);
					krnwh.setFpbadg(badgeId);
					krnwh.setFpfkey("843");//*
					krnwh.setFppcod(new BigDecimal(0));//*
					krnwh.setFstatus("m");

					if (count == 3) krnwh.setFstatus("aa");

					log.info(krnwh.toString());

					try {

						if(count == 7){
							
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

	public void processPersistence(Krnwh krnwh){
	}

}

















