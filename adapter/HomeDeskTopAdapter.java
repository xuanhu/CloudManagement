package com.tg.cloudmanagement.adapter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

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
import com.tg.cloudmanagement.util.IsTodayutil;
import com.tg.cloudmanagement.util.Tools;

public class HomeDeskTopAdapter extends MyBaseAdapter<HomeDeskTopInfo> {
	private ArrayList<HomeDeskTopInfo> list;
	private LayoutInflater inflater;
	private HomeDeskTopInfo item;

	public HomeDeskTopAdapter(Context context, ArrayList<HomeDeskTopInfo> list) {
		super(list);
		this.list = list;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		item = list.get(position);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.home_listview, null);
		}
		ImageView ivHeadimg = (ImageView) convertView.findViewById(R.id.iv_headimg);
		TextView tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
		TextView tvContent = (TextView) convertView.findViewById(R.id.tv_content);
		TextView tvTime = (TextView) convertView.findViewById(R.id.tv_time);
		TextView tvNotread = (TextView) convertView.findViewById(R.id.tv_notread);
		if (item.notread > 0) {
			tvNotread.setVisibility(View.VISIBLE);
			tvNotread.setText(String.valueOf(item.notread));
		} else {
			tvNotread.setVisibility(View.GONE);
		}
		tvTitle.setText(item.weiappname);
		tvContent.setText(item.content);
		if(item.homePushTime != null){
			boolean isToday;
			IsTodayutil isTodayUtil = new IsTodayutil();
			try {
				isToday = isTodayUtil.IsToday(item.homePushTime);
				if(isToday){//表示消息时间是今天（只显示几时几分或者刚刚）
					Date dt = new Date();
					Long time = dt.getTime();//当前时间
					long servicetime = Tools.dateString2Millis(item.homePushTime);//获取到的时间
					long timestamp = time - servicetime;//当前时间和服务时间差
					long minutes = 10*60*1000;//10分钟转换为多少毫秒
					if( timestamp > minutes){
						String nowTime = Tools.getSecondToString(servicetime);
						tvTime.setText(nowTime);
					}else {
						tvTime.setText("刚刚");
					}
				}else{
					Date dt = new Date();
					Long time = dt.getTime();//当前时间
					String newYear = Tools.getSimpleDateToString(time);//
					String serviceYear = item.homePushTime.substring(0,item.homePushTime.indexOf(" "));
					if(newYear.substring(0,4).equals(serviceYear.substring(0,4))){//表示消息时间是今年
						tvTime.setText(serviceYear.substring(5,serviceYear.length()));
					}else{
						tvTime.setText(serviceYear);
					}
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		VolleyUtils.getImage(Tools.mContext, item.icon, ivHeadimg,Tools.userHeadSize, Tools.userHeadSize, R.drawable.moren);
		return convertView;
	}
}
