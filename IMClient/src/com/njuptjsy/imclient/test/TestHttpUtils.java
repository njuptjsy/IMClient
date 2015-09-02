package com.njuptjsy.imclient.test;

import com.njuptjsy.imclient.utils.HttpUtils;

import android.test.AndroidTestCase;
import android.util.Log;

/**
 * @author  JSY
 * @version：2015年8月29日 下午6:37:53
 * 类说明：测试http工具
 */
public class TestHttpUtils extends AndroidTestCase{
	public void testSendInfo(){
		String result =  HttpUtils.doGet("你知道金思宇吗");
		Log.i("TestHttpUtils:testSendInfo", result);
		result =  HttpUtils.doGet("你知道蔡晶吗");
		Log.i("TestHttpUtils:testSendInfo", result);
		result =  HttpUtils.doGet("你好");
		Log.i("TestHttpUtils:testSendInfo", result);
		result =  HttpUtils.doGet("你真美");
		Log.i("TestHttpUtils:testSendInfo", result);
		result =  HttpUtils.doGet("亲，包邮吗");
		Log.i("TestHttpUtils:testSendInfo", result);
	}
}
