package com.tg.cloudmanagement.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.tg.cloudmanagement.R;
import com.tg.cloudmanagement.base.MyBaseAdapter;
import com.tg.cloudmanagement.info.HomeDeskTopInfo;
import com.tg.cloudmanagement.net.image.VolleyUtils;
import com.tg.cloudmanagement.util.Tools;

public class DeskTopItemAdapter extends MyBaseAdapter<HomeDeskTopInfo>{
	private ArrayList<HomeDeskTopInfo> list;
	private LayoutInflater inflater;
	private HomeDeskTopInfo item;
	private Context context;
	public DeskTopItemAdapter(Context context,ArrayList<HomeDeskTopInfo> list){
		super(list);
		this.list = list;
		this.context = context;
		inflater = LayoutInflater.from(context);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		item = list.get(position);
		if(convertView ==null ){
			convertView = inflater.inflate(R.layout.desktop_item, null);
		}
		ImageView imgNewMsg = (ImageView)convertView.findViewById(R.id.img_new_msg);
		ImageView ivHead = (ImageView) convertView.findViewById(R.id.iv_headimg);
		TextView tvContent = (TextView) convertView.findViewById(R.id.tv_content);
		TextView tvTime = (TextView) convertView.findViewById(R.id.tv_time);
		if (item.isread == 0) {
			imgNewMsg.setVisibility(View.VISIBLE);
		} else {
			imgNewMsg.setVisibility(View.GONE);
		}
		tvContent.setText(item.content);
		tvTime.setText(item.homePushTime);
		VolleyUtils.getImage(context, item.icon, ivHead,Tools.userHeadSize, Tools.userHeadSize, R.drawable.moren);
		return convertView;
	}

}
