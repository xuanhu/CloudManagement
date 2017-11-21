package com.tg.cloudmanagement.fragment;

import java.util.ArrayList;

import com.tg.cloudmanagement.ComplaintActivity;
import com.tg.cloudmanagement.MainActivity;
import com.tg.cloudmanagement.MyBrowserActivity;
import com.tg.cloudmanagement.R;
import com.tg.cloudmanagement.SettingActivity;
import com.tg.cloudmanagement.UserInfoActivity;
import com.tg.cloudmanagement.base.BaseBrowserActivity;
import com.tg.cloudmanagement.constant.Contants;
import com.tg.cloudmanagement.info.UserInfo;
import com.tg.cloudmanagement.net.DES;
import com.tg.cloudmanagement.net.MD5;
import com.tg.cloudmanagement.object.ViewConfig;
import com.tg.cloudmanagement.view.MessageArrowView;
import com.tg.cloudmanagement.view.MessageArrowView.ItemClickListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
/**
 * 个人中心
 * @author Administrator
 *
 */
public class FragmentMine extends Fragment implements ItemClickListener{
	private View mView;
	private MainActivity mActivity;
	private String sign;
	private MessageArrowView mineInfoZone1, mineInfoZone2, mineInfoZone3,mineInfoZone4;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mView = inflater.inflate(R.layout.fragment_mine_layout, container, false);
		initView();
		return mView;
	}
	
	/**
	 * 初始化
	 */
	private void initView() {
		mineInfoZone1 = (MessageArrowView) mView.findViewById(R.id.mine_info_zone1);
		mineInfoZone2 = (MessageArrowView) mView.findViewById(R.id.mine_info_zone2);
		mineInfoZone3 = (MessageArrowView) mView.findViewById(R.id.mine_info_zone3);
		mineInfoZone4 = (MessageArrowView) mView.findViewById(R.id.mine_info_zone4);
		mineInfoZone1.setItemClickListener(this);
		mineInfoZone2.setItemClickListener(this);
		mineInfoZone3.setItemClickListener(this);
		mineInfoZone4.setItemClickListener(this);

		ArrayList<ViewConfig> list1 = new ArrayList<ViewConfig>();
		ViewConfig viewConfig = new ViewConfig("个人资料", "", true);
		viewConfig.leftDrawable = getResources().getDrawable(R.drawable.geren);
		list1.add(viewConfig);
		mineInfoZone1.setData(list1);

		ArrayList<ViewConfig> list2 = new ArrayList<ViewConfig>();
		viewConfig = new ViewConfig("员工投诉", "", true);
		viewConfig.leftDrawable = getResources().getDrawable(R.drawable.tousujilu);
		list2.add(viewConfig);
		mineInfoZone2.setData(list2);

		ArrayList<ViewConfig> list3 = new ArrayList<ViewConfig>();
		viewConfig = new ViewConfig("我要投诉", "", true);
		viewConfig.leftDrawable = getResources().getDrawable(R.drawable.tousu_mine);
		list3.add(viewConfig);
		mineInfoZone3.setData(list3);
		
		ArrayList<ViewConfig> list4 = new ArrayList<ViewConfig>();
		viewConfig = new ViewConfig("更多设置", "", true);
		viewConfig.leftDrawable = getResources().getDrawable(R.drawable.shezhi);
		list4.add(viewConfig);
		mineInfoZone4.setData(list4);
		try {
			sign = MD5.getMd5Value(DES.KEY_URl+"uid"+UserInfo.uid+"username"+UserInfo.username+DES.KEY_URl).toLowerCase();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onItemClick(MessageArrowView mv, View v, int position) {
		// TODO Auto-generated method stub
		if (mv == mineInfoZone1) {
			if (position == 0) {// 个人资料
				startActivity(new Intent(mActivity, UserInfoActivity.class));
			}
		} else if (mv == mineInfoZone2) {
			if (position == 0) {// 我的投诉
				String url= Contants.URl.URl_3024+"?uid="+UserInfo.uid+"&username="+UserInfo.username+"&sign="+sign;
				Intent intent = new Intent(mActivity,MyBrowserActivity.class);
				intent.putExtra(BaseBrowserActivity.KEY_URL,url);
				startActivity(intent);
			}
		} else if (mv == mineInfoZone3) {
			if (position == 0) {// 我要投诉
				startActivity(new Intent(mActivity, ComplaintActivity.class));
			}
		}else if (mv == mineInfoZone4) {
			if (position == 0) {// 更多设置
				startActivity(new Intent(mActivity, SettingActivity.class));
			}
		}
	}
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mActivity = (MainActivity)activity;
	}
}
