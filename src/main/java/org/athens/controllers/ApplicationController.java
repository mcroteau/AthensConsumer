package org.athens.controllers;

import org.apache.log4j.Logger;

import org.athens.domain.KRNWH;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import org.springframework.beans.factory.annotation.Autowired;

import org.athens.ApplicationContextProvider;
import org.athens.ApplicationRunner;

import javax.servlet.http.HttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Enumeration;
import java.util.Properties;

import org.athens.dao.impl.KrnwhLogDaoImpl;
import org.athens.dao.impl.KrnwhDaoImpl;

import org.athens.jobs.KrnwhReportInitializer;

import org.quartz.JobKey;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;

import org.athens.domain.KrnwhLog;

import java.util.List;


@Controller
public class ApplicationController {

	@Autowired
	private ApplicationRunner applicationRunner;

    @Autowired
    private DriverManagerDataSource dataSource;


    @Autowired
    private KrnwhDaoImpl dao;

    @Autowired
    private KrnwhLogDaoImpl logDao;

	final static Logger log = Logger.getLogger(ApplicationRunner.class);

    @RequestMapping(value="/", method= RequestMethod.GET)
    public String list(final RedirectAttributes redirect){
        return "redirect:list";
    }

    @RequestMapping(value="/index", method= RequestMethod.GET)
    public String index(final RedirectAttributes redirect){
        String message = "";
        try {
            //log.info(dataSource);

            //log.info(logDao.list.jsp(10, 0));

            //log.info(dao.list.jsp());
            log.info("Running report");
            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            JobKey jobKey = new JobKey("krnwhJob", "atns");
            scheduler.triggerJob(jobKey); //trigger a job by jobkey

            message = "Successfully ran report...";

        }catch (Exception e){
            message = "Something went wrong";
            log.warn("unable to get connection");
            e.printStackTrace();
        }


        redirect.addFlashAttribute("message", message);
        //return "application/index";
        return "redirect:list";
    }

    @RequestMapping(value="/list", method=RequestMethod.GET)
    public String list(ModelMap model,
                       HttpServletRequest request,
                       final RedirectAttributes redirect,
                       @RequestParam(value="admin", required = false ) String admin,
                       @RequestParam(value="offset", required = false ) String offset,
                       @RequestParam(value="max", required = false ) String max,
                       @RequestParam(value="page", required = false ) String page){

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
                krnwhLogs = logDao.list(m, o);
            }else{
                krnwhLogs = logDao.list(10, 0);
            }

            int count = logDao.count();

            System.out.println("count : " + count);

            model.addAttribute("krnwhLogs", krnwhLogs);
            model.addAttribute("total", count);

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
                       @RequestParam(value="page", required = false ) String page){

        if(page == null){
            page = "1";
        }

        List<KRNWH> krnwhs;

        if(offset != null) {
            int m = 10;
            if(max != null){
                m = Integer.parseInt(max);
            }
            int o = Integer.parseInt(offset);
            krnwhs = dao.list(m, o);
        }else{
            krnwhs = dao.list(10, 0);
        }

        int count = dao.count();

        System.out.println("count : " + count);

        model.addAttribute("krnwhs", krnwhs);
        model.addAttribute("total", count);

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
                        @RequestParam(value="page", required = false ) String page){

        if(page == null){
            page = "1";
        }

        List<KRNWH> krnwhs;

        if(offset != null) {
            int m = 10;
            if(max != null){
                m = Integer.parseInt(max);
            }
            int o = Integer.parseInt(offset);
            krnwhs = dao.listByIngest(m, o, ingest);
        }else{
            krnwhs = dao.listByIngest(10, 0, ingest);
        }

        int count = dao.count();

        System.out.println("count : " + count);

        model.addAttribute("krnwhs", krnwhs);
        model.addAttribute("total", count);

        model.addAttribute("resultsPerPage", 10);
        model.addAttribute("activePage", page);

        model.addAttribute("krnwhsLinkActive", "active");

        return "krnwh/list";

    }


}