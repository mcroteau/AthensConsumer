package org.athens.domain;

import java.math.BigDecimal;


public class KrnwhLog {
	
	public KrnwhLog(){}
		
	private BigDecimal id;
	private String kstatus;
	private BigDecimal ktot;
	private BigDecimal kadtcnt;
	private String kaudit;
	private BigDecimal kdate;
	
	
	
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
	
	
	public String toString(){
		return  "\nid: " + this.getId() +
				" kstatus: " + this.getKstatus() +
				" ktot: " + this.getKtot() +
				" kadtcnt: " + this.getKadtcnt() +
				" kaudit: " + this.getKaudit() +
				" kdate: " + this.getKdate() + "\n";
	}

	//krnwhLog.getId(), krnwhLog.getKstatus(), krnwhLog.getKtot(),
	//krnwhLog.getKadtcnt(), krnwhLog.getKaudit(), krnwhLog.getKdate()

}

