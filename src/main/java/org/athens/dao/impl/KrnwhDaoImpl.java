package org.athens.dao.impl;

import org.athens.domain.KRNWH;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.athens.dao.KrnwhDao;

import java.util.List;


public class KrnwhDaoImpl implements KrnwhDao {

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
		//int id = getJdbcTemplate().queryForObject(countSql, new Object[0]);
		//int id = getJdbcTemplate().queryForObject(countSql, new Object[0], Integer.class);

		String saveSql = "insert into QGPL.KRNWH " +
				"( fpempn, fppunc, fptype, fpclck, fpbadg, fpfkey, fppcod, fstatus ) " +
				"values " +
				"(?, ?, ?, ?, ?, ?, ?, ?)";

		jdbcTemplate.update(saveSql, new Object[] {
				krnwh.getFpempn(), krnwh.getFppunc(), krnwh.getFptype(),
				krnwh.getFpclck(), krnwh.getFpbadg(), krnwh.getFpfkey(),
				krnwh.getFppcod(), krnwh.getFstatus()
		});

		//KRNWH savedKrnwh = find(id);

		return krnwh;
	}
	

}









