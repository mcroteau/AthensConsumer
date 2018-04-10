package org.athens.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;

import org.athens.domain.QuartzIngestLog;
import org.athens.domain.KronosWorkHour;
import org.athens.domain.QuartzJobStats;
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

import org.athens.dao.impl.QuartzIngestLogDaoImpl;
import org.athens.dao.impl.KronosWorkHourDaoImpl;
import org.quartz.JobKey;

import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.athens.common.ApplicationConstants;
import org.athens.common.CsvUtils;


@Controller
public class ApplicationController {

    final static Logger log = Logger.getLogger(ApplicationController.class);


    @Autowired
    private KronosWorkHourDaoImpl dao;

    @Autowired
    private QuartzIngestLogDaoImpl logDao;

    @Autowired
    private QuartzJobStats dailyQuartzJobStats;

    @Autowired
    private QuartzJobStats weeklyQuartzJobStats;


    @RequestMapping(value="/", method=RequestMethod.GET)
    public String list(final RedirectAttributes redirect){
        return "redirect:jobs";
    }


    @RequestMapping(value="/jobs", method=RequestMethod.GET)
    public String jobs(ModelMap model){
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

        List<QuartzIngestLog> kronosIngestLogs;

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

        model.addAttribute("kronosIngestLogs", kronosIngestLogs);

        return "ingests";
    }


    @RequestMapping(value="/punches", method=RequestMethod.GET)
    public String punches(ModelMap model,
                        HttpServletRequest request,
                        final RedirectAttributes redirect,
                        @RequestParam(value="admin", required = false ) String admin,
                        @RequestParam(value="offset", required = false ) String offset,
                        @RequestParam(value="max", required = false ) String max,
                        @RequestParam(value="page", required = false ) String page,
                        @RequestParam(value="sort", required = false ) String sort,
                        @RequestParam(value="order", required = false ) String order){

        if(page == null){
            page = "1";
        }

        List<KronosWorkHour> kronosWorkHours;

        if(offset != null) {
            int m = 10;
            if(max != null){
                m = Integer.parseInt(max);
            }
            int o = Integer.parseInt(offset);
            kronosWorkHours = dao.list(m, o);
            //kronosWorkHours = generateMockKrnwhs(m, o);
        }else{
            kronosWorkHours = dao.list(10, 0);
            //kronosWorkHours = generateMockKrnwhs(10, 0);
        }

        int count = dao.count();
        //int count = 2031;

        System.out.println("count : " + count);

        model.addAttribute("kronosWorkHours", kronosWorkHours);
        model.addAttribute("total", count);

        model.addAttribute("sort", sort);
        model.addAttribute("order", order);

        model.addAttribute("resultsPerPage", 10);
        model.addAttribute("activePage", page);

        model.addAttribute("kronosWorkHoursLinkActive", "active");

        return "punches";

    }




    @RequestMapping(value="/search", method=RequestMethod.GET)
    public String search(ModelMap model){
        model.addAttribute("searchLinkActive", "active");
        return "search";
    }



    @RequestMapping(value="/search", method=RequestMethod.POST)
    public String performSearch(ModelMap model,
                                HttpServletRequest request,
                                final RedirectAttributes redirect,
                                @RequestParam(value="start-date", required = true ) BigDecimal startDate,
                                @RequestParam(value="end-date", required = true ) BigDecimal endDate){

        if(startDate.precision() == 14 && endDate.precision() == 14){
            List<KronosWorkHour> kronosWorkHours = dao.findByDate(startDate, endDate);
            model.addAttribute("total", kronosWorkHours.size());
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
            model.addAttribute("kronosWorkHours", kronosWorkHours);
        }else{
            redirect.addFlashAttribute("message", "data is incorrect");
        }

        model.addAttribute("searchLinkActive", "active");
        return "search";
    }


    @RequestMapping(value="/export", method=RequestMethod.POST)
    public void export(ModelMap model,
                       HttpServletRequest request,
                       HttpServletResponse response,
                       final RedirectAttributes redirect,
                       @RequestParam(value="start-date", required = true ) BigDecimal startDate,
                       @RequestParam(value="end-date", required = true ) BigDecimal endDate)  throws Exception {

        response.setHeader("Content-Disposition", "attachment; filename=\"a.csv\"");

        List<KronosWorkHour> kronosWorkHours = dao.findByDate(startDate, endDate);
        //model.addAttribute("total", kronosWorkHours.size());
        //model.addAttribute("kronosWorkHours", kronosWorkHours);
        StringBuffer writer=new StringBuffer();

        for (KronosWorkHour d : kronosWorkHours) {
            List<String> list = new ArrayList<>();
            list.add(d.getId().toString());
            list.add(d.getFppunc().toString());
            list.add(d.getFptype());
            list.add(d.getFpclck());
            list.add(d.getFpbadg().toString());
            list.add(d.getFpfkey());
            list.add(d.getFppcod().toString());
            list.add(d.getFstatus());
            list.add(d.getKrnlogid().toString());

            CsvUtils.writeLine(writer, list);
        }

        response.getWriter().print(writer.toString());
    }


