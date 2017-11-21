package com.tg.cloudmanagement.view;

import com.tg.cloudmanagement.R;
import com.tg.cloudmanagement.log.Logger;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;


public class GifImageView extends View {  
	private int[] images = new int[]{
			R.drawable.rotate_2,R.drawable.rotate_3,R.drawable.rotate_4,R.drawable.rotate_5,R.drawable.rotate_6
			,R.drawable.rotate_7,R.drawable.rotate_8,R.drawable.rotate_9,R.drawable.rotate_10
			,R.drawable.rotate_11,R.drawable.rotate_12,R.drawable.rotate_13,R.drawable.rotate_14,R.drawable.rotate_15
			,R.drawable.rotate_16,R.drawable.rotate_17};
	private int mImageWidth;
	private int mImageHeight;
	private long durTime = 500;
	private boolean play = false;
    public GifImageView(Context context) {  
        super(context);  
        initView(context);
    }  
 
    public GifImageView(Context context, AttributeSet attrs) {  
        super(context, attrs); 
        initView(context);
    }  
 
    @Override 
    protected void onDraw(Canvas canvas) {  
    	Logger.logd("gif draw ---");
    	if(play){
    		drawMovie(canvas);
    		invalidate();
    	}else{
    		drawStop(canvas);
    	}
    } 
    
    @Override
    protected void onDetachedFromWindow() {
    	// TODO Auto-generated method stub
    	play = false;
    	super.onDetachedFromWindow();
    }
    
    private void initView(Context context){
    	if(images != null && images.length > 0){
    		Drawable d = getResources().getDrawable(images[0]);
    		mImageWidth = d.getIntrinsicWidth();
    		mImageHeight = d.getIntrinsicHeight();
    	}
    }
    
    private long startTime;
    private void drawMovie(Canvas canvas){
    	if(images == null || images.length == 0){
    		return;
    	}
    	int length = images.length;
    	long now = SystemClock.uptimeMillis();
    	if(startTime == 0){
    		startTime = now;
    	}
    	long realTime = (now - startTime) % durTime;
    	int index = (int)((length -1) * (realTime * 1.0f / durTime));
    	if(index < 0){
    		index = 0;
    	}
    	if(index > length -1){
    		index = length -1;
    	}
    	Logger.logd("index = "+index);
    	Drawable d = getResources().getDrawable(images[index]);
    	d.setBounds(0, 0, mImageWidth, mImageHeight);
    	d.draw(canvas);
    	if(now - startTime > durTime){
    		startTime = 0;
    	}
    	
    }
    
    private void drawStop(Canvas canvas){
    	Drawable d = getResources().getDrawable(images[0]);
    	d.setBounds(0, 0, mImageWidth, mImageHeight);
    	d.draw(canvas);
    }
    
    public void play(boolean play){
    	this.play = play;
    	startTime = 0;
    	invalidate();
    }
 
    @Override 
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
        if (images != null && images.length != 0) {  
            // 如果是GIF图片则重写设定PowerImageView的大小  
            setMeasuredDimension(mImageWidth, mImageHeight);  
        }else{
        	super.onMeasure(widthMeasureSpec, heightMeasureSpec);  
        }
    }  
 
}  
