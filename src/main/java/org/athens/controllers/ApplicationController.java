package org.athens.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;

import org.athens.domain.KronosQuartzJobStats;
import org.athens.domain.KronosQuartzIngestLog;
import org.athens.domain.KronosWorkHour;
import org.quartz.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

import org.athens.dao.impl.KronosQuartzIngestLogDaoImpl;
import org.athens.dao.impl.KronosWorkHourDaoImpl;
import org.quartz.JobKey;

import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.athens.common.ApplicationConstants;
import org.athens.common.CsvUtils;


@Controller
public class ApplicationController {

    final static Logger log = Logger.getLogger(ApplicationController.class);


    @Autowired
    private KronosWorkHourDaoImpl dao;

    @Autowired
    private KronosQuartzIngestLogDaoImpl logDao;

    @Autowired
    private KronosQuartzJobStats dailyKronosQuartzJobStats;

    @Autowired
    private KronosQuartzJobStats weeklyKronosQuartzJobStats;


    @RequestMapping(value="/", method=RequestMethod.GET)
    public String list(final RedirectAttributes redirect){
        return "redirect:jobs";
    }


    @RequestMapping(value="/jobs", method=RequestMethod.GET)
    public String jobs(ModelMap model){
        model.addAttribute("todaysDate", getFullDateTime(false, ApplicationConstants.DATE_SEARCH_FORMAT));
        model.addAttribute("tomorrowsDate", getFullDateTime(true, ApplicationConstants.DATE_SEARCH_FORMAT));
        model.addAttribute("runningJobsLinkActive", "active");
        return "jobs";
    }


    @RequestMapping(value="/ingests", method=RequestMethod.GET)
    public String ingests(ModelMap model,
                       HttpServletRequest request,
                       final RedirectAttributes redirect,
                       @RequestParam(value="offset", required = false ) String offset,
                       @RequestParam(value="max", required = false ) String max,
                       @RequestParam(value="page", required = false ) String page,
                       @RequestParam(value="sort", required = false ) String sort,
                       @RequestParam(value="order", required = false ) String order){

        if(page == null){
            page = "1";
        }

        List<KronosQuartzIngestLog> kronosIngestLogs;

        if(offset != null) {
            int m = 10;
            if(max != null){
                m = Integer.parseInt(max);
            }
            int o = Integer.parseInt(offset);
            kronosIngestLogs = logDao.list(m, o);
            //kronosIngestLogs = generateMockKrnwhLogs(m, o);
        }else{
            kronosIngestLogs = logDao.list(10, 0);
            //kronosIngestLogs = generateMockKrnwhLogs(10, 0);
        }

        int count = logDao.count();
        //int count = 192;

        model.addAttribute("total", count);

        model.addAttribute("sort", sort);
        model.addAttribute("order", order);

        model.addAttribute("activePage", page);
        model.addAttribute("resultsPerPage", 10);

        model.addAttribute("ingestsLinkActive", "active");

        model.addAttribute("todaysDate", getFullDateTime(false, ApplicationConstants.DATE_SEARCH_FORMAT));
        model.addAttribute("tomorrowsDate", getFullDateTime(true, ApplicationConstants.DATE_SEARCH_FORMAT));

        model.addAttribute("kronosIngestLogs", kronosIngestLogs);

        return "ingests";
    }



    @RequestMapping(value="/search", method=RequestMethod.GET)
    public String search(ModelMap model,
                         @RequestParam(value="startDate", required = false ) BigDecimal startDate,
                         @RequestParam(value="endDate", required = false ) BigDecimal endDate){


        if(startDate == null && endDate == null) {
            startDate = new BigDecimal(getFullDateTime(false, ApplicationConstants.DATE_SEARCH_FORMAT));
            endDate = new BigDecimal(getFullDateTime(true, ApplicationConstants.DATE_SEARCH_FORMAT));
        }

        if(startDate.precision() == 14 && endDate.precision() == 14) {
            List<KronosWorkHour> kronosWorkHours = dao.findByDate(startDate, endDate);
            model.addAttribute("total", kronosWorkHours.size());
            model.addAttribute("kronosWorkHours", kronosWorkHours);
        }

        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        model.addAttribute("startDateDisplay", parseDateDisplay(startDate));
        model.addAttribute("endDateDisplay", parseDateDisplay(endDate));

        model.addAttribute("tomorrowsDate", getFullDateTime(true, ApplicationConstants.DATE_SEARCH_FORMAT));
        model.addAttribute("todaysDate", getFullDateTime(false, ApplicationConstants.DATE_SEARCH_FORMAT));

        model.addAttribute("searchLinkActive", "active");

        return "search";
    }



