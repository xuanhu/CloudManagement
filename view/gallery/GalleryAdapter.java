package com.tg.cloudmanagement.view.gallery;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.tg.cloudmanagement.R;
import com.tg.cloudmanagement.image.BitmapCache;
import com.tg.cloudmanagement.log.Logger;
import com.tg.cloudmanagement.net.image.VolleyUtils;
import com.tg.cloudmanagement.object.ImgObj;

public class GalleryAdapter extends BaseAdapter {
	public static final int MSG_SUCCESS = 1;
	public static final int MSG_ERROR = 2;
	public static final int MSG_START_DOWNLOAD = 3;
	public static final int DELAY_TIME = 20;
	private Context context;
	private ArrayList<ImgObj> list;
	private ImageLoader imgLoader;
	private int defaultBg = -1;
	public GalleryAdapter(Context context,  ArrayList<ImgObj> l) {
		this.context = context;
		list = l; 
		defaultBg = R.color.default_bg;
		imgLoader = VolleyUtils.getImageLoader(context);
	} 
	
	public GalleryAdapter(Context context,  ArrayList<ImgObj> l, int defaultBgRes) {
		this.context = context;
		list = l; 
		defaultBg = defaultBgRes;
		imgLoader = VolleyUtils.getImageLoader(context);
	} 
	
	@Override
	public int getCount() {
		return list == null ? 0:list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			Logger.logd("null ----------------"+position);
		}else{
			Logger.logd("convertView ----------------"+position);
		}
		ImgObj item = list.get(position);
		MyImageView view = new MyImageView(context);
		view.setLayoutParams(new Gallery.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		if(!TextUtils.isEmpty(item.url)){
			imgLoader.get(item.url, getImageListener(view), 500, 500);
		}else{
			if(!TextUtils.isEmpty(item.path)){
				Bitmap bitmap = BitmapCache.getInstance(context).getBitmap(item.path);
				if(bitmap != null){
					view.setImageBitmap(bitmap);
				}else{
					File file = new File(item.path);
					if(file.exists()){
						view.setImageURI(Uri.fromFile(file));
					}else{
						view.setImageResource(defaultBg);
					}
				}
			}else if(item.resID > 0){
				view.setImageResource(item.resID);
			}else{
				view.setImageResource(defaultBg);
			}
		}
		return view;
	}
	
	public  ImageListener getImageListener(final ImageView imgView){
    	return  new ImageListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            	imgView.setImageResource(defaultBg);
            }

            @Override
            public void onResponse(ImageContainer response, boolean isImmediate) {
            		if (response.getBitmap() != null) {
                    	imgView.setImageBitmap(response.getBitmap());
            		} else {
            			imgView.setImageResource(defaultBg);
            		}
            }
        };
    }

}
