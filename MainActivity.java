package com.tg.cloudmanagement;


import im.fir.sdk.FIR;
import im.fir.sdk.VersionCheckCallback;

import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;


import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.tg.cloudmanagement.MainActivity;
import com.tg.cloudmanagement.R;
import com.tg.cloudmanagement.application.CloudManagementApplication;
import com.tg.cloudmanagement.constant.Contants;
import com.tg.cloudmanagement.fragment.FragmentCommunicate;
import com.tg.cloudmanagement.fragment.FragmentDeskTop;
import com.tg.cloudmanagement.fragment.FragmentManagement;
import com.tg.cloudmanagement.fragment.FragmentMine;
import com.tg.cloudmanagement.info.UserInfo;
import com.tg.cloudmanagement.log.Logger;
import com.tg.cloudmanagement.net.HttpTools;
import com.tg.cloudmanagement.net.MessageHandler;
import com.tg.cloudmanagement.net.RequestConfig;
import com.tg.cloudmanagement.net.RequestParams;
import com.tg.cloudmanagement.net.ResponseData;
import com.tg.cloudmanagement.net.MessageHandler.ResponseListener;
import com.tg.cloudmanagement.serice.MessageService;
import com.tg.cloudmanagement.updateapk.ApkInfo;
import com.tg.cloudmanagement.updateapk.UpdateManager;
import com.tg.cloudmanagement.util.Tools;
import com.tg.cloudmanagement.view.dialog.ToastFactory;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.OnTabChangeListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

