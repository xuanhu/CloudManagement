package com.tg.cloudmanagement;

import java.util.ArrayList;

import com.tg.cloudmanagement.ComplaintActivity;
import com.tg.cloudmanagement.R;
import com.tg.cloudmanagement.base.BaseActivity;
import com.tg.cloudmanagement.constant.Contants;
import com.tg.cloudmanagement.info.UserInfo;
import com.tg.cloudmanagement.inter.NetworkRequestListener;
import com.tg.cloudmanagement.net.HttpTools;
import com.tg.cloudmanagement.net.MessageHandler;
import com.tg.cloudmanagement.net.RequestConfig;
import com.tg.cloudmanagement.net.RequestParams;
import com.tg.cloudmanagement.net.ResponseData;
import com.tg.cloudmanagement.object.SlideItemObj;
import com.tg.cloudmanagement.view.dialog.NetWorkListDialog;
import com.tg.cloudmanagement.view.dialog.ToastFactory;
import com.tg.cloudmanagement.view.spinnerwheel.WheelVerticalView;
import com.tg.cloudmanagement.view.spinnerwheel.SlideSelectorView.OnCompleteListener;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * 投诉页面
 */
public class ComplaintActivity extends BaseActivity{
	private LinearLayout llSection,llType;
	private String sectionId="";
	private String typeId="";
	private EditText editcontent;
	private NetWorkListDialog sectionDialog,typeDialog;
	private TextView tvSection,tvType;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		llSection=(LinearLayout) findViewById(R.id.ll_section);
		llType=(LinearLayout) findViewById(R.id.ll_type);
		editcontent=(EditText) findViewById(R.id.edit_content);
		tvSection=(TextView) findViewById(R.id.tv_section);
		tvType=(TextView) findViewById(R.id.tv_type);
		
		llSection.setOnClickListener(singleListener);
		llType.setOnClickListener(singleListener);
	}
	/**
	 * 点击事件处理
	 */
	@Override
	protected boolean handClickEvent(View v) {
		switch (v.getId()) {
		case R.id.ll_section://投诉类型选择
			showSectionDialog();
			break;
		case R.id.ll_type://投诉类型选择
			showTypeDialog();
			break;
		case R.id.right_layout:// 提交
			if(sectionId.length() == 0){
				ToastFactory.showToast(this, "请选择投诉部门");
				return false;
			}
			if(typeId.length() == 0){
				ToastFactory.showToast(this, "请选择投诉类型");
				return false;
			}
			String content = editcontent.getText().toString();
			if(content.length() == 0){
				ToastFactory.showToast(this, "请输入投诉内容");
				return false;
			}
			submit();
			break;
		}
		return super.handClickEvent(v);
	}
	
	/**
	 * 提交
	 */
	private void submit() {
		RequestConfig config = new RequestConfig(this,HttpTools.SET_COMPLAINT_INFO,"提交信息");
		RequestParams params = new RequestParams();
		params.put("type",0);
		params.put("identityType",1);
		params.put("category_id",typeId);
		params.put("community_id",sectionId);
		params.put("user_id", UserInfo.uid);
		params.put("userrealname",UserInfo.realname);
		params.put("content",editcontent.getText().toString());
		HttpTools.httpPost(Contants.URl.URl_3014,"/complainRepairs" ,config, params);
	}
	
	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		// TODO Auto-generated method stub
		super.onSuccess(msg, jsonString, hintString);
		if(msg.arg1==HttpTools.SET_COMPLAINT_INFO){
			ToastFactory.showToast(this, "你的投诉已收到");
			finish();
		}
	}
	/**
	 * 选择部门
	 */
	private void showSectionDialog() {
		if (sectionDialog == null) {
			sectionDialog = new NetWorkListDialog(this);
			sectionDialog.setNetworkListener(new NetworkRequestListener() {
				@Override
				public void onSuccess(WheelVerticalView wheelView, Message msg,
						String response) {
					String jsonString = HttpTools.getContentString(response);
					if (jsonString != null) {
						ResponseData data = HttpTools.getResponseData(jsonString);
						ArrayList<SlideItemObj> list = wheelView.getList();
						list.clear();
						for (int i = 0; i < data.length; i++) {
							list.add(new SlideItemObj(
									data.getString(i, "name"), 
									data.getString(i, "id")));
						}
						sectionDialog.notifyDataInvalidated();
					}
				}

				@Override
				public void onRequest(MessageHandler msgHand) {
					// TODO Auto-generated method stub
					RequestConfig config = new RequestConfig(ComplaintActivity.this, 0);
					config.handler = msgHand.getHandler();
					RequestParams params = new RequestParams();
					params.put("page", 1);
					params.put("pagesize", 99999);
					HttpTools.httpGet(Contants.URl.URl_3013,"/family", 
							config, params);
				}

				@Override
				public void onFail(Message msg, String message) {
					// TODO Auto-generated method stub

				}
			});
			sectionDialog.setOnCompleteClickListener(new OnCompleteListener() {

				@Override
				public void onComplete(SlideItemObj item1, SlideItemObj item2) {
					// TODO Auto-generated method stub
					if (item1 != null) {
						tvSection.setText(item1.name);
						sectionId = item1.id;
					}
				}
			});
		}
		sectionDialog.show("投诉部门选择", false);

	}

	/**
	 * 选择类型
	 */
	private void showTypeDialog() {
		if (typeDialog == null) {
			typeDialog = new NetWorkListDialog(this);
			typeDialog.setNetworkListener(new NetworkRequestListener() {
				@Override
				public void onSuccess(WheelVerticalView wheelView, Message msg,
						String response) {
					String jsonString = HttpTools.getContentString(response);
					if (jsonString != null) {
						ResponseData data = HttpTools.getResponseData(jsonString);
						ArrayList<SlideItemObj> list = wheelView.getList();
						list.clear();
						for (int i = 0; i < data.length; i++) {
							list.add(new SlideItemObj(
									data.getString(i, "name"), 
									data.getString(i, "id")));
						}
						typeDialog.notifyDataInvalidated();
					}
				}

				@Override
				public void onRequest(MessageHandler msgHand) {
					// TODO Auto-generated method stub
					RequestConfig config = new RequestConfig(ComplaintActivity.this, 0);
					config.handler = msgHand.getHandler();
					RequestParams params = new RequestParams();
					params.put("state", 1);
					params.put("type", 3);
					params.put("state", 2);
					HttpTools.httpGet(Contants.URl.URl_3014,"/category/select", 
							config, params);
				}

				@Override
				public void onFail(Message msg, String message) {
					// TODO Auto-generated method stub

				}
			});
			typeDialog.setOnCompleteClickListener(new OnCompleteListener() {

				@Override
				public void onComplete(SlideItemObj item1, SlideItemObj item2) {
					// TODO Auto-generated method stub
					if (item1 != null) {
						tvType.setText(item1.name);
						typeId = item1.id;
					}
				}
			});
		}
		typeDialog.show("投诉类型选择", false);
	}

	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		return getLayoutInflater().inflate(R.layout.activity_complaint, null);
	}

	@Override
	public String getHeadTitle() {
		headView.setRightText("提交");
		headView.setRightTextColor(getResources().getColor(R.color.white));
		headView.setListenerRight(singleListener);
		return "我要投诉";
	}

}
