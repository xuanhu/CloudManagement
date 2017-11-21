package com.tg.cloudmanagement;

import java.util.ArrayList;

import com.tg.cloudmanagement.base.BaseActivity;
import com.tg.cloudmanagement.constant.Contants;
import com.tg.cloudmanagement.info.UserInfo;
import com.tg.cloudmanagement.inter.NetworkRequestListener;
import com.tg.cloudmanagement.net.HttpTools;
import com.tg.cloudmanagement.net.MessageHandler;
import com.tg.cloudmanagement.net.RequestConfig;
import com.tg.cloudmanagement.net.RequestParams;
import com.tg.cloudmanagement.net.ResponseData;
import com.tg.cloudmanagement.object.ImageParams;
import com.tg.cloudmanagement.object.SlideItemObj;
import com.tg.cloudmanagement.view.ImageViewGroup;
import com.tg.cloudmanagement.view.dialog.DialogFactory;
import com.tg.cloudmanagement.view.dialog.NetWorkListDialog;
import com.tg.cloudmanagement.view.dialog.ToastFactory;
import com.tg.cloudmanagement.view.spinnerwheel.WheelVerticalView;
import com.tg.cloudmanagement.view.spinnerwheel.SlideSelectorView.OnCompleteListener;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * 公共报修
 * @author Administrator
 *
 */
public class RepairsPulbicActivity extends BaseActivity {
	private LinearLayout repairstype;
	private EditText editcontent,etAddress;
	private ImageViewGroup imgGroup;
	private NetWorkListDialog typeDialog;
	private String houseId ="";
	private TextView tvtype;
	private String typeId = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		repairstype = (LinearLayout) findViewById(R.id.ll_repairstype);
		editcontent = (EditText) findViewById(R.id.edit_content);
		imgGroup = (ImageViewGroup) findViewById(R.id.imgGroup);
		tvtype = (TextView) findViewById(R.id.tv_type);
		etAddress=(EditText) findViewById(R.id.et_address);
		repairstype.setOnClickListener(singleListener);
		imgGroup.setAddable(true);
	}

	/**
	 * 点击事件处理
	 */
	@Override
	protected boolean handClickEvent(View v) {
		switch (v.getId()) {
		case R.id.ll_repairstype:// 选择类型
			showtypeDialog();
			break;
		case R.id.right_layout:// 提交
			String address = etAddress.getText().toString();
			if (address.length() == 0) {
				ToastFactory.showToast(this, "请输入报修地址");
				return false;
			}
			if(typeId.length() == 0){
				ToastFactory.showToast(this, "请选择报修类型");
				return false;
			}
			String content = editcontent.getText().toString();
			if(content.length() == 0){
				ToastFactory.showToast(this, "请输入报修内容");
				return false;
			}
			ArrayList<ImageParams> list = imgGroup.getPostImageParam();
			if(list == null || list.size() == 0){
				submit();
			}else{
				HttpTools.postImages(Contants.URl.URl_3020,mHand, list);
			}
			break;
		}
		return super.handClickEvent(v);
	}

	/**
	 * 提交
	 */
	private void submit() {
		RequestConfig config = new RequestConfig(this,HttpTools.SET_REPAIR_INFO,"提交信息");
		RequestParams params = new RequestParams();
		params.put("type","1");
		params.put("identityType","1");
		params.put("category_id",typeId);
		params.put("user_id", Integer.toString(UserInfo.uid));
		params.put("userrealname", UserInfo.realname);
		params.put("community_id",houseId);
		params.put("detailedaddress", etAddress.getText().toString());
		params.put("content",editcontent.getText().toString());
		String urls = imgGroup.getPostUrls();
		if(!TextUtils.isEmpty(urls)){
			params.put("imgurls", urls);
		}
		HttpTools.httpPost(Contants.URl.URl_3014,"/complainRepairs" ,config, params);
	}
	
	@Override
	public void onRequestStart(Message msg, String hintString) {
		// TODO Auto-generated method stub
		if(msg.arg1 == HttpTools.POST_IMAG){
			ImageParams param = (ImageParams)msg.getData().getParcelable(HttpTools.KEY_IMAGE_PARAMS);
			DialogFactory.getInstance().showTransitionDialog(this, 
					"正在上传图片... "+(param.position+1)+"/"+(imgGroup.getImageParams().size()-1), 
					this.toString(), HttpTools.POST_IMAG);
		}else{
			super.onRequestStart(msg, hintString);
		}
	}
	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		super.onSuccess(msg, jsonString, hintString);
		if(msg.arg1 == HttpTools.POST_IMAG){
			Bundle bundle = msg.getData();
			if(bundle != null){
				ImageParams param = (ImageParams)bundle.getParcelable(HttpTools.KEY_IMAGE_PARAMS);
				int position = param.position;
				imgGroup.getImageParams().get(position).url = Contants.URl.IMG_3020+HttpTools.getFileNameString(jsonString);
				boolean isLast = bundle.getBoolean(HttpTools.KEY_IS_LAST, false);
				if(isLast){
					submit();
				}
			}
		}else if(msg.arg1==HttpTools.SET_REPAIR_INFO){
			ToastFactory.showToast(this, "你的报修已收到");
			finish();
		}
	}

	/**
	 * 选择类型
	 */
	private void showtypeDialog() {
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
					RequestConfig config = new RequestConfig(RepairsPulbicActivity.this, 0);
					config.handler = msgHand.getHandler();
					RequestParams params = new RequestParams();
					params.put("state", 2);
					params.put("type", 1);
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
						tvtype.setText(item1.name);
						typeId = item1.id;
					}
				}
			});
		}
		typeDialog.show("报修类型", false);

	}

	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		return getLayoutInflater().inflate(R.layout.activity_repairs_pulbic,
				null);
	}

	@Override
	public String getHeadTitle() {
		// TODO Auto-generated method stub
		headView.setRightText("提交");
		headView.setRightTextColor(getResources().getColor(R.color.white));
		headView.setListenerRight(singleListener);
		return "公共报修";
	}

}
