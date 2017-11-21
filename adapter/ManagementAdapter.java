package com.tg.cloudmanagement.adapter;

import java.util.ArrayList;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tg.cloudmanagement.R;
import com.tg.cloudmanagement.base.MyBaseAdapter;
import com.tg.cloudmanagement.info.GridViewInfo;
import com.tg.cloudmanagement.net.image.VolleyUtils;

public class ManagementAdapter extends MyBaseAdapter<GridViewInfo> {
	
	private ArrayList<GridViewInfo> list;
	private LayoutInflater inflater;
	private GridViewInfo item;
	private Context context;
	public ManagementAdapter(Context con, ArrayList<GridViewInfo> list) {
		super(list);
		this.list = list;
		this.context = con;
		inflater = LayoutInflater.from(con);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = inflater.inflate(R.layout.grid_item, null);
		}
		item = list.get(position);
		TextView tvName = (TextView) convertView.findViewById(R.id.tv_grid_item);
		ImageView ivIcon = (ImageView) convertView.findViewById(R.id.iv_grid_item);
		tvName.setText(item.name);
		if(item.icon != ""){
			VolleyUtils.getImage(context, item.icon,ivIcon);
		}else {
			ivIcon.setImageResource(R.drawable.zhanwei);
		}
		return convertView;
	}
}
