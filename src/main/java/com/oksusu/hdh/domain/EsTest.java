package com.oksusu.hdh.domain;




public class EsTest {

	private int movie_no;
	private String title;
	private String runningTime;
	private String genre;
	private String summary;
	private String type;
	private String index;
	public int getMovie_no() {
		return movie_no;
	}
	public void setMovie_no(int movie_no) {
		this.movie_no = movie_no;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getRunningTime() {
		return runningTime;
	}
	public void setRunningTime(String runningTime) {
		this.runningTime = runningTime;
	}
	public String getGenre() {
		return genre;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
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
		return "EsTest [movie_no=" + movie_no + ", title=" + title + ", runningTime=" + runningTime + ", genre=" + genre
				+ ", summary=" + summary + ", type=" + type + ", index=" + index + "]";
	}
	
	
	
	
	
	}
