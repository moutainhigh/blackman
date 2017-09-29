/**
 * 
 */

package com.jx.blackmen.controllers;

import java.awt.image.BufferedImage;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.jx.argo.ActionResult;
import com.jx.argo.annotations.Path;
import com.jx.argo.controller.AbstractController;
import com.jx.blackface.tools.blackTrack.TrackLogUtils;
import com.jx.blackface.tools.blackTrack.entity.TrackInfoEntity;
import com.jx.blackface.tools.blackTrack.utils.ImageUtils;
import com.jx.blackmen.actionresult.BufferPicActionResult;
import com.jx.blackmen.common.CommonUtils;



/**
 * simple introduction
 *
 * <p>detailed comment</p>
 * @author chuxuebao 2016年3月30日
 * @see
 * @since 1.0
 */
/*
 * 微站js事件统计
 */
@Path("/lv")
public class TrackLogController extends AbstractController {
	/*
	 * js统计
	 */
	@Path("empty.png")
	public ActionResult empty(){
		BufferedImage bi = null;
		try {
			HttpServletRequest request = beat().getRequest();
			HttpServletResponse response = beat().getResponse();
			TrackInfoEntity trackLog = TrackLogUtils.getTrackLog(request, response);
			// 模块名称
			trackLog.setMoudle(request.getParameter("moudle"));
			// 用户ID
			trackLog.setUserId(CommonUtils.getUserIdFormCookie(CommonUtils.cookieName, request));
			// 引用页面
			trackLog.setPageUrl(request.getParameter("pageUrl"));
			// 来源
			trackLog.setFrom(request.getParameter("from"));
			// 业务数据
			Map<String, String> dataMap = new HashMap<String, String>();
			Enumeration<String> paramNames = request.getParameterNames();
			while(paramNames.hasMoreElements()){
				String param = paramNames.nextElement();
				String dataSep = "data_";
				if(StringUtils.startsWith(param, dataSep)){
					String dataKey = StringUtils.substringAfter(param, dataSep);
					dataMap.put(dataKey, request.getParameter(param));
				}
			}
			if(dataMap != null && !dataMap.isEmpty()){
				trackLog.setData(JSON.toJSONString(dataMap));
			}
			TrackLogUtils.removeLog(request.getParameter("logName"));
			bi = ImageUtils.getTrackLogEmptyImage();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(bi != null){
			return new BufferPicActionResult(bi);
		}else{
			return null;
		}
	}
}
