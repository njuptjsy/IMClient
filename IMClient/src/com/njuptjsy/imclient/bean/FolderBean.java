package com.njuptjsy.imclient.bean;
/**
 * @author  JSY
 * @version：2015年9月17日 上午10:36:39
 * 类说明：代表图片选择器中的文件夹
 */
public class FolderBean {
	private String currentDirPath;
	private String firstImgPath;
	private String currentDirName;
	private int dirPicCount;
	
	public String getCurrentDirPath() {
		return currentDirPath;
	}
	
	public void setCurrentDirPath(String currentDirPath) {
		this.currentDirPath = currentDirPath;
		//根据文件路径获得文件夹名称
		int lastIndexOf = this.currentDirPath.lastIndexOf("/");
		this.currentDirName = this.currentDirPath.substring(lastIndexOf+1);
	}
	
	public String getFirstImgPath() {
		return firstImgPath;
	}
	
	public void setFirstImgPath(String firstImgPath) {
		this.firstImgPath = firstImgPath;
	}
	
	public String getCurrentDirName() {
		return currentDirName;
	}
	
	public int getDirPicCount() {
		return dirPicCount;
	}
	
	public void setDirPicCount(int dirPicCount) {
		this.dirPicCount = dirPicCount;
	}


}
