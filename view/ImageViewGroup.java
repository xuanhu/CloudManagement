package com.tg.cloudmanagement.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.tg.cloudmanagement.R;
import com.tg.cloudmanagement.ShowImageActivity;
import com.tg.cloudmanagement.base.BaseActivity;
import com.tg.cloudmanagement.inter.ResultCallBack;
import com.tg.cloudmanagement.log.Logger;
import com.tg.cloudmanagement.net.image.VolleyUtils;
import com.tg.cloudmanagement.object.ImageParams;
import com.tg.cloudmanagement.util.Tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;


public class ImageViewGroup extends RelativeLayout implements OnClickListener, ResultCallBack{
	public static final int PIC_PHOTO_BY_ALBUM = Integer.MAX_VALUE -  10;
	public static final int PIC_PHOTO_BY_CAMERA = Integer.MAX_VALUE -  9;
	public static final int DELETE_PHOTO = Integer.MAX_VALUE -  8;
	private boolean isStorageMemory;
	private String PHOTO_NAME = "wisdomPark.jpg";
	private String cropPath;
	private String TAKE_PHOTO_PATH = "";
	private int columnNumber = 4;
	private int spacing;
	private int mWidth = 0;
	private int imgWidth ;
	private int childCount;
	private DisplayMetrics displayMetrics;
	private Context mContext;
	private ArrayList<ImageParams> data;
	private int maxCount = 12;
	private boolean isAddable = false;
	private OnImageItemClickListener itemClickListener;
	private BaseActivity baseActivity;
	private int clickPosition;
	private String dirPath;
	public ImageViewGroup(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		initView(context,attrs);
	}

