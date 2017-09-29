package com.jx.blackmen.common.tracklog;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.jx.blackface.tools.blackTrack.TrackLogUtils;
import com.jx.blackface.tools.blackTrack.entity.TrackInfoEntity;

public class TrackLogUtil {
	/**
	 * 确保线程安全
	 */
	static ThreadLocal<TrackLogInfo> trackLogThreadLocal = new ThreadLocal<TrackLogInfo>();

	public static TrackLogInfo getTrackLog() {
		TrackLogInfo trackLog = trackLogThreadLocal.get();
		if (trackLog == null) {   
			trackLog = new TrackLogInfo();
			trackLogThreadLocal.set(trackLog);
		}
		return trackLog;
	}
	
	
	public static void initTrackLog(String useropid,String fromchannelid,String useroption,String trackbiz) {
		try {
			TrackLogInfo trackLogInfo = getTrackLog();
			//useropid
			trackLogInfo.setUseropid(useropid);
			//fromchannelid
			trackLogInfo.setFromchannelid(fromchannelid);
			//fromchannelname
			trackLogInfo.setFromchannelname(fromchannelid);
			//useroption
			trackLogInfo.setUseroption(useroption);
			//trackbiz
			trackLogInfo.setTrackbiz(trackbiz);
			//logtimetemp
//			trackLogInfo.setKeyiddesc(keyiddesc);
			//logversion
			trackLogInfo.setLogversion("wxlog0.0.1");
		} catch (Exception e) {    
			e.printStackTrace();
		}finally {
			removeLog(trackbiz);
		}
	}
	
	private static void removeLog(String trackbiz) {
		TrackLogInfo trackLogInfo = null;
		try {
			trackLogInfo = trackLogThreadLocal.get();
			if (trackLogInfo != null) {
				trackLogInfo.setLogtimetemp(String.valueOf(System.currentTimeMillis()));
				String logStr = trackLogInfo.getLogStr();
				System.out.println(logStr);
				TrackLogLocalRecord localRecord = new TrackLogLocalRecord(trackbiz);
				localRecord.log(logStr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (trackLogInfo != null) {
				trackLogThreadLocal.remove();
			}
		}
	}
	
	
	public static void addTrackLog(String logName,HttpServletRequest request,HttpServletResponse response,String moudle,Map<String, String> dataMap){
		try {
			TrackInfoEntity trackLog = TrackLogUtils.getTrackLog(request, response);
			// 模块名称
			trackLog.setMoudle(moudle);
			// 用户ID
			trackLog.setUserId(request.getParameter("userId"));
			// 引用页面
			trackLog.setPageUrl(request.getParameter("pageUrl"));
			// 来源
			trackLog.setFrom(request.getParameter("from"));
			// 业务数据
			if(dataMap != null && !dataMap.isEmpty()){
				trackLog.setData(JSON.toJSONString(dataMap));
			}
			TrackLogUtils.removeLog(logName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
//	public static void main(String[] args) {
//		PropertyConfigurator.configure(ClassLoader.getSystemResource("META-INF/log4j.properties"));
//		//PropertyConfigurator.configure(ClassLoader.getSystemResource("log4j.properties"));
//		TrackLogUtil.initTrackLog("1234", "60", "60", "E");
//	}
}
