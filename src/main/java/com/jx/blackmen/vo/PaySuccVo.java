package com.jx.blackmen.vo;

import java.util.List;

import com.jx.service.preferential.plug.vo.UnitVO;

public class PaySuccVo {

	private long payid;
	private long orderid;
	private int localid;
	private String localstr;
	private String servername;
	private String paycount;
	private List<UnitVO> couponList ;
	
	
	public String getServername() {
		return servername;
	}
	public void setServername(String servername) {
		this.servername = servername;
	}
	public long getPayid() {
		return payid;
	}
	public void setPayid(long payid) {
		this.payid = payid;
	}
	public long getOrderid() {
		return orderid;
	}
	public void setOrderid(long orderid) {
		this.orderid = orderid;
	}
	public int getLocalid() {
		return localid;
	}
	public void setLocalid(int localid) {
		this.localid = localid;
	}
	public String getLocalstr() {
		return localstr;
	}
	public void setLocalstr(String localstr) {
		this.localstr = localstr;
	}
	public String getPaycount() {
		return paycount;
	}
	public void setPaycount(String paycount) {
		this.paycount = paycount;
	}
	public List<UnitVO> getCouponList() {
		return couponList;
	}
	public void setCouponList(List<UnitVO> couponList) {
		this.couponList = couponList;
	}

	
}
