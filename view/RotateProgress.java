package com.tg.cloudmanagement.view;

import com.tg.cloudmanagement.R;
import com.tg.cloudmanagement.util.Tools;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

public class RotateProgress extends View{
	public static final int MSG_DRAW = 1;
	private int speed = 100;
	private int width;
	private int centerX;
	private int centerY ;
	private int angle = 0;
	private int count ;
	 
	private int[] initAngles ;
	private Context context;
	private Paint paint = new Paint();
	private boolean stop = true;
	private int angleUnit;
	private int w ;
	private int h ;
	private int round;
	private int maxAlpha = 255;
	private int cw = 1;
	private int contentColor = Color.BLACK;
	private int widthDp = 30;
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(msg.what == MSG_DRAW){
				invalidate();
			}
		}
	};
	public RotateProgress(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context,attrs);
	}

	public RotateProgress(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context,attrs);
	}

	public RotateProgress(Context context) {
		super(context);
		initView(context,null);
	}
	
	private void initView(Context context,AttributeSet attrs){
		this.context = context;
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RotateProgress);
		contentColor = ta.getColor(R.styleable.RotateProgress_progress_color, Color.BLACK);
		int width = ta.getDimensionPixelSize(R.styleable.RotateProgress_size, (int)(widthDp * Tools.getDisplayMetrics(context).density));
		widthDp = (int)(width / Tools.getDisplayMetrics(context).density);
		ta.recycle();
		paint.setStyle(Style.FILL);
		paint.setColor(contentColor);
		initProperty(width);
	}
	
	public void initProperty(int size){
		width = size;
		centerX = width /2;
		centerY = width /2;
		count = widthDp > 40? 12:(widthDp>25? 10:8);
		w = width/4;
		h = w/3;
		round = h/2;
		angleUnit = 360 / count;
		initAngles = new int[count];
		for(int i = 0; i < count; i ++){
			initAngles[i] = angleUnit*i;
		}
	}
	
	public void drawChild(Canvas canvas){
		canvas.save();
		int alpha = 0;
		float unitAlpha = maxAlpha * 1.0f/count;
		for(int i = 0; i < count; i ++){
			canvas.restore();
			canvas.save();
			angle = angle % 360;
			canvas.rotate(cw*(initAngles[i]+angle),centerX,centerY);
			int x = centerX+width/2 - w, y = centerY - h/2;
			//canvas.rotate(-90,x,y);
			alpha = (int)(unitAlpha*(i+1));
			paint.setAlpha(alpha);
			canvas.drawRoundRect(new RectF(x , y, x+w, y+h), round, round, paint);
		}
		canvas.restore();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		canvas.drawColor(Color.TRANSPARENT);
		drawChild(canvas);
		angle += angleUnit;
		angle %= 360;
		if(!stop){
			handler.removeMessages(MSG_DRAW);
			handler.sendEmptyMessageDelayed(MSG_DRAW, speed);
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(width, width);
	}
	
	public void stopRotateAnimator(){
		handler.removeMessages(MSG_DRAW);
		stop = true;
	}
	
	public void startRotateAnimtor(){
		stop = false;
		angle = 0;
		invalidate();
	}
	
	@Override
	public void setVisibility(int visibility) {
		super.setVisibility(visibility);
		if(visibility == View.VISIBLE){
			startRotateAnimtor();
		}else{
			stopRotateAnimator();
		}
	}

}
