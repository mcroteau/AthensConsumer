package org.athens.dao;

import org.athens.domain.QuartzIngestLog;

import java.util.List;
import java.math.BigDecimal;


public interface KrnwhLogDao {

	public int count();

	public List<QuartzIngestLog> list(int max, int offset);
	
	public QuartzIngestLog save(QuartzIngestLog ingestLog);
	
	public QuartzIngestLog update(QuartzIngestLog ingestLog);

	public QuartzIngestLog find(BigDecimal id);

	public QuartzIngestLog findByDate(BigDecimal date);

}


