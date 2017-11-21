package com.tg.cloudmanagement.fragment;

import java.util.ArrayList;

import com.tg.cloudmanagement.ComplaintActivity;
import com.tg.cloudmanagement.DeskTopActivity;
import com.tg.cloudmanagement.MainActivity;
import com.tg.cloudmanagement.MipcaActivityCapture;
import com.tg.cloudmanagement.MyBrowserActivity;
import com.tg.cloudmanagement.R;
import com.tg.cloudmanagement.RepairsPulbicActivity;
import com.tg.cloudmanagement.UserInfoActivity;
import com.tg.cloudmanagement.adapter.HomeDeskTopAdapter;
import com.tg.cloudmanagement.base.BaseBrowserActivity;
import com.tg.cloudmanagement.constant.Contants;
import com.tg.cloudmanagement.info.HomeDeskTopInfo;
import com.tg.cloudmanagement.info.UserInfo;
import com.tg.cloudmanagement.inter.OnLoadingListener;
import com.tg.cloudmanagement.inter.SingleClickListener;
import com.tg.cloudmanagement.net.HttpTools;
import com.tg.cloudmanagement.net.MessageHandler;
import com.tg.cloudmanagement.net.RequestConfig;
import com.tg.cloudmanagement.net.RequestParams;
import com.tg.cloudmanagement.net.ResponseData;
import com.tg.cloudmanagement.net.image.VolleyUtils;
import com.tg.cloudmanagement.view.ManageMentLinearlayout;
import com.tg.cloudmanagement.view.PullRefreshListView;
import com.tg.cloudmanagement.view.RoundImageView;
import com.tg.cloudmanagement.view.ManageMentLinearlayout.NetworkRequestListener;
import com.tg.cloudmanagement.view.PullRefreshListView.NetPullRefreshOnScroll;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
/**
 * 首页
 * @author Administrator
 *
 */
