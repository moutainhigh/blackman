package com.jx.blackmen.utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import com.bj58.sfft.json.orgjson.JSONException;
import com.bj58.sfft.json.orgjson.JSONObject;
import com.jx.argo.BeatContext;

/**
 * @author bruce
 * @date 2016年7月29日
 * @email zhangyang226@gmail.com
 * @site http://blog.northpark.cn | http://northpark.cn | orginazation https://github.com/jellyband
 * 
 *  根据IP地址获取详细的地域信息
 */
public class AddressUtils {

	 public static String getIpAddr(BeatContext beat) {
         String ip = beat.getRequest().getHeader("X-Forwarded-For");
         if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
             ip = beat.getRequest().getHeader("Proxy-Client-IP");
         }
         if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
             ip = beat.getRequest().getHeader("WL-Proxy-Client-IP");
         }
         if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
             ip = beat.getRequest().getHeader("HTTP_CLIENT_IP");
         }
         if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
             ip = beat.getRequest().getHeader("HTTP_X_FORWARDED_FOR");
         }
         if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
             ip = beat.getRequest().getRemoteAddr();
         }
         return ip;
     }
	
	
	
	
	
//	// 测试
//		public static void main(String[] args) {
//			AddressUtils addressUtils = new AddressUtils();
//			// 测试ip 219.136.134.157 中国=华南=广东省=广州市=越秀区=电信
//			String ip = "124.42.107.154";
//			getAddressByIP();
//			// 输出结果为：广东省,广州市,越秀区
//		}
		
		
	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}
	public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
		} finally {
			is.close();
			// System.out.println("同时 从这里也能看出 即便return了，仍然会执行finally的！");
		}
	}
	
	
	//这里调用百度的ip定位api服务 详见 http://api.map.baidu.com/lbsapi/cloud/ip-location-api.htm
	public static String getAddressByIP(String ip){ 
		String shengfen ="";
		try{
			JSONObject json = readJsonFromUrl("http://api.map.baidu.com/location/ip?ak=F454f8a5efe5e577997931cc01de3974&ip="+ip);
			//System.out.println(json.toString());
			String all = json.toString();
			shengfen  = (String) ((JSONObject) json.get("content")).get("address");
			System.out.println(shengfen);


		}
		catch(Exception e)
		{ 
			return "读取失败"; 
		}
		return shengfen;
	}
	
	
		  public static void main(String[] args) throws IOException, JSONException {
		   //这里调用百度的ip定位api服务 详见 http://api.map.baidu.com/lbsapi/cloud/ip-location-api.htm
		    getAddressByIP("124.42.107.154");
		  }
}  
