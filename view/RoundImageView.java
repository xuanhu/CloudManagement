package com.tg.cloudmanagement.view;

import com.tg.cloudmanagement.util.Tools;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ImageView;


public class RoundImageView extends ImageView{
	public static final int TYPE_DEFAULT = 0;
	public static final int TYPE_ROUND = 1;
	public static final int TYPE_CIRCLE = 2;
	private int type = TYPE_ROUND;
	private float radiu = 5;
	private int circleRadiu;
	private Path clipPath = new Path();
	private RectF roundRect = new RectF();
	public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public RoundImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public RoundImageView(Context context) {
		super(context);
		initView(context);
	}
	
	private void setShapeType(int type){
		this.type = type;
		if(type == TYPE_DEFAULT){
			setScaleType(ScaleType.CENTER_INSIDE);
		}else{
			setScaleType(ScaleType.CENTER_CROP);
		}
		invalidate();
	}
	
	public void setRoundShape(){
		setShapeType(TYPE_ROUND);
	}
	
	public void setCircleShape(){
		setShapeType(TYPE_CIRCLE);
	}
	
	public void setRectangleShape(){
		setShapeType(TYPE_DEFAULT);
	}
	
	public void setRadiu(int radiu){
		this.radiu = radiu;
		invalidate();
	}
	
	private void initView(Context context){
		DisplayMetrics dm = Tools.getDisplayMetrics(context);
		radiu = dm.density * radiu;
		setScaleType(ScaleType.CENTER_CROP);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		if(type == TYPE_DEFAULT){
		}else if(type == TYPE_CIRCLE){//圆图
			circleRadiu = Math.min(getWidth(), getHeight())/2;
			clipPath.reset();
			clipPath.addCircle(getWidth()/2, getHeight()/2, 
					circleRadiu, Path.Direction.CW);
			canvas.clipPath(clipPath);
		}else{//圆角
			clipPath.reset();
			roundRect.right = getWidth();
			roundRect.bottom = getHeight();
			clipPath.addRoundRect(roundRect, radiu, radiu, Path.Direction.CW);
			canvas.clipPath(clipPath);
		}
		super.onDraw(canvas);
	}
 
}
