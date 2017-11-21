package com.tg.cloudmanagement;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.tg.cloudmanagement.R;
import com.tg.cloudmanagement.adapter.EmployeePhoneAdapter;
import com.tg.cloudmanagement.base.BaseActivity;
import com.tg.cloudmanagement.base.BaseBrowserActivity;
import com.tg.cloudmanagement.constant.Contants;
import com.tg.cloudmanagement.info.EmployeePhoneInfo;
import com.tg.cloudmanagement.info.LinkManInfo;
import com.tg.cloudmanagement.info.UserInfo;
import com.tg.cloudmanagement.net.HttpTools;
import com.tg.cloudmanagement.net.MessageHandler;
import com.tg.cloudmanagement.net.RequestConfig;
import com.tg.cloudmanagement.net.RequestParams;
import com.tg.cloudmanagement.net.ResponseData;
import com.tg.cloudmanagement.net.image.VolleyUtils;
import com.tg.cloudmanagement.util.Tools;
import com.tg.cloudmanagement.view.CircularImageView;
import com.tg.cloudmanagement.view.ManageMentLinearlayout;
import com.tg.cloudmanagement.view.ManageMentLinearlayout.NetworkRequestListener;
import com.tg.cloudmanagement.view.dialog.ToastFactory;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 员工名片
 * 
 * @author Administrator
 * 
 */
