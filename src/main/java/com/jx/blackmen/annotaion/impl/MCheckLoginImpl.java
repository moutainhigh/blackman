package com.jx.blackmen.annotaion.impl;

import org.apache.commons.lang.StringUtils;
import com.jx.argo.ActionResult;
import com.jx.argo.BeatContext;
import com.jx.argo.interceptor.PreInterceptor;
import com.jx.blackface.gaea.usercenter.utils.ActionResultUtils;
import com.jx.blackmen.utils.CookieUtils;

public class MCheckLoginImpl  implements PreInterceptor{
	@Override
	public ActionResult preExecute(BeatContext beat) {
	System.out.println("*********进入menter的地址:"+beat.getRequest().getRequestURL().toString());
		try {
			//如果存在此cookie则不跳转
			if(CookieUtils.checkCookieName(CookieUtils.cookicName, beat.getRequest())){
				return null;
			}
		System.out.println("*********不存在mlvuser的cookie");
			String path = beat.getRequest().getRequestURL().toString();
			//String path = "";
			String queryString = beat.getRequest().getQueryString();
			if(StringUtils.isNotBlank(queryString)){
				path += "?" + queryString;
			}
			System.out.println("***********请求的路径：" + path); 
			beat.getResponse().sendRedirect("http://m.lvzheng.com/m/login.html?flag=1&reach_url="+path);
			return ActionResultUtils.renderJson("");
		} catch (Exception e) {
			return ActionResultUtils.renderJson("");
		}
		
	}

}
