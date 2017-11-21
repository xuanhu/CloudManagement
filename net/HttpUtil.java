package com.tg.cloudmanagement.net;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tg.cloudmanagement.log.Logger;
import com.tg.cloudmanagement.object.HttpResultObj;
import com.tg.cloudmanagement.object.ImageRequestObj;
import com.tg.cloudmanagement.object.PostImageRequestObj;
import com.tg.cloudmanagement.util.Tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

public class HttpUtil {
	public static final String URL1 = "http://four.cug2313.com/api.php";//线上
	public static final String URL2 = "http://192.168.10.250/~ouxy/four.cug2313.com/html/api.php";//线下版本
	public static final String URL3 = "http://test.four.cug2313.com/api.php";//线上测试版
	public static String URL = URL1;
	public static final String KEY_HINT_MSG = "hint_message";
	public static final String KEY_REQUEST_POSITION = "request_position";
	public static final String REQUEST_JSON_KEY = "data";
	public static final String FIELD_SELECT_DATA = "selectData";
	public static final String FIELD_DATA_COUNT = "dataCount";
	public static final String FIELD_STATE = "state"; 
	public static final String FIELD_MESSAGE = "message";
	public static final String FIELD_MESSAGE_DATA = "messageData";
	public static final String FIELD_REQUEST_STATE = "requestState";
	public static final String FIELD_STATE_CODE = "stateCode";
	public static final int CONNECTION_TIMEOUT = 10*1000;
	public static final int SO_TIMEOUT = 30*1000;
	public static final int TYPE_NONE = -1;
	public static final int TYPE_WIFI = 1;
	public static final int TYPE_CMWAP = 2;
	public static final int TYPE_CMNET= 3;
	 
	public static final int HTTP_CANCEL= 3;
	public static final int HTTP_EXCEPTION_CODE = 4;
	public static final int HTTP_SUCCESS_CODE = 5;
	public static final int REQUEST_START_CODE = 6;
	