public class EmployeeDataActivity extends BaseActivity {
	public final static String CONTACTS_ID="contacts_id";
	public final static String ID="id";
	public final static String CONTACTS_CHECKED = "isChecked";
	private int contactsID; 
	private int id; 
	private ManageMentLinearlayout magLinearLayout;
	private String magUrl;
	private CheckBox cbCollect;
	private LinearLayout llSendSms;
	private LinkManInfo item;
	private EmployeePhoneInfo info;
	private TextView tvName,tvJob,tvBranch;
	private CircularImageView ivHead;
	private ImageView ivClose;
	private ListView mlListView;
	private View footView;
	private ArrayList<EmployeePhoneInfo> PhoneList = new ArrayList<EmployeePhoneInfo>(); 
	private EmployeePhoneAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_employee_data);
		Intent intent = getIntent();
		if(intent != null){
			contactsID = intent.getIntExtra(CONTACTS_ID, -1);
			id = intent.getIntExtra(ID, -1);
		}
		if(contactsID < 0){
			ToastFactory.showToast(this, "参数错误");
			finish();
			return;
		}
		RequestConfig config = new RequestConfig(this,HttpTools.GET_EMPLOYEE_INFO,"获取详细信息");
		RequestParams params = new RequestParams();
		params.put("uid",contactsID);
		params.put("operatorid",UserInfo.uid);
		params.put("page", 1);
		params.put("pagesize", 1);
		HttpTools.httpGet(Contants.URl.URl_3013, "/administrator/getadministratorbusinessCard",config, params);
		initView();
		magLinearLayout.loaddingData();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		ivHead=(CircularImageView) findViewById(R.id.iv_head);
		//ivHead.setCircleShape();
		tvName=(TextView) findViewById(R.id.tv_name);
		tvJob=(TextView) findViewById(R.id.tv_job);
		tvBranch=(TextView) findViewById(R.id.tv_branch);
		mlListView=(ListView) findViewById(R.id.lv_employee_phone);
		ivClose = (ImageView) findViewById(R.id.iv_close);
		ivClose.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		mlListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				String phone = PhoneList.get(position).phone;
				if(TextUtils.isEmpty(phone)){
					ToastFactory.showToast(EmployeeDataActivity.this, "暂无联系电话");
					return;
				}
				Tools.call(EmployeeDataActivity.this, phone);	
			}
		});
		
		llSendSms=(LinearLayout) findViewById(R.id.ll_sendsms);
		magLinearLayout=(ManageMentLinearlayout) findViewById(R.id.ll_sendemail);
		llSendSms.setOnClickListener(singleListener);
		magLinearLayout.setOnClickListener(singleListener);
		cbCollect=(CheckBox) findViewById(R.id.cb_collect);
		magLinearLayout.setNetworkRequestListener(new NetworkRequestListener() {
			
			@Override
			public void onSuccess(ManageMentLinearlayout magLearLayout, Message msg,
					String response) {
				String jsonString = HttpTools.getContentString(response);
				if (jsonString != null) {
					ResponseData data = HttpTools.getResponseData(jsonString);
					if (data.length > 0) {
						for (int i = 0; i < data.length; i++) {
							if(data.getString(i, "name").equals("邮件管理")){
								magUrl = data.getString(i, "keystr");
							}
						}
					}
				}
			}
			
			@Override
			public void onRequest(MessageHandler msgHand) {
				RequestConfig config = new RequestConfig(EmployeeDataActivity.this,0);
				config.handler = msgHand.getHandler();
				RequestParams params = new RequestParams();
				params.put("uid", UserInfo.uid);
				params.put("isHTML5url", 1);
				params.put("categoryid", 1);
				params.put("page", 1);
				params.put("pagesize", 99);
				HttpTools.httpGet(Contants.URl.URl_3011, "/weiApplication",config, params);
			}
		});
	}

	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		super.onSuccess(msg, jsonString, hintString);
		if (msg.arg1 == HttpTools.GET_EMPLOYEE_INFO) {
			String response = HttpTools.getContentString(jsonString);
			if (jsonString != null) {
				ResponseData data = HttpTools.getResponseData(response);
				item= new LinkManInfo();
				id=data.getInt("id");
				item.contactsId=data.getInt("uid");
				item.username=data.getString("username");
				item.realname=data.getString("realname");
				item.icon=data.getString("Icon");
				item.sex=data.getString("sex");
				item.email=data.getString("email");
				item.qq=data.getString("qq");
				item.phone=data.getString("Mobile");
				item.enterprise_cornet=data.getString("tel");
				item.job_name=data.getString("job_name");
				item.property_name=data.getString("property_name");
				item.iscontacts = data.getInt("iscontacts");
			}
			
			if(item!=null){
				tvName.setText(item.realname+"("+item.username+")");
				tvJob.setText(item.job_name);
				tvBranch.setText(item.property_name);
				VolleyUtils.getImage(this, item.icon,ivHead,R.drawable.moren_geren);
				if(item.iscontacts == 0){
					cbCollect.setChecked(false);
				}else if(item.iscontacts > 0){
					cbCollect.setChecked(true);
				}
				cbCollect.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if(isChecked == true){
							submit();//添加联系人
						}else if(isChecked == false){
							delete();
						}
					}
				});
				
				if(mlListView.getFooterViewsCount() > 0){
					mlListView.removeFooterView(footView);
				}
				if(item.phone != null){
					String[] str =item.phone.split("，");
					for(int i=0;i<str.length;i++){
						info = new EmployeePhoneInfo();
						info.phone=str[i];
						PhoneList.add(info);
					}
				}
				if(footView == null){
					footView = getLayoutInflater().inflate(R.layout.employee_foot, null);
				}
				TextView tvCornet= (TextView) footView.findViewById(R.id.tv_cornet);
				TextView tvSection= (TextView) footView.findViewById(R.id.tv_section);
				RelativeLayout rlEnterpriseCornet = (RelativeLayout) footView.findViewById(R.id.rl_enterprise_cornet);
				RelativeLayout rlSection = (RelativeLayout) footView.findViewById(R.id.rl_section);
				rlEnterpriseCornet.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(TextUtils.isEmpty(item.enterprise_cornet)){
							ToastFactory.showToast(EmployeeDataActivity.this, "暂无联系电话");
							return;
						}
						Tools.call(EmployeeDataActivity.this, item.enterprise_cornet);						
					}
				});
				
				rlSection.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						/*Intent intent1 = new Intent(EmployeeDataActivity.this,Organization01Activity.class);
						intent1.putExtra(Organization01Activity.TEXT_ID,UserInfo.propertyCoding);
						intent1.putExtra(Organization01Activity.TEXT_FAMILY, item.family);
						intent1.putExtra(Organization01Activity.TEXT_STRUCTURE,item.family);
						startActivity(intent1);*/
					}
				});
				
				tvCornet.setText(item.enterprise_cornet);
				tvSection.setText(item.property_name);
				mlListView.addFooterView(footView);
				adapter= new EmployeePhoneAdapter(this, PhoneList);
				mlListView.setAdapter(adapter);
			}
		}else if(msg.arg1 == HttpTools.SET_EMPLOYEE_INFO){
			ToastFactory.showToast(EmployeeDataActivity.this,hintString);
			JSONObject  response = HttpTools.getContentJSONObject(jsonString);
			if(response != null){
				try {
					id = response.getInt("id");
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}else if(msg.arg1 == HttpTools.DELETE_EMPLOYEE_INFO){
			String message = HttpTools.getMessageString(jsonString);
			ToastFactory.showToast(EmployeeDataActivity.this,message);
		}
	}

	@Override
	protected boolean handClickEvent(View v) {
		switch (v.getId()) {
		case R.id.ll_sendemail:// 发送邮件
			Intent intent = new Intent(EmployeeDataActivity.this,MyBrowserActivity.class);
			intent.putExtra(BaseBrowserActivity.KEY_URL,magUrl);
			startActivity(intent);
			break;
		case R.id.ll_sendsms:// 发送短信
			String mobiles = PhoneList.get(0).phone;
			if (Tools.checkTelephoneNumber(mobiles)) {
				Intent intent1 = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:" + mobiles));
				startActivity(intent1);
			}else {
				ToastFactory.showToast(EmployeeDataActivity.this,"手机号有误，无法发送");
			}
			break;
		}
		return super.handClickEvent(v);
	}
	
	/**
	 * 添加常用联系人
	 */
	private void submit() {
		RequestConfig config = new RequestConfig(this,HttpTools.SET_EMPLOYEE_INFO,"添加常用联系人");
		RequestParams params = new RequestParams();
		params.put("contactsIds","[{\"contactsId\":\""+contactsID+"\"}]");
		params.put("uid", String.valueOf(UserInfo.uid));
		HttpTools.httpPost(Contants.URl.URl_3013, "/enshrine",config, params);
	}
	
	/**
	 * 删除常用联系人
	 */
	private void delete() {
		RequestConfig config = new RequestConfig(this,HttpTools.DELETE_EMPLOYEE_INFO,"删除常用联系人");
		RequestParams params = new RequestParams();
		params.put("id",Integer.toString(id));
		HttpTools.httpDelete(Contants.URl.URl_3013,"/enshrine/"+id,config, params);
	}

	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getHeadTitle() {
		// TODO Auto-generated method stub
		return null;
	}
}
