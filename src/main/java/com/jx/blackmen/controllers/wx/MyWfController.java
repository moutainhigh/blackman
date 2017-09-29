package com.jx.blackmen.controllers.wx;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jx.argo.ActionResult;
import com.jx.argo.BeatContext;
import com.jx.argo.annotations.Path;
import com.jx.blackface.gaea.usercenter.entity.BFLoginEntity;
import com.jx.blackface.gaea.usercenter.utils.ActionResultUtils;
import com.jx.blackmen.buz.WeiXinBuz;
import com.jx.blackmen.common.PhoneCodeMap;
import com.jx.blackmen.common.UserLoginTool;
import com.jx.blackmen.common.UtilTool;
import com.jx.blackmen.common.tracklog.TrackLogUtil;
import com.jx.blackmen.controllers.BaseController;
import com.jx.blackmen.frame.RSBLL;
import com.jx.blackmen.utils.CommonUtils;
import com.jx.blackmen.utils.CouponUtils;
import com.jx.blackmen.utils.DicUtils;
import com.jx.blackmen.utils.EnterpriseUtils;
import com.jx.blackmen.utils.MContents;
import com.jx.blackmen.utils.MyWfUtils;
import com.jx.blackmen.utils.Sign;
import com.jx.blackmen.utils.StepUtils;
import com.jx.blackmen.utils.WAQUtils;
import com.jx.blackmen.utils.WXUtils;
import com.jx.blackmen.vo.CompanyRegTasksVo;
import com.jx.service.enterprise.contract.ILvEnterpriseRoleRelationService;
import com.jx.service.enterprise.entity.LvEnterpriseBusinessEntity;
import com.jx.service.enterprise.entity.LvEnterpriseEntity;
import com.jx.service.enterprise.entity.LvEnterpriseMainBusinessEntity;
import com.jx.service.messagecenter.common.JavaDemoHttp;
import com.jx.service.workflow.entity.LvHiActEntity;
import com.jx.service.workflow.entity.LvProcInstEntitiy;

/**
 * 
* @ClassName: MyWfController
* @Description: TODO(微信-公司注册)
* @author: RENQI  
* @date 2016年3月31日 上午11:16:28
*
 */
@Path("weixin/mywf")
public class MyWfController extends BaseController{
	
	public static final String DOMAIN = ".lvzheng.com";
	
	private static final String VAR_CITY = "city";
	
	private static final String VAR_WEB_USER_ID = "webUserId";
	
	private static final String VAR_BUS_ID = "busid";
	
	private static final String VAR_WEB_OPENID = "openId";
	
	private static final String VAR_WEB_SOURCE_TYPE = "sourceType";
	
	
	private static final String VAR_LOCK_BUS_KEY = "lockBusinessKey";
	
	private static final String PROC_DEF_KEY_BJ_SELF_REG = "bj-all-self-help-company_reg";
	
	private static final String PROC_DEF_KEY_BJ_SELF_APPOINT ="bj-all-self-help-company_appoint";
	
	private static final Integer VAR_CITY_BJ = 1; // 默认使用北京城市
	
	private static final String var_web_userId = "webUserId";
	
	private static final String BUSINESS_TYPE_THREE = "3";
	
	private static Map<String, String> businessKeyMap = new HashMap();
	private static Map<String, StepUtils> stepMap = new HashMap<String, StepUtils>();//环节对应的步骤集合
	
