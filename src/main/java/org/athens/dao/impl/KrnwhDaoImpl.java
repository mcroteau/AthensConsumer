package org.athens.dao.impl;

import org.apache.log4j.Logger;
import org.athens.domain.Krnwh;
import org.athens.domain.Krnwh;
import org.athens.domain.KrnwhLog;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.athens.dao.KrnwhDao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class KrnwhDaoImpl implements KrnwhDao {


	final static Logger log = Logger.getLogger(KrnwhDaoImpl.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public int count() {
		String sql = "select count(*) from Krnwh";
		int count = 0;
		try{
			count = jdbcTemplate.queryForObject(sql, Integer.class, new Object[0]);
		}catch(Exception e){
			e.printStackTrace();
		}
		return count;
	}


	public Krnwh find(){
		String sql = "select * from QGPL.Krnwh limit 1";
		Krnwh k = (Krnwh) jdbcTemplate.queryForObject(sql, new Object[] {},
						new BeanPropertyRowMapper(Krnwh.class));
		System.out.println(k.toString());
		return k;
	}

	public Krnwh findByPunchBadgeIdEmployeeId(BigDecimal fppunc, BigDecimal fpbadg, BigDecimal fpempn){
		Krnwh k = null;
		try{
			String sql = "select * from QGPL.Krnwh where fppunc = " + fppunc + " and fpbadg = " + fpbadg + " and fpempn = " + fpempn;
			k = (Krnwh) jdbcTemplate.queryForObject(sql, new Object[] {},
					new BeanPropertyRowMapper(Krnwh.class));
		}catch(Exception e){
			log.warn("unable to find by punch badge id employee id");
		}
		return k;
	}

	public Krnwh findByPunchBadgeId(BigDecimal fppunc, BigDecimal fpbadg){
		Krnwh k = null;
		try{
			String sql = "select * from QGPL.Krnwh where fppunc = " + fppunc + " and fpbadg = " + fpbadg;
			k = (Krnwh) jdbcTemplate.queryForObject(sql, new Object[] {},
					new BeanPropertyRowMapper(Krnwh.class));
		}catch(Exception e){
			log.warn("unable to find by punch badge id");
		}
		return k;
	}

	public Krnwh findByPunchEmployeeId(BigDecimal fppunc, BigDecimal fpempn){
		Krnwh k = null;
		try{
			String sql = "select * from QGPL.Krnwh where fppunc = " + fppunc + " and fpempn = " + fpempn;
			k = (Krnwh) jdbcTemplate.queryForObject(sql, new Object[] {},
					new BeanPropertyRowMapper(Krnwh.class));
		}catch(Exception e){
			log.warn("unable to find by punch employee id");
		}
		return k;
	}




	public List<Krnwh> list(int max, int offset){
		try{

			String sql = "select * from QGPL.Krnwh limit " + max + " offset " + offset;
			System.out.println("find all " + sql);
			List<Krnwh> krnwhs = jdbcTemplate.query(sql, new BeanPropertyRowMapper(Krnwh.class));
			
			return krnwhs;
		
		}catch (Exception e){
			e.printStackTrace();
		}	
		return null;	
	}


	public List<Krnwh> listByIngest(int max, int offset, BigDecimal ingest){
		try{

			String sql = "select * from QGPL.Krnwh where krnlogid = " + ingest + " limit " + max + " offset " + offset;
			System.out.println("find all " + sql);
			List<Krnwh> krnwhs = jdbcTemplate.query(sql, new BeanPropertyRowMapper(Krnwh.class));

			return krnwhs;

		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
/**
	fpempn decimal(9,0),
	fppunc decimal(14,0),
	fptype varchar(1),
	fpclck varchar(15),
	fpbadg decimal(8,0),
	fpkey varchar(15),
	fppcod decimal(15,3),
	fstatus varchar(1)		
**/
		
	public Krnwh save(Krnwh krnwh){

		String sql = "SELECT * FROM FINAL TABLE " +
				"(insert into QGPL.KRNWH ( fpempn, fppunc, fptype, fpclck, fpbadg, fpfkey, fppcod, fstatus, krnlogid ) " +
				"values " +
				"("   + krnwh.getFpempn() + "," +
					    krnwh.getFppunc() + "," +
				  "'" + krnwh.getFptype() + "'," +
				  "'" + krnwh.getFpclck() + "'," +
						krnwh.getFpbadg() + "," +
				  "'" + krnwh.getFpfkey() + "'," +
						krnwh.getFppcod() + "," +
				  "'" + krnwh.getFstatus() + "'," +
						krnwh.getKrnlogid() + "))";

		log.info(sql);
		Krnwh skrnwh = new Krnwh();

		try {
			skrnwh = (Krnwh) jdbcTemplate.queryForObject(sql, new Object[]{},
					new BeanPropertyRowMapper(Krnwh.class));

		}catch(Exception e){
			e.printStackTrace();
			log.warn("unable to save log ...");
		}

		return skrnwh;

	}


	public List<Krnwh> findByDate(BigDecimal startDate, BigDecimal endDate){
		String sql = "select * from QGPL.Krnwh where fppunc between " + startDate + " and " + endDate;

		log.info("find by date : " + sql);

		List<Krnwh> krnwhs = new ArrayList<Krnwh>();

		try {
			krnwhs = jdbcTemplate.query(sql, new BeanPropertyRowMapper(Krnwh.class));

		}catch(Exception e){
			log.warn("unable to find krnwhs by date...");
		}
		return krnwhs;
	}



}









