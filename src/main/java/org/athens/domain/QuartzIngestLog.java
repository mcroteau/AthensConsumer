package org.athens.domain;

import java.math.BigDecimal;


public class QuartzIngestLog {
	
	public QuartzIngestLog(){}
		
	private BigDecimal id;
	private String kstatus;
	private BigDecimal ktot;
	private BigDecimal kadtcnt;
	private String kaudit;
	private BigDecimal kdate;
	private BigDecimal kproc;
	private String ktype;
	
	
	
	public void setId(BigDecimal id){
		this.id = id;
	}
	
	public BigDecimal getId(){
		return this.id;
	}	
	
	
	public void setKstatus(String kstatus){
		this.kstatus = kstatus;
	}
	
	public String getKstatus(){
		return this.kstatus;
	}	
	
	
	public void setKtot(BigDecimal ktot){
		this.ktot = ktot;
	}
	
	public BigDecimal getKtot(){
		return this.ktot;
	}
	
	
	public void setKadtcnt(BigDecimal kadtcnt){
		this.kadtcnt = kadtcnt;
	}
	
	public BigDecimal getKadtcnt(){
		return this.kadtcnt;
	}	
	
	
	public void setKaudit(String kaudit){
		this.kaudit = kaudit;
	}
	
	public String getKaudit(){
		return this.kaudit;
	}	
	
	
	public void setKdate(BigDecimal kdate){
		this.kdate = kdate;
	}
	
	public BigDecimal getKdate(){
		return this.kdate;
	}


	public BigDecimal getKproc() {
		return kproc;
	}

	public void setKproc(BigDecimal kproc) {
		this.kproc = kproc;
	}


	public String getKType() {
		return ktype;
	}

	public void setKType(String ktype) {
		this.ktype = ktype;
	}


	public BigDecimal getPercent() {
		BigDecimal percent = kproc.divide(ktot).multiply(new BigDecimal(100));
		return percent;

	}


	public String toString(){
		return  "\nid: " + this.getId() +
				" kstatus: " + this.getKstatus() +
				" kproc: " + this.getKproc() +
				" ktot: " + this.getKtot() +
				" kadtcnt: " + this.getKadtcnt() +
				" kaudit: " + this.getKaudit() +
				" kdate: " + this.getKdate() +
				" ktype: " + this.getKType() + "/n";
	}

	//kronosIngestLog.getId(), kronosIngestLog.getKstatus(), kronosIngestLog.getKtot(),
	//kronosIngestLog.getKadtcnt(), kronosIngestLog.getKaudit(), kronosIngestLog.getKdate()

}

