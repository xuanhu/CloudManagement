package com.tg.cloudmanagement;

import com.tg.cloudmanagement.application.CloudManagementApplication;
import com.tg.cloudmanagement.base.BaseActivity;
import com.tg.cloudmanagement.constant.Contants;
import com.tg.cloudmanagement.database.SharedPreferencesTools;
import com.tg.cloudmanagement.info.UserInfo;
import com.tg.cloudmanagement.net.HttpTools;
import com.tg.cloudmanagement.net.RequestConfig;
import com.tg.cloudmanagement.net.RequestParams;
import com.tg.cloudmanagement.view.dialog.ToastFactory;

import android.os.Bundle;
import android.os.Message;
import android.R.color;
import android.view.View;
import android.widget.EditText;
/**
 * 修改密码
 * 
 * @author Administrator
 * 
 */
public class ModifiedPasswordActivity extends BaseActivity {
	private EditText editPwd1;
	private EditText editPwd2;
	private EditText editPwd3;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		editPwd1 = (EditText)findViewById(R.id.edit_password);
		editPwd2 = (EditText)findViewById(R.id.edit_password2);
		editPwd3 = (EditText)findViewById(R.id.edit_password3);
	}
	@Override
	protected boolean handClickEvent(View v) {
		// TODO Auto-generated method stub
		String pwd1;
		String pwd2;
		String pwd3;
		pwd1 = editPwd1.getText().toString();
		pwd2 = editPwd2.getText().toString();
		pwd3 = editPwd3.getText().toString();
		if(v.getId() == R.id.right_layout){
			pwd1 = editPwd1.getText().toString();
			if(pwd1.length() < 6){
				ToastFactory.showToast(this, "请输入不少于6位的密码");
				return false;
			}
			if(pwd2.length() < 6){
				ToastFactory.showToast(this, "请设置不少于6位的密码");
				return false;
			}
			if(pwd3.length() < 6){
				ToastFactory.showToast(this, "请设置不少于6位的密码");
				return false;
			}
			if(!pwd2.equals(pwd3)){
				ToastFactory.showToast(this, "确认密码和密码不一致");
				return false;
			}
			RequestConfig config = new RequestConfig(this, HttpTools.SET_PASSWORD);
			config.hintString = "修改密码";
			RequestParams params = new RequestParams("uid",UserInfo.uid);
			params.put("oldpwd",pwd1);
			params.put("newpwd",pwd2);
			HttpTools.httpGet(Contants.URl.URl_3013,"/administrator/modfiypwd", config, params);
		}
		return super.handClickEvent(v);
	}
	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		// TODO Auto-generated method stub
		super.onSuccess(msg, jsonString, hintString);
		if(msg.arg1== HttpTools.SET_PASSWORD){
			SharedPreferencesTools.clearUserId(ModifiedPasswordActivity.this);
			CloudManagementApplication.gotoLoginActivity(ModifiedPasswordActivity.this);
			finish();
		}
	}
	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		return getLayoutInflater().inflate(R.layout.activity_modified_password,
				null);
	}

	@Override
	public String getHeadTitle() {
		// TODO Auto-generated method stub
		headView.setRightText("提交");
		headView.setRightTextColor(getResources().getColor(color.white));
		headView.setListenerRight(singleListener);
		return "修改登录密码";
	}
}
