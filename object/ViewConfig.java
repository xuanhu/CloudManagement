package com.tg.cloudmanagement.object;


import com.tg.cloudmanagement.R;

import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;


public class ViewConfig {
	public int id;
	public int leftTextColor = R.color.text_color3;
	public int rightTextColor = R.color.text_color1;
	public float leftTextSize = 15;
	public float rightTextSize = 14;
	public int leftTextBgResId;
	public int rightTextBgResId;
	public int rightImgWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
	public int rightImgHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
	public String leftText = ""; 
	public String rightText = ""; 
	public Drawable leftDrawable;
	public Drawable rightDrawable;
	public boolean showArrow = true;
	public boolean enable = true;
	public boolean lineFillParent = false;
	public boolean rightEditable = false;
	public int editMaxLength = -1;
	public boolean showMiddleLine = true;
	public int inputType = InputType.TYPE_CLASS_TEXT;
	public ScaleType rightImgScaleType = ScaleType.CENTER_INSIDE;
	/**
	 * 默认第一个TextView 的weight = 1;
	 */
	public ViewConfig(String leftText, String rightText, boolean showArrow){
		super();
		this.leftText = leftText;
		this.rightText = rightText;
		this.showArrow = showArrow;
	}
	public ViewConfig(int id,String leftText, String rightText, boolean showArrow,int fristTextColor,int secondTextColor,boolean enable) {
		super();
		this.id = id;
		this.leftText = leftText;
		this.rightText = rightText;
		this.showArrow = showArrow;
		this.leftTextColor = fristTextColor;
		this.rightTextColor = secondTextColor;
		this.enable = enable;
	}
}