public class FragmentDeskTop extends Fragment implements OnItemClickListener {
	private View mView;
	private static final int ISTREAD=1;
	private TextView tvName,tvJob,tvGeneral;
	private MainActivity mActivity;
	private PullRefreshListView pullListView;
	private HomeDeskTopAdapter adapter;
	private ManageMentLinearlayout magLinearLayoutDoor,magLinearLayoutWork;
	private LinearLayout llRepairs,llComplaint;
	private String magUrl1,magUrl2;
	private ArrayList<HomeDeskTopInfo> list = new ArrayList<HomeDeskTopInfo>();
	private Intent intent ;
	private int size;
	private RelativeLayout rlSaoYiSao;
	private RoundImageView ivHead;
	private LinearLayout llHomeHead;
	private SingleClickListener singleListener = new SingleClickListener() {
		@Override
		public void onSingleClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.ll_repairs://报修
				startActivity(new Intent(mActivity, RepairsPulbicActivity.class));
				break;
			case R.id.ll_complaint://我要投诉
				startActivity(new Intent(mActivity, ComplaintActivity.class));
				break;
			case R.id.ll_door://任务工单
				intent = new Intent(mActivity,MyBrowserActivity.class);
				intent.putExtra(BaseBrowserActivity.KEY_URL,magUrl1);
				startActivity(intent);
				break;
			case R.id.ll_work://邮件
				intent = new Intent(mActivity,MyBrowserActivity.class);
				intent.putExtra(BaseBrowserActivity.KEY_URL,magUrl2);
				startActivity(intent);
				break;
			case R.id.rl_saoyisao://扫一扫
				startActivity(new Intent(mActivity,MipcaActivityCapture.class));
				break;
			case R.id.iv_head://头像
				startActivity(new Intent(mActivity,UserInfoActivity.class));
				break;
			}
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mView = inflater.inflate(R.layout.fragment_desktop_layout, container, false);
		initHomeNewsView(mView);
		initListener();
		requestData();
		return mView;
	}

	private void requestData() {
		magLinearLayoutDoor.loaddingData();
		magLinearLayoutWork.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				magLinearLayoutWork.loaddingData();
			}
		}, 1000);
	}
	private void initHomeNewsView(View view) {
		llHomeHead = (LinearLayout) mView.findViewById(R.id.ll_home_head);
		/*ivSaoYiSao=(ImageView) view.findViewById(R.id.iv_saoyisao);
		ivHead=(RoundImageView) view.findViewById(R.id.iv_head);
		ivHead.setCircleShape();
		tvName=(TextView) view.findViewById(R.id.tv_name);
		tvJob=(TextView) view.findViewById(R.id.tv_job);
		tvGeneral=(TextView) view.findViewById(R.id.tv_general);
		ivSaoYiSao.setOnClickListener(singleListener);
		ivHead.setOnClickListener(singleListener);
		initData();
		tvName.setText(UserInfo.realname+"("+UserInfo.username+")");		
		tvJob.setText(UserInfo.jobName);		
		tvGeneral.setText(UserInfo.propertyName);*/	
		mView.findViewById(R.id.ll_repairs).setOnClickListener(singleListener);//在线报修
		mView.findViewById(R.id.ll_complaint).setOnClickListener(singleListener);//我要投诉
		magLinearLayoutDoor=(ManageMentLinearlayout) mView.findViewById(R.id.ll_door);
		magLinearLayoutWork =(ManageMentLinearlayout) mView.findViewById(R.id.ll_work);
		magLinearLayoutDoor.setOnClickListener(singleListener);
		magLinearLayoutWork.setOnClickListener(singleListener);
		pullListView=(PullRefreshListView) mView.findViewById(R.id.pull_listview);
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
					HomeDeskTopInfo item;
					for (int i = 0; i < data.length; i++) {
						item = new HomeDeskTopInfo();
						item.id = data.getInt(i, "id");
						item.homePushPeople = data.getInt(i, "homePushPeople");
						item.showType = data.getInt(i, "showType");
						item.isHTML5url = data.getInt(i, "isHTML5url");
						item.isPC = data.getInt(i, "isPC");
						item.notread = data.getInt(i, "notread");
						item.content = data.getString(i, "content");
						item.icon = data.getString(i, "icon");
						item.homePushUrl = data.getString(i,"homePushUrl");
						item.homePushTime = data.getString(i, "homePushTime");
						item.weiappcode = data.getString(i, "weiappcode");
						item.weiappname = data.getString(i, "weiappname");
						item.HTML5url = data.getString(i, "HTML5url");
						item.PCurl = data.getString(i, "PCurl");
						item.secretKey = data.getString(i, "secretKey");
						item.tookiy = data.getString(i, "tookiy");
						item.keystr = data.getString(i, "keystr");
						list.add(item);
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
				params.put("page", pageIndex);
				params.put("pagesize", PullRefreshListView.PAGER_SIZE);
				HttpTools.httpGet(Contants.URl.URl_3011, "/homepush",config, params);
			}
			
			@Override
			public void onLoading(PullRefreshListView t, Handler hand) {
				// TODO Auto-generated method stub
				RequestConfig config = new RequestConfig(mActivity,PullRefreshListView.HTTP_FRESH_CODE);
				config.handler = hand;
				RequestParams params = new RequestParams();
				params.put("uid", UserInfo.uid);
				params.put("page", 1);
				params.put("pagesize", PullRefreshListView.PAGER_SIZE);
				HttpTools.httpGet(Contants.URl.URl_3011, "/homepush",config, params);
			}
		});
		/**
		 * list添加头部
		 */
		addHead();
		pullListView.setAdapter(adapter);
		pullListView.performLoading();
		pullListView.setNetPullRefreshOnScroll(new NetPullRefreshOnScroll() {
			
			@Override
			public void refreshOnScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if(firstVisibleItem > 0){
					llHomeHead.setVisibility(View.VISIBLE);
				}else{
					llHomeHead.setVisibility(View.GONE);
				}
			}
		});

	}
	
	private void addHead(){
		LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View homeHeadView = new View(mActivity);
		View userinfoView = inflater.inflate(R.layout.fragment_home_item_userinfo, null);
		View serviceView = inflater.inflate(R.layout.fragment_home_item_1, null);
		//pullListView.addHeaderView(homeHeadView);
		pullListView.addHeaderView(userinfoView);
		pullListView.addHeaderView(serviceView);
		/*
		 * 第一个头部
		 */
		rlSaoYiSao=(RelativeLayout) userinfoView.findViewById(R.id.rl_saoyisao);
		ivHead=(RoundImageView) userinfoView.findViewById(R.id.iv_head);
		ivHead.setCircleShape();
		tvName=(TextView) userinfoView.findViewById(R.id.tv_name);
		tvJob=(TextView) userinfoView.findViewById(R.id.tv_job);
		tvGeneral=(TextView) userinfoView.findViewById(R.id.tv_general);
		rlSaoYiSao.setOnClickListener(singleListener);
		ivHead.setOnClickListener(singleListener);
		initData();
		tvName.setText(UserInfo.realname+"("+UserInfo.username+")");		
		tvJob.setText(UserInfo.jobName);		
		tvGeneral.setText(UserInfo.propertyName);
		/**
		 * 第二个头部
		 */
		llRepairs =(LinearLayout) serviceView.findViewById(R.id.ll_repairs);
        llComplaint =(LinearLayout) serviceView.findViewById(R.id.ll_complaint);
        magLinearLayoutDoor =(ManageMentLinearlayout)serviceView.findViewById(R.id.ll_door);
		magLinearLayoutWork =(ManageMentLinearlayout)serviceView.findViewById(R.id.ll_work);
        llRepairs .setOnClickListener(singleListener);//在线报修
        llComplaint .setOnClickListener(singleListener);////我要投诉
        magLinearLayoutDoor.setOnClickListener(singleListener);//任务工单
		magLinearLayoutWork.setOnClickListener(singleListener);//邮件管理
	}
	//更新UI
	public void freshUI(){
		initData();
		tvName.setText(UserInfo.realname+"("+UserInfo.username+")");		
		tvJob.setText(UserInfo.jobName);		
		tvGeneral.setText(UserInfo.propertyName);
	}
	
	/**
	 * 初始化
	 */
	private void initListener(){
		magLinearLayoutDoor.setNetworkRequestListener(new NetworkRequestListener() {

					@Override
					public void onSuccess(ManageMentLinearlayout magLearLayout,Message msg, String response) {
						String jsonString = HttpTools.getContentString(response);
						if (jsonString != null) {
							ResponseData data = HttpTools.getResponseData(jsonString);
							if (data.length > 0) {
								for (int i = 0; i < data.length; i++) {
									if(data.getString(i, "name").equals("任务工单")){
										magUrl1 = data.getString(i, "keystr");
									}
								}
							}
						}
					}

			@Override
			public void onRequest(MessageHandler msgHand) {
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
		
		magLinearLayoutWork.setNetworkRequestListener(new NetworkRequestListener() {
			
			@Override
			public void onSuccess(ManageMentLinearlayout magLearLayout, Message msg,
					String response) {
				String jsonString = HttpTools.getContentString(response);
				if (jsonString != null) {
					ResponseData data = HttpTools.getResponseData(jsonString);
					if (data.length > 0) {
						for (int i = 0; i < data.length; i++) {
							if(data.getString(i, "name").equals("邮件管理")){
								magUrl2 = data.getString(i, "keystr");
							}
						}
					}
				}
			}
			
			@Override
			public void onRequest(MessageHandler msgHand) {
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
	}
	
	public void initData(){
		VolleyUtils.getImage(getActivity(),UserInfo.headUrl,ivHead, size,size,R.drawable.moren_geren);
	}
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		size = getResources().getDimensionPixelSize(R.dimen.margin_88);
		mActivity = (MainActivity)activity;
		adapter = new HomeDeskTopAdapter(mActivity, list);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		Intent intent = new Intent(mActivity, DeskTopActivity.class);
		if((int) parent.getAdapter().getItemId(position) != -1){
			HomeDeskTopInfo info = list.get((int) parent.getAdapter().getItemId(position));
			intent.putExtra(DeskTopActivity.DESKTOP_WEIAPPCODE,info);
			startActivityForResult(intent,ISTREAD);
		}else{
			return;
		}
		
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==ISTREAD){
			pullListView.performLoading();
		}
	}
}
