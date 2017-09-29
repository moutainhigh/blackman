package com.jx.blackmen.utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jx.blackmen.frame.RSBLL;
import com.jx.service.messagecenter.weixin.MyX509TrustManager;



/**
 * simple introduction
 *
 * <p>detailed comment</p>
 * @author chuxuebao 2015年4月14日
 * @see
 * @since 1.0
 */

public class WXUtils{
	
	/**
	 * 发送微信消息
	 * @param requesturl
	 * @param message
	 * @return
	 * @throws Exception
	 */
	public static String sendMessage(String requesturl, String message) throws Exception{
		URL url = new URL(requesturl);
		String retmsg = null;
		if(StringUtils.startsWith(requesturl, "https")){
			HttpsURLConnection httpUrlConn = (HttpsURLConnection) url.openConnection();
			TrustManager[] tm = { new MyX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			// 从上述SSLContext对象中得到SSLSocketFactory对象
			SSLSocketFactory ssf = sslContext.getSocketFactory();
			httpUrlConn.setSSLSocketFactory(ssf);
			httpUrlConn.setDoOutput(true);
			httpUrlConn.setDoInput(true);
			httpUrlConn.setUseCaches(false); // 设置请求方式（GET/POST）
			httpUrlConn.setRequestMethod("POST");
			OutputStream outputStream = httpUrlConn.getOutputStream();
			// 注意编码格式，防止中文乱码
			outputStream.write(message.getBytes("UTF-8"));
			outputStream.close();
			// 将返回的输入流转换成字符串
			InputStream inputStream = httpUrlConn.getInputStream();
			retmsg = IOUtils.toString(inputStream, "UTF-8");
			// 释放资源
			if(inputStream != null){
				inputStream.close();
				inputStream = null;
			}
			httpUrlConn.disconnect();
		}else if(StringUtils.startsWith(requesturl, "http")) {
			HttpURLConnection httpUrlConn =  (HttpURLConnection)url.openConnection();
			httpUrlConn.setDoOutput(true);
			httpUrlConn.setDoInput(true);
			httpUrlConn.setUseCaches(false); // 设置请求方式（GET/POST）
			httpUrlConn.setRequestMethod("POST");
			OutputStream outputStream = httpUrlConn.getOutputStream();
			// 注意编码格式，防止中文乱码
			outputStream.write(message.getBytes("UTF-8"));
			outputStream.close();
			// 将返回的输入流转换成字符串
			InputStream inputStream = httpUrlConn.getInputStream();
			retmsg = IOUtils.toString(inputStream, "UTF-8");
			// 释放资源
			if(inputStream != null){
				inputStream.close();
				inputStream = null;
			}
			httpUrlConn.disconnect();
		}
		return retmsg;
	}
	
	/**
	 * 添加微信分享配置
	 * @param beat
	 * @param url 带参数的完整url
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> addJsApiConfig(String url) throws Exception{
		if(StringUtils.isBlank(url))
			return Collections.emptyMap();
		String ticket = RSBLL.getstance().getWeixinService().getWeixinJSToken(MContents.weixin_app_id, MContents.weixin_app_secret_id);
		return Sign.tranceTokentojst(ticket, url.toString());
	}
	
	public static String getOpenId(HttpServletRequest request){
		String code = request.getParameter("code");
		if(StringUtils.isBlank(code)){
			// 获取code
    		return null;
		}
		String astokenurl = MContents.WX_url_oauth2.replace("CODE", code).replace("APP_SECRET", MContents.weixin_app_secret_id).replace("APP_ID", MContents.weixin_app_id);
		String astokenrs = null;
		try {
			astokenrs = WXUtils.sendMessage(astokenurl, "");
		} catch (Exception e) {			
			e.printStackTrace();
		}
		String openid = null;
		if(StringUtils.isNotBlank(astokenrs)){
			JSONObject jo = JSON.parseObject(astokenrs);
			if(!jo.containsKey("errcode")){
				//成功
				openid =  jo.getString("openid");
				System.out.println("success-openid----->"+openid);
			}
		}
		return openid;
	}
	
	public static String getBaseUrl(HttpServletRequest request){
		String redirectUrl = request.getRequestURL().toString();
		String queryString = request.getQueryString();
		if(StringUtils.isNotBlank(queryString)){
			queryString = queryString.replaceAll("\\&+code=\\S+\\&state=\\S+", "");
			queryString = queryString.replaceAll("code=\\S+\\&state=\\S+", "");
		}
		if(StringUtils.isNotBlank(queryString)){
			redirectUrl += "?" + queryString;
		}
		String url = null;
    	try {
    		url = MContents.WX_url_base_shouquan_page.replace("APP_ID", MContents.weixin_app_id).replace("REDIRECT_URI", URLEncoder.encode(redirectUrl, "utf-8"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
    	return url;
	}
	
	/***
	 * 根据openid获取微信的用户信息
	 * @param openId
	 * @return
	 */
	public static Map<String,Object> getWXUserBy(String openId){
		try {
			if(StringUtils.isBlank(openId)){
				return Collections.emptyMap();
			}
			String token = RSBLL.getstance().getWeixinService().getWeixinToken(MContents.weixin_app_id, MContents.weixin_app_secret_id);
			if(StringUtils.isNotBlank(token)){
				System.out.println("token>>" + token + "--openId>>"+openId);
				String wxuserurl = MContents.WX_url_getUser.replace("ACCESS_TOKEN", token).replace("OPENID", openId);
				
				String astokenrs = null;
				try {
					astokenrs = WXUtils.sendMessage(wxuserurl, "");
				} catch (Exception e) {			
					e.printStackTrace();
				}
				if(StringUtils.isNotBlank(astokenrs)){
					JSONObject jo = JSON.parseObject(astokenrs);
					if(!jo.containsKey("errcode")){
						Map<String,Object> resaultMap = new HashMap<String, Object>();
						if("1".equals(jo.getString("subscribe"))){  //关注用户
							resaultMap.put("subscribe", jo.getString("subscribe")); //是否关注
							resaultMap.put("openId", jo.getString("openid"));
							resaultMap.put("nickname", jo.getString("nickname"));  //用户昵称
							resaultMap.put("sex", jo.getString("sex"));  //用户性别	
						}else{  //未关注
							resaultMap.put("subscribe", jo.getString("subscribe"));
						}
						return resaultMap;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Collections.emptyMap();
	}
	
	
	
}
