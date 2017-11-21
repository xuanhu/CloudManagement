package com.tg.cloudmanagement.view;


import com.tg.cloudmanagement.R;
import com.tg.cloudmanagement.base.BaseActivity;
import java.io.File;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;



public class CameraView extends LinearLayout implements OnClickListener{
	public static final String XMLNS = "http://schemas.android.com/apk/res/com.tg.cloudmanagement";
	public static final int DELETE_PHOTO_CODE = 1;
	public interface OnCameraViewClickListener{
		void onCameraClick(CameraView cv, STATE state, int groupPosition, int childPosition, int position);
	} 
	private LinearLayout cameraLayout;
	private ImageView imgPhoto;
	private ImageView imgCamera;
	private TextView tvCamera;
	private Bitmap btm; 
	private String path;
	private BaseActivity baseActivity;
	private boolean takePhoto = false;
	private String url;
	public int groupPosition;
	public int childPosition;
	public int position;
	private Drawable defaultDrawable;
	private OnCameraViewClickListener listener;
	public void setOnCameraViewClickListener(OnCameraViewClickListener l){
		listener = l;
	}
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public void setPosition(int groupPosition, int childPosition, int position){
		this.groupPosition = groupPosition;
		this.childPosition = childPosition;
		this.position = position;
	}
	
	public STATE getState(){
		return state;
	}
	
	public int getGroupPosition(){
		return groupPosition;
	}
	
	public int getChildPosition(){
		return childPosition;
	}
	
	public int getPosition(){
		return position;
	}
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public enum STATE{
		STATE_CAMERA,
		STATE_PHOTO,
		STATE_SELECT
	};
	private STATE state = STATE.STATE_CAMERA;
	public CameraView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		init(context,attrs);
	}
	
	public CameraView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context,attrs);
	}

	public CameraView(Context context) {
		super(context);
		init(context,null);
	}
	
	public void setText(String str){
		if(str == null){
			str = "请拍照";
		}
		tvCamera.setText(str);
	}
	
	public void setTakePhotoBitmap(Bitmap btm){
		this.btm = btm;
		takePhoto = true;
		state = STATE.STATE_PHOTO;
		imgPhoto.setImageBitmap(btm);
		imgPhoto.setVisibility(View.VISIBLE);
		cameraLayout.setVisibility(View.GONE);
	}
	
	public void recycle(){
		if(btm != null && !btm.isRecycled() ){
			btm.recycle();
			btm = null;
		}
	}
	
	public void setTakePhotoPath(String path){
		if(TextUtils.isEmpty(path)){
			return;
		}
		File file = new File(path);
		if(!file.exists()){
			return;
		}
		setPath(path);
		state = STATE.STATE_PHOTO;
		imgPhoto.setImageURI(Uri.fromFile(file));
		imgPhoto.setVisibility(View.VISIBLE);
		cameraLayout.setVisibility(View.GONE); 
	}
	
	public boolean isSetBitmap(){
		return state == STATE.STATE_PHOTO;
	}
	
	public void setState(STATE s){
		state = s;
		switch(state){
			case STATE_CAMERA:
				showCamera();
				break;
			case STATE_PHOTO:
				break;
			case STATE_SELECT:
				setDefaultImage();
				break;
		}
	}
	
	public void hideTextView(){
		tvCamera.setVisibility(View.GONE);
	}
	
	public void setDefaultImage(){
		state = STATE.STATE_SELECT;
		if(defaultDrawable != null){
			imgPhoto.setImageDrawable(defaultDrawable);
		}else{
			imgPhoto.setImageResource(R.drawable.moren_xinxiguanli);
		}
		imgPhoto.setVisibility(View.VISIBLE);
		cameraLayout.setVisibility(View.GONE);
	}
	
	public void showCamera(){
		state = STATE.STATE_CAMERA;
		takePhoto = false;
		imgPhoto.setVisibility(View.GONE);
		cameraLayout.setVisibility(View.VISIBLE);
	}
	
	public void setSelectImage(int resID){
		state = STATE.STATE_SELECT;
		imgPhoto.setImageResource(resID);
		imgPhoto.setVisibility(View.VISIBLE);
		cameraLayout.setVisibility(View.GONE);
	}
	
	public void setSelectImage(Bitmap btm){
		state = STATE.STATE_SELECT;
		imgPhoto.setImageBitmap(btm);
		imgPhoto.setVisibility(View.VISIBLE);
		cameraLayout.setVisibility(View.GONE);
	}
	
	public void setSelectImageUrl(String url){
		state = STATE.STATE_SELECT;
		setUrl(url);
	}
	public void setSelectPath(String path){
		if(TextUtils.isEmpty(path)){
			return;
		}
		File file = new File(path);
		if(!file.exists()){
			return;
		}
		setPath(path);
		state = STATE.STATE_SELECT;
		imgPhoto.setImageURI(Uri.fromFile(file));
		imgPhoto.setVisibility(View.VISIBLE);
		cameraLayout.setVisibility(View.GONE);
	}
	
	public void setTakePhotoImage(int resID){
		state = STATE.STATE_PHOTO;
		imgPhoto.setImageResource(resID);
		imgPhoto.setVisibility(View.VISIBLE);
		cameraLayout.setVisibility(View.GONE);
	}
	
	public void init(Context context ,AttributeSet attrs){
		baseActivity = (BaseActivity)context;
		LayoutInflater.from(context).inflate(R.layout.camera_layout, this);
		cameraLayout = (LinearLayout)findViewById(R.id.camera_lyt);
		imgPhoto = (ImageView)findViewById(R.id.photo);
		imgCamera = (ImageView)findViewById(R.id.img_camera);
		tvCamera = (TextView)findViewById(R.id.tv_type);
		setOnClickListener(this);
		int resID = 0;
		boolean fitCenter = false;
		if(attrs != null){
			String text = attrs.getAttributeValue(XMLNS, "hintText");
			resID = attrs.getAttributeResourceValue(XMLNS, "background", 0);
			fitCenter = attrs.getAttributeBooleanValue(XMLNS, "fitCenter", false);
			int defBg = attrs.getAttributeResourceValue(XMLNS, "default_bg", 0);
			if(defBg > 0){
				defaultDrawable = getResources().getDrawable(defBg);
			}
			if(TextUtils.isEmpty(text)){
				text = "正面";
			}
			tvCamera.setText(text);
		}else{
			tvCamera.setText("正面");
		}
		if(resID > 0){
			imgCamera.setBackgroundResource(resID);
		}
		if(fitCenter){
			imgPhoto.setScaleType(ScaleType.FIT_CENTER);
		}else{
			imgPhoto.setScaleType(ScaleType.CENTER_CROP);
		}
	}
	
	public void setCenterInside(boolean centerInside){
		if(centerInside){
			imgPhoto.setScaleType(ScaleType.FIT_CENTER);
		}else{
			imgPhoto.setScaleType(ScaleType.CENTER_CROP);
		}
		invalidate();
	}

	@Override
	public void onClick(View v) {
		if(listener != null){
			listener.onCameraClick(this,state,groupPosition, childPosition, position);
		}
	}
}
