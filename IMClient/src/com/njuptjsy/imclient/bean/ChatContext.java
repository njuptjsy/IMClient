package com.njuptjsy.imclient.bean;

import java.util.Date;

/**
 * @author  JSY
 * @version：2015年8月29日 下午8:18:32
 * 类说明：封装每个会话中的聊天信息的bean
 */
public class ChatContext {
	private String name;
	private String msg;
	private Type type;
	private Date date;
	
	
	
	public ChatContext() {
		super();
	}

	public ChatContext(String msg, Type type, Date date) {
		super();
		this.msg = msg;
		this.type = type;
		this.date = date;
	}

	public enum Type{
		INCOMING,OUTCOMING
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
}
