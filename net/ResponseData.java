package com.tg.cloudmanagement.net;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.SparseArray;
/**
 * 数据响应
 * @author Administrator
 *
 */
public class ResponseData {
	public int length = 0;
	private SparseArray<HashMap<String,Object>> sparseArray;
	
	public ResponseData(SparseArray<HashMap<String,Object>> sparseArray){
		this.sparseArray = sparseArray;	
		length = getCount();
	}
	
	public int getCount(){
		return sparseArray == null ? 0: sparseArray.size();
	}
	
	public void recycle(){
		HashMap<String, Object> map;
		for(int i = 0 ; i < length; i ++){
			map = sparseArray.get(i);
			if(map != null){
				map.clear();
			}
		}
		if(sparseArray != null){
			sparseArray.clear();
			sparseArray = null;
		}
	}
	
	public String getString(String key){
		return getString(0,key,"");
	}
	
	public String getString(String key,String defaultValue){
		return getString(0,key,defaultValue);
	}
	
	public String getString(int i,String key){
		return getString(i,key,"");
	}
	
	public String getString(int i,String key, String defaultValue){
		int count = getCount();
		if( count == 0 || i >= count){
			return defaultValue;
		}
		HashMap<String, Object> map = sparseArray.get(i);
		if(map == null || map.size() == 0){
			return defaultValue;
		}
		Object value = map.get(key);
		if(value != null){
			return value.toString();
		}else{
			return defaultValue;
		}
	}
	public int getInt(String key){
		return getInt(0,key,0);
	}
	public int getInt(String key,int defaultValue){
		return getInt(0,key,defaultValue);
	}
	
	public int getInt(int i,String key){
		return getInt(i,key,0);
	}
	
	public int getInt(int i,String key, int defaultValue){
		int count = getCount();
		if( count == 0 || i >= count){
			return defaultValue;
		}
		HashMap<String, Object> map = sparseArray.get(i);
		if(map == null || map.size() == 0){
			return defaultValue;
		}
		Object value = map.get(key);
		if(value != null && value instanceof Number){
			Number number = (Number)value;
			return number.intValue();
		}else if(value != null && value instanceof String){
			return parseInt((String)value, defaultValue);
		}else{
			return defaultValue;
		}
	}
	
	public static boolean parseBoolean(String str,boolean defaultValue){
		if(TextUtils.isEmpty(str)){
			return defaultValue;
		}
		boolean value = false;
		try{
			value = Boolean.parseBoolean(str);
		}catch(NumberFormatException e){
			value = defaultValue;
		}
		return value;
	}
	
	public static int parseInt(String str,int defaultValue){
		if(TextUtils.isEmpty(str)){
			return defaultValue;
		}
		int value = 0;
		try{
			value = Integer.parseInt(str);
		}catch(NumberFormatException e){
			value = defaultValue;
		}
		return value;
	}
	
	public static float parseFloat(String str,float defaultValue){
		if(TextUtils.isEmpty(str)){
			return defaultValue;
		}
		float value = 0;
		try{
			value = Float.parseFloat(str);
		}catch(NumberFormatException e){
			value = defaultValue;
		}
		return value;
	}
	
	public static long parseLong(String str,long defaultValue){
		if(TextUtils.isEmpty(str)){
			return defaultValue;
		}
		long value = 0;
		try{
			value = Long.parseLong(str);
		}catch(NumberFormatException e){
			value = defaultValue;
		}
		return value;
	}
	
	public static double parseDouble(String str,double defaultValue){
		if(TextUtils.isEmpty(str)){
			return defaultValue;
		}
		double value = 0;
		try{
			value = Double.parseDouble(str);
		}catch(NumberFormatException e){
			value = defaultValue;
		}
		return value;
	}
	public float getFloat(String key){
		return getFloat(0,key,0);
	}
	public float getFloat(String key,float defaultValue){
		return getFloat(0,key,defaultValue);
	}
	
	public float getFloat(int i,String key){
		return getFloat(i,key,0);
	}
	
	public float getFloat(int i,String key, float defaultValue){
		int count = getCount();
		if( count == 0 || i >= count){
			return defaultValue;
		}
		HashMap<String, Object> map = sparseArray.get(i);
		if(map == null || map.size() == 0){
			return defaultValue;
		}
		Object value = map.get(key);
		if(value != null && value instanceof Number){
			Number number = (Number)value;
			return number.floatValue();
		}else if(value != null && value instanceof String){
			return parseFloat((String)value, defaultValue);
		}else{
			return defaultValue;
		}
	}
	
	public double getDouble(String key){
		return getDouble(0,key,0);
	}
	
	public double getDouble(String key,double defaultValue){
		return getDouble(0,key,defaultValue);
	}
	