	public static final int REQUEST_FIND_PWD_GET_CODE = 10;
	public static final int REQUEST_LAND = 11;
	public static final int REQUEST_REGISTER = 12;
	public static final int REQUEST_GET_PWD = 13;
	public static final int REQUEST_REGIST_GET_CODE = 14;
	public static final int REQUEST_ADD_INFO = 15;
	public static final int REQUEST_GET_PROTOCOL = 16;
	public static final int REQUEST_UPLOAD_IMAGE = 17;
	public static final int REQUEST_GET_SKILL = 18;
	public static final int REQUEST_POST_FEED = 19;
	public static final int REQUEST_MODIFY_ADDRESS = 20;
	public static final int REQUEST_GET_USER_INFO = 21;
	public static final int REQUEST_GET_BITMAP = 22;
	public static final int REQUEST_GET_STATISTICS = 23;
	public static final int REQUEST_MODIFY_PASSWD = 24;
	public static final int REQUEST_EXIT_ACCOUNT= 25;
	public static final int REQUEST_GET_WALLET_INFO= 26;
	public static final int REQUEST_UPGRADE_COMPANY= 27;
	public static final int REQUEST_MODIFY_BANK_PASSWD = 28;
	public static final int REQUEST_GET_NOTIFICATION_CONTENT = 29;
	public static final int REQUEST_SET_PUSH = 30;
	public static final int REQUEST_SET_CASH_PWD = 31;
	public static final int REQUEST_ADD_BANK_CARD = 32;
	public static final int REQUEST_GET_BANK_LIST= 33;
	public static final int REQUEST_CHECK_CASH_PWD= 34; 
	public static final int REQUEST_GET_PRODUCT= 35;
	public static final int REQUEST_GET_INSTALL_PRICE= 36;
	public static final int REQUEST_GET_REPAIRER_PRICE= 37;
	public static final int REQUEST_GET_NEARBY_ORDER_LIST= 38;
	public static final int REQUEST_GET_MY_ORDER_LIST= 39;
	public static final int REQUEST_RECEIVE_ORDER= 40;
	public static final int REQUEST_SET_CALL_ORDER= 41;
	public static final int REQUEST_GET_ORDER_DETAIL= 42;
	public static final int REQUEST_SET_PROMISE_TIME= 43;
	public static final int REQUEST_GET_SERVER_LIST= 44;
	public static final int REQUEST_SET_SERVER_LIST= 45;
	public static final int REQUEST_ADD_FITING_LIST= 46;
	public static final int REQUEST_GET_TRACK_LIST= 47;
	public static final int REQUEST_GET_FITING_LIST= 48;
	public static final int REQUEST_GET_TRANSACTION_LIST= 49;
	public static final int REQUEST_GET_MY_BANK_LIST= 50;
	public static final int REQUEST_DELETE_BANK = 51;
	public static final int REQUEST_CASH_MONEY = 52;
	public static final int REQUEST_COMPLETE_ORDER = 53;
	public static final int REQUEST_BACK_FITTING = 54;
	public static final int REQUEST_EXPRESS_COMPANY = 55;
	public static final int REQUEST_SELECT_NEW_PUSH = 56;
	public static final int REQUEST_DELETE_NOTIFICATION = 57;
	public static final int REQUEST_CHECK_VERIFICATION_CODE = 58;
	public static final int REQUEST_GET_HTTP_URL_CODE = 59;
	public static final int REQUEST_GET_CUG_URL_CODE = 60;
	public static final int REQUEST_GET_HELP_INFO_CODE = 61;
	public static final int REQUEST_GET_FITTING_NAME_CODE = 62;
	public static final int REQUEST_SET_DELAY_PROMISE = 63;
	public static final int REQUEST_GET_CANCEL_INFO_CODE = 64;
	public static final int REQUEST_SET_CANCEL_CODE = 65;
	public static final int REQUEST_GET_ADDRESS_CODE = 66;
	public static final int REQUEST_GET_TRANSACTION_TRACE_CODE = 67;
	public static final int REQUEST_SET_READ_MESSAGE_CODE = 68;
	public static final int REQUEST_GET_USER_INFO_CODE = 69;
	public static final int REQUEST_GET_SERVICE_TIME_CODE = 70;
	public static final int REQUEST_SET_INFO_CODE = 71;
	public static final int REQUEST_GET_DELAY_REASONS = 72;
	public static final int REQUEST_SET_APPRAISE_AGAIN = 73;
	public static final int REQUEST_GET_APPRAISE_REASON = 74;
	public static final int REQUEST_FRESH_ORDER_DETAIL = 75;
	public static final int REQUEST_SET_CHILD_ACCOUNT = 76;
	public static final int REQUEST_SET_ORDER_INFO = 77;
	public static final int REQUEST_GET_HELP_INFO = 78;
	public static final int REQUEST_GET_HELP_TITLE= 79;
	public static final int REQUEST_GET_COMPANY_LIST= 80;
	public static final int REQUEST_GET_AD_LIST= 81;
	public static final int REQUEST_GET_NEWS_URL_CODE = 82;
	public static final int REQUEST_SET_LEAVE_MESSAGE_CODE = 83;
	public static HashMap<Integer,HttpResultObj> resultMap = new HashMap<Integer,HttpResultObj>();
	private static HashMap<Integer,HttpClient> httpClientMap = new HashMap<Integer,HttpClient>();
	private  static JSONObject jObj;
	private static Object lock = new Object();
	private static ConnectivityManager connManager;
	private static boolean cancel = false;
	private static int requestPosition = 0;

	public static HttpResultObj getHttpResultObj(int requestCode){
		return resultMap.get(Integer.valueOf(requestCode));
	}
	
	public static void removeHttpResult(int requestCode){
		Integer key = Integer.valueOf(requestCode);
		if(resultMap.containsKey(key)){
			resultMap.remove(key);
		}
	}
	
	public static int getServerDataCount(int requestCode){
		HttpResultObj  resultObj = getHttpResultObj(requestCode);
		if(resultObj != null){
			HashMap<String, String> map = resultObj.headMap;
			if(map != null){
				String countStr = map.get(FIELD_DATA_COUNT);
				if(!TextUtils.isEmpty(countStr)){
					try{
						int count = Integer.parseInt(countStr);
						return count;
					}catch(NumberFormatException e){
						e.printStackTrace();
					}
				}
			}
		}
		return 0;
	}
	
