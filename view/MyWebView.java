package com.tg.cloudmanagement.view;

import com.tg.cloudmanagement.base.BaseActivity.ActivityBackListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.WebBackForwardList;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class MyWebView extends WebView implements ActivityBackListener{
	private String htmlText;
	public MyWebView(Context context, AttributeSet attrs, int defStyle,
			boolean privateBrowsing) {
		super(context, attrs, defStyle);
		init(context);
	}

	public MyWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public MyWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}


	private void init(Context context){
		WebSettings wSet = getSettings();
        wSet.setJavaScriptEnabled(true);
        wSet.setJavaScriptCanOpenWindowsAutomatically(true);
        wSet.setAllowFileAccess(true);
        wSet.setPluginState(PluginState.ON);
        wSet.setLoadWithOverviewMode(true);
	}
	
	@Override
	public void loadData(String data, String mimeType, String encoding) {
		htmlText = data;
		super.loadData(data, mimeType, encoding);
	}
	
	@Override
	public void loadDataWithBaseURL(String baseUrl, String data,String mimeType, String encoding, String historyUrl) {
		htmlText = data;
		super.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
	}
	
	public void loadHtmlText(String data){
		loadDataWithBaseURL(null,data, "text/html",  "utf-8", null);
	}
	
	@Override
	public void onBackPressed(Activity activity) {
		// TODO Auto-generated method stub
		if(canGoBack()){
			WebBackForwardList webList = copyBackForwardList();
			int currentIndex = webList.getCurrentIndex();
			String backUrl = webList.getItemAtIndex(currentIndex - 1).getUrl();
			goBack();
			if(currentIndex == 1 && "about:blank".equals(backUrl)){
				if(!TextUtils.isEmpty(htmlText)){
					loadDataWithBaseURL(null,htmlText, "text/html",  "utf-8", null);
				}
			}
		}else{
			activity.finish();
		}
	}
}
