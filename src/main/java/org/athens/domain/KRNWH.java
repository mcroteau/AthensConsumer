package org.athens.domain;

import java.math.BigDecimal;

public class KRNWH {

	public KRNWH(){}

	private BigDecimal id;
	private BigDecimal fpempn;
  	private BigDecimal fppunc;
  	private String fptype;
  	private String fpclck;
  	private BigDecimal fpbadg;
  	private String fpfkey;
  	private BigDecimal fppcod;
  	private String fstatus;
	private BigDecimal krnlogid;
	
	/**
		
	fpempn decimal(9,0),
	fppunc decimal(14,0),
	fptype varchar(1),
	fpclck varchar(15),
	fpbadg decimal(8,0),
	fpfkey varchar(15),
	fppcod decimal(15,3),
	fstatus varchar(1)
	 id numeric(8)
	 krnlogId numeric(8)
**/
	public void setId(BigDecimal id){
		this.id = id;
	}

	public BigDecimal getId(){
		return this.id;
	}



	public void setFpempn(BigDecimal fpempn){
		this.fpempn = fpempn;
	}
	
	public BigDecimal getFpempn(){
		return this.fpempn;
	}
	
	
	
	public void setFppunc(BigDecimal fppunc){
		this.fppunc = fppunc;
	}
	
	public BigDecimal getFppunc(){
		return this.fppunc;
	}
	
	
	
	public void setFptype(String fptype){
		this.fptype = fptype;
	}

	public String getFptype(){
		return this.fptype;
	}
	
	
	
	public void setFpclck(String fpclck){
		this.fpclck = fpclck;
	}
	
	public String getFpclck(){
		return this.fpclck;
	}
	
	
	
	public void setFpbadg(BigDecimal fpbadg){
		this.fpbadg = fpbadg;
	}
	
	public BigDecimal getFpbadg(){
		return this.fpbadg;
	}
	
	
	
	public void setFpfkey(String fpfkey){
		this.fpfkey = fpfkey;
	}

	public String getFpfkey(){
		return this.fpfkey;
	}

	
	
	public void setFppcod(BigDecimal fppcod){
		this.fppcod = fppcod;
	}
	
	public BigDecimal getFppcod(){
		return this.fppcod;
	}
	
	
	
	public void setFstatus(String fstatus){
		this.fstatus = fstatus;
	}
	
	public String getFstatus(){
		return this.fstatus;
	}




	public void setKrnlogid(BigDecimal krnlogid){
		this.krnlogid = krnlogid;
	}

	public BigDecimal getKrnlogid(){
		return this.krnlogid;
	}



	/**
		
	fpempn decimal(9,0),
	fppunc decimal(14,0),
	fptype varchar(1),
	fpclck varchar(15),
	fpbadg decimal(8,0),
	fpfkey varchar(15),
	fppcod decimal(15,3),
	fstatus varchar(1)
	 id numeric(8)
	 krnlogid numeric(8)
**/
	
	public String toString(){
		return  "\nid: " + this.getId() +
				" fpempn: " + this.getFpempn() +
				" fppunc: " + this.getFppunc() +
				" fptype: " + this.getFptype() +
				" fpclck: " + this.getFpclck() +
				" fpbadg: " + this.getFpbadg() +
				" fpfkey: " + this.getFpfkey() +
				" fppcod: " + this.getFppcod() +
				" fstatus: " + this.getFstatus() +
				" krnlogid: " + this.getKrnlogid() + "\n";
	}
	
	
}