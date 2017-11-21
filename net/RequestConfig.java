package com.tg.cloudmanagement.net;

import android.app.Activity;
import android.os.Handler;

import com.tg.cloudmanagement.log.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class RequestConfig {
	public String hintString = "";
	public Object tag;
	public int requestCode;
	public boolean decrypt = false;
	public Activity activity;
	public Handler handler;
	/**
	 * 对httpGet有效
	 */
	public boolean failHint = false;
	
	public RequestConfig(Activity activity, int requestCode){
		initData(activity,requestCode);
	}
	
	public RequestConfig(Activity activity, int requestCode, String hintString){
		initData(activity,requestCode);
		this.hintString = hintString;
	}
	
	public void initData(Activity activity,int requestCode){
		this.activity = activity;
		this.requestCode = requestCode;
		if(activity == null){
			return;
		}
		tag = activity.toString();
		Class<?> clazs = activity.getClass();
		try {
			Method method = clazs.getMethod("getHandler", new  Class[ 0 ]);
			handler = (Handler)method.invoke(activity, new  Object[]{});
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Logger.logd("NoSuchMethodException");
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Logger.logd("IllegalArgumentException");
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Logger.logd("IllegalAccessException");
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Logger.logd("InvocationTargetException");
		}
	}
}
