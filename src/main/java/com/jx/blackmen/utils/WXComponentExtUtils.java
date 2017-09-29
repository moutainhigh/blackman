package com.jx.blackmen.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.jx.blackmen.frame.RSBLL;

public class WXComponentExtUtils {
	private static final String var_syn_status_1 = "1"; // 未同步
	private static final String var_syn_status_2 = "2"; // 同步失败，用户名或者密码错误
	private static final String var_syn_status_3 = "3"; // 同步失败，未找到对应的公司
	private static final String var_syn_status_10 = "10"; // 同步完成
	
	/**
	 *
	* @Title: appointNoResult
	* @Description: TODO(约号结果)
	* @param @param procInstId
	* @param @return    设定文件
	* @return Map<String,String>    返回类型
	* @author: RENQI  
	* @date 2016年5月10日 上午10:29:12
	* @throws
	 */
	public static Map<String,String> getAppointNoResult(String procInstId){
		System.out.println("约号----------------------------------procInstId:"+procInstId);
		Map<String,String> resultMap = new HashMap<String, String>();
		if(StringUtils.isBlank(procInstId)){
			return null;
		}
		//通过procInstId查询相关变量
		Map<String, Object> variables = null;
		try {
			variables = RSBLL.getstance().getWfHistoryService().getVariablesByProcessInstanceId(procInstId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String enterpriseSynStatus = "";
		if(variables != null){
			enterpriseSynStatus = variables.get("enterpriseSynStatus") == null ? "" : variables.get("enterpriseSynStatus").toString();
			resultMap.put("enterpriseSynStatus", enterpriseSynStatus);
		}
		
		if(StringUtils.isNotBlank(enterpriseSynStatus) 
				&& variables.get("enterpriseId") != null
				&& var_syn_status_10.equals(enterpriseSynStatus)){//同步企业信息成功
			//通过enterpriseId查询相关企业参数
			// 业务信息详情
			Map<String, String> businessDataMap = null;
			try {
				businessDataMap = RSBLL.getstance().getEpEnterpriseService().getExtValueByEnterpriseId(variables.get("enterpriseId")+"");
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(businessDataMap != null && !businessDataMap.isEmpty()){
				resultMap.putAll(businessDataMap);
			}
		}
		return resultMap;
	}
}
