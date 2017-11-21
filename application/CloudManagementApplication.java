package com.tg.cloudmanagement.application;


import im.fir.sdk.FIR;
import java.util.LinkedList;
import java.util.List;


import cn.jpush.android.api.JPushInterface;

import com.tg.cloudmanagement.LoginActivity;
import com.tg.cloudmanagement.R;
import com.tg.cloudmanagement.database.SharedPreferencesTools;
import com.tg.cloudmanagement.log.Logger;
import com.tg.cloudmanagement.net.ResponseData;
import com.tg.cloudmanagement.util.Tools;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

public class CloudManagementApplication extends Application{
	private List<Activity> mList = new LinkedList<Activity>();
	private static CloudManagementApplication instance;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		instance = this;
		FIR.init(this);
		JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
        JPushInterface.setLatestNotificationNumber(this, 5);
		Logger.logd("WisdomParkApplication onCreate");
		Tools.mContext = getApplicationContext();
		Tools.userHeadSize = getResources().getDimensionPixelSize(R.dimen.margin_80);
		ResponseData data = SharedPreferencesTools.getUserInfo(Tools.mContext);
		Tools.loadUserInfo(data, null);
	}
	 public static CloudManagementApplication getInstance() {

         if (instance == null)
             instance = new CloudManagementApplication();

         return instance;
     }
	public void exit() { 
        try { 
            for (Activity activity : mList) { 
                if (activity != null){
                	activity.finish(); 
                }
            } 
        } catch (Exception e) { 
            e.printStackTrace(); 
        } finally { 
        	mList.clear();
            System.exit(0); 
        } 
    } 
	
	 public static Activity getCurrentActivity(Context context){
		 CloudManagementApplication application = (CloudManagementApplication)context.getApplicationContext();
		 return application.currentActivity();
	 }
	 
	 public static void gotoLoginActivity(Activity activity){
		 CloudManagementApplication application = (CloudManagementApplication)activity.getApplication();
		 application.goLoginActivity(activity);
	 }
	 
	 public Activity currentActivity(){
		 if(mList.size() == 0){
			 return null;
		 }
		 return mList.get(mList.size() -1);
	 }
	
	 public void add(Activity activity) { 
	     mList.add(activity); 
	 } 
	 
	 public void remove(Activity activity){
		 mList.remove(activity);
	 }
	 
	 private void finishOtherActivity(Class<? extends Activity> clazs){
		 Activity activity;
		 for (int i = 0 ;i < mList.size(); i ++) { 
			 activity = mList.get(i);
             if (activity != null){
            	 if(i == mList.size() - 1 && activity.getClass() == clazs){
            	 }else{
            		 activity.finish(); 
            	 }
             }
         } 
	 }
	 
	 public void goLoginActivity(Activity activity){
		 activity.startActivity(new Intent(activity,LoginActivity.class));
		 finishOtherActivity(LoginActivity.class);
	 }
	 public void onLowMemory() { 
	     super.onLowMemory();     
	     System.gc(); 
	 }
	 
	 public static void addActivity(Activity activity){
		 CloudManagementApplication application = (CloudManagementApplication)activity.getApplication();
		 application.add(activity);
	 }
	 
	 public static void removeActivity(Activity activity){
		 CloudManagementApplication application = (CloudManagementApplication)activity.getApplication();
		 application.remove(activity);
	 }
	 
	 public static void exitApp(Context context){
		 CloudManagementApplication application = (CloudManagementApplication)context.getApplicationContext();
		 application.exit();
	 }
}
