package com.njuptjsy.imclient.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.Date;

import android.util.Log;

import com.google.gson.Gson;
import com.njuptjsy.imclient.bean.ChatContext;
import com.njuptjsy.imclient.bean.ChatContext.Type;
import com.njuptjsy.imclient.bean.ServerReturn;

/**
 * @author  JSY
 * @version：2015年8月29日 下午6:15:01
 * 类说明：发送http请求的工具类
 */
public class HttpUtils {
	private static final String URL = "http://www.tuling123.com/openapi/api";
	private static final String API_KEY = "d5636633f2fa07b34dbd116e393ba45c";
	
	public static String doGet(String msg) {
		String result = "";
		String url = setParams(msg);
		InputStream serverReturnIs = null;
		ByteArrayOutputStream baos = null;
		try {
			java.net.URL urlNet = new java.net.URL(url);
			HttpURLConnection connection = (HttpURLConnection) urlNet.openConnection();
			connection.setReadTimeout(5000);
			connection.setConnectTimeout(5000);
			connection.setRequestMethod("GET");

			serverReturnIs = connection.getInputStream();//得到服务器返回的流
			int length = -1;
			byte[] buffer = new byte[128];
			baos = new ByteArrayOutputStream();//将输入流中的数据写到输出流
			while ((length = serverReturnIs.read(buffer)) != -1) {
				baos.write(buffer, 0, length);
			}
			baos.flush();
			result = new String(baos.toByteArray());

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (serverReturnIs != null) {
				try {
					serverReturnIs.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return result;
	}
	private static String setParams(String msg) {
		 
		String url = "";
		try {
			// String getURL = "http://www.tuling123.com/openapi/api?key=" + APIKEY + "&info=" + INFO;
			url = URL + "?key=" + API_KEY + "&info=" + URLEncoder.encode(msg, "UTF_8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return url;
	}
	/**
	 * 发送消息，得到放回消息并封装在chatContext中
	 * */
	public static ChatContext sendMessage(String msg) {
		ChatContext chatContext = new ChatContext();
		String jsonResult = doGet(msg);
		Log.i("HttpUtils.sendMessage", "jsonResult"+jsonResult);
		Gson gson = new Gson();
		ServerReturn serverReturn = null;
		try {
			serverReturn = gson.fromJson(jsonResult, ServerReturn.class);
			chatContext.setMsg(serverReturn.getText());
			Log.i("HttpUtils.sendMessage", ""+serverReturn.getText());
		} catch (Exception e) {
			chatContext.setMsg("Opss..与服务器通信出现异常...");
			e.printStackTrace();
		}
		chatContext.setDate(new Date());
		chatContext.setType(Type.INCOMING);
		return chatContext;
	}
}
