package com.jx.blackmen.vo;

public class UserVo {

	public long userid;
	public String openid;
	public String phonumber;
	public String username;
	public String address;
	public int authenflag; //认证标识duxf
	
	public long getUserid() {
		return userid;
	}
	public void setUserid(long userid) {
		this.userid = userid;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getPhonumber() {
		return phonumber;
	}
	public void setPhonumber(String phonumber) {
		this.phonumber = phonumber;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public int getAuthenflag() {
		return authenflag;
	}
	public void setAuthenflag(int authenflag) {
		this.authenflag = authenflag;
	}
	
	
}
