package com.jx.blackmen.controllers.m;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.jx.argo.ActionResult;
import com.jx.argo.annotations.Path;
import com.jx.blackface.gaea.usercenter.entity.BFLoginEntity;
import com.jx.blackface.tools.blackTrack.entity.WebLogs;
import com.jx.blackmen.controllers.BaseController;
import com.jx.blackmen.frame.RSBLL;
import com.jx.blackmen.service.MyLoginService;
import com.jx.blackmen.utils.ActionResultUtils;
import com.jx.blackmen.utils.CommonUtils;
import com.jx.blackmen.utils.CookieUtils;
import com.jx.blackmen.utils.MD5;

@Path("/m")
public class MLoginController extends BaseController{
	@Path("/login.html")
	public ActionResult tologIn(){
		//生成唯一标识
		CommonUtils.geneCode(beat());
		String reach_url = beat().getRequest().getParameter("reach_url");
		
		if(StringUtils.isNotBlank(reach_url)){
			beat().getModel().add("reach_url", reach_url);
		}
		return super.view("/m/login");
	}
	
	private static int channel =1; //客户渠道为微信
	@Path("/loginAction")
	public ActionResult logInAction(){
		JSONObject jr = new JSONObject();
		jr.put("ret", "fail");
		
		WebLogs logs = WebLogs.getIntanse(MLoginController.class, "logInAction");
		
		
		try {
			
			//登录校验
			MyLoginService.myLoginService.commonLoginValid(beat(), jr);
			if(jr.containsKey("msg")){
				return ActionResultUtils.renderJson(jr.toString());
			}
			logs.putParam("channel", "m");
			
			String phonenumber = model().get("phonenumber").toString();
	
			long userid = 0;
			List<BFLoginEntity> ulist = null;
			try {
				ulist = RSBLL.getstance().getLoginService().getLoginEntity(" userphone='"+phonenumber+"'", 1, 1, null);
			} catch (Exception e) {
				e.printStackTrace();
				jr.put("msg", "网络失败!请稍后重试!");
				return ActionResultUtils.renderJson(jr.toString());
			}
			if(null == ulist || ulist.size() == 0){
				String password = phonenumber.substring(phonenumber.length() - 6, phonenumber.length());
				BFLoginEntity bf = new BFLoginEntity();
				bf.setUserphone(phonenumber);
				bf.setAddtime(new Date().getTime());
				bf.setChannel(channel);
				bf.setPassword(MD5.sign(password, "utf-8"));
				bf.setUsername(phonenumber);
				try {
					userid = RSBLL.getstance().getLoginService().addLoginEntity(bf);
				} catch (Exception e) {
					e.printStackTrace();
					jr.put("msg", "您不是我们的注册用户，我们尝试新添加一个用户，但是发生了异常，请您稍后再试!");
					return ActionResultUtils.renderJson(jr.toString());
				}finally{
					if(userid>0){
						jr.put("ret", "success");
	//					jr.put("msg", "您不是我们的注册用户，我们为您注册了一个新的用户，密码为您的手机的后6位!");
					}
				}
			}else{
				userid = ulist.get(0).getUserid();
				jr.put("ret", "success");
			}
			
			if(userid > 0){
				int cookieTime = -1; //永不过期
				try {
					CookieUtils.saveCookie(userid, response(), cookieTime);
				} catch (Exception e) {
					e.printStackTrace();
	//				jr.put("msg", "登录异常！");
				}
			}
			logs.putParam("msg", jr.get("msg"));
			logs.putParam("ret", jr.get("ret"));
			
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			logs.printErrorLog();
		}
		return ActionResultUtils.renderJson(jr.toString());
	}
	
}
