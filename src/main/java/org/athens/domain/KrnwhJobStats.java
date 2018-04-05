package org.athens.domain;

import org.athens.common.ApplicationConstants;

import java.util.HashMap;
import java.util.Map;

public class KrnwhJobStats {

    private int total;
    private int saved;
    private int found;
    private int errored;
    private int processed;
    private String status;

    private Map<String, Integer> existsMap = new HashMap<String, Integer>();
    private Map<String, KronosWorkHour> auditMap = new HashMap<String, KronosWorkHour>();


    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getSaved() {
        return saved;
    }

    public void setSaved(int saved) {
        this.saved = saved;
    }

    public int getFound() {
        return found;
    }

    public void setFound(int found) {
        this.found = found;
    }

    public int getErrored() {
        return errored;
    }

    public void setErrored(int errored) {
        this.errored = errored;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getProcessed() {
        return processed;
    }

    public void setProcessed(int processed) {
        this.processed = processed;
    }


    public Map<String, Integer> getExistsMap() {
        return existsMap;
    }

    public int getExistsMapValue(String key) {
        return existsMap.get(key);
    }

    public void setExistsMapValue(String key, int occurrences) {
        existsMap.put(key, occurrences);
    }


    public Map<String, KronosWorkHour> getAuditMap() {
        return auditMap;
    }

    public KronosWorkHour getAuditMapValue(String key) {
        return auditMap.get(key);
    }

    public void setAuditMapValue(String key, KronosWorkHour kronosWorkHour) {
        auditMap.put(key, kronosWorkHour);
    }


    public boolean jobRunning() {
        return (this.status != null && this.status.equals(ApplicationConstants.RUNNING_STATUS)) ? true : false;
    }

}