	static{
		businessKeyMap.put("selfInternetInfo", "internetNav");
		businessKeyMap.put("perfectInvInfo", "internetNav");
		businessKeyMap.put("perfectAddress", "internetNav");
		businessKeyMap.put("perfectOtherInfo", "internetNav");
		businessKeyMap.put("previewCompanyInfo", "internetNav");
		//businessKeyMap.put("selfReceiveLicense", "act-ts");
		//businessKeyMap.put("receiveLicense", "act-ts");
		//businessKeyMap.put("selfCarvedChapter", "act-ts");
		businessKeyMap.put("bankAccount", "act-ts");
		businessKeyMap.put("nationalTaxReg", "act-ts");
		businessKeyMap.put("localTaxReg", "act-ts");
		businessKeyMap.put("changeCollectMainInfo", "act-ts");
		businessKeyMap.put("reviewAndSubmit", "act-ts");
		businessKeyMap.put("show", "act-ts");
		
		//初始化各个环节对应的步骤
		initStepMap();
		
	}
	
	
	private static void initStepMap(){
		StepUtils su = null;
		Map<String, String> map = null;
		
		map = new HashMap<String, String>();
		map.put("selfCheckName", "selfCheckName");
		map.put("checkNameCollect", "checkNameCollect");
		map.put("checkNameReason", "checkNameReason");
		su = new StepUtils();
		su.setStepMap(map);
		stepMap.put("selfCheckName", su);
		
		map = new HashMap<String, String>();
		map.put("selfInternetInfo", "selfInternetInfo");
		map.put("perfectInvInfo", "perfectInvInfo");
		map.put("perfectAddress", "perfectAddress");
		map.put("perfectOtherInfo", "perfectOtherInfo");
		map.put("previewCompanyInfo", "previewCompanyInfo");
		map.put("checkReason", "checkReason");
		map.put("setupDownload", "setupDownload");
		su = new StepUtils();
		su.setStepMap(map);
		stepMap.put("selfInternetInfo", su);
		
		map = new HashMap<String, String>();
		map.put("selfReceiveLicense", "selfReceiveLicense");
		map.put("receiveLicense", "receiveLicense");
		su = new StepUtils();
		su.setStepMap(map);
		stepMap.put("selfReceiveLicense", su);
		
		map = new HashMap<String, String>();
		map.put("selfCarvedChapter", "selfCarvedChapter");
		su = new StepUtils();
		su.setStepMap(map);
		stepMap.put("selfCarvedChapter", su);
		
		map = new HashMap<String, String>();
		map.put("bankAccount", "bankAccount");
		su = new StepUtils();
		su.setStepMap(map);
		stepMap.put("bankAccount", su);
		
		map = new HashMap<String, String>();
		map.put("nationalTaxReg", "nationalTaxReg");
		su = new StepUtils();
		su.setStepMap(map);
		stepMap.put("nationalTaxReg", su);
		
		map = new HashMap<String, String>();
		map.put("localTaxReg", "localTaxReg");
		su = new StepUtils();
		su.setStepMap(map);
		stepMap.put("localTaxReg", su);
		
		map = new HashMap<String, String>();
		map.put("changeCollectMainInfo", "changeCollectMainInfo");
		su = new StepUtils();
		su.setStepMap(map);
		stepMap.put("changeCollectMainInfo", su);
		
		map = new HashMap<String, String>();
		map.put("reviewAndSubmit", "reviewAndSubmit");
		su = new StepUtils();
		su.setStepMap(map);
		stepMap.put("reviewAndSubmit", su);
		
		map = new HashMap<String, String>();
		map.put("show", "show");
		su = new StepUtils();
		su.setStepMap(map);
		stepMap.put("show", su);
	}
	
	
	
	
	
		
	@Path("/index")
	public ActionResult checkNamePage(){
		/**
		 * 最近的公司注册任务
		 */
		ActionResult buildWXInfo = buildWXInfo();
		if(buildWXInfo != null){
			return buildWXInfo;
		}
		//model().add("openId", "dqx_2410508062");
		String openId = model().get("openId") == null ? "" : model().get("openId").toString();

		/**
		 * 默认行业特点
		 */
		//model().add("openId", "dqx_2410508062");
		
		int size = 99;
		List<LvEnterpriseMainBusinessEntity> mainBusinessList = null;
		try {
			mainBusinessList = RSBLL.getstance().getEpEnterpriseMainBusinessService().getDefaultMainBusinessList(size);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(mainBusinessList != null){
			model().add("mainBusinessList", mainBusinessList);
		}
		
		//String openidreq = request().getParameter("openId");
		//String openId = "oxizHs97Dv0Kb7L29D782LkdMzE4";
		//model().add("openId", openId);
		
		//查询当前登录用户的最后一个公司注册任务
		/*BFLoginEntity loginEntity = null;
		try {
			loginEntity = WeiXinBuz.wb.getBFLoginEByopenId(openId);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if(loginEntity != null){
			List<LvHiActEntity> ActEntitys = null;
			try {
				ActEntitys = RSBLL.getstance().getWfReceiveTaskService().getListReceiveTaskByProcKeyActKeyAndVariable(PROC_DEF_KEY_BJ_SELF_REG, "selfCheckName", var_web_userId, loginEntity.getUserid());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(ActEntitys != null && ActEntitys.size() > 0){
				List<CompanyRegTasksVo> tasks = getTaskListData(ActEntitys);
				if(tasks != null && tasks.size() > 0){
					model().add("lateTask", tasks.get(tasks.size() - 1));//获取最后一个
					model().add("lateTaskProcInstId", tasks.get(tasks.size() - 1).getProcInstId());
					model().add("lateTaskId", tasks.get(tasks.size() - 1).getTaskId());
				}
			}
		}*/
		
		//入口埋点
		String from = request().getParameter("from");
    	if(StringUtils.isNotBlank(from)){
    		model().add("from", from);
    	}
		Map<String,String> dataMap = new HashMap<String,String>();
		dataMap.put("action", "redirect");
		dataMap.put("from", from);
		dataMap.put("openId", openId);
		TrackLogUtil.addTrackLog("blackmantrace", request(), response(), "checkName_into", dataMap);
		
		CommonUtils.geneCode(beat());//验证码
		return view("/wx/mywf/"+PROC_DEF_KEY_BJ_SELF_REG+"/selfCheckName/checkName");//微信-核名页面
	}
	
	
	private ActionResult buildWXInfo(){
		String ua = request().getHeader("USER-AGENT");
        if(StringUtils.indexOfIgnoreCase(ua, "micromessenger") >= 0){
        	String openid = request().getParameter("openId");
        	if(StringUtils.isBlank(openid)){
        		openid = WXUtils.getOpenId(request());
        	}
        	if(StringUtils.isBlank(openid)){
        		return redirect(WXUtils.getBaseUrl(request()));
        	}
        	model().add("openId", openid);
        	StringBuffer url = beat().getRequest().getRequestURL();
    		String queryString = beat().getRequest().getQueryString();
    		if(!StringUtils.isEmpty(queryString)){
    			url.append("?" + queryString);
    		}
        	String ticket = null;
    		try {
    			ticket = RSBLL.getstance().getWeixinService().getWeixinJSToken(MContents.weixin_app_id, MContents.weixin_app_secret_id);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		System.out.println("get js token ticke is "+ticket);
    		try {
    			Map<String,Object> map = Sign.tranceTokentojst(ticket, url.toString());
    			map.put("appid", MContents.weixin_app_id);
    			model().addAll(map);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
        }
		//model().add("openId", "oxizHs97Dv0Kb7L29D782LkdMzE4");
        return null;
	}
	
	@Path("/404")
	public ActionResult asf(){
		return view("404");
	}
	
	@Path("/checkName")
	public ActionResult checkNameLoading(){
		ActionResult buildWXInfo = buildWXInfo();
		if(buildWXInfo != null){
			return buildWXInfo;
		}
		String openId = model().get("openId") == null ? "" : model().get("openId").toString();
		//String openidreq = request().getParameter("openId");
//		String openId = "oxizHs97Dv0Kb7L29D782LkdMzE4";
//		model().add("openId", openId);
		
		String queryString = request().getQueryString();
		String redirectUrl = "http://m.lvzheng.com/weixin/mywf/checkNameResult?" + queryString + "&openId="+openId;
		model().add("url", redirectUrl);
		return view("/wx/mywf/"+PROC_DEF_KEY_BJ_SELF_REG+"/loading");
	}
	
	/**
	 * 
	* @Title: checkNameIsUsable
	* @Description: TODO(用于检测公司名字是否可用)
	* @param @return    设定文件
	* @return ActionResult    返回类型
	* @author: RENQI  
	* @date 2016年4月5日 上午9:17:40
	* @throws
	 */
	@Path("/checkNameResult")
	public ActionResult checkName(){
		ActionResult buildWXInfo = buildWXInfo();
		if(buildWXInfo != null){
			return buildWXInfo;
		}
		//String openidreq = request().getParameter("openId");
		//String openId = "oxizHs97Dv0Kb7L29D782LkdMzE4";
		//model().add("openId", openId);
		
		//model().add("openId", "dqx_2410508062");
		String from = request().getParameter("from");
    	if(StringUtils.isNotBlank(from)){
    		model().add("from", from);
    	}
		String industryCharacteristics = WAQUtils.HTMLEncode(request().getParameter("industryCharacteristics"));//行业特点
		String mainBusinessUniteCode = WAQUtils.HTMLEncode(request().getParameter("hbdm"));//主营业务
		String mainBusinessCode = WAQUtils.HTMLEncode(request().getParameter("hydm"));//主营业务
		String shopName = WAQUtils.HTMLEncode(request().getParameter("zihao"));//字号
		String localCity = WAQUtils.HTMLEncode(request().getParameter("localCity"));//城市
		String organizationType = "有限公司";//组织形式 TODO
		
		String fullName = localCity + shopName + industryCharacteristics + organizationType; //城市 
				
		String foundCheck = EnterpriseUtils.foundCheck(industryCharacteristics, mainBusinessUniteCode, mainBusinessCode, fullName, shopName);
		
		if(StringUtils.isNotBlank(foundCheck)){
			model().add("fullName", fullName);
			model().add("mainBusinessCode",mainBusinessCode);
			model().add("zihao",shopName);
			model().add("industryCharacteristics",industryCharacteristics);
			
			JSONObject reason = JSONObject.parseObject(foundCheck);
			
			JSONArray cc = reason.getJSONArray("cc");
			JSONArray jyz = reason.getJSONArray("jyz");
			
			if(reason.getInteger("errorNum") == 0){//核名通过
				return view("/wx/mywf/"+PROC_DEF_KEY_BJ_SELF_REG+"/selfCheckName/checkNameSucc");
			}else if(cc.size() > 0){//未通过
				model().add("cc", cc);
				return view("/wx/mywf/"+PROC_DEF_KEY_BJ_SELF_REG+"/selfCheckName/checkNameFail");
			}else if(jyz.size() > 0){//未通过
				model().add("jyz", jyz);
				return view("/wx/mywf/"+PROC_DEF_KEY_BJ_SELF_REG+"/selfCheckName/checkNameFail");
			}
		}
		
		return view("404");
	}
	
	
	@Path("/company/detail/{procInstId:[\\w-]+}/{taskId:[\\w-]+}")
	public ActionResult companyRegAct(String procInstId, String taskId){
		StringBuilder returnurl=new StringBuilder("http://mycenter.lvzheng.com/mywf/company/detail/").append(procInstId).append("/").append(taskId);
		beat().getModel().add("returnurl", returnurl);
		String from = request().getParameter("from");
		if(StringUtils.isNotBlank(from)){
			model().add("from", from);
    	}
		CommonUtils.geneCode(beat());//验证码
		return companyRegAct(procInstId, taskId, "");
	}

	@Path("/company/detail/{procInstId:[\\w-]+}/{taskId:[\\w-]+}/{lockBusinessKey:[\\w-]+}")
	public ActionResult companyRegAct(String procInstId, String taskId, String lockBusinessKey){
		ActionResult buildWXInfo = buildWXInfo();
		if(buildWXInfo != null){
			return buildWXInfo;
		}
		
		//请求分发
		String redirectURL = ReqDistribute(procInstId);
		if(StringUtils.isNotBlank(redirectURL)){
			return redirect(redirectURL);
		}
		
		
		/**
		 * 最近的公司注册任务
		 */
		//String openId = model().get("openId").toString();
		//String openidreq = request().getParameter("openId");
		//String openId = "oxizHs97Dv0Kb7L29D782LkdMzE4";
		//model().add("openId", openId);
		//查询当前登录用户的最后一个公司注册任务
		/*BFLoginEntity loginEntity = null;
		try {
			loginEntity = WeiXinBuz.wb.getBFLoginEByopenId(openId);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if(loginEntity == null){
			return view("404");
		}
		
		List<LvHiActEntity> ActEntitys = null;
		try {
			ActEntitys = RSBLL.getstance().getWfReceiveTaskService().getListReceiveTaskByProcKeyActKeyAndVariable(PROC_DEF_KEY_BJ_SELF_REG, "selfCheckName", var_web_userId, loginEntity.getUserid());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(ActEntitys != null && ActEntitys.size() > 0){
			List<CompanyRegTasksVo> tasks = getTaskListData(ActEntitys);
			if(tasks != null && tasks.size() > 0){
				model().add("lateTask", tasks.get(tasks.size() - 1));//获取最后一个
				model().add("lateTaskProcInstId", tasks.get(tasks.size() - 1).getProcInstId());
				model().add("lateTaskId", tasks.get(tasks.size() - 1).getTaskId());
			}
		}*/
		
		
		String activityId =WAQUtils.HTMLEncode(request().getParameter("activityId"));
		
		LvHiActEntity currentAct = null;
		try {
			currentAct = RSBLL.getstance().getWfReceiveTaskService().loadCurrentActByProcInstId(procInstId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String currTaskId = currentAct.getId();
		if(StringUtils.isNoneBlank(currTaskId) && !(taskId.equals(currTaskId))){
			//转跳
			return redirect("/weixin/mywf/company/detail/" + procInstId + "/" + currTaskId);
		}
		
		// 判断 taskId是否已经完成，如果完成，或者开启了小微服务，转跳到结果页
		LvHiActEntity hiActEntity = null;
		try {
			hiActEntity = RSBLL.getstance().getWfReceiveTaskService().loadHiActByActId(taskId);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		long businessId = 0L;
		if(hiActEntity != null){
			if(StringUtils.isBlank(lockBusinessKey)){
				try {
					lockBusinessKey = RSBLL.getstance().getWfHistoryService().getVariableByProcessInstanceIdAndName(procInstId, VAR_LOCK_BUS_KEY);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				if(StringUtils.isBlank(lockBusinessKey)){
					lockBusinessKey = hiActEntity.getActivityId();
				}
			}
			
			
			//判断lockBusinessKey是否属于当前环节
			StepUtils su = stepMap.get(hiActEntity.getActivityId());//通过环节查询对应的StepUtils对象
			if(su != null){
				if(!(su.getStepMap().containsKey(lockBusinessKey))){//步骤不在当前环节中
					lockBusinessKey = hiActEntity.getActivityId();
				}
			}
			
			
		}

		LvEnterpriseBusinessEntity businessEntity = null;
		try {
			businessEntity = RSBLL.getstance().getEpEnterpriseBusinessService().getBusinessEntityByBusinessKey(lockBusinessKey);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if(businessEntity != null){
			businessId = businessEntity.getBusinessId();
		}
		List<LvEnterpriseBusinessEntity> businessKeyList = null;
		try {
			businessKeyList = RSBLL.getstance().getEpEnterpriseBusinessService().getBusinessKeyListByType(BUSINESS_TYPE_THREE);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		String nextBusinessKey = null;
		String lastBusinessKey = null;
		if(businessKeyList != null){
			for(int i = 0; i < businessKeyList.size(); i++){
				LvEnterpriseBusinessEntity enterpriseBusinessEntity = businessKeyList.get(i);
				if(StringUtils.equalsIgnoreCase(lockBusinessKey, enterpriseBusinessEntity.getBusinessKey())
						&& i < businessKeyList.size() - 1 
						&& businessKeyList.get( i + 1) != null){
					nextBusinessKey = businessKeyList.get( i + 1).getBusinessKey();
					lastBusinessKey = businessKeyList.get( ((i > 0 )?i:1) - 1).getBusinessKey();
					break;
				}
			}
		}
		
		if(StringUtils.isNotBlank(lockBusinessKey)){
			String busVal = businessKeyMap.get(lockBusinessKey);
			if(StringUtils.isNotBlank(busVal)){
				lockBusinessKey = busVal;
			}
		}
		
		if(StringUtils.equals(lockBusinessKey, "selfCheckName")){
			/**
			 * 默认行业特点
			 */
			int size = 99;
			List<LvEnterpriseMainBusinessEntity> mainBusinessList = null;
			try {
				mainBusinessList = RSBLL.getstance().getEpEnterpriseMainBusinessService().getDefaultMainBusinessList(size);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(mainBusinessList != null){
				model().add("mainBusinessList", mainBusinessList);
			}
		}
		
		// 业务配置信息
		Map<String, String> businessConfig = null;
		try {
			businessConfig = RSBLL.getstance().getEpEnterpriseBusinessService().getBusinessConfigByBusinessKey(lockBusinessKey);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if(businessConfig != null && !businessConfig.isEmpty()){
			model().add("businessConfig", businessConfig);
		}

		/**
		 * 企业信息
		 */
		String enterpriseId = null;
		try {
			enterpriseId = RSBLL.getstance().getWfHistoryService().getVariableByProcessInstanceIdAndName(procInstId, "busid");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(StringUtils.isBlank(enterpriseId)){
			// 企业ID为空，创建企业
			LvEnterpriseEntity enterpriseEntity = new LvEnterpriseEntity();
			String cityId = null;
			try {
				cityId = RSBLL.getstance().getWfHistoryService().getVariableByProcessInstanceIdAndName(procInstId, "cityid");
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(StringUtils.isNotBlank(cityId)){
				enterpriseEntity.setCityId(Long.parseLong(cityId));
			}
			String areaId = null;
			try {
				areaId = RSBLL.getstance().getWfHistoryService().getVariableByProcessInstanceIdAndName(procInstId, "areaId");
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(StringUtils.isNotBlank(areaId)){
				enterpriseEntity.setAreaId(Long.parseLong(areaId));
			}
			try {
				enterpriseId = RSBLL.getstance().getEpEnterpriseService().addEnterpriseEntity(enterpriseEntity, null) + "";
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 保存到流程
			try {
				RSBLL.getstance().getWfProcessService().setProcessVariable(procInstId, "busid", Long.parseLong(enterpriseId));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			// 业务信息详情
			Map<String, String> businessDataMap = null;
			try {
				businessDataMap = RSBLL.getstance().getEpEnterpriseBusinessDataService().getBusinessDataMapByEnterpriseId(enterpriseId);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(businessDataMap != null && !businessDataMap.isEmpty()){
				model().addAll(businessDataMap);
			}
			// 企业主信息
			Map<String, String> enterpriseMap = null;
			try {
				enterpriseMap = RSBLL.getstance().getEpEnterpriseService().getAllValueByEnterpriseId(enterpriseId);
			} catch (Exception e) {
				e.printStackTrace();
			}
			model().addAll(DicUtils.transferDicData(enterpriseMap));
		}
		
		
		LvProcInstEntitiy procInstEntitiy = null;
		try {
			procInstEntitiy = RSBLL.getstance().getWfHistoryService().loadByProcessInstanceId(procInstId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(procInstEntitiy == null){
			return view("404");
		}
		
		
		model().add("currTaskId", currTaskId);
		model().add("DicUtils", DicUtils.class);
		model().add("EnterpriseUtils", EnterpriseUtils.class);
		model().add("enterpriseId", enterpriseId);
		model().add("businessId", businessId);
		model().add("ProcessDefinitionKey", procInstEntitiy.getProcessDefinitionKey());
		if(StringUtils.isNotBlank(nextBusinessKey)){
			model().add("nextBusinessKey", nextBusinessKey);
		}
		if(StringUtils.isNotBlank(lastBusinessKey)){
			model().add("lastBusinessKey", lastBusinessKey);
		}
		model().add("businessKey", lockBusinessKey);
		model().add("procInstId", procInstId);
		model().add("taskId", taskId);
		if(hiActEntity != null){
			model().add("activityId", hiActEntity.getActivityId());
			model().add("hiActEntity", hiActEntity);
		}else{
			model().add("activityId", activityId);
		}
		
		
		System.out.println("##############################"+model().get("industryCharacteristics"));
		System.out.println("##############################"+model().get("shopName"));
		System.out.println("##############################"+model().get("mainBusinessCode"));
		
		if("act-ts".equals(lockBusinessKey)){//需要跳转至操作提示页面
			return view("/wx/mywf/"+ procInstEntitiy.getProcessDefinitionKey() +"/act-ts");
		}
		
		return view("/wx/mywf/" + procInstEntitiy.getProcessDefinitionKey() + "/act-detail");
	}
	
	
	/**
	 * 
	* @Title: enterpriseSubmit
	* @Description: TODO(企业人员信息-提交)
	* @param @return    设定文件
	* @return ActionResult    返回类型
	* @author: RENQI  
	* @date 2016年4月7日 下午3:49:08
	* @throws
	 */
	@Path("/business/enterpriseSubmit")
	public ActionResult enterpriseSubmit(){
		String procInstId = WAQUtils.HTMLEncode(request().getParameter("procInstId"));
		String taskId = WAQUtils.HTMLEncode(request().getParameter("taskId"));
		String nextBusinessKey = WAQUtils.HTMLEncode(request().getParameter("nextBusinessKey"));
		
		BFLoginEntity loginEntity = null;
		try {
			loginEntity = WeiXinBuz.wb.getUserLoginEntity(beat());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		if(loginEntity == null){
			return view("404");
		}
		try {
			RSBLL.getstance().getWfProcessService().setProcessVariable(procInstId, VAR_LOCK_BUS_KEY, nextBusinessKey);
			RSBLL.getstance().getWfReceiveTaskService().setReceiveTaskVariable(taskId, VAR_LOCK_BUS_KEY, nextBusinessKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ActionResultUtils.renderText("");
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Path("/business/enterpriseSave")
	public ActionResult enterpriseSave(){
		String data = WAQUtils.HTMLEncode(request().getParameter("data"));
		String enterpriseId = WAQUtils.HTMLEncode(request().getParameter("enterpriseId"));
		
		if(StringUtils.isBlank(data)){
			return ActionResultUtils.renderText("NULL");
		}
		JSONObject dataJson = JSONObject.parseObject(data);
		
		// 业务信息保存
		List<LvEnterpriseBusinessEntity> businessKeyList = null;
		try {
			businessKeyList = RSBLL.getstance().getEpEnterpriseBusinessService().getBusinessKeyListByType(BUSINESS_TYPE_THREE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(businessKeyList != null && !businessKeyList.isEmpty()){
			for(LvEnterpriseBusinessEntity lvEnterpriseBusinessEntity:businessKeyList){
				String businessKey = lvEnterpriseBusinessEntity.getBusinessKey();
				long businessId = lvEnterpriseBusinessEntity.getBusinessId();
				if(!dataJson.containsKey(businessKey)){
					continue;
				}
				Map businessMap = dataJson.getObject(businessKey, Map.class);
				try {
					RSBLL.getstance().getEpEnterpriseBusinessDataService().saveEnterpriseBusinessData(enterpriseId, businessId + "", businessMap, EnterpriseUtils.getLoginInfo(request()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		// 保存企业库地址信息
		if(dataJson.containsKey("addressData")){
			Map addressDataMap = dataJson.getObject("addressData", Map.class);
			try {
				RSBLL.getstance().getEpEnterpriseAddressService().saveEnterpriseAddressData(enterpriseId, addressDataMap, EnterpriseUtils.getLoginInfo(request()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return ActionResultUtils.renderJson(dataJson);
	}
	
	/**
	 * 企业人员信息保存
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Path("/business/enterpriseRoleSave")
	public ActionResult enterpriseRoleSave(){
		String data = WAQUtils.HTMLEncode(request().getParameter("data"));
		String enterpriseId = WAQUtils.HTMLEncode(request().getParameter("enterpriseId"));
		String procInstId = WAQUtils.HTMLEncode(request().getParameter("procInstId"));
		String taskId = WAQUtils.HTMLEncode(request().getParameter("taskId"));
		String code = WAQUtils.HTMLEncode(request().getParameter("code"));
		String openId = WAQUtils.HTMLEncode(request().getParameter("openId"));
		
		BFLoginEntity loginEntity = null;
		try {
			loginEntity = WeiXinBuz.wb.getUserLoginEntity(beat());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if(loginEntity == null){
			return view("404");
		}

		if(StringUtils.isBlank(enterpriseId)){//首次注册保存
			// 企业ID为空，创建企业
			LvEnterpriseEntity enterpriseEntity = new LvEnterpriseEntity();
			enterpriseEntity.setCityId(VAR_CITY_BJ);
			try {
				enterpriseId = RSBLL.getstance().getEpEnterpriseService().addEnterpriseEntity(enterpriseEntity, null)+"" ;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
			
		if(StringUtils.isNotBlank(code)){
			//通过code获取相关企业信息
			LvEnterpriseMainBusinessEntity busEntity = null;
			try {
				busEntity = RSBLL.getstance().getEpEnterpriseMainBusinessService().loadByCode(code);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Map<String,String> map = new HashMap<String, String>();
			map.put("mainBusiness", busEntity.getCodeName());
			map.put("mainBusinessUniteCode", busEntity.getParentUniteCode());
			map.put("operatingRange", busEntity.getScope());
			try {
				RSBLL.getstance().getEpEnterpriseBusinessDataService().saveEnterpriseBusinessData(enterpriseId, "2001", map, EnterpriseUtils.getLoginInfo(request()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if(StringUtils.isBlank(data)){
			return ActionResultUtils.renderText("NULL");
		}
		JSONObject dataJson = JSONObject.parseObject(data);
		// 业务信息保存
		List<LvEnterpriseBusinessEntity> businessKeyList = null;
		try {
			businessKeyList = RSBLL.getstance().getEpEnterpriseBusinessService().getBusinessKeyListByType(BUSINESS_TYPE_THREE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(businessKeyList != null && !businessKeyList.isEmpty()){
			for(LvEnterpriseBusinessEntity lvEnterpriseBusinessEntity:businessKeyList){
				String businessKey = lvEnterpriseBusinessEntity.getBusinessKey();
				long businessId = lvEnterpriseBusinessEntity.getBusinessId();
				if(!dataJson.containsKey(businessKey)){
					continue;
				}
				Map businessMap = dataJson.getObject(businessKey, Map.class);
				try {
					RSBLL.getstance().getEpEnterpriseBusinessDataService().saveEnterpriseBusinessData(enterpriseId, businessId + "", businessMap, EnterpriseUtils.getLoginInfo(request()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		// 相关角色信息
		JSONArray partnerInfoArray = dataJson.getJSONArray("partnerInfoArray");
		if(partnerInfoArray != null && !partnerInfoArray.isEmpty()){
			for(int i = 0; i < partnerInfoArray.size(); i++){
				JSONObject comPartnerJson = partnerInfoArray.getJSONObject(i);
				// 保存主体信息
				Map partnerInfoMain = comPartnerJson.getObject("partnerInfoMain", Map.class);
				if(partnerInfoMain == null || partnerInfoMain.isEmpty()){
					continue;
				}
				String dataRoleType = comPartnerJson.getString("dataRoleType");
				long roleId = 0;
				if(StringUtils.equalsIgnoreCase(dataRoleType, ILvEnterpriseRoleRelationService.ROLETYPE_LEGALPARTNER)){
					// 保存单位
					try {
						roleId = RSBLL.getstance().getEpEnterpriseService().saveEnterpriseAllEntity(partnerInfoMain, EnterpriseUtils.getLoginInfo(request()));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{
					// 保存人员
					try {
						roleId = RSBLL.getstance().getEpEnterprisePersonService().saveEnterprisePersonEntity(enterpriseId, partnerInfoMain, EnterpriseUtils.getLoginInfo(request()));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				partnerInfoMain.put("roleId", roleId);
				// 保存关系与扩展数据
				Map comPartnerExt = comPartnerJson.getObject("partnerInfoExt", Map.class);
				try {
					RSBLL.getstance().getEpEnterpriseRoleRelationService().saveRoleRelationEntity(Long.parseLong(enterpriseId), dataRoleType, roleId, comPartnerExt, EnterpriseUtils.getLoginInfo(request()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		JSONObject reObj = new JSONObject();
		if(StringUtils.isBlank(procInstId)){
			//不存在流程id，则开启流程
			//开启流程
			Map<String, String> startTask = MyWfUtils.openWorkFlow(loginEntity.getUserid(), openId, "wx_checkName",enterpriseId,true);
			
			if(startTask != null){
				procInstId = startTask.get(PROC_DEF_KEY_BJ_SELF_REG);
				LvHiActEntity currentAct = null;
				try {
					currentAct = RSBLL.getstance().getWfReceiveTaskService().loadCurrentActByProcInstId(procInstId);
				} catch (Exception e) {
					e.printStackTrace();
				}
				taskId = currentAct.getId();
			}else{
				return redirect("404");//转跳到其他页面
			}
		}
		reObj.put("enterpriseId", enterpriseId);
		reObj.put("procInstId", procInstId);
		reObj.put("taskId", taskId);
		reObj.put("lockBusinessKey", "checkNameCollect");
		return ActionResultUtils.renderJson(reObj);
	}
	
	
	@Path("/pageReg")
	public ActionResult pageReg(){//页面跳转
		ActionResult buildWXInfo = buildWXInfo();
		if(buildWXInfo != null){
			return buildWXInfo;
		}
		//String openidreq = request().getParameter("openId");
		//String openId = "oxizHs97Dv0Kb7L29D782LkdMzE4";
		//model().add("openId", openId);
		
		//model().add("openId", "dqx_2410508062");
		String from = request().getParameter("from");
		if(StringUtils.isNotBlank(from)){
    		model().add("from", from);
    	}
		String toPage = request().getParameter("toPage");
		Enumeration<String> parameterNames = request().getParameterNames();
		if(parameterNames != null){
			while(parameterNames.hasMoreElements()){
				String nextElement = parameterNames.nextElement();
				if(StringUtils.isBlank(nextElement)){
					continue;
				}
				String htmlEncode = WAQUtils.HTMLEncode(request().getParameter(nextElement));
				if(StringUtils.isBlank(htmlEncode)){
					continue;
				}
				model().add(nextElement, htmlEncode);
				
				
			}
		}
		CommonUtils.geneCode(beat());//验证码
		return view(toPage);
	}
	
	/**
	 * 
	* @Title: isLogin
	* @Description: TODO(判断是否登录)
	* @param @return    设定文件
	* @return ActionResult    返回类型
	* @author: RENQI  
	* @date 2016年4月7日 上午11:05:45
	* @throws
	 */
	@Path("/isLogin")
	public ActionResult isLogin(){
		String openId = request().getParameter("openId");
		if(StringUtils.isBlank(openId)){
			return ActionResultUtils.renderText("{\"result\":false}");
		}
		
		BFLoginEntity loginEntity = null;
		try {
			loginEntity = WeiXinBuz.wb.getUserLoginEntity(beat());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		if(loginEntity != null){
			return ActionResultUtils.renderText("{\"result\":true}");
		}
		
		return ActionResultUtils.renderText("{\"result\":false}");
	}
	
	/**
	 * 
	* @Title: roleRelationDel
	* @Description: TODO(通过relationId删除合伙人)
	* @param @return    设定文件
	* @return ActionResult    返回类型
	* @author: RENQI  
	* @date 2016年4月9日 下午4:51:18
	* @throws
	 */
	@Path("/business/roleRelationDel")
	public ActionResult roleRelationDel(){
		String roleRelationId = WAQUtils.HTMLEncode(request().getParameter("relationId"));
		if(StringUtils.isBlank(roleRelationId)){
			return ActionResultUtils.renderText("OK");
		}
		try {
			RSBLL.getstance().getEpEnterpriseRoleRelationService().delEnterpriseRoleRelationEntityById(Long.parseLong(roleRelationId), EnterpriseUtils.getLoginInfo(request()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ActionResultUtils.renderText("OK");
	}
	
	@Path("business/companyRegTask")
	public ActionResult companyRegTaskListPage(){
		ActionResult buildWXInfo = buildWXInfo();
		if(buildWXInfo != null){
			return buildWXInfo;
		}
		//model().add("openId", "dqx_2410508062");
		
		String from = request().getParameter("from");
		if(StringUtils.isNotBlank(from)){
    		model().add("from", from);
    	}
		//String openId = model().get("openId").toString();
		//String openidreq = request().getParameter("openId");
		//String openId = "oxizHs97Dv0Kb7L29D782LkdMzE4";
		//model().add("openId", openId);
		BFLoginEntity loginEntity = null;
		try {
			loginEntity = WeiXinBuz.wb.getUserLoginEntity(beat());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if(loginEntity != null){
			List<LvHiActEntity> ActEntitys = null;
			try {
				ActEntitys = RSBLL.getstance().getWfReceiveTaskService().getListReceiveTaskByProcKeyActKeyAndVariable(PROC_DEF_KEY_BJ_SELF_REG, "selfCheckName", var_web_userId, loginEntity.getUserid());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(ActEntitys == null || ActEntitys.isEmpty()){
				return redirect("http://m.lvzheng.com");
			}
			
			List<CompanyRegTasksVo> taskList = getTaskListData(ActEntitys);
			model().add("taskList", taskList);
		}
		
		return view("/wx/mywf/"+PROC_DEF_KEY_BJ_SELF_REG+"/companyRegTaskList");
	}


	/**
	 * 
	* @Title: getTaskListData
	* @Description: TODO(获取公司注册任务列表)
	* @param @param ActEntitys
	* @param @return    设定文件
	* @return List<CompanyRegTasksVo>    返回类型
	* @author: RENQI  
	* @date 2016年4月13日 上午9:28:11
	* @throws
	 */
	public List<CompanyRegTasksVo> getTaskListData(List<LvHiActEntity> ActEntitys) {
		List<CompanyRegTasksVo> taskList = new ArrayList<CompanyRegTasksVo>();
		for (LvHiActEntity lvHiActEntity : ActEntitys) {
		String enterpriseId = null;
			try {
				enterpriseId = RSBLL.getstance().getWfHistoryService().getVariableByProcessInstanceIdAndName(lvHiActEntity.getProcessInstanceId(), "busid");
			} catch (Exception e) {
				e.printStackTrace();
			}			
			//企业信息
			String entName = null;
			try {
				entName = RSBLL.getstance().getEpEnterpriseService().getValueByEnterpriseIdAndKey(enterpriseId, "name");
			} catch (Exception e) {
				e.printStackTrace();
			}
			String checkNameStatus = null;
			try {
				checkNameStatus = RSBLL.getstance().getEpEnterpriseService().getExtValueByEnterpriseIdAndKey(enterpriseId, "checkNameStatus");
			} catch (Exception e) {
				e.printStackTrace();
			}
			CompanyRegTasksVo task = new CompanyRegTasksVo();
			task.setName(entName);
			task.setProcInstId(lvHiActEntity.getProcessInstanceId());
			task.setTaskId(lvHiActEntity.getId());
			task.setCheckNameStatusKey(checkNameStatus);
			taskList.add(task);
		}
		return taskList;
	}
	
	
	/**
	 * 获取验证码
	 * @return
	 */              
	@Path("/register/makephonecode")
	public ActionResult makePhoneCode(){
		
		JSONObject jr = new JSONObject();
		
		PhoneCodeMap codemap=PhoneCodeMap.getInstance();
		String phoneNum = beat().getRequest().getParameter("jbPhone");
		String valicode = beat().getRequest().getParameter("valicdoe");
		String tokenstr = beat().getRequest().getParameter("tokenstr");
		
		
		
		if(null == tokenstr || "".equals(tokenstr)){
			jr.put("ret", "fail");
			jr.put("msg", "生成图形验证码出现问题!");//.write("1"); 
			
			return ActionResultUtils.renderJson(jr.toString());
		}
		
		
		String sestr = (String) beat().getRequest().getSession().getAttribute("valicode-"+tokenstr);
		
		if(null == valicode || "".equals(valicode) || !StringUtils.equalsIgnoreCase(valicode, sestr)){
			jr.put("ret", "fail");
			jr.put("msg", "图形验证码不正确!");//.write("1"); 
			
			return ActionResultUtils.renderJson(jr.toString());
		}
		
		//系统生成验证码
		String phonecode = UtilTool.getMath();
		
		//System.out.println(phoneNum+"登陆的手机================"+phonecode);
		HttpSession session = request().getSession();
		session.setAttribute("phonevalidateCode", phoneNum+"_"+phonecode);
		session.setMaxInactiveInterval(60 * 60 * 15);
		Map<String,Object> codeMap =new HashMap<String, Object>();
		codeMap.put("phone", phoneNum);
		codeMap.put("code", phonecode);
		codeMap.put("time", new Date());
		codemap.setMap(phoneNum, codeMap);
		int res = -1;
		try {
			res = JavaDemoHttp.sendmsg(phoneNum, "您的验证码是："+phonecode+"(15分钟有效)。");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("============验证码:"+phonecode);
		//发送成功
		if(res == 0){
			jr.put("ret", "ok");//out.write("0");
			
		}else{
			jr.put("ret", "fail");
			jr.put("msg", "发送过程中发生了异常！");
			
		}
		return ActionResultUtils.renderJson(jr.toString());
		
	}
	
	
	//验证手机code
	@Path("/yueyValidate.html")
    public ActionResult yueyValidate(){
    	return new ActionResult(){
			@Override
			public void render(BeatContext beatContext) {
				beat().getResponse().setContentType("text/html");
				beat().getResponse().setCharacterEncoding("UTF-8");
			  	String text ="未知错误";
		        String phoneNum = beat().getRequest().getParameter("phoneNum");
		        String validaCode = beat().getRequest().getParameter("phoneNumvali");
		        //调用登陆便返回UserID
		        String backValidate =(String)beat().getRequest().getSession().getAttribute("phonevalidateCode");
		        if(null != backValidate && !"".equals(backValidate)){
		        	String[] backInfo = backValidate.split("_");
		        	String phone = backInfo[0];
		        	String backvaliCode = backInfo[1];
		        	if(phone.equals(phoneNum)){
		        		if(backvaliCode != null){
		        			if(backvaliCode.equalsIgnoreCase(validaCode)){
		        				text = "yes";
		        				
		        				//添加登陆成功的一些事件
		        				loginSuccess(phoneNum);
		        			}else{
		        				text ="验证码不正确！！！";
		        			}
		        		}else{
		        			text = "验证码失效!!";
		        		}
		        		//beat().getRequest().getSession().removeAttribute("phonevalidateCode");
		        	}else{
		        		text ="输入的密码与验证密码不一致！！";
		        	}
		        }else{
		        	text ="目前系统没有需要验证的验证码！！";
		        }
		        try {
					beat().getResponse().getWriter().print(text);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
    }
    
			
	/**
     * 登陆成功
	 * @throws Exception
	 */
	public void loginSuccess(String J_mobile ) {
		try{
			
			//判断该用户没有openid就更新用户信息
			BFLoginEntity userm = RSBLL.getstance().getLoginService().getLoginEntity("userphone='"+J_mobile+"'", 1, 1, "userid").size()>0?RSBLL.getstance().getLoginService().getLoginEntity("userphone='"+J_mobile+"'", 1, 1, "userid").get(0):null;
			
			if(userm!=null){
				
	//		        			beat().getRequest().getSession().setAttribute("user", userm);
				
				Cookie cphone = new Cookie("phone", J_mobile);
	
				/* cookie的有效期为30秒 */
				cphone.setPath("/");
				cphone.setDomain(DOMAIN);
				cphone.setMaxAge(60 * 60 * 24 * 30); /* 设置cookie的有效期为 30 天 */
				beat().getResponse().addCookie(cphone);
	
			}else{//新增用户
				BFLoginEntity le = new BFLoginEntity();
				le.setAddtime(new Date().getTime());
				le.setChannel(MContents.USER_ADD_CHANNEL_M);
				le.setUserphone(J_mobile);
				le.setAuthenflag(1); //验证过的用户duxf
				RSBLL.getstance().getLoginService().addLoginEntity(le);
				
				beat().getRequest().getSession().setAttribute("user", le);
				
				Cookie cphone = new Cookie("phone", J_mobile);
				
				/* cookie的有效期为30秒 */
				cphone.setPath("/");
				cphone.setDomain(DOMAIN);
				cphone.setMaxAge(60 * 60 * 24 * 30); /* 设置cookie的有效期为 30 天 */
				beat().getResponse().addCookie(cphone);
			}
			//从cookie取出电话号码	
			Cookie[] cookies = beat().getRequest().getCookies();
			String phone = "";
				 if (cookies != null) {
						for (int i = 0; i < cookies.length; i++) {
							System.out.println("====>"+cookies[i].getName());
							if (cookies[i].getName().equals("phone")) {
								String name = cookies[i].getValue();
								if (!StringUtils.isEmpty(name)) {
									phone = name;
								}
								continue;
							}
		
						}
				 }
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据手机号添加用户
	 * authenflag是否为发送过验证码的用户
	 * @return
	 */
	@Path("/addLoginUser")
	public ActionResult addLoginUser(){
		String openid = request().getParameter("openid")==null?"":request().getParameter("openid");
		String phoneNum = request().getParameter("phoneNum");
		String authenflag = request().getParameter("authenflag")==null?"":request().getParameter("authenflag");
		if(StringUtils.isBlank(phoneNum)){
			return ActionResultUtils.renderJson("{\"error\":}");
		}
		Long addLoginUser = 0L;
		if(authenflag.equals("true")){
			addLoginUser = UserLoginTool.addLoginUser(phoneNum, openid, true);
		}else{
			addLoginUser = UserLoginTool.addLoginUser(phoneNum, openid, false);
		}
		if(addLoginUser > 0L){
			openWorkFlow(addLoginUser, openid, "wx");
		}
		return ActionResultUtils.renderJson("{\"msg\":\"success\"}");
	}
	
	/**
	 * @param loginUser
	 */
	private void openWorkFlow(Long userid, String openId, String sourceType) {
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
			// 自动开通
			Map<String,Object> map = new HashMap<String,Object>();
			map.put(VAR_CITY, VAR_CITY_BJ); // 默认是北京
			map.put(VAR_WEB_USER_ID, userid);
			map.put(VAR_WEB_OPENID, openId);
			map.put(VAR_WEB_SOURCE_TYPE, sourceType);
			// 企业ID为空，创建企业
			LvEnterpriseEntity enterpriseEntity = new LvEnterpriseEntity();
			enterpriseEntity.setCityId(VAR_CITY_BJ);
			Long enterpriseId = null;
			try {
				enterpriseId = RSBLL.getstance().getEpEnterpriseService().addEnterpriseEntity(enterpriseEntity, null) ;
			} catch (Exception e) {
				e.printStackTrace();
			}
			map.put(VAR_BUS_ID, enterpriseId);
			try {
				RSBLL.getstance().getWfTaskService().startTask(PROC_DEF_KEY_BJ_SELF_REG, map);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/***
	 * 校验用户输入的图形验证码 是否正确
	 * @return flag:8 -- 信息为空，flag:9 -- 输入的验证码不正确 ，flag:1 -- 输入的验证码正确
	 */
	@Path("/validateImgCode")
	public ActionResult sendPhoneCode(){
		String tokenstr = beat().getRequest().getParameter("tokenstr");
		String valicdoe = beat().getRequest().getParameter("validatecode");
		
		if(valicdoe == null || "".equals(valicdoe) || null == tokenstr || "".equals(tokenstr)){
			return ActionResultUtils.renderJson("{\"flag\":\"8\"}");
		}else if(!valicdoe.toUpperCase().equalsIgnoreCase((String) beat().getRequest().getSession().getAttribute("valicode"+tokenstr))){
			String sttt = (String) beat().getRequest().getSession().getAttribute("valicode"+tokenstr);
			
			System.out.println(valicdoe.toUpperCase()+"---"+sttt);
			return ActionResultUtils.renderJson("{\"flag\":\"9\"}");
		}
		
		return ActionResultUtils.renderJson("{\"flag\":\"1\"}");
	}
	
	
	
	@Path("/business/wfSignlReceiveTask")
	public ActionResult wfSignlReceiveTask(){
		String procInstId = WAQUtils.HTMLEncode(request().getParameter("procInstId"));
		String taskId = WAQUtils.HTMLEncode(request().getParameter("taskId"));
		String variableName = WAQUtils.HTMLEncode(request().getParameter("variableName"));
		String value = WAQUtils.HTMLEncode(request().getParameter("value"));
		
		if(StringUtils.isNotBlank(variableName) && StringUtils.isNotBlank(value)){
			try {
				RSBLL.getstance().getWfReceiveTaskService().setReceiveTaskVariable(taskId, variableName, value);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		LvHiActEntity currentAct = null;
		try {
			currentAct = RSBLL.getstance().getWfReceiveTaskService().loadCurrentActByProcInstId(procInstId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		/**
		 * 当前环节才触发下一任务
		 */
		if(StringUtils.equals(currentAct.getId(), taskId) && StringUtils.isNotBlank(taskId)){
			try {
				RSBLL.getstance().getWfReceiveTaskService().signlReceiveTask(procInstId, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ActionResultUtils.renderText("");
	}
	
	
	/**
	 * 
	* @Title: ReqDistribute
	* @Description: TODO(请求分发，通过procInstId查询属于哪种流程)
	* @param @return    设定文件
	* @return String    返回类型
	* @author: RENQI  
	* @date 2016年5月13日 上午10:57:59
	* @throws
	 */
	public String ReqDistribute(String procInstId){
		//通过procInstId查询属于哪种流程
		LvProcInstEntitiy proEntity = null;
		try {
			proEntity = RSBLL.getstance().getWfHistoryService().loadByProcessInstanceId(procInstId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(proEntity == null){
			return "404";
		}
		
		String prodefid = proEntity.getProcessDefinitionId();
		
		String prodefKey = prodefid.split(":")[0]; //bj-all-self-help-company_appoint:1:39184510158081
		
		if(PROC_DEF_KEY_BJ_SELF_APPOINT.equals(prodefKey)){//自助约号流程
			return "/weixin/appointNo/pageReg?toPage=wx/appointNo/appointResult&procInstId="+procInstId;
		}
		
		return null;
	}
	
	public static void main(String[] args) {
//		new MyWfController().ReqDistribute("procInstId");
		
		new MyWfController().companyRegAct("39315113850113","39315113865985","");
	}
	
}