public class MainActivity extends FragmentActivity implements ResponseListener, OnTabChangeListener {
	public static final String ACTION_FRESH_USERINFO = "com.tg.cloudmanagement.ACTION_FRESH_USERINFO";
	public static final String KEY_NEDD_FRESH = "need_fresh";
	private String firToken = "64a33a0c08c19980142d4f42e1df05d0";// fir.im 的用户
	private TabHost mTabHost;
	private boolean exit = false;//是否退出
	private boolean needGetUserInfo = true;
	private Fragment fragments[] = { new FragmentDeskTop(),new FragmentCommunicate(), new FragmentManagement(), new FragmentMine() };
	private String tabTexts[] = { "桌面", "通讯录", "微服务", "我的" };
	private MessageHandler msgHand;
	private BroadcastReceiver freshReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action.equals(ACTION_FRESH_USERINFO)) {
				FragmentManager frgManager = getSupportFragmentManager();
				Fragment fragment = frgManager.findFragmentByTag(tabTexts[0]);
				if (fragment != null) {
					FragmentDeskTop desktopFrag = (FragmentDeskTop) fragment;
					//desktopFrag.freshUI();
				}
			} 
		}
	};

	private Runnable getUserInfoRunnable = new Runnable() {
		public void run() {
			getUserInfo();
		}
	};
	private Handler hand = new Handler() {
		public void handleMessage(Message msg) {

		}
	};
	
	public Handler getHandler() {
		return msgHand.getHandler();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/**
		 * 版本检测更新
		 */
		FIR.checkForUpdateInFIR(firToken, new VersionCheckCallback() {
			@Override
			public void onSuccess(String versionJson) {
				try {
					JSONObject obj = new JSONObject(versionJson);
					String apkVersion = obj.getString("versionShort");
					int apkCode = obj.getInt("version");
					String apkSize = obj.getString("updated_at");
					String apkName = obj.getString("name");
					String downloadUrl = obj.getString("installUrl");
					String apkLog = obj.getString("changelog");
					ApkInfo apkinfo = new ApkInfo(downloadUrl, apkVersion,apkSize, apkCode, apkName, apkLog);
					if (apkinfo != null) {
						SharedPreferences mySharedPreferences= getSharedPreferences("versions",0);
						SharedPreferences.Editor editor = mySharedPreferences.edit();
						editor.putString("versionShort", apkVersion);
						editor.commit(); 
						UpdateManager manager = new UpdateManager(MainActivity.this,true);
						// 检查软件更新
						manager.checkUpdate(apkinfo);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		/**
		 * 开启服务
		 */
		startService(new Intent(MainActivity.this, MessageService.class));
		
		JPushInterface.setAlias(MainActivity.this,String.valueOf(UserInfo.uid),new TagAliasCallback() {
			
			@Override
			public void gotResult(int arg0, String arg1, Set<String> arg2) {
				/*Log.d("print","arg0="+arg0);
				Log.d("print","arg1="+arg1);
				Log.d("print","arg2="+arg2);*/
				if(arg0 == 0){
					Logger.logd("TAG","arg0="+arg0);
				}
			}
		});
		CloudManagementApplication.addActivity(this);
		msgHand = new MessageHandler(this);
		msgHand.setResponseListener(this);
		setContentView(R.layout.activity_main);
		
		Intent data = getIntent();
		if (data != null) {
			needGetUserInfo = data.getBooleanExtra(KEY_NEDD_FRESH, true);
		}
		initView();
		if (needGetUserInfo) {
			hand.postDelayed(getUserInfoRunnable, 3000);
		}
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_FRESH_USERINFO);
		registerReceiver(freshReceiver, filter);
		CheckPermission();
	}
	private void CheckPermission() {
	        /*if (Build.VERSION.SDK_INT >= 23)
	        {
	            if (!Settings.canDrawOverlays(getApplicationContext()))
	            {
	                //启动Activity让用户授权
	                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
	                if (intent!=null){
	                    intent.setData(Uri.parse("package:" + getPackageName()));
	                    startActivity(intent);
	                }
	                return;
	            } else
	            {
	                //执行6.0以上绘制代码

	            }
	        } else
	        {
	            //执行6.0以下绘制代码
	        }
*/
		if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
				!= PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
				!= PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this,
					new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
							android.Manifest.permission.ACCESS_FINE_LOCATION,
							android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
							android.Manifest.permission.CAMERA,
							android.Manifest.permission.READ_CONTACTS}, Activity.DEFAULT_KEYS_SEARCH_LOCAL);
		}
	}
	private void getUserInfo() {
		RequestConfig config = new RequestConfig(this, HttpTools.GET_USER_INFO,null);
		RequestParams params = new RequestParams();
		params.put("uid", UserInfo.uid);
		HttpTools.httpGet(Contants.URl.URl_3013,"/administrator/"+UserInfo.uid, config, params);
	}

	private void initView() {
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
		TabHost.TabSpec tab;
		int[] resIDs = { R.drawable.tab1_selector, R.drawable.tab2_selector,
				R.drawable.tab3_selector, R.drawable.tab4_selector };
		for (int i = 0; i < tabTexts.length; i++) {
			tab = mTabHost.newTabSpec(tabTexts[i]).setIndicator(getTabView(resIDs[i], tabTexts[i])).setContent(android.R.id.tabcontent);
			mTabHost.addTab(tab);
		}
		mTabHost.setOnTabChangedListener(this);
		mTabHost.setCurrentTabByTag(tabTexts[0]);
		showFragment(tabTexts[0]);
	}
	@Override
	public void onRequestStart(Message msg, String hintString) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		// TODO Auto-generated method stub
		String response = HttpTools.getContentString(jsonString);
		ResponseData data = HttpTools.getResponseData(response);
		if (msg.arg1 == HttpTools.GET_USER_INFO) {
			Tools.loadUserInfo(data,jsonString);
			sendBroadcast(new Intent(ACTION_FRESH_USERINFO));
			hand.removeCallbacks(getUserInfoRunnable);
			hand.postDelayed(getUserInfoRunnable, 10 * 60 * 1000);
		} 
	}
	@Override
	public void onFail(Message msg, String hintString) {
		// TODO Auto-generated method stub
		if (msg.arg1 == HttpTools.GET_USER_INFO) {
			hand.removeCallbacks(getUserInfoRunnable);
			hand.postDelayed(getUserInfoRunnable, 60 * 1000);
		}
	}
	public View getTabView(int resId, String tab) {
		LayoutInflater layoutInflater = getLayoutInflater();
		View v = layoutInflater.inflate(R.layout.tab_layout, null);
		ImageView img = (ImageView) v.findViewById(R.id.tab_img);
		TextView tabText = (TextView) v.findViewById(R.id.tab_text);
		tabText.setText(tab);
		img.setImageResource(resId);
		return v;
	}
	
	@Override
	public void onTabChanged(String tabId) {
		showFragment(tabId);
	}
	private void showFragment(String tabId) {
		Fragment fragment;
		FragmentManager frgManager = getSupportFragmentManager();
		FragmentTransaction transaction = frgManager.beginTransaction();
		for (int i = 0; i < tabTexts.length; i++) {
			fragment = frgManager.findFragmentByTag(tabTexts[i]);
			if (tabId.equals(tabTexts[i])) {
				if (fragment == null) {
					transaction.add(R.id.contentLayout, fragments[i],tabTexts[i]);
					fragment = fragments[i];
				}
				transaction.show(fragment);
			} else {
				if (fragment != null) {
					transaction.hide(fragment);
				}
			}
		}
		transaction.commit();
	}

	@Override
	protected void onResume() {
		super.onResume();
		int tabId = mTabHost.getCurrentTab();
		if (tabId == 0) {
				FragmentManager frgManager = getSupportFragmentManager();
				Fragment fragment = frgManager.findFragmentByTag(tabTexts[0]);
				if (fragment != null) {
					FragmentDeskTop homeFrag = (FragmentDeskTop) fragment;
					//homeFrag.freshUI();
		}
			hand.removeCallbacks(getUserInfoRunnable);
			hand.post(getUserInfoRunnable);
		}
	}
	@Override
	public void onBackPressed() {
		backPress();
	}
	
	Runnable run = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			exit = false;
		}
	};
	
	/**
	 * 退出程序
	 */
	private void backPress() {
		if (exit) {
			Intent intent = new Intent();
	        // 关闭该Service
			stopService(new Intent(this, MessageService.class));
			hand.removeCallbacksAndMessages(null);
			CloudManagementApplication.exitApp(this);
		} else {
			exit = true;
			ToastFactory.showBottomToast(this, "再按一次退出程序");
			hand.postDelayed(run, 2500);
		}
	}
	@Override
	public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case Activity.RESULT_FIRST_USER: {
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

				} else {

				}
				break;
			}
		}
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}
}
