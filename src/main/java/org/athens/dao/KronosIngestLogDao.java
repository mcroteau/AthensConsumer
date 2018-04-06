package org.athens.dao;

import org.athens.domain.KronosIngestLog;

import java.util.List;
import java.math.BigDecimal;


public interface KronosIngestLogDao {

	public int count();

	public List<KronosIngestLog> list(int max, int offset);

	public KronosIngestLog findById(BigDecimal id);

	public KronosIngestLog findByDate(BigDecimal date);

	public List<KronosIngestLog> findAllByStatus(String status);

	public KronosIngestLog save(KronosIngestLog kronosIngestLog);
	
	public KronosIngestLog update(KronosIngestLog kronosIngestLog);



}


