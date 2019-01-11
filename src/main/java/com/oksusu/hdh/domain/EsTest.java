package com.oksusu.hdh.domain;




public class EsTest {

	private int movie_no;
	private String type;
	private String index;
	
	
	public int getMovie_no() {
		return movie_no;
	}


	public void setMovie_no(int movie_no) {
		this.movie_no = movie_no;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getIndex() {
		return index;
	}


	public void setIndex(String index) {
		this.index = index;
	}


	@Override
	public String toString() {
		return "EsTest [movie_no=" + movie_no + ", type=" + type + ", index=" + index + "]";
	}
	
	
	
	}
