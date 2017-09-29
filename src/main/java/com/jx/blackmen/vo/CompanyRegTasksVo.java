package com.jx.blackmen.vo;

public class CompanyRegTasksVo {
	private String name;//企业名称
	private String procInstId;//流程id
	private String taskId;//任务id
	private String checkNameStatusKey;//核名结果
	private String time;//提交申请时间
	
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getProcInstId() {
		return procInstId;
	}
	public void setProcInstId(String procInstId) {
		this.procInstId = procInstId;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getCheckNameStatusKey() {
		return checkNameStatusKey;
	}
	public void setCheckNameStatusKey(String checkNameStatusKey) {
		this.checkNameStatusKey = checkNameStatusKey;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
}
