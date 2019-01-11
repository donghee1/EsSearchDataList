package com.oksusu.hdh.domain;

import java.util.List;
import java.util.Map;

public class EsSearchVO {

	
	private int bno;
	private String index;
	private String type;
	private String id;
	
	public int getBno() {
		return bno;
	}
	public void setBno(int bno) {
		this.bno = bno;
	}
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return "EsSearchVO [bno=" + bno + ", index=" + index + ", type=" + type + ", id=" + id + "]";
	}
		
	
	
	
}
