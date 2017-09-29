package com.jx.blackmen.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class UtilsHelper {
	
//	public static long getLoginId(BeatContext beat){ //调用此方法请参考cookieutils.getUseridByCookie();
//		long opeid = 10003;
//		return opeid;
//	}

	public static long addInworkday(long begintimes,int days){
		long nd = 0;
		if(days > 0){
			for(int i = 1;i<=days;i++){
				long trdate = begintimes+i*24*60*60*1000;
				Date dd = new Date(trdate);
				String str = formatDateTostring("yyyyMMdd",trdate);
				if(dd.getDate() >= 1 && dd.getDate() <=5){
					
				}
				if(dd.getDate() == 6 || dd.getDate() == 6){
					i--;
					continue;
				}
				
//				for(int j = 0,c = Constants.jiaqi_arr.length;j<c;i++){
//					if(str.equals(Constants.jiaqi_arr[j])){
//						i--;
//						continue;
//					}
//				}
			}
		}
		return nd;
	}
	public static synchronized int uniqueCheckCode(){
		int r = 0;
		Random random = new Random();
		r = (random.nextInt(9)+1)*100000+random.nextInt(99999);
		return r;
	}
	public static synchronized int getUniqueSceneid(){
		int i = 0;
		Random random = new Random();
		Calendar time=Calendar.getInstance();
		time.set(Calendar.HOUR_OF_DAY, 0);
		time.set(Calendar.MINUTE, 0);
		time.set(Calendar.SECOND, 0);
		time.set(Calendar.MILLISECOND, 0);
		
		i = (int) (System.currentTimeMillis() - time.getTimeInMillis() + random.nextInt(10000));
		
		return i;
	}
	public static void main(String[] args){
		
		System.out.println(formatDateTostring("yyyy-MM-dd hh:mm:ss",new Date().getTime()));
	}
	public static String formatDateTostring(String str,long datetime){
		String rs = "";
		Date d = new Date(datetime);
		rs = str.replace("yyyy", (1900+d.getYear())+"").replace("dd", d.getDate()+"").
				replace("MM", (d.getMonth()+1)+"").replace("hh", d.getHours()+"").
				replace("mm", d.getMinutes()+"").replace("ss", d.getSeconds()+"");
		return rs;
	}
	public static String testkey (String signature,String timestamp,String nonce, String echostr){
		// 重写totring方法，得到三个参数的拼接字符串
				List<String> list = new ArrayList<String>(3) {
					private static final long serialVersionUID = 2621444383236420433L;

					public String toString() {
						return this.get(0) + this.get(1) + this.get(2);
					}
				};
				list.add(MContents.TOKEN);
				list.add(timestamp);
				list.add(nonce);
				Collections.sort(list);// 排序

				MessageDigest md = null;
				String tmpStr = null;
				try {
					md = MessageDigest.getInstance("SHA-1");
					byte[] digest = md.digest(list.toString().getBytes());
					tmpStr = byteToStr(digest);
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
				if (signature.equals(tmpStr)) {
					return echostr;
				} else {
					return "";
				}
	}
	private static String byteToStr(byte[] byteArray) {
		String strDigest = "";
		for (int i = 0; i < byteArray.length; i++) {
			strDigest += byteToHexStr(byteArray[i]);
		}
		return strDigest;
	}

	private static String byteToHexStr(byte mByte) {
		char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		char[] tempArr = new char[2];
		tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
		tempArr[1] = Digit[mByte & 0X0F];
		String s = new String(tempArr);
		return s;
	}
	
	/**
	 * 
	 * @param str [yyyy-MM-dd HH:mm:ss]
	 * @param date [long 类型时间]
	 * @return
	 */
	public static String formatLongDate(String str, long date){
		java.sql.Date d = new java.sql.Date(date);
		SimpleDateFormat sd = new SimpleDateFormat(str); 
		return sd.format(d);
	}
}
