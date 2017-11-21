package com.tg.cloudmanagement;

import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tg.cloudmanagement.*;
import com.tg.cloudmanagement.application.CloudManagementApplication;
import com.tg.cloudmanagement.base.BaseActivity;
import com.tg.cloudmanagement.constant.Contants;
import com.tg.cloudmanagement.database.SharedPreferencesTools;
import com.tg.cloudmanagement.log.Logger;
import com.tg.cloudmanagement.net.HttpTools;
import com.tg.cloudmanagement.net.MD5;
import com.tg.cloudmanagement.net.RequestConfig;
import com.tg.cloudmanagement.net.RequestParams;
import com.tg.cloudmanagement.net.ResponseData;
import com.tg.cloudmanagement.object.ImageParams;
import com.tg.cloudmanagement.util.NetWorkUtils;
import com.tg.cloudmanagement.util.Tools;
import com.tg.cloudmanagement.view.dialog.ToastFactory;

import android.os.Bundle;
import android.os.Message;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.EditText;
import android.widget.ImageView;
/**
 * 登录页面
 * @author Administrator
 *
 */
public class LoginActivity extends BaseActivity implements AnimationListener {
	private EditText editUser;
	private EditText editPassword;
	private ImageView ivClose;
	private View startLayout;
	private View contentLayout;
	private Animation outAnim;
	private Animation inAnim;
	private String newPhone = "";
	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		headView.setBackgroundColor(getResources().getColor(R.color.white));
		return null;
	}

	@Override
	public String getHeadTitle() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected boolean handClickEvent(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.submit:
			login();// 登录
			break;
		/*case R.id.forget_pwd:
			forgetPassword();// 忘记密码
			break;*/
		case R.id.iv_close://关闭
			finish();
			break;
		}
		return super.handClickEvent(v);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		contentLayout = findViewById(R.id.login_content);
		editUser = (EditText) findViewById(R.id.edit_user);
		startLayout = findViewById(R.id.start_layout);
		editPassword = (EditText) findViewById(R.id.edit_password);
		ivClose = (ImageView) findViewById(R.id.iv_close);
		ivClose.setOnClickListener(singleListener);
		findViewById(R.id.submit).setOnClickListener(singleListener);
		//findViewById(R.id.forget_pwd).setOnClickListener(singleListener);
		Date dt = new Date();
		Long time = dt.getTime();
		RequestParams params = new RequestParams();
		params.put("timestamp", time);
		RequestConfig config = new RequestConfig(this, HttpTools.GET_TS);
		HttpTools.httpGetTS(Contants.URl.URl_3011,"/ts", config, params);
		editUser.setText(Tools.getUserName(this));
		showStartPager();
	}

	private void showStartPager() {
		startLayout.setVisibility(View.VISIBLE);
		contentLayout.setVisibility(View.GONE);
		mHand.postDelayed(new Runnable() {
			@Override
			public void run() {
				ResponseData userInfoData = SharedPreferencesTools.getUserInfo(LoginActivity.this);
				if (userInfoData.length > 0) {
					Tools.loadUserInfo(userInfoData, null);
					startActivity(new Intent(LoginActivity.this, com.tg.cloudmanagement.MainActivity.class));
					finish();
				} else {
					showContentView();
				}
			}
		}, 2000);
	}

	private void showContentView() {
		if (outAnim == null || inAnim == null) {
			outAnim = AnimationUtils.loadAnimation(this,
					android.R.anim.fade_out);
			inAnim = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
			outAnim.setDuration(500);
			inAnim.setDuration(500);
			outAnim.setAnimationListener(this);
			inAnim.setAnimationListener(this);
		}
		startLayout.setVisibility(View.VISIBLE);
		contentLayout.setVisibility(View.VISIBLE);
		startLayout.startAnimation(outAnim);
		contentLayout.startAnimation(inAnim);
	}


	/**
	 * 忘记密码
	 */
	public void forgetPassword() {
		//Intent intent = new Intent(this, ForgetPasswordActivity.class);
		//startActivity(intent);
	}

	/**
	 * 登录
	 */
	public void login() {
		newPhone=editUser.getText().toString();
		if (newPhone.length() <= 0) {
			ToastFactory.showToast(this, "请输入账号");
			return;
		}
		String password = editPassword.getText().toString();
		if (password.length() < 6) {
			ToastFactory.showToast(this, "请输入不少于6位的密码");
			return;
		}
		try {
			String passwordMD5 = MD5.getMd5Value(password).toLowerCase();
			String loginip = NetWorkUtils.getLocalIpAddress(this);
			Log.d("TAG","passwordMD5="+passwordMD5);
			Tools.hideKeyboard(editUser);
			RequestParams params = new RequestParams();
			params.put("userName", newPhone);
			params.put("password", passwordMD5);
			params.put("loginip", loginip);
			RequestConfig config = new RequestConfig(this, HttpTools.GET_LOGIN);
			config.hintString = "登录";
			HttpTools.httpPost(Contants.URl.URl_3013,"/administrator/login", config, params);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		super.onSuccess(msg, jsonString, hintString);
		int code = HttpTools.getCode(jsonString);
		JSONArray jsonArray = HttpTools.getContentJsonArray(jsonString);
		ResponseData data = HttpTools.getResponseContent(jsonArray);
		if (msg.arg1 == HttpTools.POST_IMAG) {
			ToastFactory.showToast(this,hintString);
			Logger.logd("POST_IMAG" + jsonString);
			ImageParams params = msg.getData().getParcelable(HttpTools.KEY_IMAGE_PARAMS);
			Logger.logd("path = " + params.path);
			Logger.logd("fileName =" + params.fileName);
			Logger.logd("position =" + params.position);
		} else if(msg.arg1 == HttpTools.GET_LOGIN){
			if (code == 0) {
				if(data!=null){
					ToastFactory.showToast(this, "登录成功");
					Date dt = new Date();
					Long time = dt.getTime();
					String date = Tools.getDateToString(time);
					Tools.saveDateInfo(this,date);
					Tools.loadUserInfo(data,jsonString);
					Intent intent = new Intent(this, com.tg.cloudmanagement.MainActivity.class);
					intent.putExtra(com.tg.cloudmanagement.MainActivity.KEY_NEDD_FRESH, false);
					startActivity(intent);
					finish();
				}
			}else{
				ToastFactory.showToast(this, "用户名或者密码错误");
			}
		}else if(msg.arg1 == HttpTools.GET_TS){
			JSONObject jb = HttpTools.getContentJSONObject(jsonString);
			if(jb != null){
				try {
					Long difference = jb.getLong("difference");
					SharedPreferences.Editor sharedata = CloudManagementApplication.getInstance().getSharedPreferences("APP_TS", 0).edit();
					sharedata.putLong(HttpTools.DIFFERENCE,difference);
					sharedata.commit();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		if (animation == outAnim) {
			startLayout.setVisibility(View.GONE);
			contentLayout.setVisibility(View.VISIBLE);
		} else {
			
		}
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFail(Message msg, String hintString) {
		// TODO Auto-generated method stub
		super.onFail(msg, hintString);
	}

	@Override
	public void onCancel(Object tag, int requestCode) {
		// TODO Auto-generated method stub
		super.onCancel(tag, requestCode);
		if (requestCode == HttpTools.GET_USER_INFO) {
			showContentView();
		}
	}
}

