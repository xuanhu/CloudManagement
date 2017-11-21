package com.tg.cloudmanagement.view.dialog;


import com.tg.cloudmanagement.R;
import com.tg.cloudmanagement.log.Logger;
import com.tg.cloudmanagement.net.HttpUtil;
import com.tg.cloudmanagement.net.MessageHandler;
import com.tg.cloudmanagement.net.MessageHandler.ResponseListener;
import com.tg.cloudmanagement.util.Tools;
import com.tg.cloudmanagement.view.RotateProgress;
import com.tg.cloudmanagement.view.spinnerwheel.WheelVerticalView;
import com.tg.cloudmanagement.inter.NetworkRequestListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NetWorkDialog implements ResponseListener,OnClickListener,OnDismissListener{
	public static final String TEXT_RELOAD = "点击重新加载";
	public static final String TEXT_LOADING = "正在加载数据...";
	public static final String TEXT_LOAD_SUCCESS = "加载成功";
	private AlertDialog dialog;
	private Activity activity;
	private boolean success = false;
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	private NetworkRequestListener netListener;
	private MessageHandler msgHand;
	private RotateProgress progressBar;
	private TextView tvLoading;
	private FrameLayout contentLayout;
	private LinearLayout loadLayout;
	private int requestPosition;
	private View contentView;
	private TextView tvCancel;
	private TextView tvComplete;
	private TextView tvTitle;
	private OnClickListener onCompleteClickListener;
	private WheelVerticalView wheelView;
	public NetWorkDialog(Activity activity,View v,WheelVerticalView wheel) {
		this.activity = activity;
		contentView = v;
		msgHand = new MessageHandler(activity);
		msgHand.setResponseListener(this);
		wheelView = wheel;
	}
	
	public void setNetworkRequestListener(NetworkRequestListener l){
		netListener = l;
	}
	
	public void showNetDialog(String title,boolean forceFresh){
		if(dialog == null){
			dialog = new AlertDialog.Builder(activity).create();
			dialog.show();
			Window window = dialog.getWindow();
			View layout = LayoutInflater.from(activity)
					.inflate(R.layout.network_dialog_layout, null);
			tvCancel = (TextView)layout.findViewById(R.id.tv_cancel);
			tvComplete= (TextView)layout.findViewById(R.id.tv_complete);
			tvTitle = (TextView)layout.findViewById(R.id.tv_title);
			loadLayout = (LinearLayout)layout.findViewById(R.id.load_layout);
			tvLoading = (TextView)layout.findViewById(R.id.tv_reLoad);
			progressBar = (RotateProgress)layout.findViewById(R.id.progressBar);
			loadLayout.setOnClickListener(this);
			tvCancel.setOnClickListener(this);
			tvComplete.setOnClickListener(this);
			contentLayout = (FrameLayout)layout.findViewById(R.id.content_layout);
			contentLayout.addView(contentView, new FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.WRAP_CONTENT));
			window.setContentView(layout);
			WindowManager.LayoutParams p = window.getAttributes();
			DisplayMetrics metrics = Tools.getDisplayMetrics(activity);
			p.width = metrics.widthPixels;
			p.height = WindowManager.LayoutParams.WRAP_CONTENT;
			p.gravity = Gravity.BOTTOM;
			window.setAttributes(p);
			dialog.setOnDismissListener(this);
		}else{
			dialog.show();
		}
		tvTitle.setText(title);
		Logger.logd("success = "+success);
		if(forceFresh){
			loadLayout.setEnabled(false);
			progressBar.setVisibility(View.VISIBLE);
			if(netListener != null){
				netListener.onRequest(msgHand);
			}
		}else{
			if(success){
				contentLayout.setVisibility(View.VISIBLE);
				loadLayout.setVisibility(View.GONE);
				progressBar.setVisibility(View.GONE);
			}else{
				loadLayout.setEnabled(false);
				progressBar.setVisibility(View.VISIBLE);
				if(netListener != null){
					netListener.onRequest(msgHand);
				}
			}
		}
	}
	
	public void hideNetDialog(){
		if(dialog != null){
			dialog.dismiss();
		}
	}

	@Override
	public void onRequestStart(Message msg, String hintMsg) {
		requestPosition = msg.getData().getInt(HttpUtil.KEY_REQUEST_POSITION);
		contentLayout.setVisibility(View.INVISIBLE);
		loadLayout.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.VISIBLE);
		tvLoading.setText(TEXT_LOADING);
		loadLayout.setEnabled(false);
	}

	@Override
	public void onFail(Message msg, String message) {
		contentLayout.setVisibility(View.INVISIBLE);
		loadLayout.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.VISIBLE);
		tvLoading.setText(TEXT_RELOAD);
		progressBar.stopRotateAnimator();
		loadLayout.setEnabled(true);
		Logger.logd("onFail -------");
		if(netListener != null){
			netListener.onFail(msg, message);
		}
		ToastFactory.showToast(activity, message);
	}

	public void setOnCompleteClickListener(OnClickListener l){
		onCompleteClickListener = l;
	}
	@Override
	public void onClick(View v) {
		if(v == tvCancel){
			dialog.dismiss();
		}else if(v == tvComplete){
			dialog.dismiss();
			if(success && onCompleteClickListener != null){
				onCompleteClickListener.onClick(v);
			}
		}else{
			if(netListener != null){
				netListener.onRequest(msgHand);
			}
		}
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		HttpUtil.cancelRequest(requestPosition);
	}

	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		// TODO Auto-generated method stub
		success = true;
		contentLayout.setVisibility(View.VISIBLE);
		loadLayout.setVisibility(View.GONE);
		tvLoading.setText(TEXT_LOAD_SUCCESS);
		progressBar.setVisibility(View.GONE);
		loadLayout.setEnabled(false);
		Logger.logd("onSuccess -------");
		if(netListener != null){
			netListener.onSuccess(wheelView,msg, jsonString);
		}
		
	}
}
