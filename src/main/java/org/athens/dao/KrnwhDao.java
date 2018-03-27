package org.athens.dao;

import org.athens.domain.KRNWH;

import java.util.List;


public interface KrnwhDao {

	public int count();

	public KRNWH find();
	
	public List<KRNWH> list(int max, int offset);
	
	public KRNWH save(KRNWH krnwh);
	
}