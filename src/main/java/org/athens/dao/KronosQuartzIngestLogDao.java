package org.athens.dao;

import org.athens.domain.KronosQuartzIngestLog;

import java.util.List;
import java.math.BigDecimal;

/**KRNLOG**/
public interface KronosQuartzIngestLogDao {

	public int count();

	public List<KronosQuartzIngestLog> list(int max, int offset);

	public KronosQuartzIngestLog findById(BigDecimal id);

	public KronosQuartzIngestLog findByDate(BigDecimal date);

	public List<KronosQuartzIngestLog> findAllByStatus(String status);

	public KronosQuartzIngestLog save(KronosQuartzIngestLog kronosIngestLog);
	
	public KronosQuartzIngestLog update(KronosQuartzIngestLog kronosIngestLog);

}


