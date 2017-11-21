package com.tg.cloudmanagement;

import java.util.ArrayList;

import com.tg.cloudmanagement.adapter.FamilyAdapter;
import com.tg.cloudmanagement.adapter.LinkmanAdapter;
import com.tg.cloudmanagement.base.BaseActivity;
import com.tg.cloudmanagement.constant.Contants;
import com.tg.cloudmanagement.info.FamilyInfo;
import com.tg.cloudmanagement.info.LinkManInfo;
import com.tg.cloudmanagement.inter.OnLoadingListener;
import com.tg.cloudmanagement.net.HttpTools;
import com.tg.cloudmanagement.net.RequestConfig;
import com.tg.cloudmanagement.net.RequestParams;
import com.tg.cloudmanagement.net.ResponseData;
import com.tg.cloudmanagement.phone.CharacterParser;
import com.tg.cloudmanagement.phone.ClearEditText;
import com.tg.cloudmanagement.view.PullRefreshListView;
import com.tg.cloudmanagement.view.dialog.ToastFactory;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 部门联系人
 * @author Administrator
 *
 */
public class OrganizationLinkMankActivity extends BaseActivity implements OnItemClickListener {
	public static final String LINKMAN_ITEM="item";
	public static final String TEXT_STRUCTURE="structure";
	private FamilyInfo info;
	private String structure="";
	private PullRefreshListView pullListView;
	private TextView tvStructure;
	private LinearLayout rlNulllinkman;
	private ArrayList<LinkManInfo> linkManList = new ArrayList<LinkManInfo>();
	private LinkmanAdapter adapter;
//	private ClearEditText mClearEditText;//搜索框
//	private CharacterParser characterParser;//汉字转拼音
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		if(intent != null){
			info=(FamilyInfo) intent.getSerializableExtra(LINKMAN_ITEM);
			structure=intent.getStringExtra(TEXT_STRUCTURE);
		}
		if(info == null){
			finish();
			ToastFactory.showToast(this, "参数有误");
			return;
		}
		headView.setTitle(info.name);
		initView();
	}

	/**
	 * 初始化
	 */
	private void initView() {
		tvStructure=(TextView) findViewById(R.id.tv_structure);
		tvStructure.setText(Html.fromHtml(structure));
		rlNulllinkman = (LinearLayout)findViewById(R.id.rl_nulllinkman);
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
		pullListView = (PullRefreshListView)findViewById(R.id.pull_listview);
		pullListView.setEnableMoreButton(false);
		pullListView.setOnItemClickListener(this);
		pullListView.setOnLoadingListener(new OnLoadingListener<PullRefreshListView>() {
			
			@Override
			public void refreshData(PullRefreshListView t, boolean isLoadMore,Message msg, String response) {
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
							item.icon = data.getString(i, "Icon");
							linkManList.add(item);
						}
					}
				}
			}
			
			@Override
			public void onLoadingMore(PullRefreshListView t, Handler hand, int pageIndex) {
				RequestConfig config = new RequestConfig(OrganizationLinkMankActivity.this,PullRefreshListView.HTTP_MORE_CODE);
				config.handler = hand;
				RequestParams params = new RequestParams();
				params.put("property_coding",info.id);
				params.put("page", 1);
				params.put("pagesize", PullRefreshListView.PAGER_SIZE);
				HttpTools.httpGet(Contants.URl.URl_3013, "/administrator/select",config, params);
				
			}
			
			@Override
			public void onLoading(PullRefreshListView t, Handler hand) {
				RequestConfig config = new RequestConfig(OrganizationLinkMankActivity.this,PullRefreshListView.HTTP_FRESH_CODE);
				config.handler = hand;
				RequestParams params = new RequestParams();
				params.put("property_coding", info.id);
				params.put("page", 1);
				params.put("pagesize", PullRefreshListView.PAGER_SIZE);
				HttpTools.httpGet(Contants.URl.URl_3013, "/administrator/select",config, params);
				
			}
		});
		adapter=new LinkmanAdapter(this,linkManList);
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
		adapter = new LinkmanAdapter(this, List);
		pullListView.setAdapter(adapter);
	}*/
	/**
	 * 点击返回
	 * @param v
	 */
	public void onclickReturn(View v){
		finish();
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		LinkManInfo item  = linkManList.get(position);
		Intent intent = new Intent(OrganizationLinkMankActivity.this,EmployeeDataActivity.class);
		intent.putExtra(EmployeeDataActivity.CONTACTS_ID,item.contactsId);
		intent.putExtra(EmployeeDataActivity.ID,item.id);
		startActivity(intent);
	}
	
	@Override
	public View getContentView() {
		return getLayoutInflater().inflate(R.layout.activity_organization_link_mank,null);
	}

	@Override
	public String getHeadTitle() {
		return null;
	}
}
