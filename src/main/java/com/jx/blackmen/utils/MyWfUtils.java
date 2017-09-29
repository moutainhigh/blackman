package com.jx.blackmen.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.jx.blackmen.frame.RSBLL;
import com.jx.service.enterprise.entity.LvEnterpriseEntity;
import com.jx.service.workflow.entity.LvProcInstEntitiy;

public class MyWfUtils {
	private static String VAR_WEB_USER_ID = "webUserId";
	
	private static String VAR_BUS_ID = "busid";
	
	private static String VAR_CITY = "city";
	
	private static String VAR_WEB_OPENID = "openId";
	
	private static String VAR_WEB_SOURCE_TYPE = "sourceType";
	
	private static String PROC_DEF_KEY_BJ_SELF_REG = "bj-all-self-help-company_reg";
	
	private static Integer VAR_CITY_BJ = 1; // 默认使用北京城市
	/**
	 * @param loginUser
	 */
	public static Map<String, String> openWorkFlow(Long userid, String openId, String sourceType,String entId,boolean isOpenMore) {
		Map<String, Object> variable = new HashMap<String, Object>();
		
		// 查询当前登录人
//    	variable.put("prodcateid", "3000");
		variable.put(VAR_WEB_USER_ID, userid);
		List<String> varKeyList = new ArrayList<String>();
		varKeyList.add(VAR_BUS_ID);
		List<LvProcInstEntitiy> pageHisProcList = null;
		try {
			pageHisProcList = RSBLL.getstance().getWfHistoryService().getPageHisProcListWithVarByVariableWithOutSubProc(variable, 0, 10, varKeyList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(pageHisProcList == null  || pageHisProcList.isEmpty() ){
			//发放优惠券duxf
			try {
				CouponUtils.getInstance().sendCoupons(userid);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("优惠券发送失败!");
			}
			return openTask(userid,openId,sourceType,entId);
		}else if(isOpenMore){
			return openTask(userid,openId,sourceType,entId);
		}
		return null;
	}
	
	
	
	public static Map<String, String> openTask(Long userid,String openId,String sourceType,String entId){
		Map<String, String> startTask = null;
		// 自动开通
		Map<String,Object> map = new HashMap<String,Object>();
		map.put(VAR_CITY, VAR_CITY_BJ); // 默认是北京
		map.put(VAR_WEB_USER_ID, userid);
		map.put(VAR_WEB_OPENID, openId);
		map.put(VAR_WEB_SOURCE_TYPE, sourceType);
		
		if(StringUtils.isBlank(entId)){// 企业ID为空，创建企业
			LvEnterpriseEntity enterpriseEntity = new LvEnterpriseEntity();
			enterpriseEntity.setCityId(VAR_CITY_BJ);
			Long enterpriseId = null;
			try {
				enterpriseId = RSBLL.getstance().getEpEnterpriseService().addEnterpriseEntity(enterpriseEntity, null) ;
			} catch (Exception e) {
				e.printStackTrace();
			}
			map.put(VAR_BUS_ID, enterpriseId);
		}else{
			map.put(VAR_BUS_ID, entId);//企业id存在，直接将企业与用户关联
		}
		try {
			startTask = RSBLL.getstance().getWfTaskService().startTask(PROC_DEF_KEY_BJ_SELF_REG, map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return startTask;
	}
	
	
		
}
