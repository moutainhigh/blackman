package com.jx.blackmen.common;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.jx.argo.BeatContext;
import com.jx.blackface.gaea.usercenter.entity.BFLoginEntity;
import com.jx.blackface.tools.blackTrack.entity.WebLogs;
import com.jx.blackmen.frame.RSBLL;
import com.jx.blackmen.utils.CookieUtils;
import com.jx.blackmen.utils.EntityUtils;
import com.jx.blackmen.utils.TokenHandler;
import com.jx.blackmen.vo.LoginUserVO;
import com.jx.service.messagecenter.weixin.MyX509TrustManager;

public class CommonUtils {
	public static final String cookieName = CookieUtils.cookicName;
	
	public static String getIpAddr(BeatContext beat) {  
		 HttpServletRequest request = beat.getRequest();
       String ip = request.getHeader("X-Forwarded-For");  
       if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
           ip = request.getHeader("Proxy-Client-IP");  
       }  
       if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
           ip = request.getHeader("WL-Proxy-Client-IP");  
       }  
       if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
           ip = request.getHeader("HTTP_CLIENT_IP");  
       }  
       if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
           ip = request.getHeader("HTTP_X_FORWARDED_FOR");  
       }  
       if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
           ip = request.getRemoteAddr();  
       }  
       
       if(null != ip){
    	   String[] arry = ip.split(",");
    	   if(arry.length >1){
    		   ip = arry[0];
    	   }
       }
       return ip;  
   }  
	
	/***
	 * 通过cookie判断用户是否还存在
	 * @param beat
	 * @return
	 */
	public static BFLoginEntity getLoginEntity(BeatContext beat){
		BFLoginEntity loginEntity = null;
		if(CommonUtils.checkCookieName(cookieName, beat.getRequest())){
			String userid = CommonUtils.getUserIdFormCookie(cookieName, beat.getRequest());
			if(StringUtils.isNotBlank(userid)){
				try {
					loginEntity = RSBLL.getstance().getLoginService().getLoginEntityById(Long.valueOf(userid));
				} catch (Exception e) {
					System.out.println("获取用户失败userid:"+userid);
					e.printStackTrace();
				}
			}
		}
		return loginEntity;
	}
	
	/***
	 * 通过cookie判断用户是否还存在
	 * @param beat
	 * @return
	 */
	public static LoginUserVO getLoginUserVo(BeatContext beat){
		LoginUserVO loginVo = null;
		if(CommonUtils.checkCookieName(cookieName, beat.getRequest())){
			String userid = CommonUtils.getUserIdFormCookie(cookieName, beat.getRequest());
			if(StringUtils.isNotBlank(userid)){
				BFLoginEntity loginEntity = null;
				try {
					loginEntity = RSBLL.getstance().getLoginService().getLoginEntityById(Long.valueOf(userid));
				} catch (Exception e) {
					System.out.println("获取用户失败userid:"+userid);
					e.printStackTrace();
				}
				if(null != loginEntity){
					loginVo = EntityUtils.transferEntity(loginEntity, LoginUserVO.class);
				}
			}
		}
		return loginVo;
	}

	public static long getLoginuserid(BeatContext beat){
		long uid = 0l;
		/*****调用通用调取cookie包 Start*******/
		if(CommonUtils.checkCookieName(cookieName, beat.getRequest())){
			String userid = CommonUtils.getUserIdFormCookie(cookieName, beat.getRequest());
			if(StringUtils.isNotBlank(userid)){
				BFLoginEntity loginEntity = null;
				try {
					loginEntity = RSBLL.getstance().getLoginService().getLoginEntityById(Long.valueOf(userid));
				} catch (Exception e) {
					System.out.println("获取用户失败userid:"+userid);
					e.printStackTrace();
				}
				if(null != loginEntity){
					uid = loginEntity.getUserid();
				}
			}
		}
		/*****End*******/
		return uid;
	}
	
	/***
	 * 从cookie中获取用户id
	 * @param request
	 */
	public static String getUserIdFormCookie(String cookieName,HttpServletRequest request){
		String userid="";
		try {
			String cookieValues = CookieUtils.getCookieValueByName(cookieName, request);
			if(StringUtils.isNotBlank(cookieValues)){
				if(StringUtils.contains(cookieValues, ":")){
					String[] splitCookieValues = StringUtils.split(cookieValues, ":");
					if(splitCookieValues.length > 0 && StringUtils.isNotBlank(splitCookieValues[0])){
						userid = splitCookieValues[0];
					}
				}else{
					userid = cookieValues;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userid;
	}
	
	/***
	 * 检查此cookie是否存在
	 * @param cookieName
	 * @param request
	 * @return true 存在 false 不存在
	 */
	public static boolean checkCookieName(String cookieName,HttpServletRequest request){
		boolean checkFlag = false;
		try {
			checkFlag = CookieUtils.checkCookieName(cookieName, request);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return checkFlag;
	}
	
	public static boolean isChinese(String text){
		if(StringUtils.isBlank(text)){
			return false;
		}
		Pattern p_str = Pattern.compile("[\\u4e00-\\u9fa5]+");
		Matcher m = p_str.matcher(text);
		if (!m.find() || !m.group(0).equals(text)) {
			return false;
		}
		return true;
	}

	public static void geneCode(BeatContext beat) {
		// TODO Auto-generated method stub
		String token = TokenHandler.generateToken();
    	beat.getModel().add("token", token);
    	beat.getRequest().getSession().setAttribute("token", token);
	}
	public static String sendMessaeg(String requesturl,String message)throws Exception{
		WebLogs logs = WebLogs.getIntanse(CommonUtils.class,"sendMessaeg");
		String retmsg;
		logs.putParam("requesturl", requesturl);
		logs.putParam("message", message);
		StringBuffer buffer = new StringBuffer();
		//String requestUrl = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=OubBCWtLMktHejKc8fnYQJyA-COCuph91X_HLg6TdOBLkCMlaITXlFfSbnBHuCKFTiaJxwvVzVSuu9-yYCXfmg";
		TrustManager[] tm = { new MyX509TrustManager() };
		SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
		sslContext.init(null, tm, new java.security.SecureRandom());
		// 从上述SSLContext对象中得到SSLSocketFactory对象
		SSLSocketFactory ssf = sslContext.getSocketFactory();
		URL url = new URL(requesturl);
		HttpsURLConnection httpUrlConn = (HttpsURLConnection) url
				.openConnection();
		httpUrlConn.setSSLSocketFactory(ssf);
		httpUrlConn.setDoOutput(true);
		httpUrlConn.setDoInput(true);
		httpUrlConn.setUseCaches(false); // 设置请求方式（GET/POST）
		httpUrlConn.setRequestMethod("POST");
//		if ("GET".equalsIgnoreCase(requestMethod))
//			httpUrlConn.connect();
		// 当有数据需要提交时
	//	if (null != outputStr) {
			OutputStream outputStream = httpUrlConn.getOutputStream();
			// 注意编码格式，防止中文乱码
			outputStream.write(message.getBytes("UTF-8"));
			outputStream.close();
			// 将返回的输入流转换成字符串
						InputStream inputStream = httpUrlConn.getInputStream();
						InputStreamReader inputStreamReader = new InputStreamReader(
								inputStream, "utf-8");
						BufferedReader bufferedReader = new BufferedReader(
								inputStreamReader);
						String str = null;
						while ((str = bufferedReader.readLine()) != null) {
							buffer.append(str);
						}
						retmsg = buffer.toString();
						bufferedReader.close();
						inputStreamReader.close();
						// 释放资源
						inputStream.close();
						inputStream = null;
						httpUrlConn.disconnect();
		//}
		logs.putParam("result", retmsg);
		return retmsg;   
	}
	public static JSONObject checkImgCode(BeatContext beat){
		String tokenstr = beat.getRequest().getParameter("tokenstr");
		String validatecode = beat.getRequest().getParameter("valCode");
		JSONObject jr = new JSONObject();
		if(StringUtils.isBlank(tokenstr)){
			jr.put("ret", "fail");
			jr.put("msg", "图形验证码失败！请刷新后重试!");
			return jr;
		}
		if(StringUtils.isBlank(validatecode)){
			jr.put("ret", "fail");
			jr.put("msg", "图形验证码错误!");
			return jr;
		}
		String sevali = (String) beat.getRequest().getSession().getAttribute("valicode"+tokenstr);
		if(StringUtils.equalsIgnoreCase(sevali,validatecode)){
			jr.put("ret", "ok");
		}else{
			jr.put("ret", "fail");
			jr.put("msg", "图形验证码错误!");
		}
		return jr;
	}
}
