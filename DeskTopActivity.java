package com.tg.cloudmanagement;

import java.util.ArrayList;

import org.json.JSONArray;

import com.tg.cloudmanagement.DeskTopActivity;
import com.tg.cloudmanagement.MyBrowserActivity;
import com.tg.cloudmanagement.R;
import com.tg.cloudmanagement.adapter.DeskTopItemAdapter;
import com.tg.cloudmanagement.base.BaseActivity;
import com.tg.cloudmanagement.base.BaseBrowserActivity;
import com.tg.cloudmanagement.constant.Contants;
import com.tg.cloudmanagement.info.HomeDeskTopInfo;
import com.tg.cloudmanagement.info.UserInfo;
import com.tg.cloudmanagement.inter.OnLoadingListener;
import com.tg.cloudmanagement.net.HttpTools;
import com.tg.cloudmanagement.net.RequestConfig;
import com.tg.cloudmanagement.net.RequestParams;
import com.tg.cloudmanagement.net.ResponseData;
import com.tg.cloudmanagement.view.PullRefreshListView;
import com.tg.cloudmanagement.view.dialog.ToastFactory;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.content.Intent;
/**
 * 消息推送消息列表
 * 
 * @author Administrator
 * 
 */
public class DeskTopActivity extends BaseActivity implements OnItemClickListener {
	public static final String DESKTOP_WEIAPPCODE="weiappcode";
	private PullRefreshListView pullListView;
	private DeskTopItemAdapter adapter;
	private HomeDeskTopInfo item;
	private ArrayList<HomeDeskTopInfo> list = new ArrayList<HomeDeskTopInfo>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent  = getIntent();
		if(intent != null){
			item=(HomeDeskTopInfo) intent.getSerializableExtra(DESKTOP_WEIAPPCODE);
		}
		if(item == null){
			ToastFactory.showToast(DeskTopActivity.this, "参数错误");
			finish();
		}
		headView.setTitle(item.weiappname);
		initView();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		pullListView = (PullRefreshListView) findViewById(R.id.pull_listview);
		adapter = new DeskTopItemAdapter(DeskTopActivity.this, list);
		pullListView.setAdapter(adapter);
		pullListView.setOnItemClickListener(this);
		pullListView.setOnLoadingListener(new OnLoadingListener<PullRefreshListView>() {
			@Override
			public void refreshData(PullRefreshListView t, boolean isLoadMore,Message msg, String response) {
				JSONArray content = HttpTools.getContentJsonArray(response);
				ResponseData data = HttpTools.getResponseContent(content);
				if(data.length > 0){
					HomeDeskTopInfo item;
					for (int i = 0; i < data.length; i++) {
						item = new HomeDeskTopInfo();
						item.id = data.getInt(i, "id");
						item.homePushPeople = data.getInt(i, "homePushPeople");
						item.showType = data.getInt(i, "showType");
						item.isHTML5url = data.getInt(i, "isHTML5url");
						item.isPC = data.getInt(i, "isPC");
						item.isread = data.getInt(i, "isread");
						item.content = data.getString(i, "content");
						item.homePushUrl = data.getString(i,"homePushUrl");
						item.homePushTime = data.getString(i, "homePushTime");
						item.weiappcode = data.getString(i, "weiappcode");
						item.weiappname = data.getString(i, "weiappname");
						item.HTML5url = data.getString(i, "HTML5url");
						item.PCurl = data.getString(i, "PCurl");
						item.secretKey = data.getString(i, "secretKey");
						item.tookiy = data.getString(i, "tookiy");
						item.keystr = data.getString(i, "keystr");
						item.icon = data.getString(i, "icon");
						list.add(item);
							}
						}
					}
			
			@Override
			public void onLoadingMore(PullRefreshListView t, Handler hand, int pageIndex) {
				RequestConfig config = new RequestConfig(DeskTopActivity.this,PullRefreshListView.HTTP_MORE_CODE);
				config.handler = hand;
				RequestParams params = new RequestParams();
				params.put("weiappcode", item.weiappcode);
				params.put("uid", UserInfo.uid);
				HttpTools.httpGet(Contants.URl.URl_3011, "/homepush/gethomePushByweiappId",config, params);
			}
			
			@Override
			public void onLoading(PullRefreshListView t, Handler hand) {
				RequestConfig config = new RequestConfig(DeskTopActivity.this,PullRefreshListView.HTTP_FRESH_CODE);
				config.handler = hand;
				RequestParams params = new RequestParams();
				params.put("weiappcode", item.weiappcode);
				params.put("uid", UserInfo.uid);
				HttpTools.httpGet(Contants.URl.URl_3011, "/homepush/gethomePushByweiappId",config, params);
			}
		});
		pullListView.performLoading();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(DeskTopActivity.this,MyBrowserActivity.class);
		intent.putExtra(BaseBrowserActivity.KEY_URL,list.get(position).keystr);
		startActivity(intent);
	}

	@Override
	public View getContentView() {
		return getLayoutInflater().inflate(R.layout.activity_desk_top, null);
	}

	@Override
	public String getHeadTitle() {
		return null;
	}
}
