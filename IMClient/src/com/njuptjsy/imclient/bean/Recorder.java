package com.njuptjsy.imclient.bean;
/**
 * @author  JSY
 * @version：2015年9月17日 上午11:36:15
 * 类说明：录音文件的bean
 */
public class Recorder{
	private float time;
	private String filePath;
	public Recorder(float time, String filePath) {
		super();
		this.time = time;
		this.filePath = filePath;
	}

	public float getTime() {
		return time;
	}

	public void setTime(float time) {
		this.time = time;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

}