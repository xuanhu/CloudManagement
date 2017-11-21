package com.tg.cloudmanagement.fragment;

import java.util.ArrayList;

import com.tg.cloudmanagement.ContactsActivity;
import com.tg.cloudmanagement.EmployeeDataActivity;
import com.tg.cloudmanagement.MainActivity;
import com.tg.cloudmanagement.Organization01Activity;
import com.tg.cloudmanagement.R;
import com.tg.cloudmanagement.adapter.CollectLinkmanAdapter;
import com.tg.cloudmanagement.constant.Contants;
import com.tg.cloudmanagement.info.LinkManInfo;
import com.tg.cloudmanagement.info.UserInfo;
import com.tg.cloudmanagement.inter.OnLoadingListener;
import com.tg.cloudmanagement.inter.SingleClickListener;
import com.tg.cloudmanagement.net.HttpTools;
import com.tg.cloudmanagement.net.RequestConfig;
import com.tg.cloudmanagement.net.RequestParams;
import com.tg.cloudmanagement.net.ResponseData;
import com.tg.cloudmanagement.view.PullRefreshListView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
/**
 * 通讯录
 * @author Administrator
 *
 */
public class FragmentCommunicate extends Fragment implements OnItemClickListener {
	private MainActivity mActivity;
	private static final int ISTREAD=1;
	private View mView;
	private TextView tvSection;
	private PullRefreshListView pullListView;
	private RelativeLayout rlNulllinkman,rlOrganization,rlDepartment,rlContacts;
	private ArrayList<LinkManInfo> linkManList = new ArrayList<LinkManInfo>();
	private CollectLinkmanAdapter adapter;
	//private ClearEditText mClearEditText;//搜索框
	//private CharacterParser characterParser;//汉字转拼音
	private SingleClickListener singleListener = new SingleClickListener() {
		
		@Override
		public void onSingleClick(View v) {
			switch(v.getId()){
			case R.id.rl_organization://组织架构
				Intent intent = new Intent(mActivity,Organization01Activity.class);
				intent.putExtra(Organization01Activity.TEXT_ID,"");
				intent.putExtra(Organization01Activity.TEXT_FAMILY, "组织架构");
				intent.putExtra(Organization01Activity.TEXT_STRUCTURE, "<font color='#53c0ff'>组织架构</font>");
				startActivity(intent);
				break;
			case R.id.rl_department://部门
				String propertyName = "<font color='#53c0ff'>"+UserInfo.propertyName+"</font>";
				Intent intent1 = new Intent(mActivity,Organization01Activity.class);
				intent1.putExtra(Organization01Activity.TEXT_ID,UserInfo.propertyCoding);
				intent1.putExtra(Organization01Activity.TEXT_FAMILY, UserInfo.propertyName);
				intent1.putExtra(Organization01Activity.TEXT_STRUCTURE,propertyName);
				startActivity(intent1);
				break;
			case R.id.rl_contacts://手机通讯录
				startActivity(new Intent(mActivity,ContactsActivity.class));
				break;
			}
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mView = inflater.inflate(R.layout.fragment_communicate_layout,container, false);
		initView();
		return mView;
	}

	/**
	 * 初始化
	 */
	private void initView() {
		rlNulllinkman = (RelativeLayout) mView.findViewById(R.id.rl_nulllinkman);
		rlOrganization = (RelativeLayout) mView.findViewById(R.id.rl_organization);
		rlDepartment = (RelativeLayout) mView.findViewById(R.id.rl_department);
		rlContacts = (RelativeLayout) mView.findViewById(R.id.rl_contacts);
		tvSection = (TextView) mView.findViewById(R.id.tv_section);
		tvSection.setText(UserInfo.propertyName);
		
		/*mClearEditText = (ClearEditText) mView.findViewById(R.id.filter_edit);
		//实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();
		//根据输入框输入值的改变来过滤搜索
		mClearEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				filterData(s.toString());
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});*/
		rlOrganization.setOnClickListener(singleListener);
		rlDepartment.setOnClickListener(singleListener);
		rlContacts.setOnClickListener(singleListener);
		
		pullListView = (PullRefreshListView) mView.findViewById(R.id.pull_listview);
		pullListView.setEnableMoreButton(false);
		pullListView.setOnItemClickListener(this);
		pullListView.setDividerHeight(0);
		pullListView.setOnLoadingListener(new OnLoadingListener<PullRefreshListView>() {
			
			@Override
			public void refreshData(PullRefreshListView t, boolean isLoadMore,
					Message msg, String response) {
				// TODO Auto-generated method stub
				String jsonString = HttpTools.getContentString(response);
				if (jsonString != null) {
					ResponseData data = HttpTools.getResponseData(jsonString);
					int total = HttpTools.getTotalCount(jsonString);
					if(total > 0){
						rlNulllinkman.setVisibility(View.GONE);
					}
					LinkManInfo item;
					for (int i = 0; i < data.length; i++) {
						item = new LinkManInfo();
						item.id = data.getInt(i, "id");
						item.contactsId = data.getInt(i, "contactsId");
						item.realname = data.getString(i, "realname");
						item.icon = data.getString(i, "Icon");
						item.job_name = data.getString(i,"job_name");
						item.property_name = data.getString(i, "property_name");
						linkManList.add(item);
					}
				}
			}
			
			@Override
			public void onLoadingMore(PullRefreshListView t, Handler hand, int pageIndex) {
				// TODO Auto-generated method stub
				RequestConfig config = new RequestConfig(mActivity,PullRefreshListView.HTTP_MORE_CODE);
				config.handler = hand;
				RequestParams params = new RequestParams();
				params.put("uid", UserInfo.uid);
				params.put("orderDirection","asc");
				params.put("page", pageIndex);
				params.put("pagesize", PullRefreshListView.PAGER_SIZE);
				HttpTools.httpGet(Contants.URl.URl_3013, "/enshrine",config, params);
				
			}
			
			@Override
			public void onLoading(PullRefreshListView t, Handler hand) {
				// TODO Auto-generated method stub
				RequestConfig config = new RequestConfig(mActivity,PullRefreshListView.HTTP_FRESH_CODE);
				config.handler = hand;
				RequestParams params = new RequestParams();
				params.put("uid", UserInfo.uid);
				params.put("orderDirection","asc");
				params.put("page", 1);
				params.put("pagesize", PullRefreshListView.PAGER_SIZE);
				HttpTools.httpGet(Contants.URl.URl_3013, "/enshrine",config, params);
				
			}
		});
		pullListView.setAdapter(adapter);
		pullListView.performLoading();
	}
	
	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * @param filterStr
	 */
	/*private void filterData(String filterStr){
		ArrayList<LinkManInfo> List = new ArrayList<LinkManInfo>();
		if(TextUtils.isEmpty(filterStr)){
			List = linkManList;
		}else{
			List.clear();
			for(LinkManInfo info : linkManList){
				String name = info.realname;
				if(name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())){
					List.add(info);
				}
			}
		}
		adapter = new CollectLinkmanAdapter(mActivity, List);
		pullListView.setAdapter(adapter);
	}*/
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mActivity = (MainActivity)activity;
		adapter=new CollectLinkmanAdapter(mActivity, linkManList);
	}

	/**
	 * 点击ListView item
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		LinkManInfo item  = linkManList.get(position);
		Intent intent = new Intent(mActivity,EmployeeDataActivity.class);
		intent.putExtra(EmployeeDataActivity.CONTACTS_ID,item.contactsId);
		intent.putExtra(EmployeeDataActivity.ID,item.id);
		startActivityForResult(intent,ISTREAD);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==ISTREAD){
			pullListView.performLoading();
		}
	}
}
