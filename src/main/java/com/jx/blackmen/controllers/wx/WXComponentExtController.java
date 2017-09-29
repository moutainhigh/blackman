package com.jx.blackmen.controllers.wx;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.jx.argo.ActionResult;
import com.jx.argo.annotations.Path;
import com.jx.blackface.gaea.usercenter.entity.BFLoginEntity;
import com.jx.blackmen.buz.WeiXinBuz;
import com.jx.blackmen.common.UserLoginTool;
import com.jx.blackmen.controllers.BaseController;
import com.jx.blackmen.frame.RSBLL;
import com.jx.blackmen.service.MyLoginService;
import com.jx.blackmen.utils.ActionResultUtils;
import com.jx.blackmen.utils.CommonUtils;
import com.jx.blackmen.utils.WAQUtils;
import com.jx.blackmen.utils.WXComponentExtUtils;
import com.jx.blackmen.utils.WXUtils;


/**
 * 
* @ClassName: WXComponentExtController
* @Description: TODO(微信功能模块扩展)
* @author: RENQI  
* @date 2016年5月9日 上午11:36:16
*
 */
@Path("weixin")
public class WXComponentExtController extends BaseController{
	
	private static final String SELF_HELP_COMPANY_APPOINT = "bj-all-self-help-company_appoint";
	private static String VAR_WEB_USER_ID = "webUserId";
	private static String VAR_WEB_OPENID = "openId";
	private static String VAR_WEB_SOURCE_TYPE = "sourceType";
	private static final String var_key_enterprise_id = "enterpriseId";
	private static final String var_key_enterprise_name = "enterpriseName"; 
	private static final String var_key_appointAccount = "appointAccount"; //预约账号
	private static final String var_key_appointPassword = "appointPassword";//预约密码
	private static final String var_key_mobilePhoneNo = "mobilePhoneNo";//预约手机号
	private static final String var_key_govAppointStartDate = "govAppointStartDate";//预约开始时间 
	private static final String var_key_govAppointEndDate = "govAppointEndDate"; //预约结束时间
	private static final String var_syn_key_status = "enterpriseSynStatus";//1，未同步；2，同步失败，用户名或者密码错误；3，同步失败，未找到企业；10，同步成功（enterpriseId，去企业库获取预约结果）；
	private static final String var_syn_status_1 = "1"; // 未同步
	private static final String var_syn_status_2 = "2"; // 同步失败，用户名或者密码错误
	private static final String var_syn_status_3 = "3"; // 同步失败，未找到对应的公司
	private static final String var_syn_status_10 = "10"; // 同步完成
	
	
	/**
	 * 
	* @Title: appointNoIndex
	* @Description: TODO(约号首页面)
	* @param @return    设定文件
	* @return ActionResult    返回类型
	* @author: RENQI  
	* @date 2016年5月10日 上午10:30:25
	* @throws
	 */
	@Path("/appointNo/index")
	public ActionResult appointNoIndex(){
		ActionResult buildWXInfo = buildWXUrl();
		if(buildWXInfo != null){
			return buildWXInfo;
		}
		
		String openId = model().get("openId").toString();
		String from = request().getParameter("from");
    	if(StringUtils.isNotBlank(from)){
    		model().add("from", from);
    	}
		//通过openid查询用户
		BFLoginEntity loginEntity = null;
		try {
			loginEntity = WeiXinBuz.wb.getBFLoginEByopenId(openId);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		

		if(loginEntity != null){
			//微信用户的电话
			model().add("userphone", loginEntity.getUserphone());
		}
		
		String procInstId = request().getParameter("procInstId");
		if(StringUtils.isNotBlank(procInstId)){
			List<String> varList = new ArrayList<String>();
			varList.add("enterpriseName");
			varList.add("appointAccount");
			Map<String ,Object> map = null;
			try {
				map = RSBLL.getstance().getWfHistoryService().getVariablesByProcessInstanceId(procInstId, varList);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(map != null){
				model().add("enterpriseName", map.get("enterpriseName"));
				model().add("appointAccount", map.get("appointAccount"));
			}
		}
		
		CommonUtils.geneCode(beat());//验证码
		model().add("WXComponentExtUtils", WXComponentExtUtils.class);
		return view("wx/appointNo/appointNoBaseInfo");
		
		//return view("wx/appointNo/appointResult");
		
	}
	
	/**
	 * 
	* @Title: startAppointNoWorkFlow
	* @Description: TODO(开启约号流程)
	* @param @return    设定文件
	* @return ActionResult    返回类型
	* @author: RENQI  
	* @date 2016年5月10日 上午10:29:59
	* @throws
	 */
	@Path("/appointNo/startAppointNo")
	public ActionResult startAppointNoWorkFlow(){
		ActionResult buildWXInfo = buildWXUrl();
		if(buildWXInfo != null){
			return buildWXInfo;
		}
		
		String openId = model().get("openId").toString();
		
		//通过openid查询用户
		BFLoginEntity loginEntity = null;
		try {
			loginEntity = WeiXinBuz.wb.getBFLoginEByopenId(openId);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		//获取请求参数
		Map<String,String> paraMap = new HashMap<String, String>();
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
				paraMap.put(nextElement, htmlEncode);
			}
		}
		
		
		// 自动开通
		Map<String,Object> map = new HashMap<String,Object>();
		map.put(var_key_appointAccount, paraMap.get("appointAccount")); //工商账户
		map.put(var_key_appointPassword, paraMap.get("appointPassword"));//工商账户密码
		map.put(var_key_mobilePhoneNo, paraMap.get("mobilePhoneNo"));//预约手机号
		map.put(var_key_enterprise_name, paraMap.get("enterpriseName"));//公司名称
		map.put(var_key_govAppointStartDate, paraMap.get("govAppointStartDate"));//预约开始时间
		map.put(var_key_govAppointEndDate, paraMap.get("govAppointEndDate"));//预约结束时间
		map.put(var_syn_key_status, var_syn_status_1);//预约状态：1，未同步；2，同步失败，用户名或者密码错误；3，同步失败，未找到企业；10，同步成功（enterpriseId，去企业库获取预约结果）；
		map.put(VAR_WEB_USER_ID, loginEntity.getUserid());
		map.put(VAR_WEB_OPENID, openId);
		map.put(VAR_WEB_SOURCE_TYPE, "wx_appointNo");
		
		try {
			Map<String, String> startTask = RSBLL.getstance().getWfTaskService().startTask(SELF_HELP_COMPANY_APPOINT, map);
			model().add("procInstId", startTask.get(SELF_HELP_COMPANY_APPOINT));//流程id
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		model().add("WXComponentExtUtils", WXComponentExtUtils.class);
		//跳转到'预约结果'页面
		return ActionResultUtils.renderJson("{\"ret\":\"success\"}");
	}
	
	/**
	 * 
	* @Title: addUser
	* @Description: TODO(约号-用户操作)
	* @param @return    设定文件
	* @return ActionResult    返回类型
	* @author: RENQI  
	* @date 2016年5月10日 上午10:29:30
	* @throws
	 */
	@Path("/appointNo/addUser")
	public ActionResult addUser(){
/*		ActionResult buildWXInfo = buildWXUrl();
		if(buildWXInfo != null){
			return buildWXInfo;
		}*/
		
		String openId = request().getParameter("openId");
		String mobilePhoneNo = request().getParameter("userphone");
		//验证手机验证码是否正确
		JSONObject jr = new JSONObject();
		jr.put("ret", "fail");
		
		//登录校验
		MyLoginService.myLoginService.commonLoginValid(beat(), jr);
		
		if(jr.containsKey("msg")){
			return ActionResultUtils.renderJson(jr.toString());
		}
		
		Map<String, Object> wxUser = WXUtils.getWXUserBy(openId);
		if(wxUser.isEmpty()){
			jr.put("msg", "未获取到用户信息,请刷新重试!"); //非法访问(防止伪造openId访问)
			return ActionResultUtils.renderJson(jr.toString());
		}
		if("0".equals(wxUser.get("subscribe"))){
			jr.put("msg", "请先关注小微律政!");  //未关注登录
			return ActionResultUtils.renderJson(jr.toString());
		}
		
		
		//通过openid查询用户
		BFLoginEntity loginEntity = null;
		try {
			loginEntity = WeiXinBuz.wb.getBFLoginEByopenId(openId);
		} catch (Exception e1) {
			e1.printStackTrace();
			jr.put("msg", "网络失败!请稍后重试!");
			return ActionResultUtils.renderJson(jr.toString());
		}
		
		if(loginEntity != null){//通过openId能查询到用户
			jr.put("ret", "success");
			return ActionResultUtils.renderJson(jr.toString());
		}
		try {
			//通过openid未查询用户  则    用手机号查询用户
			loginEntity = WeiXinBuz.wb.getBFLoginEByPhoneNum(mobilePhoneNo);
		} catch (Exception e) {
			e.printStackTrace();
			jr.put("msg", "网络失败!请稍后重试!");
			return ActionResultUtils.renderJson(jr.toString());
		}
		
		Long addLoginUser = 0L;
		if(loginEntity == null){//通过openid 和  手机号码都未找到用户，新增用户
			addLoginUser = UserLoginTool.addLoginUser(mobilePhoneNo, openId, true);
		}else if(!(openId.equals(loginEntity.getOpenid()))){//如果手机号码对应用户的openId 和  当前登录用户的openId不同
			addLoginUser = UserLoginTool.addLoginUser(mobilePhoneNo, openId, true);//更新手机号码对应的openId
		}else{
			addLoginUser = loginEntity.getUserid();
		}
		
		if(addLoginUser > 0L){
			jr.put("ret", "success");
		}else{
			jr.put("msg", "网络失败!请稍后重试!");
		}
		return ActionResultUtils.renderJson(jr.toJSONString());
	}
	
	
	@Path("/appointNo/pageReg")
	public ActionResult pageReg(){//页面跳转
		ActionResult buildWXInfo = buildWXUrl();
		if(buildWXInfo != null){
			return buildWXInfo;
		}
		
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
		
		model().add("WXComponentExtUtils", WXComponentExtUtils.class);
		return view(toPage);
	}
	
	
}
