/**
 * 
 */

package com.jx.blackmen.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.jx.argo.BeatContext;
import com.jx.blackface.tools.webblack.auth.AuthHelper;
import com.jx.blackface.tools.webblack.checkname.CheckNameHelper;
import com.jx.blackface.tools.webblack.checkname.entity.FoundCheckEntity;
import com.jx.blackface.tools.webblack.query.qichacha.QichachaHelper;
import com.jx.blackface.tools.webblack.query.qichacha.entity.QichachaEntity;
import com.jx.blackmen.frame.RSBLL;
import com.jx.service.enterprise.contract.ILvEnterpriseRoleRelationService;
import com.jx.service.enterprise.entity.LvEnterprisePersonEntity;
import com.jx.service.enterprise.entity.LvEnterpriseRoleDataEntity;
import com.jx.service.enterprise.entity.LvEnterpriseRoleRelationEntity;
import com.jx.service.enterprise.entity.LvGovAppointInfoEntity;
import com.jx.service.enterprise.entity.LvGovOrgInfoEntity;
import com.jx.service.messagecenter.util.DateUtils;



/**
 * simple introduction
 *
 * <p>detailed comment</p>
 * @author chuxuebao 2015年10月28日
 * @see
 * @since 1.0
 */

public class EnterpriseUtils {
	private static int APPOINT_TYPE_ONLINE = 2;
	private static int APPOINT_TYPE_TEL = 1;
	private static int APPOINT_TYPE_WX = 3;
	private static String APPOINT_FT_ORGCODE = "110106000";
	
	/**
	 * 获取登录信息
	 * @return
	 * @throws Exception
	 */
	public static Map<String, String> getLoginInfo(BeatContext beat) throws Exception{
		if(beat == null){
			return null;
		}
		String userId = beat.getRequest().getParameter("userid");
		Map<String, String> loginInfo = new HashMap<String, String>();
		loginInfo.put("userId", userId);
		return loginInfo;
	}
	
