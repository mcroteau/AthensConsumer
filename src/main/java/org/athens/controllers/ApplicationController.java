package org.athens.controllers;

import org.apache.log4j.Logger;

import org.athens.domain.Krnwh;
import org.athens.domain.Krnwh;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.beans.factory.annotation.Autowired;

import org.athens.ApplicationRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;

import org.athens.dao.impl.KrnwhLogDaoImpl;
import org.athens.dao.impl.KrnwhDaoImpl;
import org.quartz.JobKey;
import org.quartz.JobDetail;

import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.JobExecutionContext;
import org.athens.domain.KrnwhLog;

import java.util.ArrayList;
import java.util.List;

import java.io.FileWriter;
import org.athens.common.ApplicationConstants;
import org.athens.common.CsvUtils;

import org.quartz.core.QuartzScheduler;


@Controller
public class ApplicationController {

    @Autowired
    private KrnwhDaoImpl dao;

    @Autowired
    private KrnwhLogDaoImpl logDao;
    @Autowired
    private Scheduler scheduler;



	final static Logger log = Logger.getLogger(ApplicationController.class);


    @RequestMapping(value="/", method= RequestMethod.GET)
    public String list(final RedirectAttributes redirect){
        return "redirect:list";
    }


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

            List<KrnwhLog> krnwhLogs;

            if(offset != null) {
                int m = 10;
                if(max != null){
                    m = Integer.parseInt(max);
                }
                int o = Integer.parseInt(offset);
                //krnwhLogs = logDao.list(m, o);
                krnwhLogs = generateMockKrnwhLogs(m, o);
            }else{
                //krnwhLogs = logDao.list(10, 0);
                krnwhLogs = generateMockKrnwhLogs(10, 0);
            }

            //int count = logDao.count();
            int count = 304;

            try {
                //Scheduler scheduler = new StdSchedulerFactory().getScheduler();
                JobKey jobKey = new JobKey(ApplicationConstants.ATHENS_DAILY_QUARTZ_JOB, ApplicationConstants.ATHENS_GROUP);
                JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                log.info(jobDetail);
                int h = jobDetail.getJobDataMap().getIntegerFromString("job-count");
                model.addAttribute("jobCount", h);
                log.info("get job details" + h);
            }catch (Exception e){

            }
            model.addAttribute("krnwhLogs", krnwhLogs);
            model.addAttribute("total", count);

            model.addAttribute("sort", sort);
            model.addAttribute("order", order);

            model.addAttribute("resultsPerPage", 10);
            model.addAttribute("activePage", page);

            model.addAttribute("krnwhLogsLinkActive", "active");

            return "application/index";

    }

    @RequestMapping(value="/krnwh/list", method=RequestMethod.GET)
    public String krnws(ModelMap model,
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

        List<Krnwh> krnwhs;

        if(offset != null) {
            int m = 10;
            if(max != null){
                m = Integer.parseInt(max);
            }
            int o = Integer.parseInt(offset);
            //krnwhs = dao.list(m, o);
            krnwhs = generateMockKrnwhs(m, o);
        }else{
            //krnwhs = dao.list(10, 0);
            krnwhs = generateMockKrnwhs(10, 0);
        }

        int count = dao.count();
        //int count = 2031;

        System.out.println("count : " + count);

        model.addAttribute("krnwhs", krnwhs);
        model.addAttribute("total", count);

        model.addAttribute("sort", sort);
        model.addAttribute("order", order);

        model.addAttribute("resultsPerPage", 10);
        model.addAttribute("activePage", page);

        model.addAttribute("krnwhsLinkActive", "active");

        return "krnwh/list";

    }



    @RequestMapping(value="/krnwh/list_ingest", method=RequestMethod.GET)
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

        List<Krnwh> krnwhs;

        if(offset != null) {
            int m = 10;
            if(max != null){
                m = Integer.parseInt(max);
            }
            int o = Integer.parseInt(offset);
            //krnwhs = dao.listByIngest(m, o, ingest);
            krnwhs = generateMockKrnwhs(m, o);
        }else{
            //krnwhs = dao.listByIngest(10, 0, ingest);
            krnwhs = generateMockKrnwhs(10, 0);
        }

        //int count = dao.count();
        int count = 2031;
        System.out.println("count : " + count);

        model.addAttribute("krnwhs", krnwhs);
        model.addAttribute("total", count);

        model.addAttribute("resultsPerPage", 10);
        model.addAttribute("activePage", page);

        model.addAttribute("krnwhsLinkActive", "active");

        return "krnwh/list";

    }


    @RequestMapping(value="/krnwh/search", method=RequestMethod.GET)
    public String search(){
        return "krnwh/search";
    }


    @RequestMapping(value="/krnwh/search", method=RequestMethod.POST)
    public String performSearch(ModelMap model,
                         HttpServletRequest request,
                         final RedirectAttributes redirect,
                         @RequestParam(value="start-date", required = true ) BigDecimal startDate,
                         @RequestParam(value="end-date", required = true ) BigDecimal endDate){

        if(startDate.precision() == 14 && endDate.precision() == 14){
            List<Krnwh> krnwhs = dao.findByDate(startDate, endDate);
            model.addAttribute("total", krnwhs.size());
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
            model.addAttribute("krnwhs", krnwhs);
            return "krnwh/search";
        }else{
            redirect.addFlashAttribute("message", "data is incorrect");
            return "krnwh/search";
        }
    }


    @RequestMapping(value="/krnwh/export", method=RequestMethod.POST)
    public void export(ModelMap model,
                         HttpServletRequest request,
                         HttpServletResponse response,
                         final RedirectAttributes redirect,
                         @RequestParam(value="start-date", required = true ) BigDecimal startDate,
                         @RequestParam(value="end-date", required = true ) BigDecimal endDate)  throws Exception {

        response.setHeader("Content-Disposition", "attachment; filename=\"a.csv\"");

        List<Krnwh> krnwhs = dao.findByDate(startDate, endDate);
        //model.addAttribute("total", krnwhs.size());
        //model.addAttribute("krnwhs", krnwhs);
        StringBuffer writer=new StringBuffer();

        for (Krnwh d : krnwhs) {
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



    public List<Krnwh> generateMockKrnwhs(int max, int offset){
        List<Krnwh> krnwhs = new ArrayList<Krnwh>();
        for(int n = offset; n < max + offset; n++){
            Krnwh krnwh = new Krnwh();
            krnwh.setFpempn(new BigDecimal(n));
            krnwh.setFppunc(new BigDecimal(n));
            krnwh.setFptype("t");
            krnwh.setFpclck("");
            krnwh.setFpbadg(new BigDecimal(n));
            krnwh.setFpfkey("843");//*
            krnwh.setFppcod(new BigDecimal(n));//*
            krnwh.setFstatus("a");
            krnwh.setKrnlogid(new BigDecimal(n));
            krnwhs.add(krnwh);
        }
        return krnwhs;
    }

    public List<KrnwhLog> generateMockKrnwhLogs(int max, int offset){
        List<KrnwhLog> logs = new ArrayList<KrnwhLog>();
        for(int n = offset; n < max + offset; n++){
            KrnwhLog log = new KrnwhLog();
            log.setId(new BigDecimal(n));
            log.setKstatus(ApplicationConstants.COMPLETE_STATUS);
            log.setKtot(new BigDecimal(124));
            log.setKaudit("g");
            log.setKadtcnt(new BigDecimal(3));
            log.setKdate(new BigDecimal(20180323));
            logs.add(log);
        }
        return logs;
    }

}