package com.tg.cloudmanagement.database;

import org.json.JSONObject;

import com.tg.cloudmanagement.net.HttpTools;
import com.tg.cloudmanagement.net.ResponseData;


import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
/**
 * 共享参数工具类
 * @author Administrator
 *
 */
public class SharedPreferencesTools {
	public static final String PREFERENCES_NAME = "wisdomPark_map";
	private static SharedPreferences getSysShare(Context con){
    	return con.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }
    
    public static String getSysMapStringValue(Context con, String key){
    	return getSysShare(con).getString(key, "");
    }
    
    public static boolean getSysMapBooleanValue(Context con, String key,boolean defValue){
    	return getSysShare(con).getBoolean(key, defValue);
    }
    
    public static long getSysMapLongValue(Context con, String key,long defValue){
    	return getSysShare(con).getLong(key, defValue);
    }
    
    public static int getSysMapIntValue(Context con, String key,int defValue){
    	return getSysShare(con).getInt(key, defValue);
    }
    
    public static float getSysMapFloatValue(Context con, String key,float defValue){
    	return getSysShare(con).getFloat(key, defValue);
    }
    
    public static void saveSysMap(Context con, String key, String value){
    	if(TextUtils.isEmpty(key)){
    		return;
    	}
    	getSysShare(con).edit().putString(key, value).commit();
    }
    
    public static void saveSysMap(Context con, String key, boolean result){
    	if(TextUtils.isEmpty(key)){
    		return;
    	}
    	getSysShare(con).edit().putBoolean(key, result).commit();
    }
    
    public static void saveSysMap(Context con, String key, long result){
    	if(TextUtils.isEmpty(key)){
    		return;
    	}
    	getSysShare(con).edit().putLong(key, result).commit();
    }
    
    public static void saveSysMap(Context con, String key, int result){
    	if(TextUtils.isEmpty(key)){
    		return;
    	}
    	getSysShare(con).edit().putInt(key, result).commit();
    }
    
    public static void saveSysMap(Context con, String key, float result){
    	if(TextUtils.isEmpty(key)){
    		return;
    	}
    	getSysShare(con).edit().putFloat(key, result).commit();
    }
    
    public static void saveUserInfoJson(Context con,JSONObject jsonObj){
    	saveSysMap(con, "userInfo", jsonObj.toString());
    }
    
    public static void clearUserId(Context con){
    	saveSysMap(con, "userInfo", "");
    }
    
    public static ResponseData getUserInfo(Context con){
    	String jsonString = getSysMapStringValue(con, "userInfo");
    	return HttpTools.parseUserInfoJsonString(jsonString);
    }
}
