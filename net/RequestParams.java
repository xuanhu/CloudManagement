package com.tg.cloudmanagement.net;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

public class RequestParams {
	private JSONObject jObj = new JSONObject();
	public RequestParams(){
	}
	
	public RequestParams(String key,Object value){
		put(key,value);
	}
	
	public RequestParams put(String key, Object value){
		if(TextUtils.isEmpty(key)){
			return this;
		}
		try {
			jObj.put(key, value);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return jObj.toString();
	}
	
	
	public JSONObject toJsonObject() {
		// TODO Auto-generated method stub
		return jObj;
	}
	
	public HashMap<String, Object> toHashMap() {
		// TODO Auto-generated method stub
		return HttpTools.getMap(jObj);
	}
	public HashMap<String, String> toHashMapString() {
		// TODO Auto-generated method stub
		return HttpTools.getMapString(jObj);
	}
}
