package com.jx.blackmen.vo;

import java.io.Serializable;
import java.util.List;

import com.jx.blackface.gaea.sell.entity.LvzProductTitleEntity;

/**
 * @author bruce
 * @date 2016-08-23
 * @email zhangyang226@gmail.com
 * @site http://blog.northpark.cn | http://northpark.cn | orginazation https://github.com/jellyband
 * 
 */

public class AddressConfVO implements Serializable{


/**
	 * 
	 */
private static final long serialVersionUID = 7094604701315022478L;


private long address_conf_id;


private long cityid;


private long areaid;


private String cityname;


private String areaname;


private String address_name;


private long address_id;


private String com_type;


private String reg_money;


private String sw_info;


private Integer invoice;


private String descpation;


private String pc_fwbz;


private String pc_shbz;


private String m_fwbz;


private String ordersby;


private String address_desc;


private String pic_link;


private String pic_url;


private String run_scope;

private float market_price;

private float over_price;


private List<LvzProductTitleEntity> tags; 


	
public long getAddress_conf_id() {
	return address_conf_id;	
}
	
public void setAddress_conf_id(long address_conf_id) {
	this.address_conf_id = address_conf_id;
}
		
	
public long getCityid() {
	return cityid;	
}
	
public void setCityid(long cityid) {
	this.cityid = cityid;
}	
		
	
public long getAreaid() {
	return areaid;	
}
	
public void setAreaid(long areaid) {
	this.areaid = areaid;
}	
		
	
public String getAddress_name() {
	return address_name;	
}
	
public void setAddress_name(String address_name) {
	this.address_name = address_name;
}	
		
	
public long getAddress_id() {
	return address_id;	
}
	
public void setAddress_id(long address_id) {
	this.address_id = address_id;
}	
		
	
public String getCom_type() {
	return com_type;	
}
	
public void setCom_type(String com_type) {
	this.com_type = com_type;
}	
		
	
public String getReg_money() {
	return reg_money;	
}
	
public void setReg_money(String reg_money) {
	this.reg_money = reg_money;
}	
		
	
public String getSw_info() {
	return sw_info;	
}
	
public void setSw_info(String sw_info) {
	this.sw_info = sw_info;
}	
		
	
public Integer getInvoice() {
	return invoice;	
}
	
public void setInvoice(Integer invoice) {
	this.invoice = invoice;
}	
		
	
public String getDescpation() {
	return descpation;	
}
	
public void setDescpation(String descpation) {
	this.descpation = descpation;
}	
		
	
public String getPc_fwbz() {
	return pc_fwbz;	
}
	
public void setPc_fwbz(String pc_fwbz) {
	this.pc_fwbz = pc_fwbz;
}	
		
	
public String getPc_shbz() {
	return pc_shbz;	
}
	
public void setPc_shbz(String pc_shbz) {
	this.pc_shbz = pc_shbz;
}	
		
	
public String getM_fwbz() {
	return m_fwbz;	
}
	
public void setM_fwbz(String m_fwbz) {
	this.m_fwbz = m_fwbz;
}	
		
	
public String getOrdersby() {
	return ordersby;	
}
	
public void setOrdersby(String ordersby) {
	this.ordersby = ordersby;
}	
		
	
public String getAddress_desc() {
	return address_desc;	
}
	
public void setAddress_desc(String address_desc) {
	this.address_desc = address_desc;
}	
		
	
public String getPic_link() {
	return pic_link;	
}
	
public void setPic_link(String pic_link) {
	this.pic_link = pic_link;
}	
		
	
public String getRun_scope() {
	return run_scope;	
}
	
public void setRun_scope(String run_scope) {
	this.run_scope = run_scope;
}

public String getPic_url() {
	return pic_url;
}

public void setPic_url(String pic_url) {
	this.pic_url = pic_url;
}

public String getCityname() {
	return cityname;
}

public void setCityname(String cityname) {
	this.cityname = cityname;
}

public String getAreaname() {
	return areaname;
}

public void setAreaname(String areaname) {
	this.areaname = areaname;
}

public List<LvzProductTitleEntity> getTags() {
	return tags;
}

public void setTags(List<LvzProductTitleEntity> tags) {
	this.tags = tags;
}

public float getMarket_price() {
	return market_price;
}

public void setMarket_price(float market_price) {
	this.market_price = market_price;
}

public float getOver_price() {
	return over_price;
}

public void setOver_price(float over_price) {
	this.over_price = over_price;
}	
		

}