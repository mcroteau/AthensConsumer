package org.athens.dao.impl;

import org.apache.log4j.Logger;
import org.athens.domain.QuartzIngestLog;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.athens.dao.KrnwhLogDao;

import java.math.BigDecimal;
import java.util.List;


public class KrnwhLogDaoImpl implements KrnwhLogDao  {

    final static Logger log = Logger.getLogger(KrnwhLogDaoImpl.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;


    public int count() {
        String sql = "select count(*) from KRNLOG";
        int count = 0;
        try{
            count = jdbcTemplate.queryForObject(sql, Integer.class, new Object[0]);
        }catch(Exception e){
            e.printStackTrace();
        }
        return count;
    }

    public QuartzIngestLog save(QuartzIngestLog ingestLog){

        String sql = "SELECT * FROM FINAL TABLE " +
                "(insert into QGPL.KRNLOG ( kdate, kstatus, ktot, kadtcnt, kaudit, kproc ) " +
                "values " +
                "("   + ingestLog.getKdate() + "," +
                  "'" + ingestLog.getKstatus() + "'," +
                        ingestLog.getKtot() + "," +
                        ingestLog.getKadtcnt() + "," +
                  "'" + ingestLog.getKaudit() + "'," +
                        ingestLog.getKproc() + "))";


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



    public QuartzIngestLog update(QuartzIngestLog ingestLog){

        String updateSql = "update QGPL.KRNLOG set ( kstatus, ktot, kadtcnt, kdate, kaudit, kproc ) = (?, ?, ?, ?, ?, ?)  where id = ?";

        jdbcTemplate.update(updateSql, new Object[] {
                ingestLog.getKstatus(), ingestLog.getKtot(), ingestLog.getKadtcnt(),
                ingestLog.getKdate(), ingestLog.getKaudit(), ingestLog.getKproc(), ingestLog.getId()
        });

        return find(ingestLog.getId());

    }


    public List<QuartzIngestLog> list(int max, int offset){
        try{

            String sql = "select * from QGPL.KRNLOG limit " + max + " offset " + offset;
            List<QuartzIngestLog> krnwLogs = jdbcTemplate.query(sql, new BeanPropertyRowMapper<QuartzIngestLog>(QuartzIngestLog.class));

            return krnwLogs;

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }



    public QuartzIngestLog find(BigDecimal id){
        String findSql = "select * from QGPL.KRNLOG where id = " + id;

        QuartzIngestLog ingestLog = new QuartzIngestLog();

        try {
            ingestLog = (QuartzIngestLog) jdbcTemplate.queryForObject(findSql, new Object[]{},
                    new BeanPropertyRowMapper(QuartzIngestLog.class));

        }catch(Exception e){
            log.warn("unable to find by id...");
        }

        return ingestLog;
    }



    public QuartzIngestLog findByDate(BigDecimal date){
        String findSql = "select * from QGPL.KRNLOG where kdate = " + date + " limit 1";

        QuartzIngestLog ingestLog = null;

        try {
            ingestLog = (QuartzIngestLog) jdbcTemplate.queryForObject(findSql, new Object[]{},
                    new BeanPropertyRowMapper(QuartzIngestLog.class));

        }catch(Exception e){
            log.warn("unable to find log by date...");
        }
        return ingestLog;
    }

}


