package org.athens.dao;

import org.athens.domain.KronosWorkHour;

import java.math.BigDecimal;
import java.util.List;


public interface KrnwhDao {

	public int count();

	public KronosWorkHour save(KronosWorkHour kronosWorkHour);

	public List<KronosWorkHour> list(int max, int offset);

	public List<KronosWorkHour> findByDate(BigDecimal startDate, BigDecimal endDate);

	public List<KronosWorkHour> findByIngest(int max, int offset, BigDecimal ingest);

	public KronosWorkHour findByPunchBadgeId(BigDecimal fppunc, BigDecimal fpbadg);

	public KronosWorkHour findByPunchEmployeeId(BigDecimal fppunc, BigDecimal fpempn);

	public KronosWorkHour findByPunchBadgeIdEmployeeId(BigDecimal fppunc, BigDecimal fpbadg, BigDecimal fpempn);
	
}