package com.tg.cloudmanagement.net.image;

import java.io.File;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.http.AndroidHttpClient;
import android.os.Build;
import android.text.TextUtils;
import android.widget.ImageView;

import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.tg.cloudmanagement.R;
import com.tg.cloudmanagement.image.BitmapCache;
/**
 * Volley工具类
 * @author Administrator
 *
 */
public class VolleyUtils {
	private static ImageLoader imgLoader;
	private static RequestQueue reQueue;
    public static RequestQueue newRequestQueue(Context context ,int threadNumber) {  
    	HttpStack stack = null;
        File cacheDir = new File(context.getCacheDir(), "volley");//缓存文件  
      
        String userAgent = "volley/0";//UserAgent用来封装应用的包名跟版本号，提供给服务器，就跟浏览器信息一样  
        try {  
            String packageName = context.getPackageName();  
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);  
            userAgent = packageName + "/" + info.versionCode;  
        } catch (NameNotFoundException e) {  
        }  
      
        if (stack == null) {//一般我们都不需要传这个参数进来，而volley则在这里会根据SDK的版本号来判断   
            if (Build.VERSION.SDK_INT >= 9) {  
                stack = new HurlStack();//SDK如果大于等于9，也就是Android 2.3以后，因为引进了HttpUrlConnection，所以会用一个HurlStack  
            } else {//如果小于9,则是用HttpClient来实现  
                // Prior to Gingerbread, HttpUrlConnection was unreliable.  
                // See: http://android-developers.blogspot.com/2011/09/androids-http-clients.html  
                stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));  
            }  
        }  
      
        Network network = new BasicNetwork(stack);//创建一个Network，构造函数需要一个stack参数，Network里面会调用stack去跟网络通信  
      //创建RequestQueue，并将缓存实现DiskBasedCache和网络实现BasicNetwork传进去，然后调用start方法  
        RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir), network,threadNumber);  
        queue.start();  
         
        return queue;  
    }  
    
    public static ImageLoader getImageLoader(Context con){
    	if(reQueue == null || imgLoader == null){
    		reQueue = VolleyUtils.newRequestQueue(con, 3);
    		imgLoader = new ImageLoader(reQueue, BitmapCache.getInstance(con));
    	}
    	return imgLoader;
    }
    
    public static void getImage(Context context,final String url,final ImageView imageView){
    	if(TextUtils.isEmpty(url)){
    		imageView.setImageResource(R.color.default_bg);
    		return;
    	}
    	imageView.setTag(url);
    	getImageLoader(context).get(url, new ImageListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if(!TextUtils.equals(url, (String)imageView.getTag())){
					return;
				}
				imageView.setImageResource(R.color.default_bg);
			}
			
			@Override
			public void onResponse(ImageContainer response, boolean isImmediate) {
				if(TextUtils.equals(url, (String)imageView.getTag())){
					if(response.getBitmap() != null){
						imageView.setImageBitmap(response.getBitmap());
					}else{
						imageView.setImageResource(R.color.default_bg);
					}
				}
			}
		}, 500, 500);
    }
    
    public static void getImage(Context context,final String url,final ImageView imageView,int maxWidth,int maxHeight){
    	if(TextUtils.isEmpty(url)){
    		imageView.setImageResource(R.color.default_bg);
    		return;
    	}
    	imageView.setTag(url);
    	getImageLoader(context).get(url, new ImageListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if(!TextUtils.equals(url, (String)imageView.getTag())){
					return;
				}
				imageView.setImageResource(R.color.default_bg);
			}
			
			@Override
			public void onResponse(ImageContainer response, boolean isImmediate) {
				if(TextUtils.equals(url, (String)imageView.getTag())){
					if(response.getBitmap() != null){
						imageView.setImageBitmap(response.getBitmap());
					}else{
						imageView.setImageResource(R.color.default_bg);
					}
				}
			}
		}, maxWidth, maxHeight);
    }
    
    public static void getImage(Context context,final String url,final ImageView imageView,final int defaultImageRes){
    	if(TextUtils.isEmpty(url)){
    		imageView.setImageResource(defaultImageRes);
    		return;
    	}
    	imageView.setTag(url);
    	getImageLoader(context).get(url, new ImageListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if(TextUtils.equals(url, (String)imageView.getTag())){
					imageView.setImageResource(defaultImageRes);
				}
			}
			
			@Override
			public void onResponse(ImageContainer response, boolean isImmediate) {
				if(TextUtils.equals(url, (String)imageView.getTag())){
					if(response.getBitmap() != null){
						imageView.setImageBitmap(response.getBitmap());
					}else{
						imageView.setImageResource(defaultImageRes);
					}
				}
			}
		}, 500, 500);
    }
    
    public static void getImage(final Context context,final String url,final ImageView imageView,int maxWidth,int maxHeight,final int defaultImageRes){
    	if(TextUtils.isEmpty(url)){
    		imageView.setImageResource(defaultImageRes);
    	}
    	imageView.setTag(url);
    	getImageLoader(context).get(url, new ImageListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if(!TextUtils.equals(url, (String)imageView.getTag())){
					return;
				}
	    		imageView.setImageResource(defaultImageRes);
			}
			
			@Override
			public void onResponse(ImageContainer response, boolean isImmediate) {
				if(TextUtils.equals(url, (String)imageView.getTag())){
					if(response.getBitmap() != null){
			    		imageView.setImageBitmap(response.getBitmap());
					}else{
			    		imageView.setImageResource(defaultImageRes);
					}
				}
			}
		}, maxWidth, maxHeight);
    }
    
}
