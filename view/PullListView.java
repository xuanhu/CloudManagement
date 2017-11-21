package com.tg.cloudmanagement.view;


import com.tg.cloudmanagement.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;


public class PullListView extends ListView{
	private ListViewTouchListener touchListener;
	public void setListViewTouchListener(ListViewTouchListener l){
		touchListener =  l;
	}
	public PullListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context); 
	}
 
	public PullListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public PullListView(Context context) {
		super(context);
		init(context);
	}
	
	private void init(Context con){
		this.setOverScrollMode(View.OVER_SCROLL_NEVER);
		setCacheColorHint(0x00000000);
		setSelector(R.drawable.item_selector);
	}
	
	@Override
	protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX,
			boolean clampedY) {
		// TODO Auto-generated method stub
		super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
	}
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		if(touchListener != null ){
			if(touchListener.onListViewTouchEvent(ev)){
				return true;
			}else{
				return super.onTouchEvent(ev);
			}
		}
		return super.onTouchEvent(ev);
	}

	public interface ListViewTouchListener{
		public boolean  onListViewTouchEvent(MotionEvent ev);
	}
}
