package com.jx.blackmen.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgencyTaskMappingVo {
	//任务节点  流程list
	private static List<String> showCompanyRegList;//前台展示公司注册流程
	private static List<String> showlocalTaxList;//前台展示地税报道流程
	private static List<String> shownationalTaxList;//前台展示国税报道流程
	//处理状态映射map
	private static Map<String,String> companyRegHandleStateMap;//公司注册  处理状态映射map
	private static Map<String,String> localTaxHandleStateMap;//地税  处理状态映射map
	private static Map<String,String> nationalTaxHandleStateMap;//国税  处理状态映射map
	//处理备注映射map
	private static Map<String,String> companyRegHandleNoteMap;//公司注册  处理备注映射map
	private static Map<String,String> localTaxHandleNoteMap;//地税  处理备注映射map
	private static Map<String,String> nationalTaxHandleNoteMap;//国税  处理备注映射map
	//流程环节map
	private static Map<String,String> companyRegHandleNodeMap;//公司注册  流程环节映射map
	private static Map<String,String> localTaxHandleNodeMap;//公司注册  流程环节映射map
	private static Map<String,String> nationalTaxHandleNodeMap;//公司注册  流程环节映射map
	
	
	//任务节点映射map  (有可能流程中存在排他网关，则其中另一个结束，对应的也应该结束,比如地税报到中的孵化器办理地税报到和外勤办理地税报到)
	private static Map<String,String> exclusiveTaskMapping ;//排他网关任务映射

	
	//初始化map
	static{
		showCompanyRegList = new ArrayList<String>();
		showlocalTaxList = new ArrayList<String>();
		shownationalTaxList = new ArrayList<String>();
		companyRegHandleStateMap = new HashMap<String, String>();
		localTaxHandleStateMap = new HashMap<String, String>();
		nationalTaxHandleStateMap = new HashMap<String, String>();
		companyRegHandleNoteMap = new HashMap<String, String>();
		localTaxHandleNoteMap = new HashMap<String, String>();
		nationalTaxHandleNoteMap = new HashMap<String, String>();
		companyRegHandleNodeMap = new HashMap<String, String>();
		localTaxHandleNodeMap = new HashMap<String, String>();
		nationalTaxHandleNodeMap = new HashMap<String, String>();
		exclusiveTaskMapping = new HashMap<String, String>();
		
		//将孵化器办理地税报到任务映射  为   外勤办理地税报到
		exclusiveTaskMapping.put("LocalTaxFuHuaQiReg", "LocalTaxFieldWorkerReg");
		
		
		
		/*showCompanyRegList.add("manualtask1");*/
		showCompanyRegList.add("CheckNameInfo");
		showCompanyRegList.add("SubmitInternetApply");
		showCompanyRegList.add("InternetApply");
		showCompanyRegList.add("GongShangReservation");
		showCompanyRegList.add("LocalApply");
		showCompanyRegList.add("ReceiveAllLicense");
		showCompanyRegList.add("CarvedChapter");
		
		showlocalTaxList.add("LocalTaxInfo");
		showlocalTaxList.add("LocalTaxFieldWorkerReg");
		
		shownationalTaxList.add("NationalTaxInfo");
		shownationalTaxList.add("NationalTaxFieldWorkerReg");
		
		//公司注册任务节点处理状态
		/*companyRegHandleStateMap.put("manualtask1", "已提交核名申请");*/
		companyRegHandleStateMap.put("CheckNameInfo", "已完成核名申请");
		companyRegHandleStateMap.put("SubmitInternetApply", "已提交设立申请");
		companyRegHandleStateMap.put("InternetApply", "已完成网登申请");
		companyRegHandleStateMap.put("GongShangReservation", "已完成约号申请");
		companyRegHandleStateMap.put("LocalApply", "已完成材料提交");
		companyRegHandleStateMap.put("ReceiveAllLicense", "已领取到证照");
		companyRegHandleStateMap.put("CarvedChapter", "已完成印章刻制");
		//地税任务节点处理状态
		localTaxHandleStateMap.put("LocalTaxInfo", "已完成信息采集");
		localTaxHandleStateMap.put("LocalTaxFieldWorkerReg", "地税报到完成");
		/*localTaxHandleStateMap.put("LocalTaxFuHuaQiReg", "地税报到完成");*/
		
		//国税任务节点处理状态
		nationalTaxHandleStateMap.put("NationalTaxInfo", "已完成信息采集");
		nationalTaxHandleStateMap.put("NationalTaxFieldWorkerReg", "国税报到完成");
		
		//公司注册任务处理备注
		/*companyRegHandleNoteMap.put("manualtask1", "下一步等待核名结果，预计1～3个工作日可知结果");*/
		companyRegHandleNoteMap.put("CheckNameInfo", "下一步提交网上设立登记，请及时给您的服务专员提供资料");
		companyRegHandleNoteMap.put("SubmitInternetApply", "下一步网上登记审核通过，预计需要3～5个工作日审核完毕");
		companyRegHandleNoteMap.put("InternetApply", "下一步工商约号交件，工商约号预计需要7个工作日");
		companyRegHandleNoteMap.put("GongShangReservation", "下一步准备好资料去工商局交件");
		companyRegHandleNoteMap.put("LocalApply", "下一步领取证照，材料审核完成就可以拿到执照了");
		companyRegHandleNoteMap.put("ReceiveAllLicense", "下一步进行五章的刻制预计需要1～2个工作日");
		companyRegHandleNoteMap.put("CarvedChapter", "您的公司注册业务已经完成，服务人员会尽快联系您领取执照");
		//地税任务处理备注
		localTaxHandleNoteMap.put("LocalTaxInfo", "下一步服务人员去地税局提交材料进行报税");
		localTaxHandleNoteMap.put("LocalTaxFieldWorkerReg", "地税报到已完成，服务人员会尽快联系您领取执照");
		/*localTaxHandleNoteMap.put("LocalTaxFuHuaQiReg", "地税报到已完成，服务人员会尽快联系您领取执照");*/
		
		//国税任务处理备注
		nationalTaxHandleNoteMap.put("NationalTaxInfo", "下一步服务人员去国税局提交材料进行报税");
		nationalTaxHandleNoteMap.put("NationalTaxFieldWorkerReg", "国税报到已完成，服务人员会尽快联系您领取执照");
		
		
		//公司注册任务流程环节
		/*companyRegHandleNodeMap.put("manualtask1", "提交核名");*/
		companyRegHandleNodeMap.put("CheckNameInfo", "核名结果");
		companyRegHandleNodeMap.put("SubmitInternetApply", "提交网登");
		companyRegHandleNodeMap.put("InternetApply", "网登结果");
		companyRegHandleNodeMap.put("GongShangReservation", "预约工商");
		companyRegHandleNodeMap.put("LocalApply", "工商交件");
		companyRegHandleNodeMap.put("ReceiveAllLicense", "领取证照");
		companyRegHandleNodeMap.put("CarvedChapter", "刻制印章");
		//地税任务流程环节
		localTaxHandleNodeMap.put("LocalTaxInfo", "信息采集");
		localTaxHandleNodeMap.put("LocalTaxFieldWorkerReg", "地税报到完成");
		/*localTaxHandleNodeMap.put("LocalTaxFuHuaQiReg", "地税报到完成");*/
		
		//国税任务流程环节
		nationalTaxHandleNodeMap.put("NationalTaxInfo", "信息采集");
		nationalTaxHandleNodeMap.put("NationalTaxFieldWorkerReg", "国税报到完成");
		
	}

	
	public static Map<String, String> getCompanyRegHandleNodeMap() {
		return companyRegHandleNodeMap;
	}

	public static void setCompanyRegHandleNodeMap(
			Map<String, String> companyRegHandleNodeMap) {
		AgencyTaskMappingVo.companyRegHandleNodeMap = companyRegHandleNodeMap;
	}

	public static Map<String, String> getLocalTaxHandleNodeMap() {
		return localTaxHandleNodeMap;
	}

	public static void setLocalTaxHandleNodeMap(
			Map<String, String> localTaxHandleNodeMap) {
		AgencyTaskMappingVo.localTaxHandleNodeMap = localTaxHandleNodeMap;
	}

	public static Map<String, String> getNationalTaxHandleNodeMap() {
		return nationalTaxHandleNodeMap;
	}

	public static void setNationalTaxHandleNodeMap(
			Map<String, String> nationalTaxHandleNodeMap) {
		AgencyTaskMappingVo.nationalTaxHandleNodeMap = nationalTaxHandleNodeMap;
	}

	public static List<String> getShowCompanyRegList() {
		return showCompanyRegList;
	}

	public static void setShowCompanyRegList(List<String> showCompanyRegList) {
		AgencyTaskMappingVo.showCompanyRegList = showCompanyRegList;
	}

	public static List<String> getShowlocalTaxList() {
		return showlocalTaxList;
	}

	public static void setShowlocalTaxList(List<String> showlocalTaxList) {
		AgencyTaskMappingVo.showlocalTaxList = showlocalTaxList;
	}

	public static List<String> getShownationalTaxList() {
		return shownationalTaxList;
	}

	public static void setShownationalTaxList(List<String> shownationalTaxList) {
		AgencyTaskMappingVo.shownationalTaxList = shownationalTaxList;
	}

	public static Map<String, String> getCompanyRegHandleStateMap() {
		return companyRegHandleStateMap;
	}

	public static void setCompanyRegHandleStateMap(
			Map<String, String> companyRegHandleStateMap) {
		AgencyTaskMappingVo.companyRegHandleStateMap = companyRegHandleStateMap;
	}

	public static Map<String, String> getLocalTaxHandleStateMap() {
		return localTaxHandleStateMap;
	}

	public static void setLocalTaxHandleStateMap(
			Map<String, String> localTaxHandleStateMap) {
		AgencyTaskMappingVo.localTaxHandleStateMap = localTaxHandleStateMap;
	}

	public static Map<String, String> getNationalTaxHandleStateMap() {
		return nationalTaxHandleStateMap;
	}

	public static void setNationalTaxHandleStateMap(
			Map<String, String> nationalTaxHandleStateMap) {
		AgencyTaskMappingVo.nationalTaxHandleStateMap = nationalTaxHandleStateMap;
	}

	public static Map<String, String> getCompanyRegHandleNoteMap() {
		return companyRegHandleNoteMap;
	}

	public static void setCompanyRegHandleNoteMap(
			Map<String, String> companyRegHandleNoteMap) {
		AgencyTaskMappingVo.companyRegHandleNoteMap = companyRegHandleNoteMap;
	}

	public static Map<String, String> getLocalTaxHandleNoteMap() {
		return localTaxHandleNoteMap;
	}

	public static void setLocalTaxHandleNoteMap(
			Map<String, String> localTaxHandleNoteMap) {
		AgencyTaskMappingVo.localTaxHandleNoteMap = localTaxHandleNoteMap;
	}

	public static Map<String, String> getNationalTaxHandleNoteMap() {
		return nationalTaxHandleNoteMap;
	}

	public static void setNationalTaxHandleNoteMap(
			Map<String, String> nationalTaxHandleNoteMap) {
		AgencyTaskMappingVo.nationalTaxHandleNoteMap = nationalTaxHandleNoteMap;
	}

	public static Map<String, String> getExclusiveTaskMapping() {
		return exclusiveTaskMapping;
	}

	public static void setExclusiveTaskMapping(
			Map<String, String> exclusiveTaskMapping) {
		AgencyTaskMappingVo.exclusiveTaskMapping = exclusiveTaskMapping;
	}
	
}