	public static int getDataSize(int requestCode){
		HttpResultObj  resultObj = getHttpResultObj(requestCode);
		if(resultObj != null){
			ArrayList<HashMap<String, String>> list = resultObj.bodyList;
			if(list != null){
				return list.size();
			}
		}
		return 0;
	}
	public static HashMap<String,String> getDataMap(int requestCode, int index){
		HttpResultObj  resultObj = getHttpResultObj(requestCode);
		if(resultObj != null){
			ArrayList<HashMap<String, String>> list = resultObj.bodyList;
			if(list != null){
				if(index >= 0 && index < list.size()){
					return list.get(index);
				}
			}
		}
		return null;
	}
	
	public static String getString(int requestCode, int index, String key){
		HashMap<String,String> map = getDataMap(requestCode,index);
		String value ="";
		if(map == null || map.size() == 0){
			return value;
		}
		value = map.get(key);
		if(value == null){
			value = "";
		}
		return value;
	}
	
	public static float getFloat(int requestCode, int index, String key){
		HashMap<String,String> map = getDataMap(requestCode,index);
		float value = 0.0f;
		if(map == null || map.size() == 0){
			return 0.0f;
		}
		String strValue = map.get(key);
		if(strValue == null || strValue.length() == 0){
			strValue = "0.0";
		}
		try{
			value = Float.parseFloat(strValue);
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		return value;
	}
	
	public static long getLong(int requestCode, int index, String key){
		HashMap<String,String> map = getDataMap(requestCode,index);
		long value = 0L;
		if(map == null || map.size() == 0){
			return value;
		}
		String strValue = map.get(key);
		if(strValue == null || strValue.length() == 0){
			strValue = "0";
		}
		try{
			value = Long.parseLong(strValue);
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		return value;
	}
	
	
	public static int getInt(int requestCode, int index, String key,int defaultValue){
		HashMap<String,String> map = getDataMap(requestCode,index);
		int value = defaultValue;
		if(map == null || map.size() == 0){
			return defaultValue;
		}
		String strState = map.get(key);
		if(strState == null || strState.length() == 0){
			return defaultValue;
		}
		try{
			value = Integer.parseInt(strState);
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		return value;
	}
	
	public static int getInt(int requestCode, int index, String key){
		HashMap<String,String> map = getDataMap(requestCode,index);
		int value = 0;
		if(map == null || map.size() == 0){
			return 0;
		}
		String strState = map.get(key);
		if(strState == null || strState.length() == 0){
			strState = "0";
		}
		try{
			value = Integer.parseInt(strState);
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		return value;
	}
	
	public static int getState(int requestCode){
		HttpResultObj  resultObj = getHttpResultObj(requestCode);
		if(resultObj == null ){
			return 0;
		}
		HashMap<String, String> map = resultObj.headMap;
		if(map == null){
			return 0;
		}
		String msgData = map.get(FIELD_MESSAGE_DATA);
		int state = 0;
		if(!TextUtils.isEmpty(msgData)){
			JSONObject jobj;
			try {
				jobj = new JSONObject(msgData);
				state = jobj.getInt(FIELD_STATE);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return state;
	}
	
	public static String getMessage(int requestCode){
		String message = "";
		HttpResultObj  resultObj = getHttpResultObj(requestCode);
		if(resultObj == null ){
			return message;
		}
		HashMap<String, String> map = resultObj.headMap;
		if(map == null ){
			return message;
		}
		String msgData = map.get(FIELD_MESSAGE_DATA);
		if(TextUtils.isEmpty(msgData)){
			return message;
		}
		JSONObject jobj;
		try {
			jobj = new JSONObject(msgData);
			message = jobj.getString(FIELD_MESSAGE);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return message;
	}
	
	public static int getStateCode(int requestCode){
		HttpResultObj  resultObj = getHttpResultObj(requestCode);
		if(resultObj == null ){
			return 0;
		}
		HashMap<String, String> map = resultObj.headMap;
		if(map == null){
			return 0;
		}
		String msgData = map.get(FIELD_REQUEST_STATE);
		int state = 0;
		if(!TextUtils.isEmpty(msgData)){
			JSONObject jobj;
			try {
				jobj = new JSONObject(msgData);
				state = jobj.getInt(FIELD_STATE_CODE);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return state;
	}
	
	public static String getRequestMessage(int requestCode){
		String message = "";
		HttpResultObj  resultObj = getHttpResultObj(requestCode);
		if(resultObj == null ){
			return message;
		}
		HashMap<String, String> map = resultObj.headMap;
		if(map == null ){
			return message;
		}
		String msgData = map.get(FIELD_REQUEST_STATE);
		if(TextUtils.isEmpty(msgData)){
			return message;
		}
		JSONObject jobj;
		try {
			jobj = new JSONObject(msgData);
			message = jobj.getString(FIELD_MESSAGE);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return message;
	}
	
	
	public static String getDataString(int requestCode){
		String str ="";
		HttpResultObj  resultObj = getHttpResultObj(requestCode);
		if(resultObj == null ){
			return str;
		}
		HashMap<String, String> headMap = resultObj.headMap;
		if(headMap == null || headMap.size() == 0){
			return str;
		}
		str = headMap.get(FIELD_SELECT_DATA);
		if(str == null){
			str = "";
		}
		return str;
	}
	
	public static void initJSON(String apiName,boolean needUserToken) {
		jObj = new JSONObject();
		try {
			jObj.put("apiName", apiName);
			jObj.put("deviceSN", Tools.getDeviceSN(Tools.mContext));
			jObj.put("deviceType", 0);
			if(needUserToken){
				/*if(TextUtils.isEmpty(UserInfo.userToken)){
					UserInfo.userToken = Tools.getSysMapStringValue(Tools.mContext, Tools.KEY_USER_TOKEN);
				}
				jObj.put("userToken", UserInfo.userToken);*/
			}
			jObj.put(REQUEST_JSON_KEY,new JSONObject());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void putDataToJson(String key, Object value) {
		try {
			jObj.optJSONObject(REQUEST_JSON_KEY).put(key, value);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void parserJsonData(String json, int requestCode){
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> headMap = new HashMap<String, String>();
		try {
			JSONObject obj = new JSONObject(json);
			Iterator keys = obj.keys();
			if(keys == null || !keys.hasNext()){
				return;
			}
			String key;
			JSONArray dataArray = null;
			JSONObject dataJson = null;
			Object o;
			String value;
			while (keys.hasNext()) {
				key = (String) keys.next();
				if(key.equals(FIELD_SELECT_DATA)){
					headMap.put(key, obj.getString(key));
					o = obj.get(key);
					if(o instanceof JSONArray){
						dataArray = (JSONArray)o;
					}else if(o instanceof JSONObject){
						dataJson = (JSONObject)o;
					}
				}else{
					value = obj.getString(key);
					headMap.put(key, value);
				}
			}
			if(dataArray != null){
				int len = dataArray.length();
				JSONObject obj1;
				for(int i = 0; i< len; i ++){
					obj1 = dataArray.getJSONObject(i);
					HashMap<String,String> map = parseJSONObjToList(obj1);
					if(map != null && map.size() > 0){
						list.add(map);
					}
				}
			}else if(dataJson != null){
				HashMap<String,String> map = parseJSONObjToList(dataJson);
				if(map != null && map.size() > 0){
					list.add(map);
				}
			}else{
				
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		resultMap.put(Integer.valueOf(requestCode), new HttpResultObj(headMap, list));
	}
	
	public static HashMap<String,String> parseJSONObjToList(JSONObject obj){
		Iterator keys2 = obj.keys();
		if(keys2 == null || !keys2.hasNext()){
			return null;
		}
		HashMap<String,String> map = new HashMap<String,String>();
		String values;
		String key;
		while (keys2.hasNext()) {
			key = (String) keys2.next();
			try {
				values = obj.getString(key);
				map.put(key, values);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return map;
	}
	
	public static void cancelRequest(int requestPosition){
		synchronized(lock){
			if(httpClientMap.containsKey(requestPosition)){
				httpClientMap.get(requestPosition).getConnectionManager().shutdown();
				httpClientMap.remove(requestPosition);
			}
		}
	}
	
	public static void httpRequest(String message,final Handler hand, final int requestCode) {
		requestPosition ++;
		final int rqtPosition = requestPosition;
		final JSONObject jsonObj = jObj;
		final Message msg = Message.obtain();
		msg.arg1 = requestCode;
		Bundle bund = new Bundle();
		bund.putString(KEY_HINT_MSG, message);
		bund.putInt(KEY_REQUEST_POSITION, rqtPosition);
		msg.setData(bund);
		if(!isConnect(Tools.mContext)){
			Logger.logd("no net !!!!!!!!!!!!!!!!");
			msg.what = HTTP_EXCEPTION_CODE;
			msg.obj = "无网络,请检查网络";
			if(hand != null){
				hand.sendMessage(msg);
			}
			return;
		}
		if(jsonObj == null){
			msg.what = HTTP_EXCEPTION_CODE;
			msg.obj = "服务器数据有误，无法解析";
			hand.sendMessage(msg);
			return;
		}
		final HttpPost httpPost = new HttpPost(URL);
		String cSrc = jsonObj.toString();

		Logger.logd("src_jsonObj = \n" + cSrc);

		// 设置HTTP POST请求参数必须用NameValuePair对象
		final List<NameValuePair> params = new ArrayList<NameValuePair>();
		String str = "";
		try {
			str = AES.Encrypt(cSrc);
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		params.add(new BasicNameValuePair("key", str));
		// 设置httpPost请求参数
		final Message msg1 = Message.obtain();
		msg1.copyFrom(msg);
		msg.what = REQUEST_START_CODE;
		new Thread() {
			@Override
			public void run() {
				try {
					HttpResponse httpResponse = null;
					httpPost.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
					httpPost.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, CONNECTION_TIMEOUT);
					httpPost.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, SO_TIMEOUT);
					HttpClient httpClient = new DefaultHttpClient();
					httpClientMap.put(rqtPosition, httpClient);
					if(hand != null){
						hand.sendMessage(msg);
					}
					httpResponse = httpClient.execute(httpPost);
					int state = httpResponse.getStatusLine().getStatusCode();
					if (state == HttpStatus.SC_OK) {
						String result = EntityUtils.toString(httpResponse.getEntity());
						Logger.errord( "解密前 result =>>"+result);
						String jsonStr = AES.Decrypt(result);
						Logger.errord("JSON解析后 result---------->apiname:" +jsonObj.getString("apiName")
								+"   "+ new JSONObject(jsonStr).toString());
						msg1.what = HTTP_SUCCESS_CODE;
						msg1.obj = jsonStr;
						parserJsonData(jsonStr,msg1.arg1);
					} else {
						Logger.logd( "fail  httpResponse StatusCode = "+ state);
						msg1.what = HTTP_EXCEPTION_CODE;
						msg1.obj = "无法访问服务器";
					}
				}
				catch(ConnectTimeoutException e){
					e.printStackTrace();
					msg1.what = HTTP_EXCEPTION_CODE;
					msg1.obj = "网络连接超时";
				}
				catch(SocketTimeoutException e){
					e.printStackTrace();
					msg1.what = HTTP_EXCEPTION_CODE;
					msg1.obj = "网络通信超时";
				}
				catch (ClientProtocolException e) {
					msg1.what = HTTP_EXCEPTION_CODE;
					msg1.obj = "通信协议异常";
					e.printStackTrace();
				} catch (IOException e1) {
					msg1.what = HTTP_EXCEPTION_CODE;
					msg1.obj = "通信异常(IOException)";
					e1.printStackTrace();
				} catch (Exception e) {
					if(msg1.what == HTTP_SUCCESS_CODE){
						msg1.obj = "服务器发生错误";
					}else{
						msg1.obj = "通信异常(Exception)";
					}
					msg1.what = HTTP_EXCEPTION_CODE;
					e.printStackTrace();
				} finally {
					synchronized (lock) {
						if(!httpClientMap.containsKey(rqtPosition)){
							msg1.what = HTTP_CANCEL;
							msg1.obj = "请求已取消";
							Logger.logd("------------------------------- cancel request  code = "+msg1.arg1);
						}else{
							httpClientMap.remove(rqtPosition);
						}
					}
					if(hand != null){
						hand.sendMessage(msg1);
					}
					httpPost.abort();
				}
			}
		}.start();
	}
	
	public static boolean isConnect(Context context){
		if(connManager == null){
        	connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); 
        }
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo(); 
        return networkInfo != null && networkInfo.isAvailable();
	}

	public static int getNetType(Context context){ 
        int netType = TYPE_NONE;  
        if(connManager == null){
        	connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); 
        }
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo(); 
        if(networkInfo==null){ 
            return netType; 
        } 
        int nType = networkInfo.getType(); 
        if(nType==ConnectivityManager.TYPE_MOBILE){ 
            Log.e("networkInfo.getExtraInfo()", "networkInfo.getExtraInfo() is "+networkInfo.getExtraInfo()); 
            if(networkInfo.getExtraInfo().toLowerCase().equals("cmnet")){ 
                netType = TYPE_CMNET; 
            } 
            else
            { 
                netType = TYPE_CMWAP; 
            } 
        } 
        else if(nType==ConnectivityManager.TYPE_WIFI){ 
            netType = TYPE_WIFI; 
        } 
        return netType; 
    } 
	
	public static void postImage(final String message,final Handler hand,final ArrayList<PostImageRequestObj> paths){
		final ArrayList<PostImageRequestObj> listPath = paths;
		final JSONObject jsonObj = jObj;
		if(jsonObj == null){
			Message m = Message.obtain();
			Bundle bund = new Bundle();
			bund.putString(KEY_HINT_MSG, message);
			m.setData(bund);
			m.what = HTTP_EXCEPTION_CODE;
			m.obj = "数据有误，无法解析";
			hand.sendMessage(m);
			return;
		}
		new Thread(){
			@Override
			public void run() {
				PostImageRequestObj requestObj;
				for(int i = 0 ;i < listPath.size(); i++){
					requestPosition ++;
					final int rqtPosition = requestPosition;
					Message msg = Message.obtain();
					requestObj = listPath.get(i);
					Bundle bund = new Bundle();
					bund.putString(KEY_HINT_MSG, message);
					bund.putInt(KEY_REQUEST_POSITION, rqtPosition);
					bund.putInt("position", requestObj.position);
					msg.setData(bund);
					msg.arg1 = requestObj.requestCode;
					msg.arg2 = requestObj.index;
					if(!isConnect(Tools.mContext)){
						Logger.logd( "no net !!!!!!!!!!!!!!!!");
						msg.what = HTTP_EXCEPTION_CODE;
						msg.obj = "无网络,请检查网络";
						hand.sendMessage(msg);
						return;
					}
					msg.what = REQUEST_START_CODE;
					final Message msg1 = Message.obtain();
					msg1.copyFrom(msg);
					hand.sendMessage(msg);
					MultipartEntity mpEntity = new MultipartEntity();  
			        String cSrc = jsonObj.toString();
	
					Logger.logd("cSrc = \n" + cSrc);
					// 设置HTTP POST请求参数必须用NameValuePair对象
					String str = "";
					try {
						str = AES.Encrypt(cSrc);
					} catch (Exception e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					try {
						mpEntity.addPart("key", new StringBody(str));
					} catch (UnsupportedEncodingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			        // 图片  
					String imagepath = requestObj.path;
					if (!TextUtils.isEmpty(imagepath)) {  
						File f = new File(imagepath);
						if(!f.exists()){
							msg.what = HTTP_EXCEPTION_CODE;
							msg.obj = "文件不存在";
							hand.sendMessage(msg);
							return;
						}
						FileBody file = new FileBody(f);  
						mpEntity.addPart("image", file);  
					}else{
						msg.what = HTTP_EXCEPTION_CODE;
						msg.obj = "文件不存在";
						hand.sendMessage(msg);
						return;
					}
					// 使用HttpPost对象设置发送的URL路径  
					HttpPost post = new HttpPost(URL); 
					post.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, CONNECTION_TIMEOUT);
					post.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, SO_TIMEOUT);
					// 发送请求体  
					post.setEntity(mpEntity); 
					// 创建一个浏览器对象，以把POST对象向服务器发送，并返回响应消息  
					HttpClient httpClient = new DefaultHttpClient();  
					httpClientMap.put(rqtPosition, httpClient);
					try {
						HttpResponse response = httpClient.execute(post);
						int state = response.getStatusLine().getStatusCode();
						if(state == HttpStatus.SC_OK){
							Logger.logd("上传成功");
							String result = EntityUtils.toString(response
									.getEntity());
							Logger.logd("解密前 result =>>" );
							String strJson = "";
							strJson = AES.Decrypt(result);
							parserJsonData(strJson, msg1.arg1);
							Logger.logd("解密后 result===>>" + new JSONObject(strJson).toString());
							msg1.what = HTTP_SUCCESS_CODE;
							msg1.obj =strJson;
						}else{
							msg1.what = HTTP_EXCEPTION_CODE;
							msg1.obj="无法访问服务器";
						}
					} catch (ClientProtocolException e) {
						e.printStackTrace();
						msg1.what = HTTP_EXCEPTION_CODE;
						msg1.obj="通信协议异常";
					} catch (IOException e) {
						e.printStackTrace();
						msg1.what = HTTP_EXCEPTION_CODE;
						msg1.obj="通信异常(IOException)";
					}catch (Exception e) {
						e.printStackTrace();
						msg1.what = HTTP_EXCEPTION_CODE;
						msg1.obj="通信异常(Exception)";
					}
					finally{
						synchronized (lock) {
							if(httpClientMap.containsKey(rqtPosition)){
								hand.sendMessage(msg1);
								httpClientMap.remove(rqtPosition);
								if(msg1.what == HTTP_EXCEPTION_CODE){
									return;
								}
							}else{
								msg1.what = HTTP_CANCEL;
								msg1.obj="取消上传";
								return;
							}
						}
					}
				}
			}
		}.start();
	}
	
	public static boolean saveImage(Bitmap photo, String spath) {
    	File file = new File(spath);
    	if(!file.exists()){
    		try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
        try {
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(spath, false));
            photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
	
	public static void getBitmapByURL(final Handler hand,final ArrayList<ImageRequestObj> listRequest,final boolean needCompress) {  
		if(listRequest.size() > 0){
			new Thread(){
				@Override
				public void run() {
					URL imageUrl = null;  
					Bitmap bitmap = null;  
					String url;
					ImageRequestObj requestObj ;
					for(int i = 0; i < listRequest.size(); i++){
						requestObj = listRequest.get(i);
						Message msg = Message.obtain();
						msg.what = requestObj.requestCode;
						msg.arg1 = requestObj.groupPosition;
						msg.arg2 = requestObj.childPosition;
						url = requestObj.url;
						if(TextUtils.isEmpty(url)){
							hand.sendMessage(msg);
							return;
						}
						try {  
							imageUrl = new URL(url);  
						} catch (MalformedURLException e) {  
							e.printStackTrace();  
						}  
						if(imageUrl == null){
							hand.sendMessage(msg);
							return;
						}
						InputStream is = null;
						try {  
							HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();  
							conn.setDoInput(true);  
							conn.setConnectTimeout(CONNECTION_TIMEOUT);
							conn.connect();  
							is = conn.getInputStream();  
							bitmap = BitmapFactory.decodeStream(is);  
							conn.disconnect();
							if(needCompress){
								bitmap = Tools.getSmallBitmap(bitmap);
							}
							msg.obj = bitmap;
							Logger.logd("get bitmap success !");
						} catch (IOException e) {  
							e.printStackTrace();  
							msg.obj = null;
						}  
						finally{
							if(is != null){
								try {
									is.close();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							hand.sendMessage(msg);
						}
					}
				}
			}.start();
		}
	}  
}
