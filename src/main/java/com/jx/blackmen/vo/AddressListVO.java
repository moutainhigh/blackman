package com.jx.blackmen.vo;

import java.io.Serializable;
import java.util.List;

/**
 * @author bruce
 * @date 2016-08-23
 * @email zhangyang226@gmail.com
 * @site http://blog.northpark.cn | http://northpark.cn | orginazation https://github.com/jellyband
 * 
 */

public class AddressListVO implements Serializable{


/**
	 * 
	 */
private static final long serialVersionUID = 2036302376450552848L;


private long areaorder_id;


private long cityid;


private long areaid;


private String city_name;


private String area_name;


private String ordersby;



private List<AddressConfVO> confvo;



public long getAreaorder_id() {
	return areaorder_id;
}



public void setAreaorder_id(long areaorder_id) {
	this.areaorder_id = areaorder_id;
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



public String getCity_name() {
	return city_name;
}



public void setCity_name(String city_name) {
	this.city_name = city_name;
}



public String getArea_name() {
	return area_name;
}



public void setArea_name(String area_name) {
	this.area_name = area_name;
}



public String getOrdersby() {
	return ordersby;
}



public void setOrdersby(String ordersby) {
	this.ordersby = ordersby;
}



public List<AddressConfVO> getConfvo() {
	return confvo;
}



public void setConfvo(List<AddressConfVO> confvo) {
	this.confvo = confvo;
} 
	

}