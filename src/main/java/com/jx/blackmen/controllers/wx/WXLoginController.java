package com.jx.blackmen.controllers.wx;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.jx.argo.ActionResult;
import com.jx.argo.annotations.Path;
import com.jx.blackface.gaea.usercenter.entity.BFLoginEntity;
import com.jx.blackface.tools.blackTrack.entity.WebLogs;
import com.jx.blackmen.buz.WeiXinBuz;
import com.jx.blackmen.controllers.BaseController;
import com.jx.blackmen.frame.RSBLL;
import com.jx.blackmen.service.MyLoginService;
import com.jx.blackmen.utils.ActionResultUtils;
import com.jx.blackmen.utils.CommonUtils;
import com.jx.blackmen.utils.CookieUtils;
import com.jx.blackmen.utils.MContents;
import com.jx.blackmen.utils.MD5;
import com.jx.blackmen.utils.WXUtils;

@Path("/wx")
public class WXLoginController extends BaseController{
	@Path("/login.html")
	public ActionResult tologIn(){
		ActionResult buildWXUrl = buildWXUrl();
		if(buildWXUrl != null){
			return buildWXUrl;
		}
		//生成唯一标识
		CommonUtils.geneCode(beat());
		String reach_url = beat().getRequest().getParameter("reach_url");
		if(StringUtils.isNotBlank(reach_url)){
			reach_url = reach_url.replaceAll("WENHAO", "?").replaceAll("AND", "&");
			if(StringUtils.contains(reach_url, "code")){
				try {
					reach_url = MContents.WX_url_base_shouquan_page.replace("APP_ID", MContents.weixin_app_id).replace("REDIRECT_URI", URLEncoder.encode(reach_url, "utf-8"));
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
			}
			System.out.println(reach_url+"=========================");
			beat().getModel().add("reach_url", reach_url);
		}
		return super.view("/wx/login");
	}
	
	private static int channel =1; //客户渠道为微信
	@Path("/loginAction")
	public ActionResult logInAction(){
//		ActionResult buildWXUrl = buildWXUrl();
//		if(buildWXUrl != null){
//			return buildWXUrl;
//		}
		
		//日志
		WebLogs logs = WebLogs.getIntanse(WXLoginController.class, "logInAction");
		
		String openId = beat().getRequest().getParameter("openId");
		logs.putParam("openId", openId);
		logs.putParam("channel", "wx");
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
//		if("0".equals(wxUser.get("subscribe"))){
//			jr.put("msg", "请先关注小微律政!");  //未关注登录
//			return ActionResultUtils.renderJson(jr.toString());
//		}
		
		
		String phonenumber = model().get("phonenumber").toString();
		List<BFLoginEntity> ulist = null;
		BFLoginEntity bf = null;
		try {
			ulist = RSBLL.getstance().getLoginService().getLoginEntity(" userphone='"+phonenumber+"'", 1, 1, null);
		} catch (Exception e) {
			e.printStackTrace();
			jr.put("msg", "网络失败!请稍后重试!");
			return ActionResultUtils.renderJson(jr.toString());
		}
		if(null != ulist && ulist.size() > 0){
			BFLoginEntity bfLoginEntity = ulist.get(0);
			if(null != bfLoginEntity){
				if(StringUtils.isNotBlank(bfLoginEntity.getOpenid()) && !StringUtils.equals(openId, bfLoginEntity.getOpenid())){
					jr.put("msg", "此手机号已存在绑定的微信!且对应的不是您的微信号");
					return ActionResultUtils.renderJson(jr.toString());
				}
				bf = bfLoginEntity;
				bf.setOpenid(openId);
				//bf.setUsername(wxUser.get("nickname").toString());  //不覆盖原来客服填写的名称(其实这样做并不是好的解决方案
				try {
					RSBLL.getstance().getLoginService().updateLoginEntity(bf);
					jr.put("ret", "success");
					//保存cookie|客户的userId
					try {
						CookieUtils.saveCookie(CookieUtils.cookicName, String.valueOf(bf.getUserid()), response());
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
					jr.put("msg", "您已存在一个用户，我们尝试跟微信进行绑定，但是发生了异常，请您稍后再试!");
					return ActionResultUtils.renderJson(jr.toString());
				}
				
			}
		}
		if(bf == null){
			bf = new BFLoginEntity();
			Long userid = 0L;
			String password = phonenumber.substring(phonenumber.length() - 6, phonenumber.length());
			bf.setUserphone(phonenumber);
			bf.setAddtime(new Date().getTime());
			bf.setChannel(channel);
			bf.setPassword(MD5.sign(password, "utf-8"));
			bf.setUsername(wxUser.get("nickname")==null?"":wxUser.get("nickname").toString());
			bf.setAuthenflag(1);  //已认证
			bf.setOpenid(openId);
			try {
				userid = RSBLL.getstance().getLoginService().addLoginEntity(bf);
			} catch (Exception e) {
				e.printStackTrace();
				jr.put("msg", "您不是我们的注册用户，我们尝试新添加一个用户，但是发生了异常，请您稍后再试!");
				return ActionResultUtils.renderJson(jr.toString());
			}finally{
				if(userid>0){
					jr.put("ret", "success");
					//保存cookie|客户的userId
					try {
						CookieUtils.saveCookie(CookieUtils.cookicName, String.valueOf(userid), response());
					} catch (Exception e) {
						e.printStackTrace();
					}
//					jr.put("msg", "您不是我们的注册用户，我们为您注册了一个新的用户，密码为您的手机的后6位!");
				}
			}
		}
		
		logs.putParam("msg", jr.get("msg"));
		logs.putParam("ret", jr.get("ret"));
		
		logs.printErrorLog();
		
		return ActionResultUtils.renderJson(jr.toString());
	}
	
	
	@Path("/testop")
	public ActionResult testop(){
		String openId = beat().getRequest().getParameter("openId");
		openId = "oxizHs0x6vcm7wNBKAYgua4IkzvE";
		String rs = "1";
		try {
			if(null != WeiXinBuz.wb.getBFLoginEByopenId(openId)){
				System.out.println("get-login-by-openid-success----->"+openId);
				rs = "get-login-by-openid-success----->"+openId;
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return ActionResultUtils.renderJson(rs.toString());
	}
	
}
