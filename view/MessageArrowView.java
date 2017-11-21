package com.tg.cloudmanagement.view;

import java.util.ArrayList;

import com.tg.cloudmanagement.object.ViewConfig;
import com.tg.cloudmanagement.R;
import com.tg.cloudmanagement.inter.SingleClickListener;
import com.tg.cloudmanagement.util.Tools;

import android.content.Context;
import android.graphics.Color;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MessageArrowView extends LinearLayout {
	private Context con;
	private LayoutInflater inflater;
	private ItemClickListener listener;
	private ArrayList<ViewConfig> list;
	private DisplayMetrics metrics;
	private int marginLeft = 0;
	private boolean allEditable = true;
	private boolean showTopLine = true;
	private boolean showBottomLine = true;
	private SingleClickListener singleClickListener = new SingleClickListener(){
		@Override
		public void onSingleClick(View v) {
			if(listener != null){
				int index = 0; 
				Object tag = v.getTag();
				if(tag == null){
					return;
				}
				Integer integer = (Integer)tag;
				index = integer.intValue();
				listener.onItemClick(MessageArrowView.this,v,index);
				singleClickListener.shieldClickEvent();
			}
		}};
	public MessageArrowView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public MessageArrowView(Context context) {
		super(context);
		init(context);
	}
	
	public void setEditable(boolean params){
		allEditable = params;
		freshAll();
	}

	public void setShowTopLine(boolean show){
		showTopLine = show;
	}

	public void setShowBottomLine(boolean show){
		showBottomLine = show;
	}
	public boolean isEditable(){
		return allEditable;
	}
	
	public void setItemClickListener(ItemClickListener l){
		listener = l;
	}
	public void init(Context c){
		con = c;
		metrics = Tools.getDisplayMetrics(c);
		inflater = LayoutInflater.from(con);
		setOrientation(LinearLayout.VERTICAL);
		setBackgroundColor(Color.WHITE);
	}
	
	public void setData(ArrayList<ViewConfig> list){
		this.list = list;
		removeAllViews();
		if(list == null || list.size() == 0){
			setVisibility(View.GONE);
		}else{
			setVisibility(View.VISIBLE);
			initView();
		}
		invalidate();
	}
	
	public String getRightTextString(int position){
		View child = getView(position);
		if(child == null){
			return "";
		}
		EditText rightEdit = (EditText)child.findViewById(R.id.item_txt2);
		TextView rightTextView = (TextView)child.findViewById(R.id.item_txt2_textView);
		if(rightEdit.getVisibility() == View.VISIBLE){
			return rightEdit.getText().toString();
		}else{
			return rightTextView.getText().toString();
		}
	}
	
	public void initView(){
		LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				(int)(1 * metrics.density));
		if(showTopLine){
			//addView(inflater.inflate(R.layout.line_horizontal, null), layoutParams);
		}
		int len = list.size();
		View middleLine;
		ViewConfig vc;
		for(int i = 0 ; i < len; i++){
			vc = list.get(i);
			View child = getChildView(vc);
			LayoutParams p = new LayoutParams(
					LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
			
			child.setTag(Integer.valueOf(i));
			addView(child, p);
			if(i != len -1 && vc.showMiddleLine){
				middleLine = new View(con);
				middleLine.setBackgroundColor(getResources().getColor(R.color.line_color));
				middleLine.setVisibility(View.VISIBLE);
				p = new LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						(int)(1 * metrics.density + 0.5f));
				if(vc.lineFillParent){
					p.leftMargin = 0;
				}else{
					p.leftMargin = marginLeft;
				}
				middleLine.setLayoutParams(p);
				addView(middleLine);
			}
		}
		if(showBottomLine){
			//addView(inflater.inflate(R.layout.line_horizontal, null), layoutParams);
		}
	}
	
	public View getChildView(ViewConfig config){
		View child = inflater.inflate(R.layout.item, null);
		initData(child,config);
		return child;
	}
	
	private void initData(View child,ViewConfig config){
		if(child == null){
			return;
		}
		child.setId(config.id);
		TextView leftTv = (TextView)child.findViewById(R.id.item_txt1);
		TextView rightTextView = (TextView)child.findViewById(R.id.item_txt2_textView);
		EditText rightEdit = (EditText)child.findViewById(R.id.item_txt2);
		ImageView leftImg = (ImageView)child.findViewById(R.id.img_left);
		ImageView rightImg = (ImageView)child.findViewById(R.id.img_right);
		if(allEditable && config.enable ){
			child.setEnabled(true);
			child.setOnClickListener(singleClickListener);
		}else{
			child.setEnabled(false);
		}
		View arrow = child.findViewById(R.id.item_img_arrow);
		if(allEditable && config.showArrow){
			arrow.setVisibility(View.VISIBLE);
		}else{
			arrow.setVisibility(View.GONE);
		}
		leftTv.setText(config.leftText);
		if(config.rightEditable){
			rightTextView.setVisibility(View.GONE);
			rightEdit.setVisibility(View.VISIBLE);
			rightEdit.setText(config.rightText);
			setEditTextLength(rightEdit,config);
			setEditTextInputType(rightEdit, config);
		}else{
			rightTextView.setVisibility(View.VISIBLE);
			rightEdit.setVisibility(View.GONE);
			rightTextView.setText(config.rightText);
		}
		if(!allEditable){
			rightEdit.setEnabled(false);
		}else{
			if(config.rightEditable){
				rightEdit.setEnabled(true);
			}
		}
			//rightEdit.setVrisibility(View.VISIBLE);
		//}
		leftTv.setTextSize(config.leftTextSize);
		rightEdit.setTextSize(config.rightTextSize);
		rightTextView.setTextSize(config.rightTextSize);
		leftTv.setTextColor(getResources().getColor(config.leftTextColor));
		rightEdit.setTextColor(getResources().getColor(config.rightTextColor));
		rightTextView.setTextColor(getResources().getColor(config.rightTextColor));
		if(config.leftTextBgResId > 0){
			leftTv.setBackgroundResource(config.leftTextBgResId);
		}else{
			leftTv.setBackgroundColor(Color.TRANSPARENT);
		}
		if(config.rightTextBgResId > 0){
			rightEdit.setBackgroundResource(config.rightTextBgResId);
			rightTextView.setBackgroundResource(config.rightTextBgResId);
		}else{
			rightEdit.setBackgroundColor(Color.TRANSPARENT);
			rightTextView.setBackgroundColor(Color.TRANSPARENT);
		}
		if(config.leftDrawable != null){
			leftImg.setVisibility(View.VISIBLE);
			leftImg.setImageDrawable(config.leftDrawable);
			leftImg.measure(0, 0);
			marginLeft = (int)(20 *metrics.density+leftImg.getMeasuredWidth()+0.5f);
		}else{
			leftImg.setVisibility(View.GONE);
			marginLeft = (int)(10 *metrics.density+0.5f);
		}
		if(config.rightDrawable != null){
			rightImg.getLayoutParams().width = config.rightImgWidth;
			rightImg.getLayoutParams().height = config.rightImgHeight;
			rightImg.setVisibility(View.VISIBLE);
			rightImg.setImageDrawable(config.rightDrawable);
			rightImg.setScaleType(config.rightImgScaleType);
		}else{
			rightImg.setVisibility(View.GONE);
		}
	}
	
	public void freshView(int position){
		if(list == null || position < 0 || position >= list.size()){
			return;
		}
		
		initData(getView(position),list.get(position));
	}
	
	private void setEditTextLength(EditText editTv,ViewConfig c){
		if(c.editMaxLength > 0){
			InputFilter[] filters = {new InputFilter.LengthFilter(c.editMaxLength)};
			editTv.setFilters(filters);
		}
	}

	public ViewConfig getItem(int position){
		if(list == null || list.size() == 0){
			return null;
		}
		if(position < 0 || position >= list.size()){
			return null;
		}
		return list.get(position);
	}
	
	private void setEditTextInputType(EditText editTv,ViewConfig c){
		editTv.setInputType(c.inputType);
	}
	
	public View getView(int position){
		View child;
		for(int i = 0; i < getChildCount(); i ++){
			child = getChildAt(i);
			Object tag = child.getTag();
			if(tag == null){
				continue;
			}
			if(position == (Integer)tag){
				return child;
			}
		}
		return null;
	}
	
	public ImageView getRightImageView(int position){
		View child = getView(position);
		if(child == null){
			return null;
		}
		ImageView rightImg = (ImageView)child.findViewById(R.id.img_right);
		return rightImg;
	}
	
	public void freshAll(){
		View child;
		int position;
		for(int i = 0; i < getChildCount(); i ++){
			child = getChildAt(i);
			Object tag = child.getTag();
			if(tag == null){
				continue;
			}
			position = ((Integer)tag).intValue();
			initData(child,list.get(position));
		}
	}
	
	public interface ItemClickListener{
		public void onItemClick(MessageArrowView mv, View v, int position);
	}
}
