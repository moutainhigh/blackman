package com.jx.blackmen.service;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.jx.argo.BeatContext;
import com.jx.blackface.tools.blackTrack.entity.WebLogs;
import com.jx.blackmen.frame.RSBLL;
import com.jx.blackmen.utils.TimeUtils;

/**
 * 我的登录
 * @author flower
 */
public class MyLoginService {
	public static MyLoginService myLoginService = new MyLoginService();
	
	public JSONObject commonLoginValid(BeatContext beat, JSONObject jr){
		
		WebLogs logs = WebLogs.getIntanse(MyLoginService.class, "commonLoginValid");
		try {
			//手机验证码登录
			String tokenstr = beat.getRequest().getParameter("token");
			String phonenumber = beat.getRequest().getParameter("userphone");
			String phonecode = beat.getRequest().getParameter("usercode");
			String valicode = beat.getRequest().getParameter("validatecode");
			
			logs.putParam("tokenstr", tokenstr);
			logs.putParam("phonenumber", phonenumber);
			logs.putParam("phonecode", phonecode);
			logs.putParam("valicode", valicode);
			logs.putParam("timestr", TimeUtils.nowTime());
			
			if(StringUtils.isBlank(tokenstr)){
				jr.put("msg", "生成的图形验证码有问题，请点击验证码重试！");
				return jr;
			}
			if(StringUtils.isBlank(phonenumber)){
				jr.put("msg", "电话号码不能为空！");
				return jr;
			}
			beat.getModel().add("phonenumber", phonenumber);
			if(valicode == null || "".equals(valicode)){
				jr.put("msg", "图形验证码错误！");
				return jr;
			}else{
				String sevali = (String) beat.getRequest().getSession().getAttribute("valicode"+tokenstr);
				if(!StringUtils.equalsIgnoreCase(sevali,valicode)){
					jr.put("msg", "图形验证码不正确!");
					return jr;
				}
			}
			
			if(StringUtils.isBlank(phonecode)){
				jr.put("msg", "手机验证码不能为空！");
				return jr;
			}else{
				try {
					boolean checkPhoneCodeFlag = RSBLL.getstance().getMoblieSmsService().checkVerifyCode(phonenumber, phonecode);
					if(!checkPhoneCodeFlag){
						jr.put("msg", "手机验证码错误！");
						return jr;
					}
				} catch (Exception e) {
					e.printStackTrace();
					jr.put("msg", "手机验证码错误！");
					return jr;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			logs.printErrorLog();
		}
		return jr;
	}

}
