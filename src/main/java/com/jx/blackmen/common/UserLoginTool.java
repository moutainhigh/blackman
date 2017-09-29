package com.jx.blackmen.common;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.jx.blackface.gaea.usercenter.entity.BFLoginEntity;
import com.jx.blackmen.frame.RSBLL;
import com.jx.service.newcore.entity.LoginEntity;

/***
 * 用户登录工具类
 * @author duxiaofei
 * @date   2016年3月7日
 */
public class UserLoginTool {
	
	/***
	 * 通过用户的openid获取到用户信息，以此可判断用户是否登录过
	 * @param openid
	 * @return
	 */
	public static BFLoginEntity getLoginUser(String openid){
		if(StringUtils.isBlank(openid)){
			return null;
		}
		try {
			List<BFLoginEntity> loginEList = RSBLL.getstance().getLoginService().getLoginEntity(" openid='"+openid+"'", 1, 1, null);
			if(null != loginEList && !loginEList.isEmpty()){
				return loginEList.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("微信:UserLoginTool类getLoginUser方法通过openid+"+openid+"获取用户信息失败!");
		}
		return null;
	}
	
	/***
	 * 添加用户公共方法返回userid 如果用户存在返回存在的userid 不存在返回新添加的userid
	 * @param userPhone
	 * @param authenflag  认证标记，如果是发送过验证码的传true否则传false
	 * @return
	 */
	public static Long addLoginUser(String userPhone,boolean authenflag){
		if(StringUtils.isBlank(userPhone)){
			return 0l;
		}
		try {
			List<BFLoginEntity> loginEList = RSBLL.getstance().getLoginService().getLoginEntity("userphone='"+userPhone+"'", 1, 1, null);
			if(null != loginEList && !loginEList.isEmpty()){
				return loginEList.get(0).getUserid();
			}
			BFLoginEntity loginE = new BFLoginEntity();
			loginE.setUserphone(userPhone);
			loginE.setAddtime(new Date().getTime());  //注册时间
			loginE.setChannel(1); //用户来源微信
			if(authenflag) loginE.setAuthenflag(1); //认证标记
			return RSBLL.getstance().getLoginService().addLoginEntity(loginE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0l;
	}
	
	public static Long addLoginUser(String userPhone,String opendId, boolean authenflag){
		if(StringUtils.isBlank(userPhone)){
			return 0l;
		}
		try {
			List<BFLoginEntity> loginEList = RSBLL.getstance().getLoginService().getLoginEntity("userphone='"+userPhone+"'", 1, 1, null);
			if(null != loginEList && !loginEList.isEmpty()){
				BFLoginEntity loginEntity = loginEList.get(0);
				loginEntity.setOpenid(opendId);
				if(authenflag) loginEntity.setAuthenflag(1); //认证标记
				RSBLL.getstance().getLoginService().updateLoginEntity(loginEntity);
				return loginEntity.getUserid();
			}
			BFLoginEntity loginE = new BFLoginEntity();
			loginE.setUserphone(userPhone);
			loginE.setAddtime(new Date().getTime());  //注册时间
			loginE.setChannel(1); //用户来源微信
			loginE.setOpenid(opendId);
			if(authenflag) loginE.setAuthenflag(1); //认证标记
			return RSBLL.getstance().getLoginService().addLoginEntity(loginE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0l;
	}
}
