package com.tg.cloudmanagement.phone;

public class SortModel {

	private String name;   //要显示数据（姓名）
	public String telnum;
	public String getTelnum() {
		return telnum;
	}
	public void setTelnum(String telnum) {
		this.telnum = telnum;
	}
	private String sortLetters;  //显示数据拼音的首字母
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSortLetters() {
		return sortLetters;
	}
	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}
	
}
