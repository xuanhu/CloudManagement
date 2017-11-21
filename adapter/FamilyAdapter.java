package com.tg.cloudmanagement.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tg.cloudmanagement.R;
import com.tg.cloudmanagement.base.MyBaseAdapter;
import com.tg.cloudmanagement.info.FamilyInfo;

public class FamilyAdapter extends MyBaseAdapter<FamilyInfo>{
	private ArrayList<FamilyInfo> list;
	private LayoutInflater inflater;
	private FamilyInfo item;
	private Context context;

	public FamilyAdapter(Context con, ArrayList<FamilyInfo> list) {
		super(list);
		this.list = list;
		this.context = con;
		inflater = LayoutInflater.from(con);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.family_item, null);
		}
		item = list.get(position);
		TextView tvFamily= (TextView) convertView.findViewById(R.id.tv_family);
		tvFamily.setText(item.name);
		return convertView;
	}
}
