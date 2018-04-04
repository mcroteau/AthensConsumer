package org.athens.domain;

import java.util.HashMap;
import java.util.Map;

public class KrnwhJobStats {

    private int total;
    private int saved;
    private int processed;
    private int found;
    private int errored;

    private String status;

    private Map<String, Integer> existsMap = new HashMap<String, Integer>();
    private Map<String, Krnwh> auditMap = new HashMap<String, Krnwh>();



    public Map<String, Krnwh> getAuditMap() {
        return auditMap;
    }

    public Krnwh getAuditMapValue(String key) {
        return auditMap.get(key);
    }

    public Krnwh setAuditMapValue(String key, Krnwh krnwh) {
        auditMap.put(key, krnwh);
    }

    private Map<String, Krnwh> auditMap;

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

    public int getProcessed() {
        return processed;
    }

    public void setProcessed(int processed) {
        this.processed = processed;
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

}
