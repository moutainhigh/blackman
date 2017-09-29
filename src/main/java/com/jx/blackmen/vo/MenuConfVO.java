package com.jx.blackmen.vo;

import java.util.List;

import com.jx.blackface.gaea.usercenter.entity.LvzProductConfEntity;

/**
 * @author bruce
 * @date 2016-06-29
 * @email zhangyang226@gmail.com
 * @site http://blog.northpark.cn | http://northpark.cn | orginazation https://github.com/jellyband
 * 
 */

public class MenuConfVO {

private long menu_conf_id;

private long menu_id1;

private long menu_id2;

private String menu_name1;

private String menu_name2;

private String menu_code1;

private String menu_code2;

private String path;

private String pathname1;
private String pathname2;

private Integer flag;//分类标记[1：使用 0：不使用]

private Integer order1;
private Integer order2;

private Integer sxj;

private List<LvzProductConfEntity> prolist;

public long getMenu_conf_id() {
	return menu_conf_id;
}

public void setMenu_conf_id(long menu_conf_id) {
	this.menu_conf_id = menu_conf_id;
}

public long getMenu_id1() {
	return menu_id1;
}

public void setMenu_id1(long menu_id1) {
	this.menu_id1 = menu_id1;
}

public long getMenu_id2() {
	return menu_id2;
}

public void setMenu_id2(long menu_id2) {
	this.menu_id2 = menu_id2;
}

public String getMenu_name1() {
	return menu_name1;
}

public void setMenu_name1(String menu_name1) {
	this.menu_name1 = menu_name1;
}

public String getMenu_name2() {
	return menu_name2;
}

public void setMenu_name2(String menu_name2) {
	this.menu_name2 = menu_name2;
}

public String getPathname1() {
	return pathname1;
}

public void setPathname1(String pathname1) {
	this.pathname1 = pathname1;
}

public String getPathname2() {
	return pathname2;
}

public void setPathname2(String pathname2) {
	this.pathname2 = pathname2;
}

public Integer getFlag() {
	return flag;
}

public void setFlag(Integer flag) {
	this.flag = flag;
}

public Integer getOrder1() {
	return order1;
}

public void setOrder1(Integer order1) {
	this.order1 = order1;
}

public Integer getOrder2() {
	return order2;
}

public void setOrder2(Integer order2) {
	this.order2 = order2;
}

public Integer getSxj() {
	return sxj;
}

public void setSxj(Integer sxj) {
	this.sxj = sxj;
}

public List<LvzProductConfEntity> getProlist() {
	return prolist;
}

public void setProlist(List<LvzProductConfEntity> prolist) {
	this.prolist = prolist;
}

public String getMenu_code1() {
	return menu_code1;
}

public void setMenu_code1(String menu_code1) {
	this.menu_code1 = menu_code1;
}

public String getMenu_code2() {
	return menu_code2;
}

public void setMenu_code2(String menu_code2) {
	this.menu_code2 = menu_code2;
}

public String getPath() {
	return path;
}

public void setPath(String path) {
	this.path = path;
}


		

}