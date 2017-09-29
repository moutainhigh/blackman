package com.jx.blackmen.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.alibaba.fastjson.JSONObject;
import com.jx.argo.ActionResult;
import com.jx.argo.BeatContext;

/**
 * @author zhangzhihua
 * 直接输出文本的简化函数
 */
public class ActionResultUtils {

	private static final String ENCODING_DEFAULT = "UTF-8";

	//content-type 定义 //
	private static final String TEXT_TYPE = "text/plain";
	private static final String JSON_TYPE = "application/json";
	private static final String XML_TYPE = "text/xml";
	private static final String HTML_TYPE = "text/html";
	/**
	 * @param content
	 * @return 直接输出文本.
	 */
	public static ActionResult renderText(final String content){
		return renderText(content, ENCODING_DEFAULT);
	}
	
	/**
	 * @param content
	 * @param encoding
	 * @return 直接输出文本.
	 */
	public static ActionResult renderText(final String content, String encoding){
		return render(content, encoding, TEXT_TYPE);
	}

	/**
	 * @param content
	 * @return 直接输出html.
	 */
	public static ActionResult renderHtml(final String content){
		return renderHtml(content, ENCODING_DEFAULT);
	}
	
	/**
	 * @param content
	 * @param encoding
	 * @return 直接输出html.
	 */
	public static ActionResult renderHtml(final String content, String encoding){
		return render(content, encoding, HTML_TYPE);
	}
	
	/**
	 * @param content
	 * @return 直接输出JSON
	 */
	public static ActionResult renderJson(final String content){
		return renderJson(content, ENCODING_DEFAULT);
	}
	
	/**
	 * @param content
	 * @param encoding
	 * @return 直接输出JSON
	 */
	public static ActionResult renderJson(final String content, String encoding){
		return render(content, encoding, JSON_TYPE);
	}
	
	/**
	 * @param json
	 * @return 直接输出JSON
	 */
	public static ActionResult renderJson(JSONObject json){
		return renderJson(json, ENCODING_DEFAULT);
	}
	
	/**
	 * @param json
	 * @param encoding
	 * @return 直接输出JSON
	 */
	public static ActionResult renderJson(final JSONObject json, String encoding){
		return render(json.toString(), encoding, JSON_TYPE);
	}
	
	/**
	 * @param content
	 * @return 直接输出XML
	 */
	public static ActionResult renderXml(final String content){
		return renderXml(content, ENCODING_DEFAULT);
	}
	
	/**
	 * @param content
	 * @param encoding
	 * @return 直接输出XML
	 */
	public static ActionResult renderXml(final String content, String encoding){
		return render(content, encoding, XML_TYPE);
	}
	
	public static ActionResult renderFile(final byte[] data, final String name){
		return new ActionResult(){

			@Override
			public void render(BeatContext beatContext) {
				HttpServletResponse response = beatContext.getResponse();
				OutputStream os = null;
			    try {
			    	os = response.getOutputStream();
			    	response.reset();
			    	response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(name, "utf-8") );
			    	response.setContentType("application/octet-stream; charset=utf-8;");  
			    	IOUtils.write(data, os);
			        os.flush();  
			    }catch(Exception e){
			    	e.printStackTrace();
			    } finally{
			        if(os != null){
			            try {
							os.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
			        }
			    } 
			}
		};
	}
	
	/**
	 * @param content
	 * @param encoding
	 * @param contentType
	 * @return
	 */
	public static ActionResult render(final String content, String encoding, String contentType){
		class PlainActionResult implements ActionResult {
			private String content = "";
			private String encoding = ENCODING_DEFAULT;
			private String contentType = TEXT_TYPE;
			
			public PlainActionResult(String content, String encoding, String contentType) {
				this.content = content;
				this.encoding = encoding;
				this.contentType = contentType;
			}
			
			@Override
			public void render(BeatContext beat) {
				HttpServletResponse response = beat.getResponse();
				response.setCharacterEncoding(encoding);
				response.setContentType(contentType);
				PrintWriter pw = null;
				try {
					pw = response.getWriter();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(pw != null){
					pw.print(content);
					pw.flush();
					pw.close();
				}
			}
		}
		return new PlainActionResult(content, encoding, contentType);
	}
}