    @RequestMapping(value="/status", method=RequestMethod.GET, produces="application/json")
    public @ResponseBody String status(HttpServletRequest request){

        Map<String, QuartzJobStats> runningJobsMap = new HashMap<String, QuartzJobStats>();

        if(dailyQuartzJobStats.getStatus() == null && weeklyQuartzJobStats.getStatus() == null){
            QuartzJobStats emptyStats = new QuartzJobStats();
            emptyStats.setStatus("idle");
            runningJobsMap.put("status", emptyStats);
        }

        if(dailyQuartzJobStats.getStatus() != null){
            runningJobsMap.put("dailyJobRunning", dailyQuartzJobStats);
        }
        if(weeklyQuartzJobStats.getStatus() != null){
            runningJobsMap.put("weeklyJobRunning", weeklyQuartzJobStats);
        }

        Gson gs1 =  new GsonBuilder().setPrettyPrinting().create();
        String js1 = gs1.toJson(dailyQuartzJobStats);
        //log.info("js1" + js1);
        Gson gs2 =  new GsonBuilder().setPrettyPrinting().create();
        String js2 = gs2.toJson(weeklyQuartzJobStats);
        //log.info("js2" + js2);

        Gson gsonObj =  new GsonBuilder().setPrettyPrinting().create();
        String jsonStr = gsonObj.toJson(runningJobsMap);

        //log.info("jsonStr" + jsonStr);
        return jsonStr;
    }






    /**
    @RequestMapping(value="/list", method=RequestMethod.GET)
    public String list(ModelMap model,
                       HttpServletRequest request,
                       final RedirectAttributes redirect,
                       @RequestParam(value="admin", required = false ) String admin,
                       @RequestParam(value="offset", required = false ) String offset,
                       @RequestParam(value="max", required = false ) String max,
                       @RequestParam(value="page", required = false ) String page,
                       @RequestParam(value="sort", required = false ) String sort,
                       @RequestParam(value="order", required = false ) String order){

        if(page == null){
            page = "1";
        }

        List<QuartzIngestLog> kronosIngestLogs;

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

        //int count = logDao.count();
        int count = 304;


        model.addAttribute("kronosIngestLogs", kronosIngestLogs);
        model.addAttribute("total", count);

        model.addAttribute("sort", sort);
        model.addAttribute("order", order);

        model.addAttribute("resultsPerPage", 10);
        model.addAttribute("activePage", page);

        if(dailyQuartzJobStats.jobRunning())model.addAttribute("dailyJobRunning");

        if(weeklyQuartzJobStats.jobRunning())model.addAttribute("weeklyJobRunning");

        model.addAttribute("kronosIngestLogsLinkActive", "active");

        return "application/index";
    }

TODO:
    @RequestMapping(value="/kronosWorkHour/list_ingest", method=RequestMethod.GET)
    public String krnwsIngest(ModelMap model,
                        HttpServletRequest request,
                        final RedirectAttributes redirect,
                        @RequestParam(value="ingest", required = true ) BigDecimal ingest,
                        @RequestParam(value="admin", required = false ) String admin,
                        @RequestParam(value="offset", required = false ) String offset,
                        @RequestParam(value="max", required = false ) String max,
                        @RequestParam(value="page", required = false ) String page,
                        @RequestParam(value="sort", required = false ) String sort,
                        @RequestParam(value="order", required = false ) String order){

        if(page == null){
            page = "1";
        }

        List<KronosWorkHour> kronosWorkHours;

        if(offset != null) {
            int m = 10;
            if(max != null){
                m = Integer.parseInt(max);
            }
            int o = Integer.parseInt(offset);
            //kronosWorkHours = dao.listByIngest(m, o, ingest);
            kronosWorkHours = generateMockKrnwhs(m, o);
        }else{
            //kronosWorkHours = dao.listByIngest(10, 0, ingest);
            kronosWorkHours = generateMockKrnwhs(10, 0);
        }

        //int count = dao.count();
        int count = 2031;
        System.out.println("count : " + count);

        model.addAttribute("kronosWorkHours", kronosWorkHours);
        model.addAttribute("total", count);

        model.addAttribute("resultsPerPage", 10);
        model.addAttribute("activePage", page);

        model.addAttribute("kronosWorkHoursLinkActive", "active");

        return "kronosWorkHour/list";

    }
**/

    @RequestMapping(value="/run_daily", method= RequestMethod.POST)
    public String runDaily(final RedirectAttributes redirect){
        String message = runJob(ApplicationConstants.ATHENS_DAILY_QUARTZ_JOB);
        redirect.addFlashAttribute("message", message);
        return "redirect:list";
    }


    @RequestMapping(value="/run_weekly", method= RequestMethod.POST)
    public String runWeekly(final RedirectAttributes redirect){
        String message = runJob(ApplicationConstants.ATHENS_WEEKLY_QUARTZ_JOB);
        redirect.addFlashAttribute("message", message);
        return "redirect:list";
    }

    private String runJob(String job){
        String message = "Successfully ran report...";
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

    public List<QuartzIngestLog> generateMockKrnwhLogs(int max, int offset){
        List<QuartzIngestLog> logs = new ArrayList<QuartzIngestLog>();
        for(int n = offset; n < max + offset; n++){
            QuartzIngestLog log = new QuartzIngestLog();
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