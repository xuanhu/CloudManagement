package com.tg.cloudmanagement.view;

import com.tg.cloudmanagement.inter.WindowSoftInputListener;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;


public class MyLinearLayout extends LinearLayout{
	private WindowSoftInputListener  callBack;
	public MyLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MyLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MyLinearLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		if(oldh == 0){
			return;
		}
		if(h - oldh > 0){//隐藏键盘
			if(callBack != null){
				callBack.hide();
			}
		}else{//显示键盘
			if(callBack != null){
				callBack.show();
			}
		}
	}
	
	public void setWindowSoftInputListener(WindowSoftInputListener l){
		callBack = l;
	}
	
}
