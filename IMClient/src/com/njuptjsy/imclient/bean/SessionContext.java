package com.njuptjsy.imclient.bean;
/**
 * 封装每个会话的相关信息
 * */
public class SessionContext{
	private int picResId;
	private String sessionName;
	private String lastMsg;
	private int unReadMsg;
	
	public SessionContext(int picResId, String sessionName, String lastMsg) {
		this.picResId = picResId;
		this.sessionName = sessionName;
		this.lastMsg = lastMsg;
	}

	public int getPicResId() {
		return picResId;
	}

	public void setPicResId(int picResId) {
		this.picResId = picResId;
	}

	public String getSessionName() {
		return sessionName;
	}

	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}

	public String getLastMsg() {
		return lastMsg;
	}

	public void setLastMsg(String lastMsg) {
		this.lastMsg = lastMsg;
	}

	public int getUnReadMsg() {
		return unReadMsg;
	}

	public void setUnReadMsg(int unReadMsg) {
		this.unReadMsg = unReadMsg;
	}
	
	
	
}