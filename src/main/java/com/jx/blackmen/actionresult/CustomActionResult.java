package com.jx.blackmen.actionresult;

import java.util.Date;

import com.jx.argo.ActionResult;
import com.jx.argo.BeatContext;
import com.jx.blackmen.utils.MContents;


public class CustomActionResult implements ActionResult {

	private String remessage = "";
	private String openid = "";
	public CustomActionResult(String message,String openid){
		this.remessage = message;
		this.openid = openid;
	}
	public void render(BeatContext beat){
		// TODO Auto-generated method stub
		
		beat.getResponse().setCharacterEncoding("utf-8");
		beat.getResponse().setContentType("text/plain");
		try{
			String res = sendSigleXmlMessage(openid,remessage);
			beat.getResponse().getWriter().print(res);
			beat.getResponse().getWriter().close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	public String sendSigleXmlMessage(String openid,String content)throws Exception{
		String xml = "<xml>"
				+"<ToUserName><![CDATA["+openid+"]]></ToUserName>"
				+"<FromUserName><![CDATA["+MContents.weixin_app_name+"]]></FromUserName>"
		+"<CreateTime>"+new Date().getTime()+"</CreateTime>"
//		+"<MsgType><![CDATA[text]]></MsgType>"
		+"<MsgType><![CDATA[transfer_customer_service]]></MsgType>"
//		+"<Content><![CDATA["+content+"]]></Content>"
		+"</xml>";
		System.out.println("this real res is "+xml);
		return xml;
	}

}
