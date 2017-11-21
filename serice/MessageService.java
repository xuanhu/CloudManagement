package com.tg.cloudmanagement.serice;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.tg.cloudmanagement.AlertActivity;
import com.tg.cloudmanagement.constant.Contants;
import com.tg.cloudmanagement.info.UserInfo;
import com.tg.cloudmanagement.net.DES;
import com.tg.cloudmanagement.net.HttpTools;
import com.tg.cloudmanagement.net.MD5;
import com.tg.cloudmanagement.net.RequestParams;
import com.tg.cloudmanagement.util.Tools;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MessageService extends Service {	 
    //获取消息线程
    private MessageThread messageThread = null;
    
 
    @Override
	public void onCreate() {
        //开启线程
        messageThread = new MessageThread();
        messageThread.isRunning = true;
        messageThread.start();
		super.onCreate();
	}

	/**
	 * 从服务器端获取消息
	 * 
	 */
    class MessageThread extends Thread{
        //运行状态，下一步骤有大用
        public boolean isRunning = false;
        public void run() {
            while(isRunning){
                try {
                    Thread.sleep(5000);
                    //获取服务器消息
                    String serverMessage = getServerMessage();
                 int code = HttpTools.getCode(serverMessage);
                 if(code == 0){
                    	Intent intent = new Intent(MessageService.this,AlertActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						if(isActivityRunning(MessageService.this, "com.tg.cloudmanagement.AlertActivity")){
							//Log.d("print","正在运行");
						}else {
							intent.putExtra(AlertActivity.SERVERMESSAGE,serverMessage);
							startActivity(intent);
						}
                }
                    //一分钟请求一次
                    Thread.sleep(30000);
           } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
        /**
		 * 判断Acitivity是否已经存在
		 * 
		 * @param mContext
		 * @param activityClassName
		 * @return
		 */
		public boolean isActivityRunning(Context mContext,String activityClassName) {
			ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningTaskInfo> info = activityManager.getRunningTasks(1);
			if (info != null && info.size() > 0) {
				ComponentName component = info.get(0).topActivity;
				if (activityClassName.equals(component.getClassName())) {
					return true;
				}
			}
			return false;
		}
	}
	@Override
		public void onDestroy() {
		            System.exit(0);
		            //或者，二选一，推荐使用System.exit(0)，这样进程退出的更干净
		            super.onDestroy();
		}
	
	/**
	 * 这里以此方法为服务器Demo，仅作示例
	 * 
	 * @return 返回服务器要推送的消息，否则如果为空的话，不推送
	 */
	public String getServerMessage() {
		String apiName = "/homepush/gethomePushByuid";
		String time = Tools.getDateName(this);
		time = URLEncoder.encode(time);
		RequestParams params = new RequestParams();
		params.put("uid", UserInfo.uid);
		params.put("bTime", time);
		String url = null;
		HashMap<String, Object> paramsStr = null;
		if (params != null) {
			paramsStr = params.toHashMap();
		}else {
			paramsStr = null;
		}
		try {
			url =  Contants.URl.URl_3011 +HttpTools.GetUrl(Contants.URl.URl_3011,apiName,paramsStr);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		HttpGet getMethod = new HttpGet(url);//将URL与参数拼接
		HttpClient httpClient = new DefaultHttpClient();
		String  response="";
		try {
		    HttpResponse mHttpResponse = httpClient.execute(getMethod); //发起GET请求
		    int code = mHttpResponse.getStatusLine().getStatusCode();
		    if(code == 200){
		    	 response = EntityUtils.toString(mHttpResponse.getEntity(), "utf-8");
		    }
		} catch (ClientProtocolException e) {

		    e.printStackTrace();

		} catch (IOException e) {

		    e.printStackTrace();

		}
		return response;
	}
	
	private static String GetUrl(final String URL,final String apiname, final HashMap<String, Object> params) throws Exception {
		Date dt = new Date();
		Long time = dt.getTime();
		String Sign = MD5.getMd5Value(URL+apiname+"colourlife" + time + DES.KEY_STR +"false").toLowerCase();
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
		return apiname+ "?appID=colourlife&sign=" + Sign+"&ts="+ time + apppara;
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}