	/**
	 * 获取登录信息 TODO
	 * @return
	 * @throws Exception
	 */
	public static Map<String, String> getLoginInfo(HttpServletRequest request) throws Exception{
		String ipAddr = "";
		String empId = "";
		Map<String, String> loginInfo = new HashMap<String, String>();
		loginInfo.put("empId", empId);
		loginInfo.put("IP", ipAddr);
		return loginInfo;
	}
	
	
	/**
	 * 
	* @Title: foundCheck
	* @Description: TODO(微信-核名)
	* @param @param industryCharacteristics
	* @param @param mainBusinessUniteCode
	* @param @param mainBusinessCode
	* @param @param fullName
	* @param @param shopName
	* @param @return    设定文件
	* @return String    返回类型
	* @author: RENQI  
	* @date 2016年4月5日 上午10:27:36
	* @throws
	 */
	public static String foundCheck(String industryCharacteristics, String mainBusinessUniteCode, String mainBusinessCode, String fullName, String shopName){
		String sessionId = null;
		try {
 			sessionId = AuthHelper.postLogin();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String foundCheck = null;
		if(StringUtils.isNotBlank(sessionId)){
			FoundCheckEntity foundCheckEntity = new FoundCheckEntity();
			foundCheckEntity.setHangye(industryCharacteristics);
			foundCheckEntity.setHbdm(mainBusinessUniteCode);
			foundCheckEntity.setHydm(mainBusinessCode);
			foundCheckEntity.setQuanming(fullName);
			foundCheckEntity.setShijian(DateUtils.getFormatDateStr(DateUtils.getCurrentDate(), DateUtils.DATA_FORMAT_YYYY_MM_DD_HH_MM_SS) );
			foundCheckEntity.setZihao(shopName);
			foundCheckEntity.setZihaohangye(shopName + industryCharacteristics);
			foundCheckEntity.setSuperNameId("");
			try {
				foundCheck = CheckNameHelper.foundCheck(sessionId, foundCheckEntity);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		List<QichachaEntity> searchEnterprise = null;
		if(StringUtils.isBlank(foundCheck)){
			// 工商局又不能用了
			try {
				searchEnterprise = QichachaHelper.searchEnterprise(shopName, "北京");
			} catch (Exception e) {
				e.printStackTrace();
				foundCheck = "{\"errorNum\":1,\"jyz\":[],\"cc\":[{\"entName\":\"系统繁忙\",\"rule\":\"请稍后再试\"}]}";
			}
		}
		if(StringUtils.isBlank(foundCheck)){
			if(searchEnterprise != null && !searchEnterprise.isEmpty()){
				for(QichachaEntity qichachaEntity:searchEnterprise){
					if(!StringUtils.equals(qichachaEntity.getEntStatus(), "吊销")){
						foundCheck = "{\"errorNum\":1,\"jyz\":[],\"cc\":[{\"entName\":\"" + qichachaEntity.getName() + "\",\"rule\":\"全行业字号_查重列表(查重)\"}]}";
						break;
					}
				}
			}else{
				foundCheck = "{\"errorNum\":0,\"jyz\":[],\"cc\":[]}";
			}
		}
		if(StringUtils.isBlank(foundCheck)){
			foundCheck = "{\"errorNum\":1,\"jyz\":[],\"cc\":[{\"entName\":\"系统繁忙\",\"rule\":\"请稍后再试\"}]}";
		}
		return foundCheck;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Map<String, String>> getRoleListByEnterpriseAndRoleType(String enterpriseId, String roleType){
		List<Map<String, String>> reList = new ArrayList<Map<String, String>>();
		List<LvEnterpriseRoleRelationEntity> roleRelationList = new ArrayList<LvEnterpriseRoleRelationEntity>();
		try {
			roleRelationList = RSBLL.getstance().getEpEnterpriseRoleRelationService().getRoleRelationListByEnterpriseIdAndRoleType(Long.parseLong(enterpriseId), roleType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(roleRelationList == null || roleRelationList.isEmpty()){
			return reList;
		}
		for(LvEnterpriseRoleRelationEntity enterpriseRoleRelationEntity:roleRelationList){
			Map<String, String> roleDataMap = new HashMap<String, String>();
			if(StringUtils.equalsIgnoreCase(enterpriseRoleRelationEntity.getRoleType(), ILvEnterpriseRoleRelationService.ROLETYPE_LEGALPARTNER)){
				// 获取企业信息
				try {
					roleDataMap = RSBLL.getstance().getEpEnterpriseService().getAllValueByEnterpriseId(enterpriseRoleRelationEntity.getRoleId() + "");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				// 获取人员信息
				LvEnterprisePersonEntity enterprisePersonEntity = null;
				try {
					enterprisePersonEntity = RSBLL.getstance().getEpEnterprisePersonService().getEnterprisePersonById(enterpriseRoleRelationEntity.getRoleId());
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(enterprisePersonEntity != null){
					roleDataMap = JSONObject.parseObject(JSONObject.toJSONString(enterprisePersonEntity), Map.class);
				}
			}
			List<LvEnterpriseRoleDataEntity> roleDataList = null;
			try {
				roleDataList = RSBLL.getstance().getEpEnterpriseRoleRelationService().getRoleDataListByRoleRelationId(enterpriseRoleRelationEntity.getId());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(roleDataList != null && !roleDataList.isEmpty()){
				for(LvEnterpriseRoleDataEntity enterpriseRoleDataEntity:roleDataList){
					roleDataMap.put(enterpriseRoleDataEntity.getDataKey(), enterpriseRoleDataEntity.getDataValue());
				}
			}
			roleDataMap.put("isComplete", EnterpriseUtils.naturalRule(LvMapUtils.combine(roleDataMap))==null?"true":"false");
			roleDataMap.put("roleRelationId", enterpriseRoleRelationEntity.getId() + "");
			roleDataMap.put("roleType", enterpriseRoleRelationEntity.getRoleType());
			roleDataMap.put("isLegalPerson", EnterpriseUtils.isLegalPerson(enterpriseId, enterpriseRoleRelationEntity.getRoleId() + "", enterpriseRoleRelationEntity.getRoleType()) + "");
			
			reList.add(roleDataMap);
		}
		return reList;
	}
	
	
	/**
	 * 判断是否为企业法人
	 * @param enterpriseId 企业ID
	 * @param roleId 人员ID
	 * @return
	 */
	public static boolean isLegalPerson(String enterpriseId, String roleId, String roleType){
		if(StringUtils.isBlank(enterpriseId) || StringUtils.isBlank(roleId) || StringUtils.isBlank(roleType)){
			return false;
		}
		LvEnterpriseRoleRelationEntity enterpriseRoleRelationEntity = null;
		try {
			enterpriseRoleRelationEntity = RSBLL.getstance().getEpEnterpriseRoleRelationService().loadEnterpriseRoleRelationEntityByEnterpriseIdAndRoleIdAndRoleType(Long.parseLong(enterpriseId), Long.parseLong(roleId), ILvEnterpriseRoleRelationService.ROLETYPE_LEGALPERSON);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		if(enterpriseRoleRelationEntity != null){
			String roleDataValue = null;
			try {
				roleDataValue = RSBLL.getstance().getEpEnterpriseRoleRelationService().getRoleDataValueByRoleRelationIdAndDataKey(enterpriseRoleRelationEntity.getId(), "legalPersonMappingRoleType");
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(StringUtils.equals(roleDataValue, roleType)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 自然人股东规则
	 */
	private static Map<String, String> naturalRule = new HashMap<String, String>();
	static{
		naturalRule.put("idNum", "证件号码");
		naturalRule.put("sex", "性别");
		naturalRule.put("folk", "民族");
		naturalRule.put("residenceProv", "户籍登记地址");
		naturalRule.put("residenceCity", "户籍登记地址");
		naturalRule.put("residenceAddress", "户籍登记地址");
		naturalRule.put("capitalSize", "出资额");
	}
	public static String naturalRule(Map<String, String> naturalData){
		Set<String> keySet = naturalRule.keySet();
		for(String key:keySet){
			if(naturalData == null || naturalData.isEmpty()){
				return naturalRule.get(key);
			}
			String data = naturalData.get(key);
			if(StringUtils.isBlank(data)){
				return naturalRule.get(key) + "不能为空";
			}
		}
		return null;
	}
	/**
	 * 法人股东规则
	 */
	private static Map<String, String> logicalRule = new HashMap<String, String>();
	static{
		logicalRule.put("enterpriseType", "单位类型");
		logicalRule.put("businessLicenseType", "证件类型");
		logicalRule.put("businessLicenseNum", "证件号码");
		logicalRule.put("legalPerson", "法人代表");
		logicalRule.put("residenceProv", "住所");
		logicalRule.put("residenceCity", "住所");
		logicalRule.put("residenceAddress", "住所");
		logicalRule.put("capitalSize", "出资额");
	}
	public static String logicalRule(Map<String, String> logicalData){
		Set<String> keySet = logicalRule.keySet();
		for(String key:keySet){
			if(logicalData == null || logicalData.isEmpty()){
				return logicalRule.get(key);
			}
			String data = logicalData.get(key);
			if(StringUtils.isBlank(data)){
				return logicalRule.get(key) + "不能为空";
			}
		}
		return null;
	}
	
	
	
	/**
	 * 获取工商局信息
	 * @param orgCode
	 * @return
	 */
	public static LvGovOrgInfoEntity getGovOrgConfig(String orgCode){
		LvGovOrgInfoEntity govOrgEntity = null;
		try {
			govOrgEntity = RSBLL.getstance().getEpGovService().loadGovOrgEntityByOrgCode(orgCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return govOrgEntity;
	}
	
	
	/**
	 * 工商预约配置信息
	 * @param orgCode
	 * @return
	 */
	public static List<LvGovAppointInfoEntity> getGovAppointList(String orgCode){
		List<LvGovAppointInfoEntity> govAppointEntityList = null;
		 try {
			 govAppointEntityList = RSBLL.getstance().getEpGovService().getGovAppointEntityListByOrgCode(orgCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		 return govAppointEntityList;
	}
	
	
	/**
	 * 微信工商预约配置信息
	 * @param orgCode
	 * @return
	 */
	public static LvGovAppointInfoEntity getWxGovAppointList(String orgCode){
		System.out.println(orgCode);
		if(StringUtils.isBlank(orgCode)){
			return null;
		}
		List<LvGovAppointInfoEntity> govAppointEntityList = null;
		LvGovAppointInfoEntity govEntity = null;
		int appointType = 0;
		 try {
			 govAppointEntityList = RSBLL.getstance().getEpGovService().getGovAppointEntityListByOrgCode(orgCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
		if(govAppointEntityList != null && govAppointEntityList.size() > 0){
			for (LvGovAppointInfoEntity gov : govAppointEntityList) {
				if(APPOINT_FT_ORGCODE.equals(gov.getOrgCode())){//如果是丰台用户
					if(gov.getAppointType() == APPOINT_TYPE_TEL){//电话预约
						return gov;
					}
				}
				
				if(gov.getAppointType() == APPOINT_TYPE_ONLINE){//网上预约  优先级最高
					appointType = APPOINT_TYPE_ONLINE;
					govEntity = gov;
				}else if(gov.getAppointType() == APPOINT_TYPE_WX){//微信预约
					if(appointType != APPOINT_TYPE_ONLINE){//网上预约 优先级大于 微信预约
						appointType = APPOINT_TYPE_WX;
						govEntity = gov;
					}
				}else if(gov.getAppointType() == APPOINT_TYPE_TEL){//电话预约
					if(appointType != APPOINT_TYPE_ONLINE && appointType != APPOINT_TYPE_WX){//网上预约和微信预约优先级大于  电话预约
						appointType = APPOINT_TYPE_TEL;
						govEntity = gov;
					}
				}
			}
		}
		
		 return govEntity;
	}
	
}
