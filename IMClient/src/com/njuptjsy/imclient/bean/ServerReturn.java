package com.njuptjsy.imclient.bean;


/**
 * @author  JSY
 * @version：2015年8月29日 下午8:22:13
 * 类说明：将服务器返回结果映射为chatContext
 */
public class ServerReturn {
	private int code;
	private String text;
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}


}
