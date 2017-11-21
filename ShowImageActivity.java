package com.tg.cloudmanagement;

import java.util.ArrayList;

import com.tg.cloudmanagement.R;
import com.tg.cloudmanagement.base.BaseActivity;
import com.tg.cloudmanagement.inter.SingleClickListener;
import com.tg.cloudmanagement.log.Logger;
import com.tg.cloudmanagement.object.ImgObj;
import com.tg.cloudmanagement.util.Tools;
import com.tg.cloudmanagement.view.gallery.GalleryAdapter;
import com.tg.cloudmanagement.view.gallery.MyImageView;
import com.tg.cloudmanagement.view.gallery.PicGallery;

import android.os.Bundle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class ShowImageActivity extends BaseActivity implements OnItemSelectedListener {

	public static final int RESULT_CODE_DELETE = 0x111111;
	public static final String KEY_DEFAULT_BG_RES ="default_bg";
	public static final String KEY_IS_SELECT ="is_select";
	public static final String KEY_PIC_RES_ID ="res_id";
	public static final String KEY_PATH_LIST = "path_list";
	public static final String KEY_URL_LIST = "url_list";
	public static final String KEY_DELETE_PATH_LIST = "delete_path_list";
	public static final String KEY_CURRENT_INDEX = "current_index";
	private ImageView imgDelete;
	private boolean isSelect = true;
	private int defaultBg = R.color.default_bg;
	private PicGallery gallery;
	private TextView tvPicNum;
	private ArrayList<ImgObj> list = new ArrayList<ImgObj>();
	private int count; 
	private int current = 1;
	private int currentIndex = 0;
	private ArrayList<String> urlList;
	private ArrayList<String> pathList;
	private ArrayList<String> deletePathList = new ArrayList<String>();;
	private int picResId = -1;
	public static int screenWidth;
	public static int screenHeight;
	private GalleryAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_image);
		initView();
		imgDelete.setOnClickListener(new SingleClickListener() {
			@Override
			public void onSingleClick(View v) {
				onDelete(v);
			}
		});
		initData();
		mAdapter = new GalleryAdapter(this,list,defaultBg);
		gallery.setAdapter(mAdapter);
		count = list.size();
		if( currentIndex >= 0 && currentIndex < count){
		}else{
			currentIndex = 0;
		}
		gallery.setSelection(currentIndex);
		current = currentIndex+1;
		tvPicNum.setText(current+"/"+count);
	}
	
	private void initData(){
		list.clear();
		Intent data = getIntent();
		if(data != null){
			isSelect = data.getBooleanExtra(KEY_IS_SELECT,true);
			defaultBg = data.getIntExtra(KEY_DEFAULT_BG_RES, R.color.default_bg);
			picResId = data.getIntExtra(KEY_PIC_RES_ID, -1);
			currentIndex = data.getIntExtra(KEY_CURRENT_INDEX, 0);
			urlList = data.getStringArrayListExtra(KEY_URL_LIST);
			pathList = data.getStringArrayListExtra(KEY_PATH_LIST);
		}
		if(isSelect){
			imgDelete.setVisibility(View.GONE);
			if(urlList != null){
				ImgObj obj;
				for(int i = 0; i < urlList.size();i ++){
					obj = new ImgObj();
					obj.url = urlList.get(i);
					list.add(obj);
				}
			}else{
				//tvPicNum.setVisibility(View.GONE);
				if(picResId > 0){
					list.add(getImageViewByResid(picResId,0));
				}else{
					list.add(getImageViewByResid(0,0));
				}
			}
		}else{
			imgDelete.setVisibility(View.VISIBLE);
			if(pathList != null){
				Logger.logd("-----------------listPath size ="+pathList.size());
				for(int i = 0; i < pathList.size();i ++){
					list.add(getImageView(pathList.get(i),i));
				}
			}else if(urlList != null){
				ImgObj obj;
				String url = null;
				for(int i = 0; i < urlList.size();i ++){
					obj = new ImgObj();
					url = urlList.get(i);
					if(!TextUtils.isEmpty(url)){
						if(url.startsWith("http")){
							obj.url = url;
						}else{
							obj.path = url;
						}
						list.add(obj);
					}
				}
			}
			else{
				if(picResId > 0){
					list.add(getImageViewByResid(picResId,0));
				}else{
					list.add(getImageViewByResid(0,0));
				}
			}
		}
	}
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if(hasFocus && screenHeight == 0){
			DisplayMetrics metrics = Tools.getDisplayMetrics(this);
			screenWidth = metrics.widthPixels;
			screenHeight = metrics.heightPixels - Tools.getStatusBarHeight(this);
		}
	}
	
	private void initView(){
		gallery = (PicGallery)findViewById(R.id.gallery);
		imgDelete = (ImageView)findViewById(R.id.img_delete);
		tvPicNum = (TextView)findViewById(R.id.tv_pic_num);
		gallery.setOnItemSelectedListener(this);
		gallery.setVerticalFadingEdgeEnabled(false);// 取消竖直渐变边框
		gallery.setHorizontalFadingEdgeEnabled(false);// 取消水平渐变边框
		gallery.setDetector(new GestureDetector(this,new MySimpleGesture()));
	}
	
	private class MySimpleGesture extends SimpleOnGestureListener {
		// 按两下的第二下Touch down时触发
		@Override
		public boolean onDoubleTap(MotionEvent e) {

			View view = gallery.getSelectedView();
			if (view instanceof MyImageView) {
				MyImageView imageView = (MyImageView) view;
				if (imageView.getScale() > imageView.getMiniZoom()) {
					imageView.zoomTo(imageView.getMiniZoom());
				} else {
					imageView.zoomTo(imageView.getMaxZoom());
				}
			} else {

			}
			return true;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			// Logger.LOG("onSingleTapConfirmed",
			// "onSingleTapConfirmed excute");
			// mTweetShow = !mTweetShow;
			// tweetLayout.setVisibility(mTweetShow ? View.VISIBLE
			// : View.INVISIBLE);
			return true;
		}
	}

	
	public ImgObj getImageViewByResid(int resID,int index){
		ImgObj obj = new ImgObj();
		obj.index = index;
		obj.resID = resID;
		return obj;
	}
	
	public ImgObj getImageView(String path,int index){
		ImgObj obj = new ImgObj();
		obj.index = index;
		obj.path = path;
		Logger.logd(" path = "+path);
		return obj;
	}
	
	public void onBack(View v){
		if(deletePathList != null && deletePathList.size() > 0){//被删除过图片
			Intent data = new Intent();
			data.putStringArrayListExtra(KEY_DELETE_PATH_LIST, deletePathList);
			setResult(RESULT_CODE_DELETE,data);
		}
		finish();
	}
	
	@Override
	public void onBackPressed() {
		if(deletePathList != null && deletePathList.size() > 0){//被删除过图片
			Intent data = new Intent();
			data.putStringArrayListExtra(KEY_DELETE_PATH_LIST, deletePathList);
			setResult(RESULT_CODE_DELETE,data);
		}
		finish();
		super.onBackPressed();
	}
	
	private void recycleImageView(ImageView img){
		if(img != null){
			Bitmap btm = (Bitmap)img.getTag();
			if(btm != null && !btm.isRecycled()){
				btm.recycle();
			}
		}
	}
	@Override
	protected void onDestroy() {
		for(int i = 0; i < list.size(); i ++){
			recycleImageView(list.get(i).img);
		}
		super.onDestroy();
	}
	public void onDelete(View v){
		int index = current - 1;
		if(index >= 0 && index < list.size()){
			String path = null;
			if(pathList != null ){
				path = list.get(index).path;
				
			}else if(urlList != null){
				path = urlList.get(index);
			}
			Logger.logd("index = "+index+"  path = "+path);
			if( path != null){
				deletePathList.add(path);
				recycleImageView(list.remove(index).img);
				if(list.size() == 0){
					if(deletePathList != null && deletePathList.size() > 0){//被删除过图片
						Intent data = new Intent();
						data.putStringArrayListExtra(KEY_DELETE_PATH_LIST, deletePathList);
						setResult(RESULT_CODE_DELETE,data);
					}
					finish();
					return;
				}
				mAdapter.notifyDataSetChanged();
				freshTitle();
			}
		}
	}
	
	private void freshTitle(){
		if(tvPicNum.getVisibility() == View.VISIBLE){
			current = gallery.getSelectedItemPosition()+1;
			count = list.size();
			tvPicNum.setText(current+"/"+count);
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		freshTitle();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	@Override
	public View getContentView() {
		return null;
	}

	@Override
	public String getHeadTitle() {
		return null;
	}
}
