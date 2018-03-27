package org.athens.dao;

import org.athens.domain.KrnwhLog;

import java.util.List;
import java.math.BigDecimal;


public interface KrnwhLogDao {

	public int count();

	public List<KrnwhLog> list(int max, int offset);
	
	public KrnwhLog save(KrnwhLog krnwhLog);
	
	public KrnwhLog update(KrnwhLog krnwhLog);

	public KrnwhLog find(BigDecimal id);
	
}


