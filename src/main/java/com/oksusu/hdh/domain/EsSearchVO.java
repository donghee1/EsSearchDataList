package com.oksusu.hdh.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class EsSearchVO {
	
	private String index;
	private String type;
	private String id;
	private String[] idkey;
	private String[] idvalue;
	private String config;
	private Integer searchSize;
	
	
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
	public String[] getIdkey() {
		return idkey;
	}
	public void setIdkey(String[] idkey) {
		this.idkey = idkey;
	}
	public String[] getIdvalue() {
		return idvalue;
	}
	public void setIdvalue(String[] idvalue) {
		this.idvalue = idvalue;
	}
	public String getConfig() {
		return config;
	}
	public void setConfig(String config) {
		this.config = config;
	}
	public Integer getSearchSize() {
		return searchSize;
	}
	public void setSearchSize(Integer searchSize) {
		this.searchSize = searchSize;
		
	}
	@Override
	public String toString() {
		return "EsSearchVO [index=" + index + ", type=" + type + ", id=" + id + ", idkey=" + Arrays.toString(idkey)
				+ ", idvalue=" + Arrays.toString(idvalue) + ", config=" + config + ", searchSize=" + searchSize + "]";
	}
	
	
	
	
	
	
}
