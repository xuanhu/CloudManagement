package com.tg.cloudmanagement;

import java.util.ArrayList;

import com.tg.cloudmanagement.adapter.FamilyAdapter;
import com.tg.cloudmanagement.base.BaseActivity;
import com.tg.cloudmanagement.constant.Contants;
import com.tg.cloudmanagement.info.FamilyInfo;
import com.tg.cloudmanagement.inter.OnLoadingListener;
import com.tg.cloudmanagement.net.HttpTools;
import com.tg.cloudmanagement.net.RequestConfig;
import com.tg.cloudmanagement.net.RequestParams;
import com.tg.cloudmanagement.net.ResponseData;
import com.tg.cloudmanagement.phone.CharacterParser;
import com.tg.cloudmanagement.phone.ClearEditText;
import com.tg.cloudmanagement.view.PullRefreshListView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class Organization02Activity  extends BaseActivity implements OnItemClickListener {
	public static final String TEXT_STRUCTURE="Structure";
	public static final String TEXT_ID="id"; 
	public static final String TEXT_FAMILY="family"; 
	private PullRefreshListView pullListView1, pullListView2;
	private String id;
	private String structure="";
	private String family="";
	//private RelativeLayout rlNulllinkman;
	private TextView tvStructure;
	private ArrayList<FamilyInfo> familyList = new ArrayList<FamilyInfo>();
	//private ArrayList<LinkManInfo> linkManList = new ArrayList<LinkManInfo>();
	private FamilyAdapter adapter;
	//private LinkmanAdapter adapterLinkMan;
//	private ClearEditText mClearEditText;//搜索框
//	private CharacterParser characterParser;//汉字转拼音

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent  intent  = getIntent();
		if(intent != null){
			id=intent.getStringExtra(TEXT_ID);
			family=intent.getStringExtra(TEXT_FAMILY);
			structure=intent.getStringExtra(TEXT_STRUCTURE);
		}
		headView.setTitle(family);
		initView();
	}



	/**
	 * 初始化控件
	 */
	private void initView() {
		tvStructure=(TextView) findViewById(R.id.tv_structure);
		tvStructure.setText(Html.fromHtml(structure));
		//rlNulllinkman = (RelativeLayout) findViewById(R.id.rl_nulllinkman);
		/*mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);
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
		pullListView1 = (PullRefreshListView) findViewById(R.id.pull_listview1);
		pullListView1.setEnableMoreButton(false);
		pullListView1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				String agreement ="<font color='#53c0ff'>"+structure+" > </font>"+"<font color='#999999'>"+familyList.get(position).name+"</font>";
				if(familyList.get(position).counts > 0){
					Intent intent = new Intent(Organization02Activity.this,Organization01Activity.class);
					intent.putExtra(Organization01Activity.TEXT_ID, familyList.get(position).id);
					intent.putExtra(Organization01Activity.TEXT_STRUCTURE,agreement);
					intent.putExtra(Organization01Activity.TEXT_FAMILY, familyList.get(position).name);
					startActivity(intent);
				}else{
					Intent intent = new Intent(Organization02Activity.this,OrganizationLinkMankActivity.class);
					FamilyInfo info = familyList.get(position);
					intent.putExtra(OrganizationLinkMankActivity.LINKMAN_ITEM,info);
					intent.putExtra(OrganizationLinkMankActivity.TEXT_STRUCTURE, agreement);
					startActivity(intent);
				}
			}
		});
		pullListView1.setOnLoadingListener(new OnLoadingListener<PullRefreshListView>() {

			@Override
			public void refreshData(PullRefreshListView t,
					boolean isLoadMore, Message msg, String response) {
				String jsonString = HttpTools.getContentString(response);
				if (jsonString != null) {
					ResponseData data = HttpTools.getResponseData(jsonString);
					FamilyInfo item;
					for (int i = 0; i < data.length; i++) {
						item = new FamilyInfo();
						item.id = data.getString(i, "id");
						item.headid = data.getInt(i, "headid");
						item.counts = data.getInt(i, "counts");
						item.name = data.getString(i, "name");
						item.Province = data.getString(i,"Province");
						item.City = data.getString(i, "City");
						item.area = data.getString(i, "area");
						familyList.add(item);
					}
				}
			}

			@Override
			public void onLoadingMore(PullRefreshListView t,Handler hand, int pageIndex) {
				RequestConfig config = new RequestConfig(Organization02Activity.this,PullRefreshListView.HTTP_MORE_CODE);
				config.handler = hand;
				RequestParams params = new RequestParams();
				if(!id.equals("-1")){
					params.put("headid", id);
				}
				HttpTools.httpGet(Contants.URl.URl_3013, "/family/select",config, params);

			}

			@Override
			public void onLoading(PullRefreshListView t, Handler hand) {
				RequestConfig config = new RequestConfig(Organization02Activity.this,PullRefreshListView.HTTP_FRESH_CODE);
				config.handler = hand;
				RequestParams params = new RequestParams();
				if(!id.equals("-1")){
					params.put("headid", id);
				}
				HttpTools.httpGet(Contants.URl.URl_3013, "/family/select",config, params);

			}
		});
		adapter = new FamilyAdapter(this, familyList);
		pullListView1.setAdapter(adapter);
		pullListView1.performLoading();
		

		/*pullListView2 = (PullRefreshListView) findViewById(R.id.pull_listview2);
		pullListView2.setEnableMoreButton(false);
		pullListView2.setOnItemClickListener(this);
		pullListView2.setOnLoadingListener(new OnLoadingListener<PullRefreshListView>() {

			@Override
			public void refreshData(PullRefreshListView t,
					boolean isLoadMore, Message msg, String response) {
				// TODO Auto-generated method stub
				String jsonString = HttpTools.getContentString(response);
				if (jsonString != null) {
					ResponseData data = HttpTools.getResponseData(jsonString);
					if(data.length > 0){
					rlNulllinkman.setVisibility(View.GONE);
					LinkManInfo item;
					for (int i = 0; i < data.length; i++) {
						item = new LinkManInfo();
						item.contactsId = data.getInt(i, "uid");
						item.realname = data.getString(i, "realname");
						item.icon = data.getString(i,"Icon");
						linkManList.add(item);
					}
				}
				}
			}

			@Override
			public void onLoadingMore(PullRefreshListView t,
					Handler hand, int pageIndex) {
				// TODO Auto-generated method stub
				RequestConfig config = new RequestConfig(Organization02Activity.this,PullRefreshListView.HTTP_MORE_CODE);
				config.handler = hand;
				RequestParams params = new RequestParams();
				if(!id.equals("-1")){
					params.put("property_coding", id);
				}
				params.put("page", pageIndex);
				params.put("pagesize", PullRefreshListView.PAGER_SIZE);
				HttpTools.httpGet(Contants.URl.URl_3013, "/administrator/select",
						config, params);
			}

			@Override
			public void onLoading(PullRefreshListView t, Handler hand) {
				RequestConfig config = new RequestConfig(Organization02Activity.this,PullRefreshListView.HTTP_FRESH_CODE);
				config.handler = hand;
				RequestParams params = new RequestParams();
				if(!id.equals("-1")){
					params.put("property_coding", id);
				}
				params.put("page", 1);
				params.put("pagesize", PullRefreshListView.PAGER_SIZE);
				HttpTools.httpGet(Contants.URl.URl_3013, "/administrator/select",config, params);
			}
		});
		adapterLinkMan = new LinkmanAdapter(this, linkManList);
		pullListView2.setAdapter(adapterLinkMan);
		pullListView2.performLoading();*/
		
	}
	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * @param filterStr
	 */
	/*private void filterData(String filterStr){
		ArrayList<FamilyInfo> List = new ArrayList<FamilyInfo>();
		if(TextUtils.isEmpty(filterStr)){
			List = familyList;
		}else{
			List.clear();
			for(FamilyInfo info : familyList){
				String name = info.name;
				if(name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())){
					List.add(info);
				}
			}
		}
		adapter = new FamilyAdapter(this, List);
		pullListView1.setAdapter(adapter);
	}
	*/
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		/*LinkManInfo item  = linkManList.get(position);
		Intent intent = new Intent(Organization02Activity.this,EmployeeDataActivity.class);
		intent.putExtra(EmployeeDataActivity.CONTACTS_ID,item.contactsId);
		intent.putExtra(EmployeeDataActivity.ID,item.id);
		startActivity(intent);*/
	}
	@Override
	public View getContentView() {
		return getLayoutInflater().inflate(R.layout.activity_organization, null);
	}

	@Override
	public String getHeadTitle() {
		return "组织架构";
	}

}