    @RequestMapping(value="/search", method=RequestMethod.POST)
    public String performSearch(ModelMap model,
                                HttpServletRequest request,
                                final RedirectAttributes redirect,
                                @RequestParam(value="startDate", required = true ) BigDecimal startDate,
                                @RequestParam(value="endDate", required = true ) BigDecimal endDate,
                                @RequestParam(value="employeeId", required = false ) BigDecimal employeeId){

        if(employeeId != null && employeeId.precision() < 3){
            redirect.addFlashAttribute("message", "Employee Id must be at least 3 in length");
            return "redirect:search";
        }


        if(startDate.precision() == 14 && endDate.precision() == 14 && employeeId == null){
            List<KronosWorkHour> kronosWorkHours = dao.findByDate(startDate, endDate);
            setRequestModelAttributes(model, kronosWorkHours, startDate, endDate);
        }else{
            redirect.addFlashAttribute("message", "data is incorrect, select a date");
        }


        if(startDate.precision() == 14 && endDate.precision() == 14 && employeeId != null){
            List<KronosWorkHour> kronosWorkHours = dao.findByDateEmployeeId(startDate, endDate, employeeId);
            setRequestModelAttributes(model, kronosWorkHours, startDate, endDate);
            model.addAttribute("employeeId", employeeId);
        }else{
            redirect.addFlashAttribute("message", "data is incorrect, select a date");
        }

        model.addAttribute("tomorrowsDate", getFullDateTime(true, ApplicationConstants.DATE_SEARCH_FORMAT));
        model.addAttribute("todaysDate", getFullDateTime(false, ApplicationConstants.DATE_SEARCH_FORMAT));

        model.addAttribute("searchLinkActive", "active");

        return "search";
    }


    @RequestMapping(value="/export", method=RequestMethod.POST)
    public void export(ModelMap model,
                       HttpServletRequest request,
                       HttpServletResponse response,
                       final RedirectAttributes redirect,
                       @RequestParam(value="startDate", required = true ) BigDecimal startDate,
                       @RequestParam(value="endDate", required = true ) BigDecimal endDate,
                       @RequestParam(value="employeeId", required = false ) BigDecimal employeeId)  throws Exception {

        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        model.addAttribute("startDateDisplay", parseDateDisplay(startDate));
        model.addAttribute("endDateDisplay", parseDateDisplay(endDate));

        model.addAttribute("todaysDate", getFullDateTime(false, ApplicationConstants.DATE_SEARCH_FORMAT));
        model.addAttribute("tomorrowsDate", getFullDateTime(true, ApplicationConstants.DATE_SEARCH_FORMAT));

        response.setHeader("Content-Disposition", "attachment; filename=\"q.csv\"");

        List<KronosWorkHour> kronosWorkHours = new ArrayList<KronosWorkHour>();

        if(startDate.precision() == 14 && endDate.precision() == 14 && employeeId == null){
            kronosWorkHours = dao.findByDate(startDate, endDate);
        }

        if(startDate.precision() == 14 && endDate.precision() == 14 && employeeId != null) {
            kronosWorkHours = dao.findByDateEmployeeId(startDate, endDate, employeeId);
        }

        StringBuffer writer=new StringBuffer();

        for (KronosWorkHour kronosWorkHour : kronosWorkHours) {
            List<String> list = new ArrayList<>();
            list.add(kronosWorkHour.getId().toString());
            list.add(kronosWorkHour.getFpempn().toString());
            list.add(kronosWorkHour.getFppunc().toString());
            list.add(kronosWorkHour.getFptype());
            list.add(kronosWorkHour.getFpclck());
            list.add(kronosWorkHour.getFpbadg().toString());
            list.add(kronosWorkHour.getFpfkey());
            list.add(kronosWorkHour.getFppcod().toString());
            list.add(kronosWorkHour.getFstatus());
            list.add(kronosWorkHour.getKrnlogid().toString());

            CsvUtils.writeLine(writer, list);
        }

        response.getWriter().print(writer.toString());
    }


