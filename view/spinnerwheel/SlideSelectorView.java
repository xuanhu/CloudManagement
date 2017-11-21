package com.tg.cloudmanagement.view.spinnerwheel;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tg.cloudmanagement.R;
import com.tg.cloudmanagement.object.SlideItemObj;
import com.tg.cloudmanagement.util.PinyinComparator;
import com.tg.cloudmanagement.view.spinnerwheel.adapter.ArrayWheelAdapter;

public class SlideSelectorView extends LinearLayout implements OnClickListener{
	private Context con;
	private AbstractWheel wheelListView1;
	private AbstractWheel wheelListView2;
	private LinearLayout layout1;
	private LinearLayout layout2;
	private TextView tvTitle;
	private TextView tvComplete;
	private TextView tvCancel; 
	private ArrayWheelAdapter<SlideItemObj> adapter1;
	private ArrayWheelAdapter<SlideItemObj> adapter2;
	private OnCompleteListener completeListener;
	private OnCancelListener cancelListener;
	private PinyinComparator pinyinComparator = new PinyinComparator();
	private ArrayList<SlideItemObj> list1;
	private ArrayList<SlideItemObj> list2;
	private boolean needSort = true;
	public SlideSelectorView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		init(context);
	} 

	public SlideSelectorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SlideSelectorView(Context context) {
		super(context);
		init(context);
		setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));
	}
	
	
	private void init(Context con){
		this.con = con;
		LayoutInflater.from(con).inflate(R.layout.bank_selector, this);
		layout1 = (LinearLayout)findViewById(R.id.layout1);
		layout2 = (LinearLayout)findViewById(R.id.layout2);
		wheelListView1 = (AbstractWheel) findViewById(R.id.list1);
		wheelListView2 = (AbstractWheel) findViewById(R.id.list2);
		tvTitle = (TextView)findViewById(R.id.tv_title);
		tvComplete = (TextView)findViewById(R.id.tv_complete);
		tvCancel = (TextView)findViewById(R.id.tv_cancel);
		tvComplete.setOnClickListener(this);
		tvCancel.setOnClickListener(this);
		tvTitle.setText("");
		wheelListView1.setVisibleItems(5);
		wheelListView2.setVisibleItems(5);
	}
	
	public void setTitle(String text){
		tvTitle.setText(text);
	}
	
	public int getCurrentPosition1(){
		if(wheelListView1 != null && adapter1 != null && adapter1.getItemsCount() > 0){
			return wheelListView1.getCurrentItemPosition();
		}
		return -1;
	}
	
	public int getCurrentPosition2(){
		if(wheelListView2 != null && adapter2 != null&& adapter2.getItemsCount() > 0){
			return wheelListView2.getCurrentItemPosition();
		}
		return -1;
	}
	
	public int getCount1(){
		if(adapter1 != null){
			return adapter1.getItemsCount();
		}else{
			return 0;
		}
	}
	
	public int getCount2(){
		if(adapter2 != null){
			return adapter2.getItemsCount();
		}else{
			return 0;
		}
	}
	
	public void setCurrentPosition(int position1, int position2){
		if(wheelListView1 != null && adapter1 != null ){
			if(position1 >= 0 && position1 < adapter1.getItemsCount()){
				wheelListView1.setCurrentItem(position1);
			}else{
				wheelListView1.setCurrentItem(0);
			}
		}
		if(wheelListView2 != null && adapter2 != null ){
			if(position2 >= 0 && position2 < adapter2.getItemsCount()){
				wheelListView2.setCurrentItem(position2);
			}else{
				wheelListView2.setCurrentItem(0);
			}
		}
	}
	
	public void setCycle(boolean cycle1,boolean cycle2){
		if(wheelListView1 != null){
			wheelListView1.setCyclic(cycle1);
		}
		if(wheelListView2 != null){
			wheelListView2.setCyclic(cycle2);
		}
	}
	
	public void setOnScrollListener(OnWheelScrollListener l1){
		wheelListView1.addScrollingListener(l1);
	}
	public void notifyDataChange(int index){
		if(index == 0){
			if( adapter1 != null){
				if(needSort){
					Collections.sort(list1, pinyinComparator);
				}
				adapter1.notifyDataChange();
			}
		}else if(index == 1){
			if(adapter2 != null){
				if(needSort){
					Collections.sort(list2, pinyinComparator);
				}
				adapter2.notifyDataChange();
			}
		}
	}
	
	public void notifyDataInvalidated(int index){
		if(index == 0){
			if( adapter1 != null){
				if(needSort){
					Collections.sort(list1, pinyinComparator);
				}
				adapter1.notifyDataInvalidated();
			}
		}else if(index == 1){
			if(adapter2 != null){
				if(needSort){
					Collections.sort(list2, pinyinComparator);
				}
				adapter2.notifyDataInvalidated();
			}
		}
	}
	
	public void setNeedSort(boolean need){
		needSort = need;
	}
	
	public void setList(ArrayList<SlideItemObj> list1, ArrayList<SlideItemObj> list2){
		if(list1 == null || list1.size() == 0){
			layout1.setVisibility(View.GONE);
		}else{
			layout1.setVisibility(View.VISIBLE);
			if(needSort){
				Collections.sort(list1, pinyinComparator);
			}
			if(adapter1 == null){
				this.list1 = list1;
				adapter1 =new ArrayWheelAdapter<SlideItemObj>(
						con, this.list1,R.layout.bank_item,R.id.bank_tv);
				wheelListView1.setViewAdapter(adapter1);
			}else{
				if(this.list1 != list1){
					this.list1.clear();
					this.list1.addAll(list1);
				}
				
				adapter1.notifyDataInvalidated();	
			}
			if(list1.size() <= 1){
				wheelListView1.setCurrentItem(0);
			}else{
				wheelListView1.setCurrentItem(1);
			}
		}
		
		if(list2 == null || list2.size() == 0){
			layout2.setVisibility(View.GONE);
		}else{
			layout2.setVisibility(View.VISIBLE);
			if(needSort){
				Collections.sort(list2, pinyinComparator);
			}
			if(adapter2 == null){
				this.list2 = list2;
				adapter2 =new ArrayWheelAdapter<SlideItemObj>(
						con, this.list2,R.layout.bank_item,R.id.bank_tv);
				wheelListView2.setViewAdapter(adapter2);
			}else{
				if(this.list2 != list2){
					this.list2.clear();
					this.list2.addAll(list2);
				}
				adapter2.notifyDataInvalidated();
			}
			if(list2.size() <= 1){
				wheelListView2.setCurrentItem(0);
			}else{
				wheelListView2.setCurrentItem(1);
			}
		}
	}

	@Override
	public void onClick(View v) {
		if(v == tvCancel){
			if(cancelListener != null){
				cancelListener.onCancel();
			}
		}else{
			if(completeListener != null){
				SlideItemObj item1 = null ,item2 = null;
				if(adapter1 != null){
					item1 = wheelListView1.getCurrentItem();
				}
				if(adapter2 != null){
					item2 = wheelListView2.getCurrentItem();
				}
				completeListener.onComplete(item1,item2 );
			}
		}
	}
	
	public void setOnCompleteListener(OnCompleteListener l){
		completeListener = l;
	}
	public void setOnCancelListener(OnCancelListener l){
		cancelListener = l;
	}
	public interface OnCompleteListener{
		public void onComplete(SlideItemObj item1, SlideItemObj item2);
	}
	
	public interface OnCancelListener{
		public void onCancel();
	}
}
