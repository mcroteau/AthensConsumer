package org.athens.dao.impl;

import org.apache.log4j.Logger;
import org.athens.domain.KronosQuartzIngestLog;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.athens.dao.KronosQuartzIngestLogDao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class KronosQuartzIngestLogDaoImpl implements KronosQuartzIngestLogDao {

    final static Logger log = Logger.getLogger(KronosQuartzIngestLogDaoImpl.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;


    public int count() {
        //String sql = "select count(*) from QGPL.KRNLOG";
        String sql = "select count(*) from KRNLOG";
        int count = 0;
        try{
            count = jdbcTemplate.queryForObject(sql, Integer.class, new Object[0]);
        }catch(Exception e){
            e.printStackTrace();
        }
        return count;
    }


    public List<KronosQuartzIngestLog> list(int max, int offset){
        List<KronosQuartzIngestLog> krnwLogs = new ArrayList<KronosQuartzIngestLog>();
        try{

            //String sql = "select * from QGPL.KRNLOG order by kdate desc limit " + max + " offset " + offset;
            String sql = "select * from KRNLOG order by kdate desc limit " + max + " offset " + offset;

            krnwLogs = jdbcTemplate.query(sql, new BeanPropertyRowMapper<KronosQuartzIngestLog>(KronosQuartzIngestLog.class));

        }catch (Exception e){
            e.printStackTrace();
        }
        return krnwLogs;
    }



    public KronosQuartzIngestLog findById(BigDecimal id){
        //String findSql = "select * from QGPL.KRNLOG where id = " + id;
        String findSql = "select * from KRNLOG where id = " + id;

        KronosQuartzIngestLog kronosIngestLog = new KronosQuartzIngestLog();

        try {
            kronosIngestLog = (KronosQuartzIngestLog) jdbcTemplate.queryForObject(findSql, new Object[]{},
                    new BeanPropertyRowMapper(KronosQuartzIngestLog.class));

        }catch(Exception e){
            log.warn("unable to find by id...");
        }

        return kronosIngestLog;
    }



    public KronosQuartzIngestLog findByDate(BigDecimal date){
        //String findSql = "select * from QGPL.KRNLOG where kdate = " + date + " limit 1";
        String findSql = "select * from krnlog where kdate = " + date + " limit 1";//TODO:

        KronosQuartzIngestLog kronosIngestLog = null;

        try {
            kronosIngestLog = (KronosQuartzIngestLog) jdbcTemplate.queryForObject(findSql, new Object[]{},
                    new BeanPropertyRowMapper(KronosQuartzIngestLog.class));

        }catch(Exception e){
            log.warn("unable to find log by date...");
        }
        return kronosIngestLog;
    }


    public List<KronosQuartzIngestLog> findAllByStatus(String status){
        List<KronosQuartzIngestLog> krnwLogs = new ArrayList<KronosQuartzIngestLog>();
        try{

            //String sql = "select * from QGPL.KRNLOG where kstatus = '" + status + "'";
            String sql = "select * from krnlog where kstatus = '" + status + "'";//TODO:
            krnwLogs = jdbcTemplate.query(sql, new BeanPropertyRowMapper<KronosQuartzIngestLog>(KronosQuartzIngestLog.class));

        }catch (Exception e){
            e.printStackTrace();
        }
        return krnwLogs;
    }


    public KronosQuartzIngestLog save(KronosQuartzIngestLog kronosIngestLog){

        /**
        String sql = "SELECT * FROM FINAL TABLE " +
                "(insert into QGPL.KRNLOG ( kdate, kstatus, ktot, kadtcnt, kaudit, ktype, kproc ) " +
                "values " +
                "("   + kronosIngestLog.getKdate() + "," +
                "'" + kronosIngestLog.getKstatus() + "'," +
                kronosIngestLog.getKtot() + "," +
                kronosIngestLog.getKadtcnt() + "," +
                "'" + kronosIngestLog.getKaudit() + "'," +
                "'" + kronosIngestLog.getKtype() + "'," +
                kronosIngestLog.getKproc() + "))";

        **/

        String sql = "insert into krnlog ( kdate, ktot, kproc, kadtcnt, ktype, kstatus, kaudit ) VALUES (?, ?, ?, ?, ?, ?, ?);";


        KronosQuartzIngestLog skronosIngestLog = new KronosQuartzIngestLog();

        try {

            jdbcTemplate.update(sql, new Object[] {
                    kronosIngestLog.getKdate(), kronosIngestLog.getKtot(), kronosIngestLog.getKproc(),
                    kronosIngestLog.getKadtcnt(), kronosIngestLog.getKtype(), kronosIngestLog.getKstatus(),
                    kronosIngestLog.getKaudit()
            });

            //String searchSql = "select * from krnlog where id = last_insert_id();";//TODO:
            //String searchSql = "select * from krnlog where id = select currval(pg_get_serial_sequence('krnlog','id'));";
            String searchSql = "select * from krnlog order by id desc limit 1;";

            skronosIngestLog = (KronosQuartzIngestLog) jdbcTemplate.queryForObject(searchSql, new Object[]{},
                    new BeanPropertyRowMapper(KronosQuartzIngestLog.class));

            /**
            skronosIngestLog = (KronosQuartzIngestLog) jdbcTemplate.queryForObject(sql, new Object[]{},
                    new BeanPropertyRowMapper(KronosQuartzIngestLog.class));
            **/

        }catch(Exception e){
            e.printStackTrace();
            log.warn("unable to save log ...");
        }

        return skronosIngestLog;
    }
//TODO:

    public KronosQuartzIngestLog update(KronosQuartzIngestLog kronosIngestLog){

        //String sql = "update QGPL.KRNLOG set ( kstatus, ktot, kadtcnt, kaudit, kproc, ktype ) = (?, ?, ?, ?, ?, ?)  where id = ?";
        String sql = "update KRNLOG set ( kstatus, ktot, kadtcnt, kaudit, kproc, ktype ) = (?, ?, ?, ?, ?, ?)  where id = ?";//TODO:

        jdbcTemplate.update(sql, new Object[] {
                kronosIngestLog.getKstatus(), kronosIngestLog.getKtot(), kronosIngestLog.getKadtcnt(),
                kronosIngestLog.getKaudit(), kronosIngestLog.getKproc(), kronosIngestLog.getKtype(), kronosIngestLog.getId()
        });

        return findById(kronosIngestLog.getId());

    }



    public KronosQuartzIngestLog updateStatus(KronosQuartzIngestLog kronosIngestLog){

        //String sql = "update QGPL.KRNLOG set ( kstatus ) = (?)  where id = ?";
        String sql = "update KRNLOG set ( kstatus ) = (?)  where id = ?";

        log.info(sql);
        jdbcTemplate.update(sql, new Object[] {
                kronosIngestLog.getKstatus(), kronosIngestLog.getId()
        });

        return findById(kronosIngestLog.getId());

    }

}


