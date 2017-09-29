package com.jx.blackmen.common;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataFormat {
	/*double 显示位数*/
	public static String doubleFormatInt(double para){
		NumberFormat nf = new DecimalFormat("0.##");
		return nf.format(para);
	}
	
	/*手机号  中间部分用   * 代替*/
	public static String phoneFormatHide(String phoneNumber){
		return phoneNumber .substring(0,3) + "****" + phoneNumber .substring(7, phoneNumber .length());
	}
	
	/*时间 格式处理  yyyy.mm.dd*/
	public static String dateFormat(Date date){
		String dateStr = null;
		try {
			dateStr = new SimpleDateFormat("yyyy.MM.dd").format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateStr == null ? "" : dateStr;
	}
}
