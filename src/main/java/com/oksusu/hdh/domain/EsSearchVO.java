package com.oksusu.hdh.domain;

import java.util.List;
import java.util.Map;

public class EsSearchVO {

	
	
	private String index;
	private String type;
	private String id;
	
	
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
		return "EsSearchVO [index=" + index + ", type=" + type + ", id=" + id + "]";
	}
	
	
	
	
}
