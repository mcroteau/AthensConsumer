package org.athens.dao.impl;

import org.apache.log4j.Logger;
import org.athens.domain.KronosWorkHour;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.athens.dao.KronosWorkHourDao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class KronosWorkHourDaoImpl implements KronosWorkHourDao {


	final static Logger log = Logger.getLogger(KronosWorkHourDaoImpl.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;


	public int count() {
		String sql = "select count(*) from QGPL.KRNWH";
		int count = 0;
		try{
			count = jdbcTemplate.queryForObject(sql, Integer.class, new Object[0]);
		}catch(Exception e){
			e.printStackTrace();
		}
		return count;
	}




	public KronosWorkHour save(KronosWorkHour kronosWorkHour){

		String sql = "SELECT * FROM FINAL TABLE " +
				"(insert into QGPL.KRNWH ( fpempn, fppunc, fptype, fpclck, fpbadg, fpfkey, fppcod, fstatus, krnlogid ) " +
				"values " +
				"("   + kronosWorkHour.getFpempn() + "," +
				kronosWorkHour.getFppunc() + "," +
				"'" + kronosWorkHour.getFptype() + "'," +
				"'" + kronosWorkHour.getFpclck() + "'," +
				kronosWorkHour.getFpbadg() + "," +
				"'" + kronosWorkHour.getFpfkey() + "'," +
				kronosWorkHour.getFppcod() + "," +
				"'" + kronosWorkHour.getFstatus() + "'," +
				kronosWorkHour.getKrnlogid() + "))";

		log.info(sql);
		KronosWorkHour skrnwh = (KronosWorkHour) jdbcTemplate.queryForObject(sql, new Object[]{},
					new BeanPropertyRowMapper(KronosWorkHour.class));

		return skrnwh;

	}



	public List<KronosWorkHour> list(int max, int offset){
		List<KronosWorkHour> kronosWorkHours = new ArrayList<KronosWorkHour>();
		try{

			String sql = "select * from QGPL.KRNWH limit " + max + " offset " + offset;
			System.out.println("find all " + sql);
			kronosWorkHours = jdbcTemplate.query(sql, new BeanPropertyRowMapper(KronosWorkHour.class));

		}catch (Exception e){
			e.printStackTrace();
		}	
		return kronosWorkHours;
	}



	public List<KronosWorkHour> findByIngest(int max, int offset, BigDecimal ingest){
		List<KronosWorkHour> kronosWorkHours = new ArrayList<KronosWorkHour>();
		try{

			String sql = "select * from QGPL.KRNWH where krnlogid = " + ingest + " limit " + max + " offset " + offset;
			System.out.println("find all " + sql);
			kronosWorkHours = jdbcTemplate.query(sql, new BeanPropertyRowMapper(KronosWorkHour.class));

		}catch (Exception e){
			log.warn("exception : unable to find punch by ingest...");
		}
		return kronosWorkHours;
	}



	public List<KronosWorkHour> findByDate(BigDecimal startDate, BigDecimal endDate){
		String sql = "select * from QGPL.KRNWH where fppunc between " + startDate + " and " + endDate;

		log.info("find by date : " + sql);

		List<KronosWorkHour> kronosWorkHours = new ArrayList<KronosWorkHour>();

		try {
			kronosWorkHours = jdbcTemplate.query(sql, new BeanPropertyRowMapper(KronosWorkHour.class));

		}catch(Exception e){
			log.warn("exception : unable to find kronosWorkHours by date...");
		}
		return kronosWorkHours;
	}



	public KronosWorkHour findByPunchBadgeId(BigDecimal fppunc, BigDecimal fpbadg){
		KronosWorkHour k = null;
		try{
			String sql = "select * from QGPL.KRNWH where fppunc = " + fppunc + " and fpbadg = " + fpbadg;
			k = (KronosWorkHour) jdbcTemplate.queryForObject(sql, new Object[] {},
					new BeanPropertyRowMapper(KronosWorkHour.class));
		}catch(Exception e){
			log.warn("exception : unable to find by punch badge id");
		}
		return k;
	}



	public KronosWorkHour findByPunchEmployeeId(BigDecimal fppunc, BigDecimal fpempn){
		KronosWorkHour k = null;
		try{
			String sql = "select * from QGPL.KRNWH where fppunc = " + fppunc + " and fpempn = " + fpempn;
			k = (KronosWorkHour) jdbcTemplate.queryForObject(sql, new Object[] {},
					new BeanPropertyRowMapper(KronosWorkHour.class));
		}catch(Exception e){
			log.warn("exception : unable to find by punch employee id");
		}
		return k;
	}




	public KronosWorkHour findByPunchBadgeIdEmployeeId(BigDecimal fppunc, BigDecimal fpbadg, BigDecimal fpempn){
		KronosWorkHour k = null;
		try{
			String sql = "select * from QGPL.KRNWH where fppunc = " + fppunc + " and fpbadg = " + fpbadg + " and fpempn = " + fpempn;
			k = (KronosWorkHour) jdbcTemplate.queryForObject(sql, new Object[] {},
					new BeanPropertyRowMapper(KronosWorkHour.class));
		}catch(Exception e){
			log.warn("exception : unable to find by punch badge id employee id");
		}
		return k;
	}



}









