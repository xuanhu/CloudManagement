package com.tg.cloudmanagement.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tg.cloudmanagement.R;
import com.tg.cloudmanagement.constant.Contants;
import com.tg.cloudmanagement.database.SharedPreferencesTools;
import com.tg.cloudmanagement.info.UserInfo;
import com.tg.cloudmanagement.log.Logger;
import com.tg.cloudmanagement.net.HttpTools;
import com.tg.cloudmanagement.net.ResponseData;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;

public class Tools {
	public static final String PREFERENCES_NAME = "park_cache_map"; 
	private static String[] propertys = {"ro.boot.serialno", "ro.serialno"};  
	public static final String KEY_USER_NAME ="user_name";
	public static final String KEY_USER_TOKEN ="user_token";
	public static final String KEY_GPS_CITY_NAME ="gps_city";
	public static final String KEY_GPS_PROVINCE_NAME ="gps_province";
	public static final String KEY_TIME_STAMP ="time_stamp";
	public static final String KEY_JPUSH_ALIAS ="jpush_alias";
	private static DisplayMetrics metrics ;
	private static Animation progressAnim;
	private static String deviceSN = null;
	public static Context mContext; 
	public static boolean setAlias = false;
	public static int userHeadSize;
	public static DisplayMetrics getDisplayMetrics(Context context){
		if(metrics == null){
			metrics = context.getResources().getDisplayMetrics();
		}
		return metrics;
	}
	
	
	public static int getStatusBarHeight(Activity activity){
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		return frame.top;
	}
	
	public static void initProgressAnim(Context con){
		progressAnim = AnimationUtils.loadAnimation(con, R.anim.progess_anim);
	}
	
	public static Animation getProgressAnim(Context con){
		if(progressAnim == null){
			initProgressAnim(con);
		}
		return progressAnim;
	}
	
	public static String getRootPath(Context con){
		return con.getFilesDir()+"/park";
	}
	
	public static String getCompletePath(Context con,int orderID,int groupPosition, int position, int index){
		return getRootPath(con)+"/"+orderID+"/"+"complete_"+groupPosition+"_"+position+"_"+index+".jpg";
	}
	
	public static void call(Context context,String phoneNumber){
		Intent intent = new Intent(Intent.ACTION_DIAL);
		intent.setData(Uri.parse("tel:"+phoneNumber));
		context.startActivity(intent);
	}
	
	public static int getTextHeight(String text,float fontSize){
		Rect bound = new Rect();
		Paint p = new Paint();
		p.setTextSize(fontSize);
		p.getTextBounds(text, 0, 1, bound);
		return bound.bottom - bound.top;
	}
	
	public static void checkTelephonePermission(Context con){
	}
	
	public static int getTextWidth(String text,float fontSize){
		Rect bound = new Rect();
		Paint p = new Paint();
		p.setTextSize(fontSize);
		p.getTextBounds(text, 0, 1, bound);
		return bound.right - bound.left;
	}
	
	public static int sp2px(Context context, float spValue) { 
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity; 
        return (int) (spValue * fontScale + 0.5f); 
    }  
	
	public static SpannableString getSpannableString(String text, int start, int end,int color) {
	    SpannableString spanString = new SpannableString(text);    
	    ForegroundColorSpan span = new ForegroundColorSpan(color);    
	    spanString.setSpan(span, start , end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); 
	    return spanString;    
	}
	
	public static String getDateToString(long time) {
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        return sf.format(d);
    }
	
	public static String getSecondToString(long time) {
		Date d = new Date(time);
		SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
		return sf.format(d);
	}
	
	public static String getSimpleDateToString(long time) {
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        return sf.format(d);
    }
	
