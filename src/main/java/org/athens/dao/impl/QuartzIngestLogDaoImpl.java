package org.athens.dao.impl;

import org.apache.log4j.Logger;
import org.athens.domain.QuartzIngestLog;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.athens.dao.QuartzIngestLogDao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class QuartzIngestLogDaoImpl implements QuartzIngestLogDao {

    final static Logger log = Logger.getLogger(QuartzIngestLogDaoImpl.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;


    public int count() {
        String sql = "select count(*) from QGPL.KRNLOG";
        int count = 0;
        try{
            count = jdbcTemplate.queryForObject(sql, Integer.class, new Object[0]);
        }catch(Exception e){
            e.printStackTrace();
        }
        return count;
    }


    public List<QuartzIngestLog> list(int max, int offset){
        List<QuartzIngestLog> krnwLogs = new ArrayList<QuartzIngestLog>();
        try{

            String sql = "select * from QGPL.KRNLOG order by kdate desc limit " + max + " offset " + offset;
            krnwLogs = jdbcTemplate.query(sql, new BeanPropertyRowMapper<QuartzIngestLog>(QuartzIngestLog.class));

        }catch (Exception e){
            e.printStackTrace();
        }
        return krnwLogs;
    }



    public QuartzIngestLog findById(BigDecimal id){
        String findSql = "select * from QGPL.KRNLOG where id = " + id;

        QuartzIngestLog kronosIngestLog = new QuartzIngestLog();

        try {
            kronosIngestLog = (QuartzIngestLog) jdbcTemplate.queryForObject(findSql, new Object[]{},
                    new BeanPropertyRowMapper(QuartzIngestLog.class));

        }catch(Exception e){
            log.warn("unable to find by id...");
        }

        return kronosIngestLog;
    }



    public QuartzIngestLog findByDate(BigDecimal date){
        String findSql = "select * from QGPL.KRNLOG where kdate = " + date + " limit 1";

        QuartzIngestLog kronosIngestLog = null;

        try {
            kronosIngestLog = (QuartzIngestLog) jdbcTemplate.queryForObject(findSql, new Object[]{},
                    new BeanPropertyRowMapper(QuartzIngestLog.class));

        }catch(Exception e){
            log.warn("unable to find log by date...");
        }
        return kronosIngestLog;
    }


    public List<QuartzIngestLog> findAllByStatus(String status){
        List<QuartzIngestLog> krnwLogs = new ArrayList<QuartzIngestLog>();
        try{

            String sql = "select * from QGPL.KRNLOG where kstatus = '" + status + "'";
            krnwLogs = jdbcTemplate.query(sql, new BeanPropertyRowMapper<QuartzIngestLog>(QuartzIngestLog.class));

        }catch (Exception e){
            e.printStackTrace();
        }
        return krnwLogs;
    }


    public QuartzIngestLog save(QuartzIngestLog kronosIngestLog){

        String sql = "SELECT * FROM FINAL TABLE " +
                "(insert into QGPL.KRNLOG ( kdate, kstatus, ktot, kadtcnt, kaudit, kproc ) " +
                "values " +
                "("   + kronosIngestLog.getKdate() + "," +
                "'" + kronosIngestLog.getKstatus() + "'," +
                kronosIngestLog.getKtot() + "," +
                kronosIngestLog.getKadtcnt() + "," +
                "'" + kronosIngestLog.getKaudit() + "'," +
                kronosIngestLog.getKproc() + "))";


        QuartzIngestLog skrnwhLog = new QuartzIngestLog();

        try {
            skrnwhLog = (QuartzIngestLog) jdbcTemplate.queryForObject(sql, new Object[]{},
                    new BeanPropertyRowMapper(QuartzIngestLog.class));

        }catch(Exception e){
            e.printStackTrace();
            log.warn("unable to save log ...");
        }

        return skrnwhLog;
    }



    public QuartzIngestLog update(QuartzIngestLog kronosIngestLog){

        String updateSql = "update QGPL.KRNLOG set ( kstatus, ktot, kadtcnt, kdate, kaudit, kproc ) = (?, ?, ?, ?, ?, ?)  where id = ?";

        jdbcTemplate.update(updateSql, new Object[] {
                kronosIngestLog.getKstatus(), kronosIngestLog.getKtot(), kronosIngestLog.getKadtcnt(),
                kronosIngestLog.getKdate(), kronosIngestLog.getKaudit(), kronosIngestLog.getKproc(), kronosIngestLog.getId()
        });

        return findById(kronosIngestLog.getId());

    }


}