	public double getDouble(int i,String key){
		return getDouble(i,key,0);
	}
	
	public double getDouble(int i,String key, double defaultValue){
		int count = getCount();
		if( count == 0 || i >= count){
			return defaultValue;
		}
		HashMap<String, Object> map = sparseArray.get(i);
		if(map == null || map.size() == 0){
			return defaultValue;
		}
		Object value = map.get(key);
		if(value != null && value instanceof Number){
			Number number = (Number)value;
			return number.doubleValue();
		}else if(value != null && value instanceof String){
			return parseDouble((String)value, defaultValue);
		}else{
			return defaultValue;
		}
	}
	
	public long getLong(String key){
		return getLong(0,key,0);
	}
	
	public long getLong(String key,long defaultValue){
		return getLong(0,key,defaultValue);
	}
	
	public long getLong(int i,String key){
		return getLong(i,key,0);
	}
	
	public long getLong(int i,String key, long defaultValue){
		int count = getCount();
		if( count == 0 || i >= count){
			return defaultValue;
		}
		HashMap<String, Object> map = sparseArray.get(i);
		if(map == null || map.size() == 0){
			return defaultValue;
		}
		Object value = map.get(key);
		if(value != null && value instanceof Number){
			Number number = (Number)value;
			return number.longValue();
		}else if(value != null && value instanceof String){
			return parseLong((String)value, defaultValue);
		}else{
			return defaultValue;
		}
	}
	public boolean getBoolean(String key){
		return getBoolean(0,key,false);
	}
	
	public boolean getBoolean(String key,boolean defaultValue){
		return getBoolean(0,key,defaultValue);
	}
	
	public boolean getBoolean(int i,String key){
		return getBoolean(i,key,false);
	}
	
	public boolean getBoolean(int i,String key, boolean defaultValue){
		int count = getCount();
		if( count == 0 || i >= count){
			return defaultValue;
		}
		HashMap<String, Object> map = sparseArray.get(i);
		if(map == null || map.size() == 0){
			return defaultValue;
		}
		Object value = map.get(key);
		if(value != null && value instanceof Boolean){
			Boolean bValue = (Boolean)value;
			return bValue.booleanValue();
		}else if(value != null && value instanceof String){
			return parseBoolean((String)value, defaultValue);
		}else{
			return defaultValue;
		}
	}
	
	public Object getObject(String key){
		return getObject(0,key,null);
	}
	
	public Object getObject(String key,Object defaultValue){
		return getObject(0,key,defaultValue);
	}
	
	public Object getObject(int i,String key){
		return getObject(i,key,null);
	}
	
	public Object getObject(int i,String key, Object defaultValue){
		int count = getCount();
		if( count == 0 || i >= count){
			return defaultValue;
		}
		HashMap<String, Object> map = sparseArray.get(i);
		if(map == null || map.size() == 0){
			return defaultValue;
		}
		Object value = map.get(key);
		return value;
	}
	
	public JSONObject getJSONObject(String key){
		return getJSONObject(0,key,null);
	}
	
	public JSONObject getJSONObject(String key,JSONObject defaultValue){
		return getJSONObject(0,key,defaultValue);
	}
	
	public JSONObject getJSONObject(int i,String key){
		return getJSONObject(i,key,null);
	}
	
	public JSONObject getJSONObject(int i,String key, JSONObject defaultValue){
		int count = getCount();
		if( count == 0 || i >= count){
			return defaultValue;
		}
		HashMap<String, Object> map = sparseArray.get(i);
		if(map == null || map.size() == 0){
			return defaultValue;
		}
		Object value = map.get(key);
		if(value != null && value instanceof JSONObject){
			JSONObject jsonObj = (JSONObject)value;
			return jsonObj;
		}else{
			return defaultValue;
		}
	}
	
	public JSONArray getJSONArray(String key){
		return getJSONArray(0,key,null);
	}
	
	public JSONArray getJSONArray(String key,JSONArray defaultValue){
		return getJSONArray(0,key,defaultValue);
	}
	
	public JSONArray getJSONArray(int i,String key){
		return getJSONArray(i,key,null);
	}
	
	public JSONArray getJSONArray(int i,String key, JSONArray defaultValue){
		int count = getCount();
		if( count == 0 || i >= count){
			return defaultValue;
		}
		HashMap<String, Object> map = sparseArray.get(i);
		if(map == null || map.size() == 0){
			return defaultValue;
		}
		Object value = map.get(key);
		if(value != null && value instanceof JSONArray){
			JSONArray jsonArray = (JSONArray)value;
			return jsonArray;
		}else{
			return defaultValue;
		}
	}
}
