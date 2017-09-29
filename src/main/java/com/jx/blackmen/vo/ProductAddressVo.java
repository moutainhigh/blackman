package com.jx.blackmen.vo;

import java.io.Serializable;
import java.util.List;

import com.jx.blackface.gaea.sell.entity.LvzProductTitleEntity;

public class ProductAddressVo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8911360115559770058L;
	private long sellerid;
	private String sellername;
	private List<LvzProductTitleEntity> titlelist;
	private float sellerprice;
	private float marketprice;
	private String sell_address;
	private String imgurl;
	
	public long getSellerid() {
		return sellerid;
	}
	public void setSellerid(long sellerid) {
		this.sellerid = sellerid;
	}
	public String getSellername() {
		return sellername;
	}
	public void setSellername(String sellername) {
		this.sellername = sellername;
	}
	public List<LvzProductTitleEntity> getTitlelist() {
		return titlelist;
	}
	public void setTitlelist(List<LvzProductTitleEntity> titlelist) {
		this.titlelist = titlelist;
	}
	public float getSellerprice() {
		return sellerprice;
	}
	public void setSellerprice(float sellerprice) {
		this.sellerprice = sellerprice;
	}
	public float getMarketprice() {
		return marketprice;
	}
	public void setMarketprice(float marketprice) {
		this.marketprice = marketprice;
	}
	public String getSell_address() {
		return sell_address;
	}
	public void setSell_address(String sell_address) {
		this.sell_address = sell_address;
	}
	public String getImgurl() {
		return imgurl;
	}
	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}
	
	

}
