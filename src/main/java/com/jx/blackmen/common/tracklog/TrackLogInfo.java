package com.jx.blackmen.common.tracklog;

public class TrackLogInfo {
	
	private String useropid = ""; //微信的openID
	private String fromchannelid = ""; //渠道的ID
	private String fromchannelname = ""; //渠道名称
	private String useroption = "";     //用户操作
	private String trackbiz ="" ;     //预留字段
	
	private String logtimetemp = ""; //日志时间戳
	private String logversion = ""; //日志版本
	
	public TrackLogInfo() {}	

	private String getValue(String value){
		if(value==null||value.trim().equals("")){
			return "";
		}else{
			value=value.replaceAll("[:,\"{}]", "");
			return value;
		}
	}
	
	public String getLogStr() {	
		StringBuilder logJson = new StringBuilder();
		logJson.append("{");
		logJson.append("\"useropid\"").append(":\"").append(getValue(useropid)).append("\"").append(",");
		logJson.append("\"fromchannelid\"").append(":\"").append(getValue(fromchannelid)).append("\"").append(",");
		logJson.append("\"fromchannelname\"").append(":\"").append(getValue(fromchannelname)).append("\"").append(",");
		logJson.append("\"useroption\"").append(":\"").append(getValue(useroption)).append("\"").append(",");
		logJson.append("\"trackbiz\"").append(":\"").append(getValue(trackbiz)).append("\"").append(",");
		logJson.append("\"logtimetemp\"").append(":\"").append(getValue(logtimetemp)).append("\"").append(",");
		logJson.append("\"logversion\"").append(":\"").append(getValue(logversion)).append("\"").append(",");
		logJson.append("}");
		return logJson.toString();
	}
	
	
	public String getUseropid() {
		return useropid;
	}
	public void setUseropid(String useropid) {
		this.useropid = useropid;
	}
	public String getFromchannelid() {
		return fromchannelid;
	}
	public void setFromchannelid(String fromchannelid) {
		this.fromchannelid = fromchannelid;
	}
	public String getFromchannelname() {
		return fromchannelname;
	}
	public void setFromchannelname(String fromchannelid) {
		if("50".equals(fromchannelid)){
			this.fromchannelname = "58精准";
		}else if("51".equals(fromchannelid)){
			this.fromchannelname = "PC";
		}else if("52".equals(fromchannelid)){
			this.fromchannelname = "小车";
		}else if("53".equals(fromchannelid)){
			this.fromchannelname = "传单";
		}else if("54".equals(fromchannelid)){
			this.fromchannelname = "多盟-摆渡车";
		}else if("55".equals(fromchannelid)){
			this.fromchannelname = "挪车牌";
		}else if("56".equals(fromchannelid)){
			this.fromchannelname = "易拉宝";
		}else if("57".equals(fromchannelid)){
			this.fromchannelname = "QQ-墙体海报";
		}else if("58".equals(fromchannelid)){
			this.fromchannelname = "手提袋";
		}else if("59".equals(fromchannelid)){
			this.fromchannelname = "新物料";
		}else if("60".equals(fromchannelid)){
			this.fromchannelname = "优惠券-微信";
		}else if("61".equals(fromchannelid)){
			this.fromchannelname = "优惠券-地推A";
		}else if("62".equals(fromchannelid)){
			this.fromchannelname = "优惠券-PC";
		}else if("63".equals(fromchannelid)){
			this.fromchannelname = "优惠券-地推B";
		}else if("H5A".equals(fromchannelid)){
			this.fromchannelname = "优惠券-H5-A";
		}else if("H5B".equals(fromchannelid)){
			this.fromchannelname = "优惠券-H5-B";
		}else if("H5C".equals(fromchannelid)){
			this.fromchannelname = "优惠券-H5-C";
		}else if("H5D".equals(fromchannelid)){
			this.fromchannelname = "优惠券-H5-D";
		}else if("H5E".equals(fromchannelid)){
			this.fromchannelname = "优惠券-H5-E";
		}else if("PCA".equals(fromchannelid)){
			this.fromchannelname = "优惠券-PC-A";
		}else if("PCB".equals(fromchannelid)){
			this.fromchannelname = "优惠券-PC-B";
		}else if("PCC".equals(fromchannelid)){
			this.fromchannelname = "优惠券-PC-C";
		}else if("PCD".equals(fromchannelid)){
			this.fromchannelname = "优惠券-PC-D";
		}else if("PCE".equals(fromchannelid)){
			this.fromchannelname = "优惠券-PC-E";
		}else{
			this.fromchannelname = fromchannelid;
		}
	}
	public String getUseroption() {
		return useroption;
	}
	public void setUseroption(String useroption) {
		this.useroption = useroption;
	}
	public String getTrackbiz() {
		return trackbiz;
	}
	public void setTrackbiz(String trackbiz) {
		this.trackbiz = trackbiz;
	}
	public String getLogtimetemp() {
		return logtimetemp;
	}
	public void setLogtimetemp(String logtimetemp) {
		this.logtimetemp = logtimetemp;
	}
	public String getLogversion() {
		return logversion;
	}
	public void setLogversion(String logversion) {
		this.logversion = logversion;
	}
	
	

}
