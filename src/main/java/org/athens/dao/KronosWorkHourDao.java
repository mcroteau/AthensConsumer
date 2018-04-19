package org.athens.dao;

import org.athens.domain.KronosWorkHour;

import java.math.BigDecimal;
import java.util.List;


public interface KronosWorkHourDao {

	public KronosWorkHour save(KronosWorkHour kronosWorkHour);

	public List<KronosWorkHour> findByDate(BigDecimal startDate, BigDecimal endDate);

	public List<KronosWorkHour> findByDateEmployeeId(BigDecimal startDate, BigDecimal endDate, BigDecimal fpempn);

	public KronosWorkHour findByPunchBadgeId(BigDecimal fppunc, BigDecimal fpbadg);

	public KronosWorkHour findByPunchEmployeeId(BigDecimal fppunc, BigDecimal fpempn);

	public KronosWorkHour findByPunchBadgeIdEmployeeId(BigDecimal fppunc, BigDecimal fpbadg, BigDecimal fpempn);
	
}