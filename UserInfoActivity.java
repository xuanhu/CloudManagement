package com.tg.cloudmanagement;

import java.util.ArrayList;

import org.json.JSONArray;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.tg.cloudmanagement.R.color;
import com.tg.cloudmanagement.base.BaseActivity;
import com.tg.cloudmanagement.constant.Contants;
import com.tg.cloudmanagement.info.UserInfo;
import com.tg.cloudmanagement.net.HttpTools;
import com.tg.cloudmanagement.net.RequestConfig;
import com.tg.cloudmanagement.net.RequestParams;
import com.tg.cloudmanagement.net.ResponseData;
import com.tg.cloudmanagement.net.image.VolleyUtils;
import com.tg.cloudmanagement.object.ImageParams;
import com.tg.cloudmanagement.object.ViewConfig;
import com.tg.cloudmanagement.util.Tools;
import com.tg.cloudmanagement.view.CameraView;
import com.tg.cloudmanagement.view.MessageArrowView;
import com.tg.cloudmanagement.view.CameraView.STATE;
import com.tg.cloudmanagement.view.MessageArrowView.ItemClickListener;
import com.tg.cloudmanagement.view.dialog.DialogFactory;
import com.tg.cloudmanagement.view.dialog.ToastFactory;

import android.os.Bundle;
import android.os.Message;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class UserInfoActivity extends BaseActivity implements ItemClickListener, OnClickListener {
	private MessageArrowView messageView1;
	private MessageArrowView messageView2;
	private TextView tvRealName,tvGender,tvSection,tvJobName;
	private ArrayList<ViewConfig> list1 = new ArrayList<ViewConfig>();
	private ArrayList<ViewConfig> list2 = new ArrayList<ViewConfig>();
	private boolean needPostImage = false;
	private String userUrl = "";
	private String mobile = "";
	//private String email = "";
	private String sex = "";
	private String headImgPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		messageView1 = (MessageArrowView) findViewById(R.id.messageView1);
		messageView2 = (MessageArrowView) findViewById(R.id.messageView2);
		messageView1.setItemClickListener(this);
		messageView2.setItemClickListener(this);
		messageView1.setEditable(true);
		messageView2.setEditable(true);
		initView();
		RequestParams params = new RequestParams();
		params.put("uid", UserInfo.uid);
		HttpTools.httpGet(Contants.URl.URl_3013,"/administrator/"+UserInfo.uid, 
				new RequestConfig(this, HttpTools.GET_USER_INFO), params);
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		tvRealName=(TextView) findViewById(R.id.tv_realname);
		tvGender=(TextView) findViewById(R.id.tv_gender);
		tvSection=(TextView) findViewById(R.id.tv_section);
		tvJobName=(TextView) findViewById(R.id.tv_jobname);
		tvRealName.setText(UserInfo.realname);
		tvGender.setText(UserInfo.sex);
		tvSection.setText(UserInfo.propertyName);
		tvJobName.setText(UserInfo.jobName);
		
		userUrl = UserInfo.headUrl;
	//	sex = UserInfo.sex;
		mobile=UserInfo.Mobile;
	//	email=UserInfo.email;
		
		int size = (int)(50 * Tools.getDisplayMetrics(this).density);
		list1.clear();
		ViewConfig config = new ViewConfig("头像","",false);
		config.rightDrawable = getResources().getDrawable(R.drawable.moren_xinxiguanli);
		config.rightImgWidth = size;
		config.rightImgHeight = size;
		config.rightImgScaleType = ImageView.ScaleType.CENTER_CROP;
		list1.add(config);
		messageView1.setData(list1);
		
		
		list2.clear();
		config = new ViewConfig("手机号码",mobile,false);
		config.rightEditable = true;
		config.enable = false;
		list2.add(config);
		/*config = new ViewConfig("E-mail",email,false);
		config.rightEditable = true;
		config.enable = false;
		list2.add(config);*/
		messageView2.setData(list2);
		if(!TextUtils.isEmpty(UserInfo.headUrl)){
			VolleyUtils.getImageLoader(this).get(UserInfo.headUrl, new ImageListener() {
				
				@Override
				public void onErrorResponse(VolleyError arg0) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onResponse(ImageContainer arg0, boolean arg1) {
					// TODO Auto-generated method stub
					if(arg0.getBitmap() != null){
						list1.get(0).rightDrawable = new BitmapDrawable(arg0.getBitmap());
						messageView1.freshView(0);
					}
				}
			}, size, size);
		}
	}
	private void updateView(){
		tvRealName.setText(UserInfo.realname);
		tvGender.setText(UserInfo.sex);
		tvSection.setText(UserInfo.propertyName);
		tvJobName.setText(UserInfo.jobName);
		list2.get(0).rightText = UserInfo.Mobile;
		//list2.get(1).rightText = UserInfo.email;
		messageView2.freshAll();
	}
	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		super.onSuccess(msg, jsonString, hintString);
		JSONArray response = HttpTools.getContentJsonArray(jsonString);
		ResponseData data = HttpTools.getResponseContent(response);
		if(msg.arg1 == HttpTools.SET_USER_INFO){
			headView.setRightText("保存");
			messageView1.setEditable(true);
			messageView2.setEditable(true);
			setUserInfo();
			updateView();
			ToastFactory.showToast(this, hintString);
			sendBroadcast(new Intent(MainActivity.ACTION_FRESH_USERINFO));
			finish();
		}else if(msg.arg1 == HttpTools.GET_USER_INFO){
			Log.d("TAG", "个人信息   jsonString="+jsonString);
			if(Tools.loadUserInfo(data,jsonString)){
				updateView();
			}
		}else if(msg.arg1 == HttpTools.POST_IMAG){
			needPostImage = false;
			userUrl = Contants.URl.IMG_3020+HttpTools.getFileNameString(jsonString);
			submitUserInfo();
		}
	}
	
	private void setUserInfo(){
		UserInfo.headUrl = userUrl;
		//UserInfo.sex = sex;
		UserInfo.Mobile = mobile;
		//UserInfo.email = email;
		Tools.saveUserInfo(this);
	}
	
	@Override
	public void returnData(CameraView cv, STATE state, int groupPosition,
			int childPosition, int position, Bitmap bitmap, String path) {
		// TODO Auto-generated method stub
		super.returnData(cv, state, groupPosition, childPosition, position, bitmap,
				path);
		needPostImage = true;
		list1.get(0).rightDrawable = new BitmapDrawable(bitmap);
		messageView1.freshView(0);
	}
	
	@Override
	public void onItemClick(MessageArrowView mv, View v, int position) {
		// TODO Auto-generated method stub
		if (mv == messageView1) {
			if (position == 0) {
				if (headImgPath == null) {
					headImgPath = getFilesDir().getAbsolutePath() + "/"+ "head.jpg";
				}
				DialogFactory.getInstance().showPhotoSelector(this, null,headImgPath, 0, 0, 0);
			}
		}
	}
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.back_layout){
			if(hasChanged()){//已经修改过信息
				DialogFactory.getInstance().showDialog(UserInfoActivity.this, new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				}, null, "信息还没保存，确定要返回吗？", null, null);
			}else{
				finish();
			}
		}else {
			if(messageView1.isEditable()){
				if(needPostImage){
					ImageParams imgParams = new ImageParams();
					imgParams.fileName = "head.jpg";
					imgParams.path = headImgPath;
					HttpTools.postAnImage(Contants.URl.URl_3020,mHand, imgParams);
				}else{
					submitUserInfo();
				}
				
			}else{
				headView.setRightText("保存");
				messageView1.setEditable(true);
				messageView2.setEditable(true);
			}
		}
	}
	@Override
	public void onBackPressed() {
		backPress();
	}
	
	protected void backPress() {
		super.onDestroy();
		if(hasChanged()){//已经修改过信息
			DialogFactory.getInstance().showDialog(UserInfoActivity.this, new OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			}, null, "信息还没保存，确定要返回吗？", null, null);
		}else{
			finish();
		}
	}
	private void submitUserInfo(){
		if(!hasChanged()){
			headView.setRightText("保存");
			messageView1.setEditable(true);
			messageView2.setEditable(true);
			return;
		}
		RequestConfig config = new RequestConfig(this, HttpTools.SET_USER_INFO);
		RequestParams params = new RequestParams
				("uid", Integer.toString(UserInfo.uid)).
				put("Icon",userUrl).
				put("Mobile",mobile);
		config.hintString = "修改个人信息";
		HttpTools.httpPut(Contants.URl.URl_3013, "/administrator", config,params);
	}
	
	private boolean hasChanged(){
		mobile=messageView2.getRightTextString(0);
		//email=messageView2.getRightTextString(1);
		if(!TextUtils.equals(userUrl, UserInfo.headUrl)){
			return true;
		}
		if(!TextUtils.equals(mobile, UserInfo.Mobile)){
			return true;
		}
		/*if(!TextUtils.equals(email, UserInfo.email)){
			return true;
		}*/
		return false;
	}
	
	
	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		return getLayoutInflater().inflate(R.layout.activity_user_info, null);
	}

	@Override
	public String getHeadTitle() {
		// TODO Auto-generated method stub
		headView.setRightText("保存");
		headView.setRightTextColor(getResources().getColor(color.white));
		headView.setListenerRight(this);
		headView.setListenerBack(this);
		return "个人资料";
	}
}
