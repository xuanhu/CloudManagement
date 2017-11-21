package com.tg.cloudmanagement.inter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.os.Handler;
import android.text.TextUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

public class MyObjectAnimtor {
	public static final int MSG_START = 1;
	public static final int MSG_UPDATE = 2;
	public static final int MSG_END = 3;
	private long duration = 300;
	private String property;
	private Object target;
	private String setMethodName;
	private long startTime;
	private MyObjectAnimatorListener listener;
	private float startValue;
	private float endValue;
	private float offsetValue;
	private boolean isIntValue = true;
	private Interpolator interpolator = new LinearInterpolator();
	private Handler hand = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
				case MSG_START:
					startTime = System.currentTimeMillis();
					if(isIntValue){
						invokeSetmethod((int)startValue);
					}else{
						invokeSetmethod(startValue);
					}
					if(listener != null){
						listener.animatorStart(MyObjectAnimtor.this);
					}
					hand.sendEmptyMessage(MSG_UPDATE);
					break;
				case MSG_END:
					if(isIntValue){
						invokeSetmethod((int)endValue);
					}else{
						invokeSetmethod(endValue);
					}
					if(listener != null){
						listener.animatorEnd(MyObjectAnimtor.this);
					}
					break;
				case MSG_UPDATE:
					long currentTime = System.currentTimeMillis();
					long passTime = currentTime - startTime;
					if(passTime >= duration){
						hand.removeMessages(MSG_UPDATE);
						hand.sendEmptyMessage(MSG_END);
						return; 
					}
					float perf = passTime*1.0f / duration;
					float value;
					value = startValue + offsetValue * interpolator.getInterpolation(perf);
					if(isIntValue){
						value = (int)value;
						invokeSetmethod((int)value);
					}else{
						invokeSetmethod(value);
					}
					if(listener != null){
						listener.onUpdate(MyObjectAnimtor.this, value);
					}
					hand.sendEmptyMessageDelayed(MSG_UPDATE, 40);
					break;
			}
		}
	};
	
	public static MyObjectAnimtor ofFloat(float startValue, float endValue){
		return ofFloat(null,null,startValue,endValue);
	}
	public static MyObjectAnimtor ofInt(int startValue, int endValue){
		return ofInt(null,null,startValue,endValue);
	}
	
	public static MyObjectAnimtor ofFloat(Object target, String property, float startValue, float endValue){
		MyObjectAnimtor anim = new MyObjectAnimtor();
		anim.isIntValue = false;
		anim.target = target;
		anim.property = property;
		anim.startValue = startValue;
		anim.endValue = endValue;
		anim.offsetValue = endValue - startValue;
		anim.setMethodName = anim.getMethodName(property);
		return anim;
	}
	
	public static MyObjectAnimtor ofInt(Object target, String property, int startValue, int endValue){
		MyObjectAnimtor anim = ofFloat(target,property,startValue,endValue);
		anim.isIntValue = true;
		return anim;
	}
	
	public void setInterpolator(Interpolator i){
		interpolator = i ;
	}
	
	public void setDuration(long d){
		duration = d;
	}
	
	private String getMethodName(String property){
		if(TextUtils.isEmpty(property)){
			return null;
		}
		String strOther = "";
		String strFrist = property.substring(0, 1);
		if(property.length() > 1){
			strOther = property.substring(1);
		}
		String upperName = strFrist.toUpperCase();
		return "set"+upperName + strOther;
	}
	
	public void invokeSetmethod(int value){
		if(TextUtils.isEmpty(setMethodName)){
			return;
		}
		Class<?> objClass = target.getClass();
		try {
			Method method = objClass.getMethod(setMethodName, int.class);
			method.invoke(target, value);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void invokeSetmethod(float value){
		if(TextUtils.isEmpty(setMethodName)){
			return;
		}
		Class<?> objClass = target.getClass();
		try {
			Method method = objClass.getMethod(setMethodName, float.class);
			method.invoke(target, value);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void start(){
		hand.sendEmptyMessage(MSG_START);
	}
	
	public void forceEnd(){
		hand.removeCallbacksAndMessages(null);
		hand.sendEmptyMessage(MSG_END);
	}
	
	public void cancel(){
		hand.removeCallbacksAndMessages(null);
	}
	
	
	public void setAnimtorListener(MyObjectAnimatorListener l){
		listener = l;
	}
}
