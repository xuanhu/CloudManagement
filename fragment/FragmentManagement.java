package com.tg.cloudmanagement.fragment;

import java.util.ArrayList;

import com.tg.cloudmanagement.ComplaintActivity;
import com.tg.cloudmanagement.MainActivity;
import com.tg.cloudmanagement.MyBrowserActivity;
import com.tg.cloudmanagement.R;
import com.tg.cloudmanagement.RepairsPulbicActivity;
import com.tg.cloudmanagement.adapter.ManagementAdapter;
import com.tg.cloudmanagement.base.BaseBrowserActivity;
import com.tg.cloudmanagement.constant.Contants;
import com.tg.cloudmanagement.info.GridViewInfo;
import com.tg.cloudmanagement.info.UserInfo;
import com.tg.cloudmanagement.net.HttpTools;
import com.tg.cloudmanagement.net.MessageHandler;
import com.tg.cloudmanagement.net.RequestConfig;
import com.tg.cloudmanagement.net.RequestParams;
import com.tg.cloudmanagement.net.ResponseData;
import com.tg.cloudmanagement.util.Tools;
import com.tg.cloudmanagement.view.MyGridView;
import com.tg.cloudmanagement.view.MyGridView.NetworkRequestListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
/**
 * 微物管
 * @author Administrator
 *
 */
