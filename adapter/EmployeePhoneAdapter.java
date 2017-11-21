package com.tg.cloudmanagement.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tg.cloudmanagement.R;
import com.tg.cloudmanagement.base.MyBaseAdapter;
import com.tg.cloudmanagement.info.EmployeePhoneInfo;
import com.tg.cloudmanagement.info.LinkManInfo;

public class EmployeePhoneAdapter extends MyBaseAdapter<EmployeePhoneInfo>{

	private ArrayList<EmployeePhoneInfo> list;
	private LayoutInflater inflater;
	private EmployeePhoneInfo item;
	private Context context;

	public EmployeePhoneAdapter(Context con, ArrayList<EmployeePhoneInfo> list) {
		super(list);
		this.list = list;
		this.context = con;
		inflater = LayoutInflater.from(con);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.employee_phone, null);
		}
		item = list.get(position);
		TextView tvPhone= (TextView) convertView.findViewById(R.id.tv_phone);
		tvPhone.setText(item.phone);
		return convertView;
	}
}