	public static long dateString2Seconds(String date){
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date d = sf.parse(date);
			return d.getTime()/1000;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	public static long simpledateString2Mini(String date){
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date d = sf.parse(date);
			return d.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	public static long getCurrentMillis(){
		return Calendar.getInstance().getTimeInMillis();
	}
	
	public static long dateString2Millis(String date){
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date d = sf.parse(date);
			return d.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	private static boolean isBackgroundRunning(Context context) {
		String processName = "match.android.activity";

		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);

		if (activityManager == null) return false;
		// get running application processes
		List<RunningAppProcessInfo> processList = activityManager.getRunningAppProcesses();
		for (RunningAppProcessInfo process : processList) {
			if (process.processName.startsWith(processName)) {
				boolean isBackground = process.importance != RunningAppProcessInfo.IMPORTANCE_FOREGROUND && 
						process.importance != RunningAppProcessInfo.IMPORTANCE_VISIBLE;
				boolean isLockedState = keyguardManager.inKeyguardRestrictedInputMode();
				if (isBackground || isLockedState){
					return true;
				}
				else {
					return false;
				}
			}
		}
		return false;
	}
	
	public static boolean toBackgroud(Context con){
		String packageName = "null";
		boolean result = false;
		ActivityManager am = (ActivityManager)con.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> taskInfos= am.getRunningTasks(1);
		if(taskInfos != null && taskInfos.size() > 0){
			RunningTaskInfo info = taskInfos.get(0);
			packageName = info.topActivity.getPackageName();
		}
		Logger.logd("current packageName = "+packageName);
		result = !packageName.equals(con.getPackageName());
		return result;
	}
	
	public static boolean appAlreadyLauchAtTop(Context con){
		String packageName = "null";
		boolean result = false;
		ActivityManager am = (ActivityManager)con.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> taskInfos= am.getRunningTasks(1);
		if(taskInfos != null && taskInfos.size() > 0){
			RunningTaskInfo info = taskInfos.get(0);
			if(info.topActivity != null){
				packageName = info.topActivity.getPackageName();
			}
		}
		Logger.logd("current packageName = "+packageName);
		result = packageName.equals(con.getPackageName());
		return result;
	}
	
	public static Bitmap compressImage(Bitmap image) {  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        image.compress(CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;  
        while ( baos.toByteArray().length / 1024>100) {   //循环判断如果压缩后图片是否大于100kb,大于继续压缩                 
            baos.reset();//重置baos即清空baos  
            image.compress(CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10  
        }  
        if(image != null && !image.isRecycled()){
        	image.recycle();
        	image = null;
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream�?  
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片  
        return bitmap;  
    } 
	
	public static Bitmap getSmallBitmap(String path) {  
        BitmapFactory.Options newOpts = new BitmapFactory.Options();  
      //开始读入图片，此时把options.inJustDecodeBounds 设回true了 
        newOpts.inJustDecodeBounds = true;  
        Bitmap bitmap = BitmapFactory.decodeFile(path, newOpts);  
        newOpts.inJustDecodeBounds = false;  
        int w = newOpts.outWidth;  
        int h = newOpts.outHeight;  
      //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为   
        float hh = mContext.getResources().getDimensionPixelSize(R.dimen.margin_90);
        float ww = mContext.getResources().getDimensionPixelSize(R.dimen.margin_90);
      //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可  
        int be = 1;//be=1表示不缩放   
        if (w >= h && w > ww) {//如果宽度大的话根据宽度固定大小缩放  
            be = (int) (newOpts.outWidth / ww);  
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放  
            be = (int) (newOpts.outHeight / hh);  
        }  
        if (be <= 0) {
        	be = 1;  
        }
        newOpts.inSampleSize = be;//设置缩放比例  
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了  
        bitmap = BitmapFactory.decodeFile(path, newOpts);  
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩  
    }  
	
    public static Bitmap getSmallBitmap(Bitmap image) {  
    	if(image == null){
    		return null;
    	}
        ByteArrayOutputStream baos = new ByteArrayOutputStream();    
        image.compress(CompressFormat.JPEG, 100, baos);
        if( baos.toByteArray().length >= 1024*1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出    
            baos.reset();
            image.compress(CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
        }  
        if(image != null && !image.isRecycled()){
        	image.recycle();
        	image = null;
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());  
        BitmapFactory.Options newOpts = new BitmapFactory.Options();  
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了  
        newOpts.inJustDecodeBounds = true;  
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);  
        newOpts.inJustDecodeBounds = false;  
        int w = newOpts.outWidth;  
        int h = newOpts.outHeight;
        
        float hh = mContext.getResources().getDimensionPixelSize(R.dimen.margin_90);
        float ww = mContext.getResources().getDimensionPixelSize(R.dimen.margin_90);
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可  
        int be = 1;//be=1表示不缩放  
        if (w >= h && w > ww) {//如果宽度大的话根据宽度固定大小缩放  
            be = (int) (newOpts.outWidth / ww);  
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放  
            be = (int) (newOpts.outHeight / hh);  
        }  
        if (be <= 0) {
        	be = 1;  
        }
        newOpts.inSampleSize = be;//设置缩放比例  
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了  
        isBm = new ByteArrayInputStream(baos.toByteArray());  
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);  
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩  
    }  
    
    public static boolean saveImageToLocal(Resources res,int resID, String path){
    	boolean result = false;
    	File file = new File(path);
    	if(file.exists()){
    		return true;
    	}else{
    		file.getParentFile().mkdirs();
    	}
    	FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
    	Bitmap btm = BitmapFactory.decodeResource(res, resID);
    	if(out != null && btm.compress(CompressFormat.JPEG, 100, out)){
    		result = true;
    	}
    	if(out != null ){
    		try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	return result;
    }
	
    public static void compressImage(Context con,Bitmap image ,String path) {  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        image.compress(CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;  
        int len = baos.toByteArray().length;
        while (len > 300*1024) {  //循环判断如果压缩后图片是否大于300kb,大于继续压缩    
        	len = baos.toByteArray().length;
        	baos.reset();//重置baos即清空baos  
    		options -= 10;//每次都减少10  
        	if(options < 0){
        		options = 0;
        	}
            image.compress(CompressFormat.JPEG, options, baos);
        }  
        if(image != null && !image.isRecycled()){
			image.recycle();
			image = null;
		}
        FileOutputStream stream = null;
		try {
			File file = new File(path);
			if(!file.getParentFile().exists()){
				file.getParentFile().mkdirs();
			}
			stream = new FileOutputStream(file);
			baos.writeTo(stream);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			baos.reset();
			baos.close();
			if(stream != null){
				stream.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static String getSimpleProvinceName(String provinceName){
    	String simpleName = "";
    	if(TextUtils.isEmpty(provinceName)){
    		return simpleName;
    	}
    	if(provinceName.length() >= 2){
    		simpleName = provinceName.substring(0, 2);
    	}
    	return simpleName;
    }
    
    public static void saveImageToPath(Context con,String sPath,String oPath) {  
        BitmapFactory.Options newOpts = new BitmapFactory.Options();  
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了  
        newOpts.inJustDecodeBounds = true;  
        Bitmap bitmap = BitmapFactory.decodeFile(sPath, newOpts);  
        newOpts.inJustDecodeBounds = false;  
        int w = newOpts.outWidth;  
        int h = newOpts.outHeight;  
        Logger.logd("w ="+w +" h = "+h);
        float hh = 500f;
        float ww = 500f;
        int be = 1;
        if (w >= h && w > ww) { 
            be = (int) (newOpts.outWidth / ww);  
        } else if (w < h && h > hh) {
            be = (int) (newOpts.outHeight / hh);  
        }  
        if (be <= 0)  {
        	be = 1;  
        }
        newOpts.inSampleSize = be;
        bitmap = BitmapFactory.decodeFile(sPath, newOpts);  
        int newW = newOpts.outWidth;  
        int newH = newOpts.outHeight;  
        Logger.logd("getByteCount = "+bitmap.getHeight() * bitmap.getRowBytes()+"  newW = "+newW +" newH = "+newH+"   inPreferredConfig = "+newOpts.inPreferredConfig);
        compressImage(con,bitmap,oPath);//压缩好比例大小后再进行质量压缩  
    }  
    
    public static void saveUserInfo(Context context){
    	if(UserInfo.uid <= 0){
    		return;
    	}
    	JSONObject jsonObj = new JSONObject();
    	try {
			jsonObj.put("uid", UserInfo.uid);
			jsonObj.put("internal", UserInfo.internal);
			jsonObj.put("disable", UserInfo.disable);
			jsonObj.put("job_id", UserInfo.jobId);
			jsonObj.put("online", UserInfo.online);
			jsonObj.put("admintype", UserInfo.admintype);
			jsonObj.put("Jobonline", UserInfo.Jobonline);
			jsonObj.put("sort", UserInfo.sort);
			jsonObj.put("sex", UserInfo.sex);
			jsonObj.put("username", UserInfo.username);
			jsonObj.put("realname", UserInfo.realname);
			jsonObj.put("password", UserInfo.password);
			jsonObj.put("identifier", UserInfo.identifier);
			jsonObj.put("Icon", UserInfo.headUrl);
			jsonObj.put("job_name", UserInfo.jobName);
			jsonObj.put("property_coding", UserInfo.propertyCoding);
			jsonObj.put("property_name", UserInfo.propertyName);
			jsonObj.put("groupid", UserInfo.groupid);
			jsonObj.put("notes", UserInfo.notes);
			jsonObj.put("logintime", UserInfo.logintime);
			jsonObj.put("loginip", UserInfo.loginip);
			jsonObj.put("lastlogintime", UserInfo.lastlogintime);
			jsonObj.put("lastloginip", UserInfo.lastloginip);
			jsonObj.put("onlinetime", UserInfo.onlinetime);
			jsonObj.put("weathercity", UserInfo.weathercity);
			jsonObj.put("Jobonlinetext", UserInfo.Jobonlinetext);
			jsonObj.put("purview", UserInfo.purview);
			jsonObj.put("createtime", UserInfo.createtime);
			jsonObj.put("uptime", UserInfo.uptime);
			jsonObj.put("operator", UserInfo.operator);
			jsonObj.put("tel", UserInfo.tel);
			jsonObj.put("Company_tel", UserInfo.Company_tel);
			jsonObj.put("Mobile", UserInfo.Mobile);
			jsonObj.put("email", UserInfo.email);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
    	SharedPreferencesTools.saveUserInfoJson(context, jsonObj);
    }
    
    public static boolean loadUserInfo(ResponseData data,String response){
    	if(!TextUtils.isEmpty(response)){
    		String jsonString = HttpTools.getContentString(response);
    		JSONArray jsonArrayContent = HttpTools.getContentJsonArray(response);
    		JSONArray jsonArrayData = HttpTools.getData(jsonString);
    		if(jsonArrayContent != null && jsonArrayContent.length() > 0){
    			JSONObject jsonObj = jsonArrayContent.optJSONObject(0);
    			SharedPreferencesTools.saveUserInfoJson(Tools.mContext, jsonObj);
    		}else if(jsonArrayData != null && jsonArrayData.length() > 0){
    			JSONObject jsonObj = jsonArrayData.optJSONObject(0);
    			SharedPreferencesTools.saveUserInfoJson(Tools.mContext, jsonObj);
    		}
    	}
    	boolean changed = false;
		int userId = data.getInt("uid",-1);
		if(userId <= 0){
			return false;
		}
		String userName = data.getString("username");
		String realName = data.getString("realname");
		String password = data.getString("password"); 
		String headUrl = data.getString("headUrl"); 
		String identifier = data.getString("identifier");
		int internal = data.getInt("internal");
		int disable = data.getInt("disable");
		int job_id = data.getInt("job_id");
		String sex = data.getString("sex");
		String tel = data.getString("tel");
		String Company_tel = data.getString("Company_tel");
		String Mobile = data.getString("Mobile");
		String email = data.getString("email");
		String Icon = data.getString("Icon");
		String job_name = data.getString("job_name");
		String property_coding = data.getString("property_coding");
		String property_name = data.getString("property_name");
		String groupid = data.getString("groupid");
		String notes = data.getString("notes");
		String logintime = data.getString("logintime");
		String loginip = data.getString("loginip");
		String lastlogintime = data.getString("lastlogintime");
		String lastloginip = data.getString("lastloginip");
		String onlinetime = data.getString("onlinetime");
		String weathercity = data.getString("weathercity");	
		String Jobonlinetext = data.getString("Jobonlinetext");	
		String purview = data.getString("purview");	
		String createtime = data.getString("createtime");	
		String uptime = data.getString("uptime");	
		String operator = data.getString("operator");	
		int online = data.getInt("online");
		int admintype = data.getInt("admintype");
		int Jobonline = data.getInt("Jobonline");
		int sort = data.getInt("sort");
		
		Tools.saveUserName(Tools.mContext, UserInfo.username);
		
		initInfo:{
			if(UserInfo.uid != userId){
				changed = true;
				break initInfo;
			}
			if(UserInfo.internal != internal){
				changed = true;
				break initInfo;
			}
			if(UserInfo.disable != disable){
				changed = true;
				break initInfo;
			}
			if(UserInfo.jobId != job_id){
				changed = true;
				break initInfo;
			}
			if(UserInfo.online != online){
				changed = true;
				break initInfo;
			}
			if(UserInfo.admintype != admintype){
				changed = true;
				break initInfo;
			}
			if(UserInfo.Jobonline != Jobonline){
				changed = true;
				break initInfo;
			}
			if(UserInfo.sort != sort){
				changed = true;
				break initInfo;
			}
			if(!TextUtils.equals(UserInfo.sex, sex)){
				changed = true;
				break initInfo;
			}
			if(!TextUtils.equals(UserInfo.username, userName)){
				changed = true;
				break initInfo;
			}
			if(!TextUtils.equals(UserInfo.realname, realName)){
				changed = true;
				break initInfo;
			}
			if(!TextUtils.equals(UserInfo.password, password)){
				changed = true;
				break initInfo;
			}
			if(!TextUtils.equals(UserInfo.identifier, identifier)){
				changed = true;
				break initInfo;
			}
			if(!TextUtils.equals(UserInfo.jobName, job_name)){
				changed = true;
				break initInfo;
			}
			if(!TextUtils.equals(UserInfo.headUrl, headUrl)){
				changed = true;
				break initInfo;
			}
			if(!TextUtils.equals(UserInfo.propertyCoding, property_coding)){
				changed = true;
				break initInfo;
			}
			if(!TextUtils.equals(UserInfo.propertyName, property_name)){
				changed = true;
				break initInfo;
			}
			if(!TextUtils.equals(UserInfo.groupid, groupid)){
				changed = true;
				break initInfo;
			}
			if(!TextUtils.equals(UserInfo.notes, notes)){
				changed = true;
				break initInfo;
			}
			if(!TextUtils.equals(UserInfo.logintime, logintime)){
				changed = true;
				break initInfo;
			}
			if(!TextUtils.equals(UserInfo.loginip, loginip)){
				changed = true;
				break initInfo;
			}
			if(!TextUtils.equals(UserInfo.lastlogintime, lastlogintime)){
				changed = true;
				break initInfo;
			}
			if(!TextUtils.equals(UserInfo.lastloginip, lastloginip)){
				changed = true;
				break initInfo;
			}
			if(!TextUtils.equals(UserInfo.onlinetime, onlinetime)){
				changed = true;
				break initInfo;
			}
			if(!TextUtils.equals(UserInfo.weathercity, weathercity)){
				changed = true;
				break initInfo;
			}
			if(!TextUtils.equals(UserInfo.Jobonlinetext, Jobonlinetext)){
				changed = true;
				break initInfo;
			}
			if(!TextUtils.equals(UserInfo.purview, purview)){
				changed = true;
				break initInfo;
			}
			if(!TextUtils.equals(UserInfo.createtime, createtime)){
				changed = true;
				break initInfo;
			}
			if(!TextUtils.equals(UserInfo.uptime, uptime)){
				changed = true;
				break initInfo;
			}
			if(!TextUtils.equals(UserInfo.operator, operator)){
				changed = true;
				break initInfo;
			}
			if(!TextUtils.equals(UserInfo.headUrl, Icon)){
				changed = true;
				break initInfo;
			}
			if(!TextUtils.equals(UserInfo.tel, tel)){
				changed = true;
				break initInfo;
			}
			if(!TextUtils.equals(UserInfo.Company_tel, Company_tel)){
				changed = true;
				break initInfo;
			}
			if(!TextUtils.equals(UserInfo.email, email)){
				changed = true;
				break initInfo;
			}
			if(!TextUtils.equals(UserInfo.Mobile, Mobile)){
				changed = true;
				break initInfo;
			}
		}
		UserInfo.uid = userId;
		UserInfo.username = userName;
		UserInfo.realname = realName;
		UserInfo.password = password;
		UserInfo.internal=internal;
		UserInfo.headUrl = headUrl;
		UserInfo.disable =disable;
		UserInfo.jobId = job_id;
		UserInfo.sex = sex;
		UserInfo.online = online;
		UserInfo.admintype = admintype;
		UserInfo.Jobonline = Jobonline;
		UserInfo.sort = sort;
		UserInfo.identifier = identifier;
		UserInfo.jobName = job_name;
		UserInfo.propertyName = property_name;
		UserInfo.propertyCoding=property_coding;
		UserInfo.groupid=groupid;
		UserInfo.notes=notes;
		UserInfo.logintime=logintime;
		UserInfo.loginip=loginip;
		UserInfo.lastlogintime = lastlogintime;
		UserInfo.lastloginip = lastloginip;
		UserInfo.onlinetime = onlinetime;
		UserInfo.weathercity = weathercity;
		UserInfo.Jobonlinetext = Jobonlinetext;
		UserInfo.purview = purview;
		UserInfo.createtime = createtime;
		UserInfo.uptime = uptime;
		UserInfo.operator = operator;
		UserInfo.headUrl = Icon;
		UserInfo.tel = tel;
		UserInfo.Company_tel = Company_tel;
		UserInfo.email = email;
		UserInfo.Mobile = Mobile;
		return changed;
	}
    
    public static String getPathByDocumentUri(Context context, Uri contentUri){
    	String filePath = null;
    	String wholeID = contentUri.getLastPathSegment();
        String ids[] = wholeID.split(":");
        String id = null;
        if(ids != null && ids.length == 2){
        	id = ids[1];
        	String[] column = { MediaColumns.DATA };
        	String sel = BaseColumns._ID + "=?";
        	Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column,
        			sel, new String[] { id }, null);
        	if(cursor == null){
        		return null;
        	}
        	int columnIndex = cursor.getColumnIndex(column[0]);
        	if (cursor.moveToFirst()) {
        		filePath = cursor.getString(columnIndex);
        	}
        	cursor.close();
        }
        return filePath;
    }
    
    public static String getPathByGeneralUri(Context context, Uri contentUri){
    	String filePath = null;
   	 	String[] projection = {MediaColumns.DATA };   
    	Cursor cursor = context.getContentResolver().query(contentUri, projection, null, null, null);
    	if(cursor == null){
    		return null;
    	}
    	int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);   
    	if(cursor.moveToFirst()){
    		filePath = cursor.getString(column_index);
    	}
    	cursor.close();
    	return filePath;
    }
    public static String getPathByUri(Context context, Uri contentUri) 
    {   
    	//file:///storage/emulated/0/DCIM/Camera/IMG20150524000853.jpg
    	//content://com.android.providers.media.documents/document/image:3951
    	//content://media/external/images/media/3951
    	String filePath = null;
    	Logger.logd("contentUri = "+contentUri);
    	String scheme = contentUri.getScheme();
    	if("file".equals(scheme)){
    		filePath = contentUri.getPath();
    	}else{
    		filePath = getPathByGeneralUri(context,contentUri);
    		if(filePath == null){
    			filePath = getPathByDocumentUri(context,contentUri);
    		}
    	}
    	return filePath;   
    } 
    public static void insertPhotoToAlbum(Context con,String path){
		MediaScannerConnection.scanFile(con, new String[]{path}, null, null);
	}
    
    public static void scanPhotos(String filePath, Context context) {
        Intent intent = new Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setData(uri);
        context.sendBroadcast(intent);
}
    
    public static void saveImageToPath(Context con,Uri uri,String oPath) {  
    	String sPath = getPathByUri(con,uri);
    	BitmapFactory.Options newOpts = new BitmapFactory.Options();  
    	//开始读入图片，此时把options.inJustDecodeBounds 设回true了  
    	newOpts.inJustDecodeBounds = true;  
    	Bitmap bitmap = BitmapFactory.decodeFile(sPath, newOpts);  
    	newOpts.inJustDecodeBounds = false;  
    	int w = newOpts.outWidth;  
    	int h = newOpts.outHeight;  
    	Logger.logd("w ="+w +" h = "+h);
    	float hh = 500f;
    	float ww = 500f;
    	int be = 1;
    	if (w >= h && w > ww) { 
    		be = (int) (newOpts.outWidth / ww);  
    	} else if (w < h && h > hh) {
    		be = (int) (newOpts.outHeight / hh);  
    	}  
    	if (be <= 0)  {
    		be = 1;  
    	}
    	newOpts.inSampleSize = be;
    	bitmap = BitmapFactory.decodeFile(sPath, newOpts);  
    	int newW = newOpts.outWidth;  
    	int newH = newOpts.outHeight;  
    	Logger.logd("getByteCount = "+bitmap.getHeight() * bitmap.getRowBytes()+"  newW = "+newW +" newH = "+newH+"   inPreferredConfig = "+newOpts.inPreferredConfig);
    	compressImage(con,bitmap,oPath);//压缩好比例大小后再进行质量压缩  
    } 
    
   
    
	public static Bitmap createRoundConerImage(Bitmap source,float radiu)  
    {  
        final Paint paint = new Paint();  
        paint.setAntiAlias(true);  
        Bitmap target = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Config.ARGB_8888);  
        Canvas canvas = new Canvas(target);  
        RectF rect = new RectF(0, 0, source.getWidth(), source.getHeight());  
        canvas.drawRoundRect(rect, radiu, radiu, paint);  
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));  
        canvas.drawBitmap(source, 0, 0, paint); 
        source.recycle();
        source = null;
        return target;  
    }  
	
	public static Bitmap createRoundConerImage(Context context,int drawable,float radiu)  
    {  
		Bitmap source = BitmapFactory.decodeResource(context.getResources(), drawable);
        final Paint paint = new Paint();  
        paint.setAntiAlias(true);  
        Bitmap target = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Config.ARGB_8888);  
        Canvas canvas = new Canvas(target);  
        RectF rect = new RectF(0, 0, source.getWidth(), source.getHeight());  
        canvas.drawRoundRect(rect, radiu, radiu, paint);  
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));  
        canvas.drawBitmap(source, 0, 0, paint); 
        source.recycle();
        source = null;
        return target;  
    }  
	public static SpannableString getForegroundSpannableString(String text,int start,int end,int color) {    
		if(text == null || text.trim().length() <= 0){
			return null;
		}
	    SpannableString spanString = new SpannableString(text);    
	    ForegroundColorSpan span = new ForegroundColorSpan(color);    
	    spanString.setSpan(span, start , end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);    
	    return spanString;    
	}   
	
	public static String getTwoDecimalPlaces(float num){
		String str = "0.00";
		DecimalFormat df = new DecimalFormat("0.00");
		str = df.format(num);
		return str;
	}
	
    public static void hideKeyboard(final View v)
    {
        Timer timer = new Timer();
	      timer.schedule(new TimerTask(){
	      @Override
	      public void run()
	      {
	          InputMethodManager imm = ( InputMethodManager ) v.getContext( ).getSystemService( Context.INPUT_METHOD_SERVICE );     
	          if ( imm.isActive( ) )
	          {     
	              imm.hideSoftInputFromWindow( v.getApplicationWindowToken( ) , 0 );
	          }    
	       }  
	      }, 10);
	}
    
    public static void showKeyboard(final View v)
    {
        Timer timer = new Timer();
	      timer.schedule(new TimerTask(){
	      @Override
	      public void run()
	      {
	          InputMethodManager imm = ( InputMethodManager ) v.getContext( ).getSystemService( Context.INPUT_METHOD_SERVICE );     
	          imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
	       }  
	      }, 100);
	}
	
	public static String getOneDecimalString(float num){
		String str = "0.0";
		DecimalFormat df = new DecimalFormat("0.0");
		str = df.format(num);
		return str;
	}
	
	public static String getStringByFormat(long num,String format){
		String str = format;
		DecimalFormat df = new DecimalFormat(format);
		str = df.format(num);
		return str;
	}
	
	public static String getStringByFormat(double num,String format){
		String str = format;
		DecimalFormat df = new DecimalFormat(format);
		str = df.format(num);
		return str;
	}
	
	
	public static float getOneDecimalFloat(float num){
		float result = 0.0f;
		DecimalFormat df = new DecimalFormat("0.0");
		String str = df.format(num);
		try{
			result = Float.parseFloat(str);
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		return result;
	}
	
	public static boolean getIsFirstTimeStarting(Context con){
		boolean result = getSysShare(con).getBoolean(Contants.First.IS_FIRST_TIME_STARTING, true);
		return result;
	}
	
	public static void setIsFirstTimeStarting(Context con, boolean isFrist){
		getSysShare(con).edit().putBoolean(Contants.First.IS_FIRST_TIME_STARTING, isFrist).commit();
	}
	
	public static boolean checkTelephoneNumber(String mobiles) {  
		 /* 
	    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188 
	    联通：130、131、132、152、155、156、185、186 
	    电信：133、153、180、189、（1349卫通） 
	    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9 
	    */  
	    String telRegex = "[1][34578]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。  
	    if (TextUtils.isEmpty(mobiles)){
	    	return false;  
	    }else {
	    	return mobiles.matches(telRegex);  
	    }
   }  	
	
	public static boolean emailValidation(String email) {
        String regex = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
        return email.matches(regex);
  }
	
	public static boolean isCity(String name){
		if(TextUtils.isEmpty(name)){
			return false;
		}
		if(name.startsWith("北京") || name.startsWith("天津")||
				name.startsWith("上海")|| name.startsWith("重庆")){
			return true;
		}
		return false;
	}
	
      
    
    public static String getAndroidOsSystemProperties(String key) {  
        String ret;  
        Method systemProperties_get = null;
        try {  
            systemProperties_get = Class.forName("android.os.SystemProperties").getMethod("get", String.class);  
            if ((ret = (String) systemProperties_get.invoke(null, key)) != null){
            	return ret;  
            }
        } catch (Exception e) {  
            e.printStackTrace();  
            return null;  
        }  
        return "";  
    }  
    
    public static String getDeviceSN(Context con){
    	if(TextUtils.isEmpty(deviceSN)){
    		deviceSN = getDeviceId(con);
    		deviceSN = deviceSN.replaceAll("-", "_").replaceAll(":", "_");
    	}
    	return deviceSN;
    }
    
    public static Bitmap getCircleImage(Bitmap source, int size)  
    {  
    	int srcW = source.getWidth();
    	int srcH = source.getHeight();
    	if(size <= 0){
    		size = Math.min(srcW, srcH);
    	}
        final Paint paint = new Paint();  
        paint.setAntiAlias(true);  
        Bitmap target = Bitmap.createBitmap(size, size, Config.ARGB_8888);  
        /** 
         * 产生一个同样大小的画布 
         */  
        Canvas canvas = new Canvas(target);  
        /** 
         * 首先绘制圆形 
         */  
        canvas.drawCircle(size / 2, size / 2, size / 2, paint);  
        /** 
         * 使用SRC_IN 
         */  
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));  
        /** 
         * 绘制图片 
         */  
        int x = (srcW -size)/2;
        int y = (srcH - size)/2;
        Rect srcRect = new Rect(
        		x,
        		y,
        		x+size,
        		y+size
        		);
        Rect tagRect = new Rect(
        		0,
        		0,
        		size,
        		size
        		);
        canvas.drawBitmap(source, srcRect, tagRect, paint); 
        return target;  
    }  
    
    /**
     * deviceID的组成为：渠道标志+识别符来源标志+hash后的终端识别符
     * 
     * 渠道标志为：
     * 1，andriod（a）
     *
     * 识别符来源标志：
     * 1， wifi mac地址（wifi）；
     * 2， IMEI（imei）；
     * 3， 序列号（sn）；
     * 4， id：随机码。若前面的都取不到时，则随机生成一个随机码，需要缓存。
     *
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {
	    StringBuilder deviceId = new StringBuilder();
	    // 渠道标志
	    deviceId.append("a_");
	    try {
	    	for(int i = 0; i < propertys.length; i++ ){
	    		String sn = getAndroidOsSystemProperties(propertys[i]);
	    		if(!TextUtils.isEmpty(sn)){
	    			deviceId.append("serialno_");
	    			deviceId.append(sn);
	    			return deviceId.toString();
	    		}
	    	}
	    	  //wifi mac地址
		    WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		    WifiInfo info = wifi.getConnectionInfo();
		    String wifiMac = info.getMacAddress();
		    if(!TextUtils.isEmpty(wifiMac)){
			    deviceId.append("wifi_");
			    deviceId.append(wifiMac);
			    return deviceId.toString();
		    }
		  //IMEI（imei）
		    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		    String imei = tm.getDeviceId();
		    if(!TextUtils.isEmpty(imei)){
			    deviceId.append("imei_");
			    deviceId.append(imei);
			    return deviceId.toString();
		    }
		  //序列号（sn）
		    String sn = tm.getSimSerialNumber();
		    if(!TextUtils.isEmpty(sn)){
			    deviceId.append("sn_");
			    deviceId.append(sn);
			    return deviceId.toString();
		    }
		    //如果上面都没有， 则生成一个id：随机码
		    String uuid = getUUID(context);
		    if(!TextUtils.isEmpty(uuid)){
			    deviceId.append("uuid_");
			    deviceId.append(uuid);
			    return deviceId.toString();
	    	}
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	deviceId.append("uuid_").append(getUUID(context));
	    }
	    return deviceId.toString();
    }

    /**
    * 得到全局唯一UUID
    */
    public static String getUUID(Context context){
	    SharedPreferences mShare = getSysShare(context);
	    String uuid = null;
		if(mShare != null){
			uuid = mShare.getString("uuid", "");
	    }
	    if(TextUtils.isEmpty(uuid)){
	    	uuid = UUID.randomUUID().toString();
	    	uuid = uuid.replaceAll("-", "_").replaceAll(":", "_");
	    	saveSysMap(context , "uuid", uuid);
	    }
	    return uuid;
    }
    
    public static SharedPreferences getSysShare(Context con){
    	return con.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }
    
    public static String getSysMapStringValue(Context con, String key){
    	return getSysShare(con).getString(key, "");
    }
    
    public static boolean getSysMapBooleanValue(Context con, String key,boolean defValue){
    	return getSysShare(con).getBoolean(key, defValue);
    }
    
    public static void saveUserName(Context con, String userName){
    	getSysShare(con).edit().putString(KEY_USER_NAME, userName).commit();
    }
    
    public static String getUserName(Context con){
    	return getSysShare(con).getString(KEY_USER_NAME, "");
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
    
    public static void saveDateInfo(Context con,String time){
    	getSysShare(con).edit().
    	putString("time", time).commit();
    }
    public static String getDateName(Context con){
    	return getSysShare(con).getString("time", "");	
    }
    
    public static String getOfficeName(Context con){
    	return getSysShare(con).getString("office", "");	
    }
    
    public static void saveOfficeInfo(Context con,String time){
    	getSysShare(con).edit().
    	putString("office", time).commit();
    }
    
    public static String getManagementName(Context con){
    	return getSysShare(con).getString("management", "");	
    }
    
    public static void saveManagementInfo(Context con,String time){
    	getSysShare(con).edit().
    	putString("management", time).commit();
    }
    
    public static String getSignatures(Context context) {
    	  PackageManager pm = context.getPackageManager();
    	  PackageInfo packageInfo = null;
		try {
			packageInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		if(packageInfo != null){
			return packageInfo.signatures[0].toCharsString();
		}
    	  return null;
	}
    
    public static boolean checkIDNumber(String idNum){
		if(idNum.length() < 15){
			return false;
		}
		Pattern idNumPattern = Pattern.compile("(\\d{14}[0-9a-zA-Z])|(\\d{17}[0-9a-zA-Z])");
	    Matcher idNumMatcher = idNumPattern.matcher(idNum); 
	    if(idNumMatcher.matches()){
	    	 Pattern birthDatePattern= Pattern.compile("\\d{6}(\\d{4})(\\d{2})(\\d{2}).*");//身份证上的前6位以及出生年月日  
             //通过Pattern获得Matcher  
             Matcher birthDateMather= birthDatePattern.matcher(idNum);  
             //通过Matcher获得用户的出生年月日  
             if(birthDateMather.find()){  
                 String year = birthDateMather.group(1);  
                 String month = birthDateMather.group(2);  
                 String date = birthDateMather.group(3);  
                 Logger.logd(year+"年"+month+"月"+date+"日");                 
                 try{
                	 int y = Integer.parseInt(year);
                	 int m = Integer.parseInt(month);
                	 int d = Integer.parseInt(date);
                	 if(y< 1930 || y > 2004 || m == 0 || m > 12 || d == 0 || d > 31){
                		 return false;
                	 }
                 }catch(NumberFormatException e){
                	 e.printStackTrace();
                	 return false;
                 }
                 //输出用户的出生年月日  
             }    
	    	return true;
	    }else{
	    	return false;
	    }
	}
    
    public static int getAppVersion(Context context) {
        try {
          PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
          return info.versionCode;
        } catch (NameNotFoundException e) {
          e.printStackTrace();
        }
        return 1;
      }
    
    public static File getCacheDir(Context context, String uniqueName) { 
	    // Check if media is mounted or storage is built-in, if so, try and use external cache dir 
	    // otherwise use internal cache dir 
	    String cachePath = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED 
	            || !Environment.isExternalStorageRemovable() ? 
	                    context.getExternalCacheDir().getPath() : context.getCacheDir().getPath(); 
	    return new File(cachePath + File.separator + uniqueName); 
	}
	
	public static String getCacheDirPath(Context context) { 
	    // Check if media is mounted or storage is built-in, if so, try and use external cache dir 
	    // otherwise use internal cache dir 
	    String cachePath = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED 
	            || !Environment.isExternalStorageRemovable() ? 
	                    context.getExternalCacheDir().getPath() : context.getCacheDir().getPath(); 
	    return cachePath; 
	}
	
	public static String hashKeyForDisk(String key) {
		  String cacheKey;
		  try {
		    final MessageDigest mDigest = MessageDigest.getInstance("MD5");
		    mDigest.update(key.getBytes());
		    cacheKey = bytesToHexString(mDigest.digest());
		  } catch (NoSuchAlgorithmException e) {
		    cacheKey = String.valueOf(key.hashCode());
		  }
		  return cacheKey;
		}
	
	private static String bytesToHexString(byte[] bytes) {
		  StringBuilder sb = new StringBuilder();
		  for (int i = 0; i < bytes.length; i++) {
		    String hex = Integer.toHexString(0xFF & bytes[i]);
		    if (hex.length() == 1) {
		      sb.append('0');
		    }
		    sb.append(hex);
		  }
		  return sb.toString();
		}
}