public class FragmentManagement extends Fragment{
	private MainActivity mActivity;
	private View mView;
	private Intent intent;
	private MyGridView mGridView1;
	private MyGridView mGridView2;
	private ManagementAdapter adapter1,adapter2;
	private ArrayList<GridViewInfo> gridlist1 = new ArrayList<GridViewInfo>();
	private ArrayList<GridViewInfo> gridlist2 = new ArrayList<GridViewInfo>();
	private String officejsonStr,managementjsonStr;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mView = inflater.inflate(R.layout.fragment_management_layout,container, false);
		initView();
		requestData();
		return mView;
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		mGridView1=(MyGridView) mView.findViewById(R.id.gridview1);
		mGridView2=(MyGridView) mView.findViewById(R.id.gridview2);
		outLocalData();
		mGridView1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
					GridViewInfo info = gridlist1.get(position);
					if(info.keystr != ""){
						intent = new Intent(mActivity,MyBrowserActivity.class);
						intent.putExtra(BaseBrowserActivity.KEY_URL,info.keystr);
						startActivity(intent);
					}
					
			}
		});
		mGridView1.setNetworkRequestListener(new NetworkRequestListener() {
			@Override
			public void onSuccess(MyGridView gridView, Message msg,String response) {
				String jsonString = HttpTools.getContentString(response);
				if (jsonString != null) {
					ResponseData data = HttpTools.getResponseData(jsonString);
					if (data.length > 0) {
						Tools.saveOfficeInfo(mActivity, response);
						gridlist1 = new ArrayList<GridViewInfo>();
						GridViewInfo item = null;
						if(data.length % 3 == 0){
							for (int i = 0; i < data.length; i++) {
								item = new GridViewInfo();
								item.name = data.getString(i, "name");
								item.icon = data.getString(i, "icon");
								item.keystr = data.getString(i, "keystr");
								gridlist1.add(item);
							}
						}else{
							for (int i = 0; i < data.length +(3- data.length % 3); i++) {
								item = new GridViewInfo();
								item.name = data.getString(i, "name");
								item.icon = data.getString(i, "icon");
								item.keystr = data.getString(i, "keystr");
								gridlist1.add(item);
							}
						}
						
					}
				}
				adapter1 = new ManagementAdapter(mActivity, gridlist1);
				mGridView1.setAdapter(adapter1);
			}
			@Override
			public void onRequest(MessageHandler msgHand) {
				// TODO Auto-generated method stub
				RequestConfig config = new RequestConfig(mActivity,0);
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
		
		//gridview2
		mGridView2.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(position == 0){
					startActivity(new Intent(mActivity, RepairsPulbicActivity.class));
				}else if(position == 1){
					startActivity(new Intent(mActivity, ComplaintActivity.class));
				}else {
					GridViewInfo info = gridlist2.get(position);
					if(info.keystr != ""){
						intent = new Intent(mActivity,MyBrowserActivity.class);
						intent.putExtra(BaseBrowserActivity.KEY_URL,info.keystr);
						startActivity(intent);
					}
				}
			}
		});
		mGridView2.setNetworkRequestListener(new NetworkRequestListener() {
			
			@Override
			public void onSuccess(MyGridView gridView, Message msg,String response) {
				String jsonString = HttpTools.getContentString(response);
				if (jsonString != null) {
					ResponseData data = HttpTools.getResponseData(jsonString);
					if (data.length > 0) {
						Tools.saveManagementInfo(mActivity, response);
						gridlist2 = new ArrayList<GridViewInfo>();
						GridViewInfo item = null;
						if(data.length % 3 == 0){
							for (int i = 0; i < data.length; i++) {
								item = new GridViewInfo();
								item.name = data.getString(i, "name");
								item.icon = data.getString(i, "icon");
								item.keystr = data.getString(i, "keystr");
								gridlist2.add(item);
							}
						}else{
							for (int i = 0; i < data.length + (3-data.length % 3); i++) {
								item = new GridViewInfo();
								item.name = data.getString(i, "name");
								item.icon = data.getString(i, "icon");
								item.keystr = data.getString(i, "keystr");
								gridlist2.add(item);
							}
						}
					}
				}
				adapter2 = new ManagementAdapter(mActivity, gridlist2);
				mGridView2.setAdapter(adapter2);
			}
			@Override
			public void onRequest(MessageHandler msgHand) {
				// TODO Auto-generated method stub
				RequestConfig config = new RequestConfig(mActivity,0);
				config.handler = msgHand.getHandler();
				RequestParams params = new RequestParams();
				params.put("uid", UserInfo.uid);
				params.put("isHTML5url", 1);
				params.put("categoryid", 2);
				params.put("page", 1);
				params.put("pagesize", 99);
				HttpTools.httpGet(Contants.URl.URl_3011, "/weiApplication",config, params);
			}
		});
	}
	/**
	 * 从本地数据库取出数据
	 */
	public void outLocalData(){
		adapter1 = new ManagementAdapter(mActivity, gridlist1);
		adapter2 = new ManagementAdapter(mActivity, gridlist2);
		officejsonStr = Tools.getOfficeName(mActivity);
		managementjsonStr = Tools.getManagementName(mActivity);
		//基础办公
		String officejsonString = HttpTools.getContentString(officejsonStr);
		if (officejsonString != null) {
			ResponseData data = HttpTools.getResponseData(officejsonString);
			if (data.length > 0) {
				GridViewInfo item = null;
				if(data.length % 3 == 0){
					for (int i = 0; i < data.length; i++) {
						item = new GridViewInfo();
						item.name = data.getString(i, "name");
						item.icon = data.getString(i, "icon");
						item.keystr = data.getString(i, "keystr");
						gridlist1.add(item);
					}
				}else{
					for (int i = 0; i < data.length + (3-data.length % 3); i++) {
						item = new GridViewInfo();
						item.name = data.getString(i, "name");
						item.icon = data.getString(i, "icon");
						item.keystr = data.getString(i, "keystr");
						gridlist1.add(item);
					}
				}
			}
			mGridView1.setAdapter(adapter1);
		}
		//日常管理
		String managementjsonString = HttpTools.getContentString(managementjsonStr);
		if (officejsonString != null) {
			ResponseData data = HttpTools.getResponseData(managementjsonString);
			if (data.length > 0) {
				GridViewInfo item = null;
				if(data.length % 3 == 0){
					for (int i = 0; i < data.length; i++) {
						item = new GridViewInfo();
						item.name = data.getString(i, "name");
						item.icon = data.getString(i, "icon");
						item.keystr = data.getString(i, "keystr");
						gridlist2.add(item);
					}
				}else{
					for (int i = 0; i < data.length + (3-data.length % 3); i++) {
						item = new GridViewInfo();
						item.name = data.getString(i, "name");
						item.icon = data.getString(i, "icon");
						item.keystr = data.getString(i, "keystr");
						gridlist2.add(item);
					}
				}
			}
			mGridView2.setAdapter(adapter2);
		}
	}
	public void requestData() {
		mGridView1.loaddingData();
		mGridView2.loaddingData();
	}
	
	@Override
	public void onDestroy() {
		if(adapter1 != null && adapter1.getList() != null){
			adapter1.getList().clear();
			adapter1.notifyDataSetChanged();
		}
		if(adapter2 != null && adapter2.getList() != null){
			adapter2.getList().clear();
			adapter2.notifyDataSetChanged();
		}
		super.onDestroy();
	}
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mActivity = (MainActivity) activity;
	}
}

