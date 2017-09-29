package com.jx.blackmen.actionresult;

import java.util.Date;

import com.jx.argo.ActionResult;
import com.jx.argo.BeatContext;
import com.jx.blackmen.utils.MContents;

public class MessageActionResult implements ActionResult {

	private String remessage = "";
	private String openid = "";
	public MessageActionResult(String message,String openid){
		this.remessage = message;
		this.openid = openid;
	}
	
	public String sendSigleXmlMessage(String openid,String content)throws Exception{
		String xml = "<xml>"
				+"<ToUserName><![CDATA["+openid+"]]></ToUserName>"
				+"<FromUserName><![CDATA["+MContents.weixin_app_name+"]]></FromUserName>"
		+"<CreateTime>"+new Date().getTime()+"</CreateTime>"
		+"<MsgType><![CDATA[text]]></MsgType>"
//		+"<MsgType><![CDATA[transfer_customer_service]]></MsgType>"
		+"<Content><![CDATA["+content+"]]></Content>"
		+"</xml>";
		System.out.println("this real res is "+xml);
		return xml;
	}
	public void render(BeatContext beatContext) {
		// TODO Auto-generated method stub

		beatContext.getResponse().setCharacterEncoding("utf-8");
		beatContext.getResponse().setContentType("text/plain");
		String res = "";
		try {
			res = sendSigleXmlMessage(openid,remessage);
			beatContext.getResponse().getWriter().print(res);
			beatContext.getResponse().getWriter().close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