	public ImageViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context,attrs);
	}

	public ImageViewGroup(Context context) {
		super(context);
		initView(context,null);
	}
	
	private void deleteFile(File file){
		file.delete();
	}
	
	private void deleteFile(String filePath){
		if(TextUtils.isEmpty(filePath)){
			return;
		}
		File file = new File(filePath);
		if(file.exists()){
			file.delete();
		}
	}
	
	private void deleteAll(){
		if(dirPath == null){
			return;
		}
		File dir = new File(dirPath);
		if(!dir.exists()){
			return;
		}
		if(dir.isDirectory()){
			deleteDir(dir);
		}else{
			deleteFile(dir);
		}
	}

	private void deleteDir(File dir){
		File[] files = dir.listFiles();
		if(files == null || files.length == 0){
			dir.delete();
			return;
		}
		for(File file :files){
			if(file.isFile()){
				deleteFile(file);
			}else{
				deleteDir(file);
			}
		}
	}
	
	private void initView(Context context,AttributeSet set){
		mContext = context;
		baseActivity = (BaseActivity)context;
		dirPath = baseActivity.getFilesDir()+"/imgViewGroup";
		deleteAll();
		if(baseActivity != null){
			baseActivity.setResultCallBack(this);
		}
		displayMetrics = getResources().getDisplayMetrics();
		spacing = (int)(10 * displayMetrics.density+0.5f);
		setPadding(0, 0, spacing, spacing);
		if(set != null){
			TypedArray ta = context.obtainStyledAttributes(set, R.styleable.ImageViewGroup);
			columnNumber = ta.getInt(R.styleable.ImageViewGroup_column_count, 4);
			ta.recycle();
		}
	}
	
	public void setAddable(boolean param){
		if(isAddable != param){
			isAddable = param;
			initView();
		}
	}
	
	private void initView(){
		if(isAddable){
			if(data == null){
				data = new ArrayList<ImageParams>();
			}
			data.clear();
			ImageParams param = new ImageParams();
			param.resId = R.drawable.tianjiatu_;
			data.add(param);
			setImageParams(data);
		}else{
			if(data == null){
				data = new ArrayList<ImageParams>();
			}
			data.clear();
		}
	}
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		super.onLayout(changed, l, t, r, b);
		Logger.logd("onLayout");
		if(mWidth == 0){
			mWidth = getMeasuredWidth();
			initImageViewDimen();
			Logger.logd("onLayout mWidth = "+mWidth+" imgWidth = "+imgWidth);
			if(imgWidth > 0){
				freshDimen();
			}
		}
	}
	
	public void setImageUrls(String[] urls){
		if(urls == null || urls.length == 0){
			setVisibility(View.GONE);
			return;
		}
		if(data == null){
			data = new ArrayList<ImageParams>();
		}
		data.clear();
		ImageParams params;
		for(int i = 0; i < urls.length; i++){
			params = new ImageParams();
			params.url = urls[i];
			data.add(params);
		}
		setImageParams(data);	
	}
	
	public void setImageParams(ArrayList<ImageParams> list){
		data = list;
		if(data == null || data.size() == 0){
			setVisibility(View.GONE);
			return;
		}
		setVisibility(View.VISIBLE);
		int len = data.size();
		Logger.logd("setImageParams size = "+len);
		setVisibility(View.VISIBLE);
		setImageViews(len);
		for(int i = 0; i < len; i ++){
			freshImageSrc(i);
		}
	}
	
	public ArrayList<ImageParams> getImageParams(){
		return data;
	}
	
	public void freshImageSrc(int i){
		if(i < 0 || i >= data.size()){
			return;
		}
		ImageView imgView;
		ImageParams params;
		imgView = getImageView(i);
		params = data.get(i);
		if(params.resId > 0){
			imgView.setImageResource(params.resId);
		}else if(!TextUtils.isEmpty(params.path)){
			File file = new File(params.path);
			if(file.exists()){
				imgView.setImageURI(Uri.fromFile(file));
			}else{
				imgView.setImageResource(R.color.default_bg);
			}
		}else if(!TextUtils.isEmpty(params.url)){
			VolleyUtils.getImage(mContext, 
					params.url, imgView, 
					imgWidth, imgWidth, 
					R.color.default_bg);
		}else{
			imgView.setImageResource(R.color.default_bg);
		}
	}
	
	public void setColumnCount(int count){
		if(columnNumber != count){
			columnNumber = count;
			initImageViewDimen();
			if(imgWidth > 0){
				freshDimen();
			}
		}
	}
	
	private void initImageViewDimen(){
		imgWidth = (mWidth - spacing * (columnNumber+1))/ columnNumber;
	}
	public void freshDimen(){
		LayoutParams lp;
		ImageView image;
		for(int i = 0; i < getChildCount(); i++){
			image = (ImageView)getChildAt(i);
			lp = (LayoutParams)image.getLayoutParams();
			lp.width = imgWidth;
			lp.height = imgWidth;
			image.setLayoutParams(lp);
		}
		
	}
	
	public void setImageViews(int count){
		if(count < 0){
			removeAllViews();
			return;
		}
		if(count == childCount){
			return;
		}
		int offset = childCount - count;
		childCount = count;
		if(offset > 0){
			removeImageFrom(count);
		}else{
			addImageFrom(count,Math.abs(offset));
		}
		requestLayout();
	}
	
	private void addImageFrom(int index,int count){
		for(int i = index; i < index + count; i ++){
			addImageView(i);
		}
	}
	
	private void removeImageFrom(int index){
		int count = getChildCount();
		for(int i = index; i < count; i ++){
			Logger.logd("removeImageFrom i = "+i);
			removeImageView(i);
		}
	}
	
	public void removeImageView(int index){
		View child = getImageView(index);
		if(child != null){
			removeView(child);
			childCount = getChildCount();
		}
	}
	
	public ImageView getImageView(int index){
		int id= index +1;
		ImageView child = (ImageView)findViewById(id);
		return child;
	}
	
	public void addImageView(){
		addImageView(-1);
	}
	
	public void addImageView(int index){
		if(index < 0 || index >= getChildCount()){
			addAtLast();
			return;
		}
		insertAt(index);
	}
	
	public void insertAt(int index){
		int count = getChildCount();
		int offSet = index - count;
		if(offSet > 0){
			setImageIdAddFrom(index);
		}
		addAtIndex(index);
		if(offSet > 0){
			int start = index + 1;
			for(int i = start; i < start+offSet; i ++ ){
				layoutImageView(i);
			}
		}
	}
	
	public void insertAt(int index,ImageParams params){
		if(data == null){
			data = new ArrayList<ImageParams>();
		}
		data.add(index, params);
		int count = getChildCount();
		int offSet = count - index;
		if(offSet > 0){
			setImageIdAddFrom(index);
		}
		addAtIndex(index);
		freshImageSrc(index);
		if(offSet > 0){
			int start = index + 1;
			for(int i = start; i < start+offSet; i ++ ){
				layoutImageView(i);
			}
		}
		
	}
	
	/**
	 * id++
	 * @param index
	 */
	private void setImageIdAddFrom(int index){
		View child;
		for(int i = getChildCount() - 1 ; i >= index; i --){
			child = getImageView(i);
			if(child != null)
			{
				child.setId(child.getId()+1);
			}
		}
	}
	
	private void addAtLast(){
		addAtIndex(getChildCount());
	}
	
	public boolean canAdd(){
		return data.size() <= maxCount;
	}
	
	public void layoutImageView(int index){
		ImageView imgView = getImageView(index);
		if(imgView == null){
			return;
		}
		if(index >= maxCount){
			removeView(imgView);
			return;
		}
		LayoutParams lp =
				(LayoutParams)imgView.getLayoutParams();
		lp.leftMargin = spacing;
		lp.topMargin = spacing;
		int id = imgView.getId();
		int[] rules = lp.getRules();
		for(int i = 0 ; rules != null && i < rules.length; i ++){
			rules[i] = 0;
		}
		if(id % columnNumber != 1){
			lp.addRule(RelativeLayout.RIGHT_OF, id-1);
		}else{
			lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		}
		if(id > columnNumber){
			lp.addRule(BELOW, id- columnNumber);
		}else{
			lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		}
		imgView.setLayoutParams(lp);
	}
	
	private void addAtIndex(int index){
		ImageView imgView;
		LayoutParams lp;
		int id = index +1;
		imgView = new RoundImageView(mContext);
		imgView.setOnClickListener(this);
		imgView.setScaleType(ScaleType.CENTER_CROP);
		imgView.setId(id);
		lp = new LayoutParams(imgWidth, imgWidth);
		lp.leftMargin = spacing;
		lp.topMargin = spacing;
		if(id % columnNumber != 1){
			lp.addRule(RelativeLayout.RIGHT_OF, id-1);
		}else{
			lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		}
		if(id > columnNumber){
			lp.addRule(BELOW, id- columnNumber);
		}else{
			lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		}
		addView(imgView, lp);
		childCount = getChildCount();
	}
	
	public void setOnItemClickListener(OnImageItemClickListener l){
		itemClickListener = l;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		clickPosition = v.getId() -1;
		if(isAddable){
			if(clickPosition == getChildCount()-1 && canAdd()){//添加
				cropPath = dirPath +File.separator+
						String.valueOf(System.currentTimeMillis())+".jpg";
				showPhotoSelector();
			}else{//查看本地图片
				selectLocalImage();
			}
		}else{//查看网络图片
			selectNetImage();
		}
		if(itemClickListener != null){
			itemClickListener.onItemClick(this, v, v.getId() - 1);
		}
	}
	
	private void selectLocalImage(){
		Intent intent = new Intent(baseActivity,ShowImageActivity.class);
		intent.putExtra(ShowImageActivity.KEY_IS_SELECT, false);
		intent.putExtra(ShowImageActivity.KEY_CURRENT_INDEX, clickPosition);
		intent.putExtra(ShowImageActivity.KEY_PATH_LIST,getPathList());
		baseActivity.startActivityForResult(intent, DELETE_PHOTO);
	}
	
	private void selectNetImage(){
		Intent intent = new Intent(baseActivity,ShowImageActivity.class);
		intent.putExtra(ShowImageActivity.KEY_IS_SELECT, true);
		intent.putExtra(ShowImageActivity.KEY_CURRENT_INDEX, clickPosition);
		intent.putExtra(ShowImageActivity.KEY_URL_LIST,getUrlList());
		baseActivity.startActivity(intent);
	}
	
	private ArrayList<String> getUrlList(){
		ArrayList<String> listPath = new ArrayList<String>();
		for(ImageParams imgParam: data){
			if(!TextUtils.isEmpty(imgParam.url)){
				listPath.add(imgParam.url);
			}
		}
		return listPath;
	}
	
	private ArrayList<String> getPathList(){
		ArrayList<String> listPath = new ArrayList<String>();
		for(ImageParams imgParam: data){
			if(!TextUtils.isEmpty(imgParam.path)){
				listPath.add(imgParam.path);
			}
		}
		return listPath;
	}
	
	public interface OnImageItemClickListener{
		public void onItemClick(ImageViewGroup imageGroup, View v, int position);
	}
	
	private void deleteImages(ArrayList<String> paths){
		if(paths == null || paths.size() == 0){
			return;
		}
		for(int i = 0; i < paths.size(); i ++){
			deleteImage(paths.get(i));
		}
	}
	private void deleteImage(String path){
		if(TextUtils.isEmpty(path)){
			return;
		}
		for(int i = 0; i < data.size() - 1; i ++){
			if(path.equals(data.get(i).path)){
				Logger.logd("delete path ="+path);
				data.remove(i);
				deleteFile(path);
				return;
			}
		}
	}
	
	public String getPostUrls(){
		if(data.size() <= 1){
			return null;
		}
		 StringBuilder strBuilder = new  StringBuilder();
		 String url;
		 int len = data.size()-1;
		for(int i = 0 ; i < len; i ++){
			url = data.get(i).url;
			if(!TextUtils.isEmpty(url)){
				strBuilder.append(url);
				if(i != len - 1){
					strBuilder.append(",");
				}
			}
		}
		return strBuilder.toString();
	}
	
	public ArrayList<ImageParams> getPostImageParam(){
		if(data == null || data.size() <= 1){
			return null;
		}
		ArrayList<ImageParams> list = new ArrayList<ImageParams>();
		ImageParams param;
		for(int i = 0 ; i < data.size(); i ++){
			param = data.get(i);
			if(!TextUtils.isEmpty(param.path) && 
					TextUtils.isEmpty(param.url)){
				param.position = i;
				list.add(param);
			}
		}
		return list;
	}
	
	@Override
	public void onResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (requestCode) {
		case DELETE_PHOTO:{
			if(resultCode == ShowImageActivity.RESULT_CODE_DELETE){
				ArrayList<String> listPath = data.getStringArrayListExtra(ShowImageActivity.KEY_DELETE_PATH_LIST);
				deleteImages(listPath);
				setImageParams(this.data);
			}
			break;
		}
		case PIC_PHOTO_BY_ALBUM:
		{
			if (resultCode != Activity.RESULT_OK) {
				return;
			}
			Uri uri = data.getData();
			if (uri != null) {
				String filePath = Tools.getPathByUri(baseActivity, uri);
				if (filePath == null) {
					if (data != null && data.getExtras() != null) {
						Bitmap btm = data.getExtras().getParcelable("data");
						Tools.compressImage(baseActivity, btm, cropPath);
						ImageParams param = new ImageParams();
						param.path = cropPath;
						param.fileName = cropPath.substring(
								cropPath.lastIndexOf(File.separator)+1);
						insertAt(clickPosition, param);
					}
				} else {
					recycleBitmap(data);
					// startCropPhoto(uri);
					Tools.saveImageToPath(baseActivity, filePath,
							cropPath);
					ImageParams param = new ImageParams();
					param.path = cropPath;
					insertAt(clickPosition, param);
				}
				
			} else {
				if (data != null && data.getExtras() != null) {
					Bitmap btm = data.getExtras().getParcelable("data");
					Tools.compressImage(baseActivity, btm, cropPath);
					ImageParams param = new ImageParams();
					param.path = cropPath;
					insertAt(clickPosition, param);
				}
			}
			break;
		}
		case PIC_PHOTO_BY_CAMERA:
		{
			if (resultCode != Activity.RESULT_OK) {
				return;
			}
			if(isStorageMemory){
				Bitmap btm = data.getExtras().getParcelable("data");
				Tools.compressImage(baseActivity, btm, cropPath);
			}else{
				Tools.saveImageToPath(baseActivity, TAKE_PHOTO_PATH,
						cropPath);
			}
			ImageParams param = new ImageParams();
			param.path = cropPath;
			insertAt(clickPosition, param);
			break;
		}
		}
	}
	
	AlertDialog photoDialog;
	public void showPhotoSelector(){
		if (photoDialog == null) {
			photoDialog = new AlertDialog.Builder(baseActivity).create();
			photoDialog.show();
			View v = LayoutInflater.from(baseActivity).inflate(
					R.layout.set_photo_dialog_layout, null);
			v.findViewById(R.id.take_photo).setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							getImageFromCamera();
							photoDialog.dismiss();
						}
					});
			v.findViewById(R.id.choose_photo).setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							getImageFromAlbum();
							photoDialog.dismiss();
						}
					});
			v.findViewById(R.id.cancel).setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							photoDialog.dismiss();
						}
					});
			Window window = photoDialog.getWindow();
			WindowManager.LayoutParams p = window.getAttributes();
			DisplayMetrics metrics = Tools.getDisplayMetrics(baseActivity);
			p.width = metrics.widthPixels;
			p.gravity = Gravity.BOTTOM;
			window.setAttributes(p);
			window.setContentView(v);
		}
		photoDialog.show();
	}
	
	protected void getImageFromAlbum() {
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");// 相片类型
		baseActivity.startActivityForResult(intent, PIC_PHOTO_BY_ALBUM);
	}

	protected void getImageFromCamera() {
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		//M兆
		int maxMemorySize = maxMemory/(1024*1024);
    	Logger.logd("maxMemorySize = "+maxMemorySize+"Mb");
		if (android.os.Build.VERSION.SDK_INT <= 10 || maxMemorySize <= 32) {
			isStorageMemory = true;
			Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			baseActivity.startActivityForResult(cameraintent,
					PIC_PHOTO_BY_CAMERA);
			Logger.logd("no SD card");
		} else {
			isStorageMemory = false;
			FileOutputStream out = null;
			try {
				out = baseActivity.openFileOutput(PHOTO_NAME, Context.MODE_WORLD_WRITEABLE);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				if(out != null){
					out.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			TAKE_PHOTO_PATH = baseActivity.getFilesDir().getAbsolutePath()+
					File.separator+PHOTO_NAME;
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			// 指定调用相机拍照后照片的储存路径
			//intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0); 
			intent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(new File(TAKE_PHOTO_PATH)));
			baseActivity.startActivityForResult(intent,
					PIC_PHOTO_BY_CAMERA);
		}
	}
	

	public void recycleBitmap(Bitmap btm) {
		if (btm != null && !btm.isRecycled()) {
			btm.recycle();
			btm = null;
		}
	}


	public void recycleBitmap(Intent data) {
		if (data != null && data.getExtras() != null) {
			Bitmap btm = data.getExtras().getParcelable("data");
			recycleBitmap(btm);
			btm = null;
		}
	}
}
