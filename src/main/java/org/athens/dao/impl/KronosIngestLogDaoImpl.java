package org.athens.dao.impl;

import org.apache.log4j.Logger;
import org.athens.domain.KronosIngestLog;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.athens.dao.KronosIngestLogDao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class KronosIngestLogDaoImpl implements KronosIngestLogDao {

    final static Logger log = Logger.getLogger(KronosIngestLogDaoImpl.class);

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


    public List<KronosIngestLog> list(int max, int offset){
        List<KronosIngestLog> krnwLogs = new ArrayList<KronosIngestLog>();
        try{

            String sql = "select * from QGPL.KRNLOG order by kdate desc limit " + max + " offset " + offset;
            krnwLogs = jdbcTemplate.query(sql, new BeanPropertyRowMapper<KronosIngestLog>(KronosIngestLog.class));

        }catch (Exception e){
            e.printStackTrace();
        }
        return krnwLogs;
    }



    public KronosIngestLog findById(BigDecimal id){
        String findSql = "select * from QGPL.KRNLOG where id = " + id;

        KronosIngestLog kronosIngestLog = new KronosIngestLog();

        try {
            kronosIngestLog = (KronosIngestLog) jdbcTemplate.queryForObject(findSql, new Object[]{},
                    new BeanPropertyRowMapper(KronosIngestLog.class));

        }catch(Exception e){
            log.warn("unable to find by id...");
        }

        return kronosIngestLog;
    }



    public KronosIngestLog findByDate(BigDecimal date){
        String findSql = "select * from QGPL.KRNLOG where kdate = " + date + " limit 1";

        KronosIngestLog kronosIngestLog = null;

        try {
            kronosIngestLog = (KronosIngestLog) jdbcTemplate.queryForObject(findSql, new Object[]{},
                    new BeanPropertyRowMapper(KronosIngestLog.class));

        }catch(Exception e){
            log.warn("unable to find log by date...");
        }
        return kronosIngestLog;
    }


    public List<KronosIngestLog> findAllByStatus(String status){
        List<KronosIngestLog> krnwLogs = new ArrayList<KronosIngestLog>();
        try{

            String sql = "select * from QGPL.KRNLOG where kstatus = '" + status + "'";
            krnwLogs = jdbcTemplate.query(sql, new BeanPropertyRowMapper<KronosIngestLog>(KronosIngestLog.class));

        }catch (Exception e){
            e.printStackTrace();
        }
        return krnwLogs;
    }


    public KronosIngestLog save(KronosIngestLog kronosIngestLog){

        String sql = "SELECT * FROM FINAL TABLE " +
                "(insert into QGPL.KRNLOG ( kdate, kstatus, ktot, kadtcnt, kaudit, kproc ) " +
                "values " +
                "("   + kronosIngestLog.getKdate() + "," +
                "'" + kronosIngestLog.getKstatus() + "'," +
                kronosIngestLog.getKtot() + "," +
                kronosIngestLog.getKadtcnt() + "," +
                "'" + kronosIngestLog.getKaudit() + "'," +
                kronosIngestLog.getKproc() + "))";


        KronosIngestLog skrnwhLog = new KronosIngestLog();

        try {
            skrnwhLog = (KronosIngestLog) jdbcTemplate.queryForObject(sql, new Object[]{},
                    new BeanPropertyRowMapper(KronosIngestLog.class));

        }catch(Exception e){
            e.printStackTrace();
            log.warn("unable to save log ...");
        }

        return skrnwhLog;
    }



    public KronosIngestLog update(KronosIngestLog kronosIngestLog){

        String updateSql = "update QGPL.KRNLOG set ( kstatus, ktot, kadtcnt, kdate, kaudit, kproc ) = (?, ?, ?, ?, ?, ?)  where id = ?";

        jdbcTemplate.update(updateSql, new Object[] {
                kronosIngestLog.getKstatus(), kronosIngestLog.getKtot(), kronosIngestLog.getKadtcnt(),
                kronosIngestLog.getKdate(), kronosIngestLog.getKaudit(), kronosIngestLog.getKproc(), kronosIngestLog.getId()
        });

        return findById(kronosIngestLog.getId());

    }


}


