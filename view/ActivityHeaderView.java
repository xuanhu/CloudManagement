package com.tg.cloudmanagement.view;


import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tg.cloudmanagement.R;

public class ActivityHeaderView extends LinearLayout implements OnClickListener{
	private OnClickListener listenerBack;
	private OnClickListener listenerRight;
	private int resId;
	private Activity activity;
	private ImageView imgBack;
	private ImageView imgRight;
	private Drawable background = null;
	private TextView tvBack;
	private TextView tvRight;
	private String titleText;
	private TextView tvTitle;
	private FrameLayout backLayout;
	private FrameLayout rightLayout;
	private FrameLayout headContentLayout;
	private boolean hideBackButton = false;
	private int titleTextColorRes;
	private int backGroundRes;
	

	public ActivityHeaderView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context,attrs);
	}

	public ActivityHeaderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context,attrs);
	}
	public ActivityHeaderView(Context context) {
		super(context);
		init(context,null);
	}
	public void setListenerRight(OnClickListener l){
		listenerRight  = l;
	}
	
	public void setListenerBack(OnClickListener l){
		listenerBack  = l;
	}
	public void setLineVisibility(int visibility){
		findViewById(R.id.head_line).setVisibility(visibility);
	}
	private void init(Context con,AttributeSet attr){
		if(con instanceof Activity){
			activity =(Activity)con;
		}
		if(attr != null){
			backGroundRes = attr.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "background", -1);
			TypedArray array = con.obtainStyledAttributes(attr, R.styleable.ActivityHeaderView);
			resId = array.getResourceId(R.styleable.ActivityHeaderView_rightViewRes, 0);
			titleText = array.getString(R.styleable.ActivityHeaderView_titleText);
			hideBackButton = array.getBoolean(R.styleable.ActivityHeaderView_hideBackButton, false);
			titleTextColorRes = array.getResourceId(R.styleable.ActivityHeaderView_titletextColor, -1);
			array.recycle();
		}
		 View v = LayoutInflater.from(con).inflate(R.layout.activity_activity_header_view, this);
		 tvTitle = (TextView)v.findViewById(R.id.tv_title);
			if(titleTextColorRes != -1){
				tvTitle.setTextColor(getResources().getColor(titleTextColorRes));
			}
			if(titleText != null){
				tvTitle.setText(titleText);
			}
			headContentLayout = (FrameLayout)v.findViewById(R.id.head_content);
			if(backGroundRes != -1){
				headContentLayout.setBackgroundResource(backGroundRes);
			}
			backLayout = (FrameLayout)v.findViewById(R.id.back_layout);
			rightLayout = (FrameLayout)v.findViewById(R.id.right_layout);
			imgBack = (ImageView)v.findViewById(R.id.back_img);
			tvBack = (TextView)v.findViewById(R.id.back_txt);
			
			imgRight = (ImageView)v.findViewById(R.id.img_right);
			tvRight = (TextView)v.findViewById(R.id.text_right);
			if(resId != 0){
				setRightImage(resId);
			}else{
				hideRightView();
			}
			if(hideBackButton){
				//hideLeftView();
				backLayout.setVisibility(View.INVISIBLE);
				tvBack.setVisibility(View.GONE);
				imgBack.setVisibility(View.VISIBLE);
				imgBack.setImageResource(R.drawable.leftarrow);
			}else{
				setLeftImage(R.drawable.leftarrow);
			}
			backLayout.setOnClickListener(this);
			rightLayout.setOnClickListener(this);
			setLineVisibility(View.GONE);
	}
	public void setLeftText(String str){
		backLayout.setVisibility(View.VISIBLE);
		tvBack.setVisibility(View.VISIBLE);
		imgBack.setVisibility(View.GONE);
		tvBack.setText(str);
	}
	
	public void setLeftTextColor(int color){
		backLayout.setVisibility(View.VISIBLE);
		tvBack.setVisibility(View.VISIBLE);
		imgBack.setVisibility(View.GONE);
		tvBack.setTextColor(color);
	}
	
	public void setLeftTextColor(ColorStateList states){
		backLayout.setVisibility(View.VISIBLE);
		tvBack.setVisibility(View.VISIBLE);
		imgBack.setVisibility(View.GONE);
		tvBack.setTextColor(states);
	}
	
	public void setLeftTextSize(float size){
		backLayout.setVisibility(View.VISIBLE);
		tvBack.setVisibility(View.VISIBLE);
		imgBack.setVisibility(View.GONE);
		tvBack.setTextSize(size);
	}
	
	public void setLeftImage(int resID){
		backLayout.setVisibility(View.VISIBLE);
		tvBack.setVisibility(View.GONE);
		imgBack.setVisibility(View.VISIBLE);
		imgBack.setImageResource(resID);
	}
	
	public void hideLeftView(){
		backLayout.setVisibility(View.GONE);
	}
	
	public void setRightText(String str){
		rightLayout.setVisibility(View.VISIBLE);
		tvRight.setVisibility(View.VISIBLE);
		imgRight.setVisibility(View.GONE);
		tvRight.setText(str);
		tvRight.setTextColor(Color.WHITE);
	}
	
	public void setRightTextColor(int color){
		rightLayout.setVisibility(View.VISIBLE);
		tvRight.setVisibility(View.VISIBLE);
		imgRight.setVisibility(View.GONE);
		tvRight.setTextColor(color);
	}
	
	public void setRightTextColor(ColorStateList states){
		rightLayout.setVisibility(View.VISIBLE);
		tvRight.setVisibility(View.VISIBLE);
		imgRight.setVisibility(View.GONE);
		tvRight.setTextColor(states);
	}
	
	public void setRightTextSize(float size){
		rightLayout.setVisibility(View.VISIBLE);
		tvRight.setVisibility(View.VISIBLE);
		imgRight.setVisibility(View.GONE);
		tvRight.setTextSize(size);
	}
	
	public void setRightImage(int resID){
		rightLayout.setVisibility(View.VISIBLE);
		tvRight.setVisibility(View.GONE);
		imgRight.setVisibility(View.VISIBLE);
		imgRight.setImageResource(resID);
	}
	
	public void hideRightView(){
		rightLayout.setVisibility(View.GONE);
	}
	
	public void showRightView(){
		rightLayout.setVisibility(View.VISIBLE);
	}
	
	public void setTitle(String text){
		tvTitle.setVisibility(View.VISIBLE);
		tvTitle.setText(text);
		tvTitle.setTextColor(getResources().getColor(R.color.white));
	}
	
	public void hideTitle(){
		tvTitle.setVisibility(View.INVISIBLE);
	}
	
	@Override
	public void setBackgroundColor(int color){
		headContentLayout.setBackgroundColor(color);
	}
	
	@Override
	public void onClick(View v) {
		if(v == backLayout){
			if(listenerBack != null){
				listenerBack.onClick(v);
			}else{
				if(activity != null){
					activity.finish();
				}
			}
		}else{
			if(listenerRight != null){
				listenerRight.onClick(v);
			}
		}
	}
}

