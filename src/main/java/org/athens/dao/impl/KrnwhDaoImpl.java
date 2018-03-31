package org.athens.dao.impl;

import org.apache.log4j.Logger;
import org.athens.domain.KRNWH;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.athens.dao.KrnwhDao;

import java.math.BigDecimal;
import java.util.List;


public class KrnwhDaoImpl implements KrnwhDao {


	final static Logger log = Logger.getLogger(KrnwhDaoImpl.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public int count() {
		String sql = "select count(*) from KRNWH";
		int count = 0;
		try{
			count = jdbcTemplate.queryForObject(sql, Integer.class, new Object[0]);
		}catch(Exception e){
			e.printStackTrace();
		}
		return count;
	}


	public KRNWH find(){
		String sql = "select * from QGPL.KRNWH limit 1";
		KRNWH k = (KRNWH) jdbcTemplate.queryForObject(sql, new Object[] {},
						new BeanPropertyRowMapper(KRNWH.class));
		System.out.println(k.toString());
		return k;
	}

	public KRNWH findByPunchBadgeIdEmployeeId(BigDecimal fppunc, BigDecimal fpbadg, BigDecimal fpempn){
		KRNWH k = null;
		try{
			String sql = "select * from QGPL.KRNWH where fppunc = " + fppunc + " and fpbadg = " + fpbadg + " and fpempn = " + fpempn;
			k = (KRNWH) jdbcTemplate.queryForObject(sql, new Object[] {},
					new BeanPropertyRowMapper(KRNWH.class));
		}catch(Exception e){
			log.warn("unable to find by punch badge id employee id");
		}
		return k;
	}

	public KRNWH findByPunchBadgeId(BigDecimal fppunc, BigDecimal fpbadg){
		KRNWH k = null;
		try{
			String sql = "select * from QGPL.KRNWH where fppunc = " + fppunc + " and fpbadg = " + fpbadg;
			k = (KRNWH) jdbcTemplate.queryForObject(sql, new Object[] {},
					new BeanPropertyRowMapper(KRNWH.class));
		}catch(Exception e){
			log.warn("unable to find by punch badge id");
		}
		return k;
	}

	public KRNWH findByPunchEmployeeId(BigDecimal fppunc, BigDecimal fpempn){
		KRNWH k = null;
		try{
			String sql = "select * from QGPL.KRNWH where fppunc = " + fppunc + " and fpempn = " + fpempn;
			k = (KRNWH) jdbcTemplate.queryForObject(sql, new Object[] {},
					new BeanPropertyRowMapper(KRNWH.class));
		}catch(Exception e){
			log.warn("unable to find by punch employee id");
		}
		return k;
	}




	public List<KRNWH> list(int max, int offset){
		try{

			String sql = "select * from QGPL.KRNWH limit " + max + " offset " + offset;
			System.out.println("find all " + sql);
			List<KRNWH> krnwhs = jdbcTemplate.query(sql, new BeanPropertyRowMapper(KRNWH.class));
			
			return krnwhs;
		
		}catch (Exception e){
			e.printStackTrace();
		}	
		return null;	
	}


	public List<KRNWH> listByIngest(int max, int offset, BigDecimal ingest){
		try{

			String sql = "select * from QGPL.KRNWH where krnlogid = " + ingest + " limit " + max + " offset " + offset;
			System.out.println("find all " + sql);
			List<KRNWH> krnwhs = jdbcTemplate.query(sql, new BeanPropertyRowMapper(KRNWH.class));

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
		
	public KRNWH save(KRNWH krnwh){
		String countSql = "select max(id) + 1 from QGPL.KRNWH";

		int id;

		try{
			id = jdbcTemplate.queryForObject(countSql, Integer.class, new Object[0]);
		}catch(Exception e){
			log.warn("unable to get next id");
			id = 0;
		}

		String saveSql = "insert into QGPL.KRNWH " +
				"( id, fpempn, fppunc, fptype, fpclck, fpbadg, fpfkey, fppcod, fstatus, krnlogid ) " +
				"values " +
				"(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		jdbcTemplate.update(saveSql, new Object[] {
				id, krnwh.getFpempn(), krnwh.getFppunc(), krnwh.getFptype(),
				krnwh.getFpclck(), krnwh.getFpbadg(), krnwh.getFpfkey(),
				krnwh.getFppcod(), krnwh.getFstatus(), krnwh.getKrnlogid()
		});

		//KRNWH savedKrnwh = find(id);

		return krnwh;
	}
	

}









