package com.jx.blackmen.buz;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.jx.argo.BeatContext;
import com.jx.blackface.gaea.usercenter.contract.ILoginService;
import com.jx.blackface.gaea.usercenter.entity.BFLoginEntity;
import com.jx.blackmen.frame.RSBLL;
import com.jx.service.messagecenter.contract.IWeixinActionService;

/***
 * 微信的工具类
 * @author duxiaofei
 * @date   2016年4月11日
 */
public class WeiXinBuz {

	public static WeiXinBuz wb = new WeiXinBuz();
	private static IWeixinActionService wxservice = RSBLL.getstance().getWeixinActionService();
	private static ILoginService us = RSBLL.getstance().getLoginService();
	
	//declared by bruce
	/**
	 * oxmap是存放特殊openid 的map
	 */
	private static Map<Long,String> oxmap  =  new HashMap<Long, String>();
	/**
	 * oxmap是存放特殊openid 的set
	 * 
	 */
	private static Set<String> openidset = new HashSet<String>();
	
	static{
		try {
			//初始化存入静态组件
			
			List<BFLoginEntity> oxlist = RSBLL.getstance().getLoginService().getLoginEntity(" openid like '0x' ", 1, 99, "");
			
			if(CollectionUtils.isNotEmpty(oxlist)){
				for (int i = 0; i < oxlist.size(); i++) {
					Long userid = oxlist.get(i).getUserid();
					String openid = oxlist.get(i).getOpenid();
					oxmap.put(userid, openid);
					openidset.add(openid);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
	}
	/**
	 * 获取openId 只适用于微信菜单的controller
	 * @param beat
	 * @return
	 */
	public String getopenId(BeatContext beat){
		return beat.getRequest().getParameter("openId");
	}
	
	@Deprecated
	public BFLoginEntity getUserLoginEntity(BeatContext bt)throws Exception{
		BFLoginEntity loginE = null;
		String code = bt.getRequest().getParameter("code");
		String userid = bt.getRequest().getParameter("userid");
		String openId = bt.getRequest().getParameter("openId");
		System.out.println("code is "+code+" userid is "+userid+" openId is "+openId);
		
		if(StringUtils.isNotBlank(userid) && Long.parseLong(userid) > 0){
			loginE = us.getLoginEntityById(Long.parseLong(userid));
		}else if(StringUtils.isNotBlank(openId)){
			loginE = getBFLoginEByopenId(openId);
		}else  if(StringUtils.isNotBlank(code)){
			openId = getopenIdByauth(code);
			if(StringUtils.isNotBlank(openId)){
				loginE = getBFLoginEByopenId(openId);
			}
		}
		return loginE;
	}
	
	/**
	 * 根据微信的code获取用户openid
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public String getopenIdByauth(String code)throws Exception{
		System.out.println("code is "+code);
		String openId = wxservice.getOpenidBycode(code);
		return openId;
	}

	/**
	 * 根据openid获取用户数据
	 * @param openId
	 * @return
	 * @throws Exception
	 */
	public BFLoginEntity getBFLoginEByopenId(String openId)throws Exception{
		BFLoginEntity loginE = null;
		
		
		//获取包含16进制字符的用户特殊处理
		if(openidset.contains(openId)){
			
			Iterator<Entry<Long, String>> iterator = oxmap.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<Long, String> entry = iterator.next();
				if(entry.getValue().equals(openId)||entry.getValue()==(openId)){
					
					System.out.println(entry.getKey() + "---->" + entry.getValue().toString());
					long uid  = entry.getKey();
					loginE = us.getLoginEntityById(uid);
					break;
				}
			}
		}else if("oxizHs5kzkDG3Jmwu_nc_0xjX6V8".equals(openId)){//CHULI DAMA FAN XING ZHE WU JIANG
			long uid  = 39982689008641L;
			loginE = us.getLoginEntityById(uid);
		}else{
			String condition = "openid='"+openId+"'";
			List<BFLoginEntity> list = us.getLoginEntity(condition, 1, 1, null);
			System.out.println("WeixinBuz  getBFLoginEByopenId list:  "+(null != list && list.size() > 0 ? list.size() : 0));
			long uid = 0;
			if(!CollectionUtils.isEmpty(list)){
				loginE = list.get(0);
				uid = loginE.getUserid();
			}
			System.out.println("WeixinBuz  getBFLoginEByopenId result:  "+uid);
		}
		
		return loginE;
	}
	
	/**
	 * 根据手机号获取用户数据
	 * @param openId
	 * @return
	 * @throws Exception
	 */
	public BFLoginEntity getBFLoginEByPhoneNum(String phoneNum)throws Exception{
		BFLoginEntity loginE = null;
		String condition = "userphone='"+phoneNum+"'";
		List<BFLoginEntity> list = us.getLoginEntity(condition, 1, 1, null);
		if(null != list && list.size() > 0){
			loginE = list.get(0);
		}
//		else{
//			loginE = new BFLoginEntity();
//			loginE.setopenId(openId);
//		}
		return loginE;
	}
}
