package org.athens.dao.impl;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.athens.domain.KrnwhLog;
import org.athens.dao.KrnwhLogDao;

import java.math.BigDecimal;
import java.util.List;

/**
viewing ingest/saved
comparison logic
authorization
style guide
 **/


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
	
    public List<KrnwhLog> list(int max, int offset){
        try{

            String sql = "select * from QGPL.KRNLOG limit " + max + " offset " + offset;
            List<KrnwhLog> krnwLogs = jdbcTemplate.query(sql, new BeanPropertyRowMapper<KrnwhLog>(KrnwhLog.class));

            return krnwLogs;

        }catch (Exception e){
            e.printStackTrace();
        }
		return null;
    }


    public KrnwhLog save(KrnwhLog krnwhLog){

        String sql = "SELECT * FROM FINAL TABLE " +
                "(insert into QGPL.KRNLOG ( kdate, kstatus, ktot, kadtcnt, kaudit ) " +
                "values " +
                "("   + krnwhLog.getKdate() + "," +
                  "'" + krnwhLog.getKstatus() + "'," +
                        krnwhLog.getKtot() + "," +
                        krnwhLog.getKadtcnt() + "," +
                  "'" + krnwhLog.getKaudit() + "'))";


        KrnwhLog skrnwhLog = new KrnwhLog();

        try {
            skrnwhLog = (KrnwhLog) jdbcTemplate.queryForObject(sql, new Object[]{},
                    new BeanPropertyRowMapper(KrnwhLog.class));

        }catch(Exception e){
            e.printStackTrace();
            log.warn("unable to save log ...");
        }

        return skrnwhLog;
    }



    public KrnwhLog update(KrnwhLog krnwhLog){

        String updateSql = "update QGPL.KRNLOG set ( kstatus, ktot, kadtcnt, kdate, kaudit ) = (?, ?, ?, ?, ?)  where id = ?";

        jdbcTemplate.update(updateSql, new Object[] {
                krnwhLog.getKstatus(), krnwhLog.getKtot(), krnwhLog.getKadtcnt(),
                krnwhLog.getKdate(), krnwhLog.getKaudit(), krnwhLog.getId()
        });

        return find(krnwhLog.getId());

    }



    public KrnwhLog find(BigDecimal id){
        String findSql = "select * from QGPL.KRNLOG where id = " + id;

        KrnwhLog krnwhLog = new KrnwhLog();

        try {
            krnwhLog = (KrnwhLog) jdbcTemplate.queryForObject(findSql, new Object[]{},
                    new BeanPropertyRowMapper(KrnwhLog.class));

        }catch(Exception e){
            log.warn("unable to find by id...");
        }

        return krnwhLog;
    }



    public KrnwhLog findByDate(BigDecimal date){
        String findSql = "select * from QGPL.KRNLOG where kdate = " + date + " limit 1";

        KrnwhLog krnwhLog = null;

        try {
            krnwhLog = (KrnwhLog) jdbcTemplate.queryForObject(findSql, new Object[]{},
                    new BeanPropertyRowMapper(KrnwhLog.class));

        }catch(Exception e){
            log.warn("unable to find log by date...");
        }
        return krnwhLog;
    }

}


