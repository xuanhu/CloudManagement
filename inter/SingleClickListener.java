package com.tg.cloudmanagement.inter;

import android.os.Handler;
import android.os.Message;
import android.view.View;

public abstract class SingleClickListener implements View.OnClickListener{
	public static final int RESTORE_CLICKT_EVENT_CODE = 1;
	private boolean needShield = false;
	public abstract void onSingleClick(View v);
	private Handler hand = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what == RESTORE_CLICKT_EVENT_CODE){
				needShield = false; 
			}
		}
	};
	@Override
	public void onClick(View v) {
		if(needShield){
			return;
		}
		onSingleClick(v);
	}
	
	public void shieldClickEvent(){
		needShield = true;
		hand.sendEmptyMessageDelayed(RESTORE_CLICKT_EVENT_CODE, 400);
	}
}
