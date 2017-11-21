package com.tg.cloudmanagement.view;

import com.tg.cloudmanagement.net.HttpTools;
import com.tg.cloudmanagement.net.MessageHandler;
import com.tg.cloudmanagement.net.MessageHandler.ResponseListener;
import com.tg.cloudmanagement.net.ResponseData;

import android.app.Activity;
import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class ManageMentLinearlayout extends LinearLayout implements ResponseListener{
	public interface NetworkRequestListener {
		public void onRequest(MessageHandler msgHand);

		public void onSuccess(ManageMentLinearlayout magLearLayout, Message msg, String response);
	}
	private NetworkRequestListener requestListener;
	private boolean isLoadding = false;
	private MessageHandler msgHandler;
	private Activity mActivity;

	public ManageMentLinearlayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public ManageMentLinearlayout(Context context) {
		super(context);
		initView(context);
	}

	public ManageMentLinearlayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}
	private void initView(Context con) {
		mActivity = (Activity) con;
		msgHandler = new MessageHandler(con);
		msgHandler.setResponseListener(this);
	}

	public void loaddingData() {
		if (!isLoadding) {
			if (requestListener != null) {
				isLoadding = true;
				requestListener.onRequest(msgHandler);
			}
		}
	}

	public void setNetworkRequestListener(NetworkRequestListener l) {
		requestListener = l;
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

	@Override
	public void onRequestStart(Message msg, String hintString) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		// TODO Auto-generated method stub
		String repsone = HttpTools.getContentString(jsonString);
		if (repsone != null) {
			ResponseData data = HttpTools.getResponseData(repsone);
			if (data == null || data.length == 0) {
				
			} else {
				if (requestListener != null) {
					requestListener.onSuccess(this, msg, jsonString);
				}
			}
		}
		isLoadding= false;
	}

	@Override
	public void onFail(Message msg, String hintString) {
		// TODO Auto-generated method stub
	}
}
