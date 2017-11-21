package com.tg.cloudmanagement.net;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

public class MessageHandler {
	public interface ResponseListener{
		public void onRequestStart(Message msg, String hintString);
		public void onSuccess(Message msg, String jsonString, String hintString);
		public void onFail(Message msg, String hintString);
	}
	private Context mContext;
	private ResponseListener callback;
	public MessageHandler(Context con){
		mContext = con;
	} 
	public void setResponseListener(ResponseListener l){
		callback = l;
	}
	
	protected Handler mHand = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(mContext instanceof Activity){
				Activity activity = (Activity)mContext;
				if(activity == null || activity.isFinishing()){
					return;
				}
			}
			String hintString = "";
			Bundle data = msg.getData();
			if(data != null){
				hintString = data.getString(HttpTools.KEY_HINT_STRING, null);
			}
			switch (msg.what) {
				case HttpTools.RESPONSE_ERROR:
					if(callback != null){
						callback.onFail(msg, hintString);
					}
					break;
				case HttpTools.RESPONSE_START:
					if(callback != null){
						if(hintString != null && !"".equals(hintString)){
							hintString = "正在"+hintString+"...";
						}
						callback.onRequestStart(msg, hintString);
					}
					break;
				case HttpTools.RESPONSE_SUCCES:
					// 0 :get 1:post 2：上传图片
					int httpType = msg.arg2;
					String responseString = null;
					boolean failNeedHint = false;
					if(data != null){
						responseString = data.getString(HttpTools.KEY_RESPONSE_MSG);
						failNeedHint = data.getBoolean(HttpTools.KEY_FAIL_NEED_HINT,false);
					}
					int code = HttpTools.getCode(responseString);
				if (code != 301) {
					String error = HttpTools.getMessageString(responseString);
					if (error == null || "null".equals(error)) {
						error = "";
					}
						if(httpType == 0){//get
							String dataCount = HttpTools.getContentString(responseString);
							String datastr = HttpTools.getDataString(responseString);
						if (dataCount != null || datastr != null || code == 0) {
								if(callback != null){
									callback.onSuccess(msg,responseString ,TextUtils.isEmpty(hintString)? "":hintString+"成功");
								}
							}else{
								if(!failNeedHint){
									error = "";
								}
								if(callback != null){
									callback.onFail(msg, error);
								}
							}
						}else{//post
							String dataCount = HttpTools.getContentString(responseString);
							if(dataCount != null){
								if(callback != null){
									callback.onSuccess(msg,responseString ,error);
								}
							}else{
								if(TextUtils.isEmpty(error)){
									error = hintString+"失败";
								}
								if(callback != null){
									callback.onFail(msg, error);
								}
							}
						}
					}else{ 
						if(callback != null){
							String error = HttpTools.getMessageString(responseString);
							callback.onFail(msg, error);
						}
					}
					break;
			}
		}
	};
	
	public Handler getHandler() {
		return mHand;
	}
}
