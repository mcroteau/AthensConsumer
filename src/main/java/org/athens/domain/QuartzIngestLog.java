package org.athens.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;


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


	public String getKtype() {
		return ktype;
	}

	public void setKtype(String ktype) {
		this.ktype = ktype;
	}


	public BigDecimal getPercent() {
		BigDecimal percent = new BigDecimal(0);
		if(kproc != null &&
				ktot != null &&
					kproc.compareTo(new BigDecimal(0)) != 0 &&
						ktot.compareTo(new BigDecimal(0)) != 0){
			percent = kproc.divide(ktot, 6, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
		}
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
				" ktype: " + this.getKtype() + "/n";
	}

}

