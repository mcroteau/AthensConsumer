package org.athens.dao;

import org.athens.domain.QuartzIngestLog;

import java.util.List;
import java.math.BigDecimal;


public interface QuartzIngestLogDao {

	public int count();

	public List<QuartzIngestLog> list(int max, int offset);

	public QuartzIngestLog findById(BigDecimal id);

	public QuartzIngestLog findByDate(BigDecimal date);

	public List<QuartzIngestLog> findAllByStatus(String status);

	public QuartzIngestLog save(QuartzIngestLog kronosIngestLog);
	
	public QuartzIngestLog update(QuartzIngestLog kronosIngestLog);



}


