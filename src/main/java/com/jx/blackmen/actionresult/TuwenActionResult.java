package com.jx.blackmen.actionresult;

import java.io.IOException;
import java.util.Date;

import com.jx.argo.ActionResult;
import com.jx.argo.BeatContext;
import com.jx.blackmen.utils.MContents;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class TuwenActionResult implements ActionResult {

	private String jsonstr = "";
	private String openid = "";
	public TuwenActionResult(String json,String openid){
		this.jsonstr = json;
		this.openid = openid;
	}
	public void render(BeatContext beat){
		// TODO Auto-generated method stub
		beat.getResponse().setCharacterEncoding("utf-8");
		beat.getResponse().setContentType("text/plain");
		StringBuffer sb = new StringBuffer();
		JSONObject jo = JSONObject.fromObject(jsonstr);
		if(jo != null){
			sb.append("<xml>");
			sb.append("<ToUserName><![CDATA["+openid+"]]></ToUserName>");
			sb.append("<FromUserName><![CDATA["+MContents.weixin_app_name+"]]></FromUserName>");
			sb.append("<CreateTime>"+new Date().getTime()+"</CreateTime>");
			sb.append("<MsgType><![CDATA[news]]></MsgType>");
			JSONArray ja = jo.getJSONArray("content");
			sb.append("<ArticleCount>"+ja.size()+"</ArticleCount>");
			sb.append("<Articles>");
			for(int i=0,c=ja.size();i<c;i++){
				JSONObject j = ja.getJSONObject(i);
				sb.append("<item>");
				sb.append("<Title><![CDATA["+j.getString("title")+"]]></Title>"); 
				sb.append("<Description><![CDATA["+j.getString("desc")+"]]></Description>");
				sb.append("<PicUrl><![CDATA["+j.getString("picurl")+"]]></PicUrl>");
				sb.append("<Url><![CDATA["+j.getString("url")+"]]></Url>");
				sb.append("</item>");
			}
			sb.append("</Articles>");
			sb.append("</xml>");
		}
		
		
		
		System.out.println("this final xml is "+sb.toString());
		
		try {
			beat.getResponse().getWriter().print(sb.toString());
			beat.getResponse().getWriter().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
