package com.oksusu.hdh.domain;

import java.util.List;
import java.util.Map;

public class EsSearchVO {

	
	private int bno;
	private String index;
	private String type;
	private String id;
	private String title;
	private String name;
	private String content;
	private List<Map<String, String>> params;
	private List<Map<String, String[]>> arrayParams;
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public List<Map<String, String>> getParams() {
		return params;
	}
	public void setParams(List<Map<String, String>> params) {
		this.params = params;
	}
	public List<Map<String, String[]>> getArrayParams() {
		return arrayParams;
	}
	public void setArrayParams(List<Map<String, String[]>> arrayParams) {
		this.arrayParams = arrayParams;
	}
	@Override
	public String toString() {
		return "BoardVO [bno=" + bno + ", index=" + index + ", type=" + type + ", id=" + id + ", title=" + title
				+ ", name=" + name + ", content=" + content + ", params=" + params + ", arrayParams=" + arrayParams
				+ "]";
	}
	
	
	
	
}
