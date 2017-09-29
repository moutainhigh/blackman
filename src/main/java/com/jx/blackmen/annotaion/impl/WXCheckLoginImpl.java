package com.jx.blackmen.annotaion.impl;

import org.apache.commons.lang.StringUtils;

import com.jx.argo.ActionResult;
import com.jx.argo.BeatContext;
import com.jx.argo.interceptor.PreInterceptor;
import com.jx.blackmen.buz.WeiXinBuz;
import com.jx.blackmen.utils.ActionResultUtils;
import com.jx.blackmen.utils.WXUtils;

public class WXCheckLoginImpl  implements PreInterceptor{
	@Override
	public ActionResult preExecute(BeatContext beat) {
//		String openId = "";
//		try {
//			openId =CookieUtils.getCookieValueByName(CookieUtils.userOpenId, beat.getRequest());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		String openId = beat.getRequest().getParameter("openId");
		
		//根据##[ appid和secret获取code，]##code获取accesstoken，accesstoken获取openid
		if(StringUtils.isBlank(openId)){
			openId = WXUtils.getOpenId(beat.getRequest());
		}
		//空openid跳回微信原地址
		if(StringUtils.isBlank(openId)){
			try {
				beat.getResponse().sendRedirect(WXUtils.getBaseUrl(beat.getRequest()));
				return ActionResultUtils.renderJson("");
			} catch (Exception e) {
				return ActionResultUtils.renderJson("");
			}
		}
		
		
		String path = beat.getRequest().getRequestURL().toString();
		String queryString = beat.getRequest().getQueryString();
		if(StringUtils.isNotBlank(queryString)){
			path += "?" + queryString;
		}
		
		path = path.replaceAll("[?]", "WENHAO").replaceAll("[&]", "AND");
		System.out.println("***********抵达的地址" + path); 
		
		if(StringUtils.isNotBlank(openId)){
			try {
				beat.getModel().add("openId", openId);
				if(null != WeiXinBuz.wb.getBFLoginEByopenId(openId)){
					System.out.println("get-login-by-openid-success----->"+openId);
					return null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
//			
			try {
				beat.getResponse().sendRedirect("http://m.lvzheng.com/wx/login.html?openId="+openId+"&reach_url="+path);
				return ActionResultUtils.renderJson("");
			} catch (Exception e) {
				e.printStackTrace();
				return ActionResultUtils.renderJson("");
			}
		}
		System.out.println("***********未保存OPENID的cookie跳转到登录页面");
		try {
			beat.getResponse().sendRedirect("http://m.lvzheng.com/wx/login.html?reach_url="+path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ActionResultUtils.renderJson("");
	}

}
