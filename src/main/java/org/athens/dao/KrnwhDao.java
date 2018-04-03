package org.athens.dao;

import org.athens.domain.Krnwh;
import org.athens.domain.Krnwh;

import java.math.BigDecimal;
import java.util.List;


public interface KrnwhDao {

	public int count();

	public Krnwh find();
	
	public List<Krnwh> list(int max, int offset);
	
	public Krnwh save(Krnwh krnwh);

	public Krnwh findByPunchBadgeIdEmployeeId(BigDecimal fppunc, BigDecimal fpbadg, BigDecimal fpempn);

	public Krnwh findByPunchBadgeId(BigDecimal fppunc, BigDecimal fpbadg);

	public Krnwh findByPunchEmployeeId(BigDecimal fppunc, BigDecimal fpempn);
	
}