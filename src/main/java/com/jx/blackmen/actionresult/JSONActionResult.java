package com.jx.blackmen.actionresult;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import com.jx.argo.ActionResult;
import com.jx.argo.BeatContext;


public class JSONActionResult implements ActionResult{

	private String jsonstr = null;
	public JSONActionResult(String jsonstr){
		this.jsonstr = jsonstr;
	}
	public void render(BeatContext beat) {
		// TODO Auto-generated method stub
		HttpServletResponse response = beat.getResponse();
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html");
		PrintWriter pw = null;
		try {
			pw = response.getWriter();
			pw.print(jsonstr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(pw != null){
				pw.close();
			}
		}
		
	}

}
