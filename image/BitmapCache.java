package com.tg.cloudmanagement.image;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import libcore.io.DiskLruCache;
import libcore.io.DiskLruCache.Snapshot;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.text.TextUtils;

import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.tg.cloudmanagement.log.Logger;
import com.tg.cloudmanagement.util.Tools;


public class BitmapCache implements ImageCache {  
	private static final long DISK_CACHE_SIZE_30M = 1024 * 1024 * 30;// 30M
	private static final long DISK_CACHE_SIZE_100M = 1024 * 1024 * 100;// 100M
	private static final String DISK_CACHE_SUBDIR = "image_disk";
    private LruCache<String, Bitmap> mCache;  
    private DiskLruCache mDiskCache;
    private Context con;
    private long diskCacheSize;
    public static BitmapCache bitmapCache;
    public BitmapCache(Context context) {  
    	con = context;
    	int maxMemory = (int) Runtime.getRuntime().maxMemory();
    	Logger.logd("maxMemory = "+maxMemory);
        int mCacheSize = maxMemory / 8;
        mCache = new LruCache<String, Bitmap>(mCacheSize) {  
            @Override  
            protected int sizeOf(String key, Bitmap btm) {  
            	if(btm == null){
            		return 0;
            	}
            	int size = btm.getRowBytes() * btm.getHeight();
                return size;  
            }  
            
            @Override
            protected void entryRemoved(boolean evicted, String key,
            		Bitmap oldValue, Bitmap newValue) {
            	super.entryRemoved(evicted, key, oldValue, newValue);
            	if(newValue != oldValue && oldValue != null && !oldValue.isRecycled()){
            		//oldValue.recycle();
            		oldValue = null;
            		Logger.logd("entryRemoved oldValue = "+oldValue);
            	}
            }
        };  
        String path;
        if(Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED 
        		&& context.getExternalCacheDir() != null){
    		path = context.getExternalCacheDir().getPath();
        	diskCacheSize = DISK_CACHE_SIZE_100M;
        }else{
        	path = context.getCacheDir().getPath();
        	diskCacheSize = DISK_CACHE_SIZE_30M;
        }
        File cacheDir = new File(path + File.separator + DISK_CACHE_SUBDIR);
        try {
			mDiskCache = DiskLruCache.open(cacheDir, Tools.getAppVersion(con), 1, diskCacheSize);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    }  
  
    public static BitmapCache getInstance(Context con){
    	if(bitmapCache == null){
    		bitmapCache = new BitmapCache(con);
    	}
    	return bitmapCache;
    }
    @Override  
    public Bitmap getBitmap(String url) {  
    	if(TextUtils.isEmpty(url)){
    		return null;
    	}
    	Bitmap btm = mCache.get(url);
    	if(btm != null && !btm.isRecycled()){
    		return btm;
    	}
    	Bitmap bitmap = getBitmapFromDiskCache(url);
    	if(bitmap != null){
    		mCache.put(url, bitmap);  
    	}
        return bitmap;  
    }  
  
    @Override  
    public void putBitmap(String url, Bitmap bitmap) {  
    	if(TextUtils.isEmpty(url)){
    		return ;
    	}
    	Bitmap btm = mCache.remove(url);
    	if(btm != null && !btm.isRecycled()){
			btm.recycle();
			btm = null;
		}
		mCache.put(url, bitmap);  
        saveBitmapToDiskCache(url,bitmap,false);
    }  
    
    public void clearAllCache(){
    	mCache.evictAll();
    	try {
			mDiskCache.delete();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public void removeBitmap(String url){
    	Bitmap btm = mCache.remove(url);
		if(btm != null && !btm.isRecycled()){
			btm.recycle();
			btm = null;
		}
    	String key = Tools.hashKeyForDisk(url);
    	try {
			mDiskCache.remove(key);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void removeBitmap(List<String> urls){
    	if(urls == null || urls.size() == 0){
    		return;
    	}
    	for(String url :urls){
    		Bitmap btm = mCache.remove(url);
    		if(btm != null && !btm.isRecycled()){
    			btm.recycle();
    			btm = null;
    		}
    		String key = Tools.hashKeyForDisk(url);
    		try {
    			mDiskCache.remove(key);
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    }
  
    public void saveBitmapToDiskCache(String url, final Bitmap bitmap,boolean compressPNG){
    	final String key = Tools.hashKeyForDisk(url);
    	 new Thread(new Runnable() {
       	  @Override
       	  public void run() {
       	    try {
       	      DiskLruCache.Editor editor = mDiskCache.edit(key);
       	      if (editor != null) {
	       	        OutputStream outputStream = editor.newOutputStream(0);
	       	        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
	       	        editor.commit();
       	      }
       	      mDiskCache.flush();
       	    } catch (IOException e) {
       	      e.printStackTrace();
       	    }catch(IllegalStateException e){
       	    	e.printStackTrace();
       	    }
       	  }
       	}).start();
    }
    
    public Bitmap getBitmapFromDiskCache(String url){
    	String key = Tools.hashKeyForDisk(url);
    	Snapshot snapShot = null;
    	try {
			snapShot = mDiskCache.get(key);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	if(snapShot == null){
    		return null;
    	}
    	InputStream is = snapShot.getInputStream(0);
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        return bitmap;
    }
}  
