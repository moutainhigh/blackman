package com.jx.blackmen.vo;

import java.util.Date;
import java.util.List;

import com.jx.service.workflow.entity.LvTaskEntity;

public class AgencyTaskVo {
	private List<LvTaskEntity> currentTaskNodes;//当前任务节点
	private String currentNode;
	private String datatime;//处理时间
	private String handleStateText;//处理状态
	private String handleNoteText;//处理备注
	private String companyName;//公司名称
	private List<LvTaskEntity> hisTaskNodes;//历史任务节点
	
	
	
	
	public String getDatatime() {
		return datatime;
	}
	public void setDatatime(String datatime) {
		this.datatime = datatime;
	}
	public String getHandleStateText() {
		return handleStateText;
	}
	public void setHandleStateText(String handleStateText) {
		this.handleStateText = handleStateText;
	}
	public String getHandleNoteText() {
		return handleNoteText;
	}
	public void setHandleNoteText(String handleNoteText) {
		this.handleNoteText = handleNoteText;
	}
	public List<LvTaskEntity> getCurrentTaskNodes() {
		return currentTaskNodes;
	}
	public void setCurrentTaskNodes(List<LvTaskEntity> currentTaskNodes) {
		this.currentTaskNodes = currentTaskNodes;
	}
	public List<LvTaskEntity> getHisTaskNodes() {
		return hisTaskNodes;
	}
	public void setHisTaskNodes(List<LvTaskEntity> hisTaskNodes) {
		this.hisTaskNodes = hisTaskNodes;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getCurrentNode() {
		return currentNode;
	}
	public void setCurrentNode(String currentNode) {
		this.currentNode = currentNode;
	}
	
}
