package com.jx.blackmen.actionresult;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import com.jx.argo.ActionResult;
import com.jx.argo.BeatContext;

public class BufferPicActionResult implements ActionResult{

	BufferedImage bi ;
	public BufferPicActionResult(BufferedImage bi) {
		this.bi = bi;
	}

	public void render(BeatContext beat){
		HttpServletResponse response = beat.getResponse();
		response.setContentType("image/png");
		try {
			ImageIO.write(bi, "png", response.getOutputStream());
			response.getOutputStream().flush();
			response.getOutputStream().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