    @RequestMapping(value="/status", method=RequestMethod.GET, produces="application/json")
    public @ResponseBody String status(HttpServletRequest request){

        Map<String, KronosQuartzJobStats> runningJobsMap = new HashMap<String, KronosQuartzJobStats>();

        if(dailyKronosQuartzJobStats.getStatus() == null && weeklyKronosQuartzJobStats.getStatus() == null){
            KronosQuartzJobStats emptyStats = new KronosQuartzJobStats();
            emptyStats.setStatus("idle");
            runningJobsMap.put("status", emptyStats);
        }

        if(dailyKronosQuartzJobStats.getStatus() != null){
            runningJobsMap.put("dailyJobRunning", dailyKronosQuartzJobStats);
        }
        if(weeklyKronosQuartzJobStats.getStatus() != null){
            runningJobsMap.put("weeklyJobRunning", weeklyKronosQuartzJobStats);
        }

        Gson gs1 =  new GsonBuilder().setPrettyPrinting().create();
        String js1 = gs1.toJson(dailyKronosQuartzJobStats);
        //log.info("js1" + js1);
        Gson gs2 =  new GsonBuilder().setPrettyPrinting().create();
        String js2 = gs2.toJson(weeklyKronosQuartzJobStats);
        //log.info("js2" + js2);

        Gson gsonObj =  new GsonBuilder().setPrettyPrinting().create();
        String jsonStr = gsonObj.toJson(runningJobsMap);

        //log.info("jsonStr" + jsonStr);
        return jsonStr;
    }


    @RequestMapping(value="/run_daily", method= RequestMethod.POST)
    public String runDaily(final RedirectAttributes redirect){
        String message = runJob(ApplicationConstants.ATHENS_DAILY_QUARTZ_JOB);
        redirect.addFlashAttribute("message", message);
        return "redirect:jobs";
    }


    @RequestMapping(value="/run_weekly", method= RequestMethod.POST)
    public String runWeekly(final RedirectAttributes redirect){
        String message = runJob(ApplicationConstants.ATHENS_WEEKLY_QUARTZ_JOB);
        redirect.addFlashAttribute("message", message);
        return "redirect:jobs";
    }


    private void setRequestModelAttributes(ModelMap model, List<KronosWorkHour> kronosWorkHours, BigDecimal startDate, BigDecimal endDate){
        model.addAttribute("total", kronosWorkHours.size());
        model.addAttribute("kronosWorkHours", kronosWorkHours);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("startDateDisplay", parseDateDisplay(startDate));
        model.addAttribute("endDateDisplay", parseDateDisplay(endDate));
    }

    private String runJob(String job){
        String message = "Successfully started job...";
        try {
            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            JobKey jobKey = new JobKey(job, ApplicationConstants.ATHENS_GROUP);
            scheduler.triggerJob(jobKey);
        }catch (Exception e){
            message = "Something went wrong";
            log.warn("unable to get connection");
            e.printStackTrace();
        }
        return message;
    }


    private String getFullDateTime(boolean tomorrow, String format) {
        Calendar cal = Calendar.getInstance();//
        if (tomorrow) cal.add(Calendar.DATE, 1);
        DateFormat dateFormat = new SimpleDateFormat(format);
        String fullDate = dateFormat.format(cal.getTime());
        return fullDate;
    }


    private String parseDateDisplay(BigDecimal date){
        String dateString = date.toString();
        String month = dateString.substring(4, 6);
        String day = dateString.substring(6, 8);
        String year = dateString.substring(0, 4);
        return month +"/" + day + "/" + year;
    }


    public List<KronosWorkHour> generateMockKrnwhs(int max, int offset){
        List<KronosWorkHour> kronosWorkHours = new ArrayList<KronosWorkHour>();
        for(int n = offset; n < max + offset; n++){
            KronosWorkHour kronosWorkHour = new KronosWorkHour();
            kronosWorkHour.setFpempn(new BigDecimal(n));
            kronosWorkHour.setFppunc(new BigDecimal(n));
            kronosWorkHour.setFptype("t");
            kronosWorkHour.setFpclck("");
            kronosWorkHour.setFpbadg(new BigDecimal(n));
            kronosWorkHour.setFpfkey("843");//*
            kronosWorkHour.setFppcod(new BigDecimal(n));//*
            kronosWorkHour.setFstatus("a");
            kronosWorkHour.setKrnlogid(new BigDecimal(n));
            kronosWorkHours.add(kronosWorkHour);
        }
        return kronosWorkHours;
    }


    public List<KronosQuartzIngestLog> generateMockKrnwhLogs(int max, int offset){
        List<KronosQuartzIngestLog> logs = new ArrayList<KronosQuartzIngestLog>();
        for(int n = offset; n < max + offset; n++){
            KronosQuartzIngestLog log = new KronosQuartzIngestLog();
            log.setId(new BigDecimal(n));
            log.setKstatus(ApplicationConstants.COMPLETE_STATUS);
            log.setKproc(new BigDecimal(72));
            log.setKtot(new BigDecimal(124));
            log.setKaudit("g");
            log.setKadtcnt(new BigDecimal(3));
            log.setKdate(new BigDecimal(20180323));
            logs.add(log);
        }
        return logs;
    }

}