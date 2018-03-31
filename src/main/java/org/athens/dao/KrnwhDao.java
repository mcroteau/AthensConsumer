package org.athens.dao;

import org.athens.domain.KRNWH;

import java.math.BigDecimal;
import java.util.List;


public interface KrnwhDao {

	public int count();

	public KRNWH find();
	
	public List<KRNWH> list(int max, int offset);
	
	public KRNWH save(KRNWH krnwh);

	public KRNWH findByPunchBadgeIdEmployeeId(BigDecimal fppunc, BigDecimal fpbadg, BigDecimal fpempn);

	public KRNWH findByPunchBadgeId(BigDecimal fppunc, BigDecimal fpbadg);

	public KRNWH findByPunchEmployeeId(BigDecimal fppunc, BigDecimal fpempn);
	
}