package com.tg.cloudmanagement.net;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.SocketTimeoutException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.SparseArray;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.RequestQueue.RequestFilter;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tg.cloudmanagement.application.CloudManagementApplication;
import com.tg.cloudmanagement.log.Logger;
import com.tg.cloudmanagement.object.ImageParams;
import com.tg.cloudmanagement.util.Tools;

public class HttpTools {
	public static final String DIFFERENCE = "difference";
	public static final int RESPONSE_ERROR = 0;
	public static final int RESPONSE_SUCCES = 1;
	public static final int RESPONSE_START = 2;
	public static final String KEY_IS_LAST = "isLast";
	public static final String KEY_HINT_STRING = "hint";
	public static final String KEY_RESPONSE_MSG = "response";
	public static final String KEY_SILENT_REQUEST = "silent";
	public static final String KEY_FAIL_NEED_HINT = "fail_hint";
	public static final String KEY_IMAGE_PARAMS = "img_params";

	public static final String FIELD_CODE = "code";
	public static final String FIELD_MESSAGE= "message";
	public static final String FIELD_CONTENT = "content";
	public static final String FIELD_TOTAL_COUNT = "total";
	public static final String FIELD_DATA = "data";
	public static int TIME_OUT = 30000;

	private static int BASE_CODE = 1;
	public static final int GET_CODE = BASE_CODE++;
	public static final int POST_IMAG = BASE_CODE++;
	public static final int GET_TS = BASE_CODE++;
	public static final int GET_LOGIN = BASE_CODE++;
	public static final int GET_USER_INFO = BASE_CODE++;
	public static final int SET_REPAIR_INFO = BASE_CODE++;
	public static final int SET_COMPLAINT_INFO = BASE_CODE++;
	public static final int SET_USER_INFO = BASE_CODE++;
	public static final int SET_OPINION_INFO = BASE_CODE++;
	public static final int SET_PASSWORD = BASE_CODE++;
	public static final int GET_EMPLOYEE_INFO = BASE_CODE++;
	public static final int SET_EMPLOYEE_INFO = BASE_CODE++;
	public static final int DELETE_EMPLOYEE_INFO = BASE_CODE++;
	private static RequestQueue mQueue;
	private static HttpClient postClient = null;
	private static boolean cancelPost = false;
	private static boolean test = false;

