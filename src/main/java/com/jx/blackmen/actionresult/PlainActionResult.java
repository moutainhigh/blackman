package com.jx.blackmen.actionresult;

import java.io.IOException;

import com.jx.argo.ActionResult;
import com.jx.argo.BeatContext;


public class PlainActionResult implements ActionResult{
	private String text = null;
	private String encoding = "UTF-8";

	public PlainActionResult(String text, String encoding) {
		this.text = text;
		this.encoding = encoding;
	}

	public PlainActionResult(String text) {
		this.text = text;
	}

	public void render(BeatContext beat){
		// TODO Auto-generated method stub
		//beat.getResponse().reset();
		if (encoding == null)
			encoding = "UTF-8";
		beat.getResponse().setCharacterEncoding(encoding);
		if (text == null)
			text = "";
		//beat.getResponse().setContentLength(text.length());
		beat.getResponse().setContentType("text/plain");
		try {
			beat.getResponse().getWriter().print(text);
			beat.getResponse().getWriter().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