	public static RequestQueue getRequestQueue() {
		if (mQueue == null) {
			mQueue = Volley.newRequestQueue(Tools.mContext);
		}
		return mQueue;
	}
	/**
	 * 获取时间
	 * 
	 * @return
	 */
	private static String  getTime() {
		Date dt = new Date();
		Long time = dt.getTime();
		SharedPreferences sharedata =CloudManagementApplication.getInstance().getSharedPreferences("APP_TS", 0);
		Long difference = sharedata.getLong(DIFFERENCE,-1);
		Long timelogin = time + difference;
		String lastTime = String.valueOf(timelogin);
		return lastTime;
	}
	/**
	 * 数据请求
	 * 
	 * @param URL
	 * @param apiName
	 * @param method
	 * @param rqtConfig
	 * @param param
	 */
	private static void post(final String URL_NAME,int method, final RequestConfig rqtConfig, final HashMap<String, String> param) {
		StringRequest stringRequest = new StringRequest(method, URL_NAME,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						if (rqtConfig.activity != null
								&& rqtConfig.activity.isFinishing()) {
							return;
						}
						Logger.logd("[ " + URL_NAME + " ]response = " + response);
						String decryptStr = "";
						if (rqtConfig.decrypt) {
							try {
								decryptStr = URLDecoder.decode(
										DES.Decrypt(response), "UTF-8");
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else {
							decryptStr = response;
						}
						if (rqtConfig.handler != null) {
							Bundle bundle = new Bundle();
							bundle.putString(KEY_RESPONSE_MSG, decryptStr);
							bundle.putBoolean(KEY_SILENT_REQUEST,
									rqtConfig.hintString == null);
							Message msg = Message.obtain();
							msg.what = RESPONSE_SUCCES;
							msg.arg1 = rqtConfig.requestCode;
							msg.arg2 = 1;
							msg.setData(bundle);
							msg.obj = rqtConfig.tag;
							rqtConfig.handler.sendMessage(msg);
						}
					}

					@Override
					public void onPrepare(String hintString) {// 子线程中
						// TODO Auto-generated method stub
						if (rqtConfig.activity != null
								&& rqtConfig.activity.isFinishing()) {
							return;
						}
						if (rqtConfig.handler != null) {
							Bundle bundle = new Bundle();
							bundle.putString(KEY_HINT_STRING, hintString);
							bundle.putBoolean(KEY_SILENT_REQUEST,
									hintString == null);
							Message msg = Message.obtain();
							msg.what = RESPONSE_START;
							msg.arg1 = rqtConfig.requestCode;
							msg.arg2 = 1;
							msg.setData(bundle);
							msg.obj = rqtConfig.tag;
							rqtConfig.handler.sendMessage(msg);
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if (rqtConfig.activity != null
								&& rqtConfig.activity.isFinishing()) {
							return;
						}
						String errorStr = getExceptionMessage(error);
						Logger.errord("errorStr = " + errorStr);
						Logger.errord("[ " + URL_NAME + " ]error = " + errorStr);
						Writer info = new StringWriter();
						PrintWriter printWriter = new PrintWriter(info);
						error.printStackTrace(printWriter);
						Logger.errord(info.toString());
						if (rqtConfig.handler != null) {
							Bundle bundle = new Bundle();
							bundle.putString(KEY_HINT_STRING, errorStr);
							bundle.putBoolean(KEY_SILENT_REQUEST,
									rqtConfig.hintString == null);
							Message msg = Message.obtain();
							msg.what = RESPONSE_ERROR;
							msg.arg1 = rqtConfig.requestCode;
							msg.arg2 = 1;
							msg.setData(bundle);
							msg.obj = rqtConfig.tag;
							rqtConfig.handler.sendMessage(msg);
						}
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				String sign = "";
				String ts =getTime();
				try {
					sign = getSign(URL_NAME,ts);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("appID", DES.APP_ID);
				map.put("sign", sign);
				map.put("ts",ts);
				HashMap<String, String> mapsum = new HashMap<String, String>();
				mapsum.putAll(map);
				mapsum.putAll(param);
				return mapsum;
			}
		};
		if (rqtConfig.tag != null) {
			stringRequest.setTag(rqtConfig.tag);
		}
		stringRequest.setHintString(rqtConfig.hintString);
		stringRequest.setShouldCache(false);
		stringRequest.setRetryPolicy(new DefaultRetryPolicy(TIME_OUT, 0, 1));
		getRequestQueue().add(stringRequest);
	}
	/**
	 * 获取拼接完成的网址(获取ts);
	 * 
	 * @param Method
	 *            方法名
	 * @param Params
	 *            参数
	 * @return 网址
	 * @throws Exception
	 */
	private static String GetUrlTS(final String URL,final String apiname, final HashMap<String, Object> params) throws Exception {
		String Sign = MD5.getMd5Value(URL+apiname+DES.APP_ID + DES.APP_TS + DES.KEY_STR +"false").toLowerCase();
		String apppara = "";
		Iterator<String> keys = params.keySet().iterator();
		if (keys != null) {
			String key;
			while (keys.hasNext()) {
				key = keys.next();
				Object value = params.get(key);
				if (!value.toString().isEmpty()) {
					apppara+="&";
					apppara += key+"="+value.toString();
				}
			}
		}
		return apiname+ "?appID="+DES.APP_ID+"&sign=" + Sign+"&ts="+ DES.APP_TS + apppara;
	}
	
	/**
	 * 获取拼接完成的网址(获取ts);
	 * 
	 * @param Method
	 *            方法名
	 * @param Params
	 *            参数
	 * @return 网址
	 * @throws Exception
	 */
	public static String GetUrl(final String URL,final String apiname, final HashMap<String, Object> params) throws Exception {
		String ts = getTime();
		String Sign = MD5.getMd5Value(URL+apiname+DES.APP_ID +ts+ DES.KEY_STR +"false").toLowerCase();
		String apppara = "";
		Iterator<String> keys = params.keySet().iterator();
		if (keys != null) {
			String key;
			while (keys.hasNext()) {
				key = keys.next();
				Object value = params.get(key);
				if (!value.toString().isEmpty()) {
					apppara+="&";
					apppara += key+"="+value.toString();
				}
			}
		}
		return apiname+ "?appID="+DES.APP_ID+"&sign=" + Sign+"&ts="+ts+ apppara;
	}
	
	/**
	 * 
	 * @param Method
	 *            :方法名
	 * @param Params
	 *            :加密的参数
	 * @return
	 * @throws Exception
	 */
	private static String getSign(String URLNAME,String lastTime)
			throws Exception {
		return MD5.getMd5Value(URLNAME+DES.APP_ID + lastTime + DES.KEY_STR +"false").toLowerCase();
	}
	/**
	 * Get请求数据
	 * 
	 * @param URL
	 * @param apiName
	 * @param rqtConfig
	 * @param params
	 */
	public static void get(final String URL, int method,final String apiName,final RequestConfig rqtConfig, RequestParams params) {
		
		StringRequest requestStr = new StringRequest(method, URL,
				new Listener<String>() {
					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Logger.logd("response = "+response);
						if (rqtConfig.activity == null|| rqtConfig.activity.isFinishing()) {
							return;
						}
						String decryptStr = "";
						if (rqtConfig.decrypt) {
							try {
								decryptStr = URLDecoder.decode(DES.Decrypt(response), "UTF-8");
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else {
							decryptStr = response;
						}
						if (rqtConfig.handler != null) {
							Bundle bundle = new Bundle();
							bundle.putString(KEY_RESPONSE_MSG, decryptStr);
							bundle.putBoolean(KEY_SILENT_REQUEST,rqtConfig.hintString == null);
							bundle.putBoolean(KEY_FAIL_NEED_HINT,rqtConfig.failHint);
							Message msg = Message.obtain();
							msg.what = RESPONSE_SUCCES;
							msg.arg1 = rqtConfig.requestCode;
							msg.arg2 = 0;
							msg.setData(bundle);
							msg.obj = rqtConfig.tag;
							rqtConfig.handler.sendMessage(msg);
						} else {
							Logger.logd("rqtConfig.handler == null");
						}
					}

					@Override
					public void onPrepare(String hintString) {
						// TODO Auto-generated method stub
						if (rqtConfig.activity != null
								&& rqtConfig.activity.isFinishing()) {
							return;
						}
						if (rqtConfig.handler != null) {
							Bundle bundle = new Bundle();
							bundle.putString(KEY_HINT_STRING, hintString);
							bundle.putBoolean(KEY_SILENT_REQUEST,
									hintString == null);
							bundle.putBoolean(KEY_FAIL_NEED_HINT,
									rqtConfig.failHint);
							Message msg = Message.obtain();
							msg.what = RESPONSE_START;
							msg.arg1 = rqtConfig.requestCode;
							msg.arg2 = 0;
							msg.setData(bundle);
							msg.obj = rqtConfig.tag;
							rqtConfig.handler.sendMessage(msg);
						}
					}
				}, new ErrorListener() {
					
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Logger.logd("error = "+error);
						if (rqtConfig.activity != null
								&& rqtConfig.activity.isFinishing()) {
							return;
						}
						String errorStr = getExceptionMessage(error);
						Writer info = new StringWriter();
						PrintWriter printWriter = new PrintWriter(info);
						error.printStackTrace(printWriter);
						Logger.errord(info.toString());

						if (rqtConfig.handler != null) {
							Bundle bundle = new Bundle();
							bundle.putString(KEY_HINT_STRING, errorStr);
							bundle.putBoolean(KEY_SILENT_REQUEST,
									rqtConfig.hintString == null);
							bundle.putBoolean(KEY_FAIL_NEED_HINT,
									rqtConfig.failHint);
							Message msg = Message.obtain();
							msg.what = RESPONSE_ERROR;
							msg.arg1 = rqtConfig.requestCode;
							msg.arg2 = 0;
							msg.setData(bundle);
							msg.obj = rqtConfig.tag;
							rqtConfig.handler.sendMessage(msg);
						}
					}
				});
		if (rqtConfig.tag != null) {
			requestStr.setTag(rqtConfig.tag);
		}
		requestStr.setHintString(rqtConfig.hintString);
		requestStr.setShouldCache(false);
		requestStr.setRetryPolicy(new DefaultRetryPolicy(TIME_OUT, 0, 1));
		getRequestQueue().add(requestStr);
		
	}

	/**
	 * 上传多张
	 * 
	 * @param hand
	 * @param imgParams
	 */
	public static void postImages(final String URL, final Handler hand,
			final ArrayList<ImageParams> imgParams) {
		cancelPost = false;
		new Thread() {
			public void run() {
				ImageParams imgParam;
				boolean isLast = false;
				int len = imgParams.size();
				for (int i = 0; i < len; i++) {
					isLast = i == len - 1;
					imgParam = imgParams.get(i);
					if (!postAnImg(URL, isLast, hand, imgParam)) {
						return;
					}
				}
			}
		}.start();
	}

	/**
	 * 上传一张
	 * 
	 * @param hand
	 * @param imgParams
	 */
	public static void postAnImage(final String URL, final Handler hand,
			final ImageParams imgParams) {
		cancelPost = false;
		new Thread() {
			@Override
			public void run() {
				postAnImg(URL, true, hand, imgParams);
			}
		}.start();
	}

	private static boolean postAnImg(final String URL, boolean isLast,
			Handler hand, ImageParams imgParams) {
		Message msg = Message.obtain();
		msg.arg1 = POST_IMAG;
		msg.arg2 = 2;
		Bundle bundle = new Bundle();
		bundle.putString(KEY_HINT_STRING, "上传图片");
		bundle.putBoolean(KEY_SILENT_REQUEST, false);
		bundle.putBoolean(KEY_IS_LAST, isLast);
		bundle.putParcelable(KEY_IMAGE_PARAMS, imgParams);
		msg.setData(bundle);
		if (!isConnection(Tools.mContext)) {
			Logger.logd("no net !!!!!!!!!!!!!!!!");
			msg.what = RESPONSE_ERROR;
			bundle.putString(KEY_HINT_STRING, "无网络,请检查网络");
			hand.sendMessage(msg);
			return false;
		}
		MultipartEntity mpEntity = new MultipartEntity();
		String apiName = "/picupload";
		String ts = getTime();
		try {
			String sign = getSign(URL+apiName,ts);
			mpEntity.addPart("appID", new StringBody(DES.APP_ID));
			mpEntity.addPart("sign", new StringBody(sign));
			mpEntity.addPart("ts", new StringBody(ts));
			// 图片
			if (!TextUtils.isEmpty(imgParams.path)) {
				File file = new File(imgParams.path);
				if (!file.exists()) {
					msg.what = RESPONSE_ERROR;
					msg.getData().putString(KEY_HINT_STRING,"图片不存在" + imgParams.path);
					hand.sendMessage(msg);
					return false;
				}
				mpEntity.addPart("file", new FileBody(file));
			} else {
				msg.what = RESPONSE_ERROR;
				msg.getData().putString(KEY_HINT_STRING, "图片路径为空");
				hand.sendMessage(msg);
				return false;
					}
		}catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			msg.what = RESPONSE_ERROR;
			bundle.putString(KEY_HINT_STRING, "参数编码异常");
			hand.sendMessage(msg);
			return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			msg.what = RESPONSE_ERROR;
			bundle.putString(KEY_HINT_STRING, "参数加密异常");
			hand.sendMessage(msg);
			return false;
		}
		HttpPost post = new HttpPost(URL+apiName);
		post.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
		post.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
		post.setEntity(mpEntity);
		postClient = new DefaultHttpClient();
		if (cancelPost) {
			return false;
		}
		final Message msg1 = Message.obtain();
		msg1.copyFrom(msg);
		msg.what = RESPONSE_START;
		hand.sendMessage(msg);
		try {
			HttpResponse response = postClient.execute(post);
			int state = response.getStatusLine().getStatusCode();
			if (state == HttpStatus.SC_OK) {
				String result = EntityUtils.toString(response.getEntity());
				Logger.logd("图片上传成功 result = " + result);
				msg1.what = RESPONSE_SUCCES;
				msg1.getData().putString(KEY_RESPONSE_MSG, result);
				msg1.getData().putString(KEY_HINT_STRING, "图片上传成功");
			} else {
				msg1.what = RESPONSE_ERROR;
				msg1.getData().putString(KEY_HINT_STRING, "无法访问服务器");
			}
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			msg1.what = RESPONSE_ERROR;
			msg1.getData().putString(KEY_HINT_STRING, "请求超时");
		} catch (ConnectTimeoutException e) {
			e.printStackTrace();
			msg1.what = RESPONSE_ERROR;
			msg1.getData().putString(KEY_HINT_STRING, "连接超时");
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			msg1.what = RESPONSE_ERROR;
			msg1.getData().putString(KEY_HINT_STRING, "通信异常");
		} catch (IOException e) {
			e.printStackTrace();
			msg1.what = RESPONSE_ERROR;
			msg1.getData().putString(KEY_HINT_STRING, "通信异常");
		} catch (Exception e) {
			e.printStackTrace();
			msg1.what = RESPONSE_ERROR;
			msg1.getData().putString(KEY_HINT_STRING, "通信异常");
		}
		if (cancelPost) {
			return false;
		} else {
			hand.sendMessage(msg1);
			if (msg1.what == RESPONSE_ERROR) {
				return false;
			} else {
				return true;
			}
		}
	}

	public static boolean isConnection(Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isAvailable();
	}

	public static void cancelRequest(Object tag) {
		if (tag != null) {
			Logger.logd("cancel Request " + tag);
			getRequestQueue().cancelAll(tag);
		}
	}

	public static void cancelPost() {
		cancelPost = true;
		if (postClient != null) {
			postClient.getConnectionManager().shutdown();
			postClient = null;
		}

	}

	public static void cancelAllRequest() {
		getRequestQueue().cancelAll(new RequestFilter() {
			@Override
			public boolean apply(Request<?> request) {
				return true;
			}
		});
	}

	public static String getExceptionMessage(Object error) {
		if (error instanceof TimeoutError) {
			return "网络不给力";
		} else if (isServerProblem(error)) {
			return "服务器发生错误";
		} else if (isNetworkProblem(error)) {
			return "无法连接网络";
		}
		return "网络异常";
	}

	/**
	 * 服务器发生错误
	 * 
	 * @param error
	 * @return
	 */
	private static boolean isServerProblem(Object error) {
		return (error instanceof ServerError)
				|| (error instanceof AuthFailureError);
	}

	/**
	 * Determines whether the error is related to network
	 * 无法连接网络
	 * @param error
	 * @return
	 */
	private static boolean isNetworkProblem(Object error) {
		return (error instanceof NetworkError)
				|| (error instanceof NoConnectionError);
	}
	/**
	 * get请求
	 * @param URL
	 * @param apiName
	 * @param rqtConfig
	 * @param params
	 */
		public static void httpGet(final String URL, String apiName,final RequestConfig rqtConfig, RequestParams params){
			String url = null;
			HashMap<String, Object> paramsStr = null;
			if (params == null) {
				paramsStr = null;
			} else {
				paramsStr = params.toHashMap();
			}
			try {
				url =  URL +GetUrl(URL,apiName,paramsStr);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			get(url, Method.GET, apiName, rqtConfig, params);
		}
		/**
		 * get请求(ts)
		 * @param URL
		 * @param apiName
		 * @param rqtConfig
		 * @param params
		 */
		public static void httpGetTS(final String URL, String apiName,final RequestConfig rqtConfig, RequestParams params){
			String url = null;
			HashMap<String, Object> paramsStr = null;
			if (params == null) {
				paramsStr = null;
			} else {
				paramsStr = params.toHashMap();
			}
			try {
				url =  URL +GetUrlTS(URL,apiName,paramsStr);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			get(url, Method.GET, apiName, rqtConfig, params);
		}
		/**
		 * Delete请求
		 * @param URL
		 * @param apiName
		 * @param rqtConfig
		 * @param requestParams
		 */
		public static void httpDelete(final String URL, String apiName,final RequestConfig rqtConfig, RequestParams params) {
			String url = null;
			HashMap<String, Object> paramsStr = null;
			if (params == null) {
				paramsStr = null;
			} else {
				paramsStr = params.toHashMap();
			}
			try {
				url =  URL +GetUrl(URL,apiName,paramsStr);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			get(url, Method.DELETE, apiName, rqtConfig, params);
		}
	/**
	 * Post请求
	 * @param URL
	 * @param apiName
	 * @param rqtConfig
	 * @param requestParams
	 */
	public static void httpPost(final String URL, String apiName,final RequestConfig rqtConfig, RequestParams requestParams) {
		HashMap<String, String> paramsStr = null;
		if (requestParams == null) {
			paramsStr = null;
		} else {
			paramsStr = requestParams.toHashMapString();
		}
		post(URL+apiName, Method.POST, rqtConfig, paramsStr);
	}
	/**
	 * Put请求
	 * @param URL
	 * @param apiName
	 * @param rqtConfig
	 * @param requestParams
	 */
	public static void httpPut(final String URL, String apiName,final RequestConfig rqtConfig, RequestParams requestParams) {
		HashMap<String, String> paramsStr = null;
		if (requestParams == null) {
			paramsStr = null;
		} else {
			paramsStr = requestParams.toHashMapString();
		}
		post(URL+apiName,Method.PUT, rqtConfig, paramsStr);
	}
	/**
	 * 获取code
	 * @param jsonString
	 * @return
	 */
	public static int getCode(String jsonString) {
		if (TextUtils.isEmpty(jsonString)) {
			return -1;
		}
		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject(jsonString);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		return jsonObj.optInt(FIELD_CODE, -1);
	}

	/**
	 * 获取Message信息
	 * 
	 * @param jsonString
	 * @return
	 */
	public static String getMessageString(String jsonString) {
		if (TextUtils.isEmpty(jsonString)) {
			return null;
		}
		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject(jsonString);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		if (jsonObj.isNull(FIELD_MESSAGE)) {
			return null;
		}
		return jsonObj.optString(FIELD_MESSAGE, null);
	}
	/**
	 * 获取图片名字
	 * 
	 * @param jsonString
	 * @return
	 */
	public static String getFileNameString(String jsonString) {
		if (TextUtils.isEmpty(jsonString)) {
			return null;
		}
		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject(jsonString);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		if (jsonObj.isNull("filename")) {
			return null;
		}
		return jsonObj.optString("filename", null);
	}
	/**
	 * 获取Content信息(JSONObject)
	 * 
	 * @param jsonString
	 * @return
	 */
	public static JSONObject getContentJSONObject(String jsonString) {
		if (TextUtils.isEmpty(jsonString)) {
			return null;
		}
		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject(jsonString);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		if (jsonObj.isNull(FIELD_CONTENT)) {
			return null;
		}
		return jsonObj.optJSONObject(FIELD_CONTENT);
	}
	/**
	 * 获取Content信息(String)
	 * 
	 * @param jsonString
	 * @return
	 */
	public static String getContentString(String jsonString) {
		if (TextUtils.isEmpty(jsonString)) {
			return null;
		}
		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject(jsonString);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		if (jsonObj.isNull(FIELD_CONTENT)) {
			return null;
		}
		return jsonObj.optString(FIELD_CONTENT);
	}
	/**
	 * 获取Datat信息(String)
	 * 
	 * @param jsonString
	 * @return
	 */
	public static String getDataString(String jsonString) {
		if (TextUtils.isEmpty(jsonString)) {
			return null;
		}
		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject(jsonString);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		if (jsonObj.isNull(FIELD_DATA)) {
			return null;
		}
		return jsonObj.optString(FIELD_DATA);
	}
	/**
	 * 获取Content信息(String)
	 * 
	 * @param jsonString
	 * @return
	 */
	public static JSONArray getContentJsonArray(String jsonString) {
		if (TextUtils.isEmpty(jsonString)) {
			return null;
		}
		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject(jsonString);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		if (jsonObj.isNull(FIELD_CONTENT)) {
			return null;
		}
		return jsonObj.optJSONArray(FIELD_CONTENT);
	}
	/**
	 * 获取total信息
	 * 
	 * @param jsonString
	 * @return
	 */
	public static int getTotalCount(String jsonString) {
		int count = 0;
		if (TextUtils.isEmpty(jsonString)) {
			return count;
		}
		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject(jsonString);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return count;
		}
		return jsonObj.optInt(FIELD_TOTAL_COUNT, count);
	}
	/**
	 * 获取Data详细数据信息
	 * 
	 * @param jsonString
	 * @return
	 */
	public static JSONArray getData(String jsonString) {
		if (TextUtils.isEmpty(jsonString)) {
			return null;
		}
		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject(jsonString);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return jsonObj.optJSONArray(FIELD_DATA);
	}

	/**
	 * 获取Data长度信息
	 * 
	 * @param jsonString
	 * @return
	 */
	public static int getDataCount(String jsonString) {
		JSONArray jarray = getData(jsonString);
		if (jarray == null) {
			return 0;
		}
		return jarray.length();
	}

	/**
	 * 获取Data详细数据调用方法
	 * 
	 * @param jsonString
	 * @return
	 */
	public static ResponseData getResponseData(String jsonString) {
		JSONArray array = getData(jsonString);
		if (array == null || array.length() == 0) {
			return new ResponseData(null);
		}
		return parseJsonArray(array);
	}
	/**
	 * 获取Content详细数据调用方法
	 * @param jsonString
	 * @return
	 */
	public static ResponseData getResponseContent(JSONArray jsonString) {
		if (jsonString == null || jsonString.length() == 0) {
			return new ResponseData(null);
		}
		return parseJsonArray(jsonString);
	}

	public static ResponseData parseJsonArray(JSONArray jsonArray) {
		if (jsonArray == null || jsonArray.length() == 0) {
			return new ResponseData(null);
		}
		SparseArray<HashMap<String, Object>> sparseArray = new SparseArray<HashMap<String, Object>>();
		int len = jsonArray.length();
		JSONObject jsonObj;
		for (int i = 0; i < len; i++) {
			jsonObj = jsonArray.optJSONObject(i);
			if (jsonObj == null) {
				continue;
			}
			sparseArray.put(i, getMap(jsonObj));
		}
		return new ResponseData(sparseArray);
	}

	public static ResponseData parseUserInfoJsonString(String jsonString) {
		if (TextUtils.isEmpty(jsonString)) {
			return new ResponseData(null);
		}
		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject(jsonString);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseData(null);
		}
		SparseArray<HashMap<String, Object>> sparseArray = new SparseArray<HashMap<String, Object>>();
		sparseArray.put(0, getMap(jsonObj));
		return new ResponseData(sparseArray);
	}

	private static ResponseData parseJsonString(String jsonString) {
		if (TextUtils.isEmpty(jsonString)) {
			return new ResponseData(null);
		}
		JSONArray jsonArray = null;
		try {
			jsonArray = new JSONArray(jsonString);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseData(null);
		}
		SparseArray<HashMap<String, Object>> sparseArray = new SparseArray<HashMap<String, Object>>();
		int len = jsonArray.length();
		JSONObject jsonObj;
		for (int i = 0; i < len; i++) {
			jsonObj = jsonArray.optJSONObject(i);
			sparseArray.put(i, getMap(jsonObj));
		}
		return new ResponseData(sparseArray);
	}

	public static HashMap<String, Object> getMap(JSONObject jsonObj) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		if (jsonObj == null) {
			return map;
		}
		Iterator<String> keys = jsonObj.keys();
		if (keys == null) {
			return map;
		}
		String key;
		while (keys.hasNext()) {
			key = keys.next();
			if (jsonObj.isNull(key)) {
				map.put(key, "");
			} else {
				map.put(key, jsonObj.opt(key));
			}
		}
		return map;
	}
	public static HashMap<String, String> getMapString(JSONObject jsonObj) {
		HashMap<String, String> map = new HashMap<String, String>();
		if (jsonObj == null) {
			return map;
		}
		Iterator<String> keys = jsonObj.keys();
		if (keys == null) {
			return map;
		}
		String key;
		while (keys.hasNext()) {
			key = keys.next();
			if (jsonObj.isNull(key)) {
				map.put(key, "");
			} else {
				if(jsonObj.opt(key) instanceof String){
					map.put(key, (String) jsonObj.opt(key));
				}else{
					map.put(key, String.valueOf(jsonObj.opt(key)));
				}
			}
		}
		return map;
	}
}
