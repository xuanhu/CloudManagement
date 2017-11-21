package com.tg.cloudmanagement.view.gallery;


import com.tg.cloudmanagement.ShowImageActivity;
import com.tg.cloudmanagement.log.Logger;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Gallery;


public class PicGallery extends Gallery {
	private GestureDetector gestureScanner;
	private MyImageView imageView;

	public PicGallery(Context context) {
		super(context);
	} 

	public PicGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setDetector(GestureDetector dectector) {
		gestureScanner = dectector;
	}

	public PicGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOnTouchListener(new OnTouchListener() {

			float baseValue;
			float originalScale;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				View view = PicGallery.this.getSelectedView();
				if (view instanceof MyImageView) {
					imageView = (MyImageView) view;

					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						baseValue = 0;
						originalScale = imageView.getScale();
					}
					if (event.getAction() == MotionEvent.ACTION_MOVE) {
						if (event.getPointerCount() == 2) {
							Logger.logd("=-========================");
							float x = event.getX(0) - event.getX(1);
							float y = event.getY(0) - event.getY(1);
							float value = (float) Math.sqrt(x * x + y * y);// 计算两点的距离
							// System.out.println("value:" + value);
							if (baseValue == 0) {
								baseValue = value;
							} else {
								float scale = value / baseValue;// 当前两点间的距离除以手指落下时两点间的距离就是需要缩放的比例。
								// scale the image
								imageView.zoomTo(originalScale * scale, x
										+ event.getX(1), y + event.getY(1));

							}
						}
					}
				}
				return false;
			}

		});
	}

	float v[] = new float[9];
	
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		Logger.logd("onScroll ====================");
		View view = PicGallery.this.getSelectedView();
		if (view instanceof MyImageView) {
			
			float xdistance = calXdistance(e1, e2);
			float min_distance = ShowImageActivity.screenWidth / 4f;
			Logger.logd("xdistance=" + xdistance + ",min_distance="
					+ min_distance);
			if (isScrollingLeft(e1, e2) && xdistance > min_distance) {
				kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
			} else if (!isScrollingLeft(e1, e2) && xdistance > min_distance) {
				kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
			}
			
			imageView = (MyImageView) view;

			Matrix m = imageView.getImageMatrix();
			m.getValues(v);
			// 图片实时的上下左右坐标
			float left, right;
			// 图片的实时宽，高
			float width = imageView.getScale() * imageView.getImageWidth();
			float height = imageView.getScale() * imageView.getImageHeight();
			
			if ((int) width <= ShowImageActivity.screenWidth
					&& (int) height <= ShowImageActivity.screenHeight)// 如果图片当前大小<屏幕大小，直接处理滑屏事件
			{
				super.onScroll(e1, e2, distanceX, distanceY);
			} else {
				left = v[Matrix.MTRANS_X];
				right = left + width;
				Rect r = new Rect();
				imageView.getGlobalVisibleRect(r);
				float absX = Math.abs(distanceX);
				float absY = Math.abs(distanceY);
				if(height <= ShowImageActivity.screenHeight){//高没有充满屏幕
					if (r.right < ShowImageActivity.screenWidth || 
							left > 0 ||
							r.left > 0 || 
							right < ShowImageActivity.screenWidth) {
						super.onScroll(e1, e2, distanceX, distanceY);
					}else{
						imageView.postTranslate(-distanceX, 0);
					}
					return false;
				}else if(width <= ShowImageActivity.screenWidth){//宽没有充满屏幕
					if((int) height > ShowImageActivity.screenHeight && absX < absY){
						imageView.postTranslate(0, -distanceY);
					}else{
						super.onScroll(e1, e2, distanceX, distanceY);
					}
					return false;
				}
				if(distanceX > 0){//左
					if(r.left > 0 || right < ShowImageActivity.screenWidth){
						super.onScroll(e1, e2, distanceX, distanceY);
					}else{
						imageView.postTranslate(-distanceX, -distanceY);
					}
				}else{//右
					if(r.right < ShowImageActivity.screenWidth || left > 0){
						super.onScroll(e1, e2, distanceX, distanceY);
					}else{
						imageView.postTranslate(-distanceX, -distanceY);
					}
				}

			}

		} else {
			super.onScroll(e1, e2, distanceX, distanceY);
		}
		return false;
	}

	private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {
		return e2.getX() > e1.getX();
	}

	private float calXdistance(MotionEvent e1, MotionEvent e2) {
		return Math.abs(e2.getX() - e1.getX());
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		Logger.logd("onFling +++++++++++++++++++++ velocityX = "+velocityX+" velocityY = "+velocityY);
		Logger.logd(" onFling e1 = "+e1.getX()+"  "+e1.getY()+"  e2 = "+e2.getX()+"  "+e2.getY());
		return false;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//Logger.d(DEBUG,"[PicGallery.onTouchEvent]"+"PicGallery.onTouchEvent");
		if (gestureScanner != null) {
			gestureScanner.onTouchEvent(event);
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			// 判断边界是否越界
			View view = PicGallery.this.getSelectedView();
			int index = this.getSelectedItemPosition();
			Logger.logd("index = "+index);
			if (view instanceof MyImageView) {
				
				if(kEvent != KEY_INVALID) { // 是否切换上一页或下一页
					onKeyDown(kEvent, null);
					kEvent = KEY_INVALID;
				}
				
				imageView = (MyImageView) view;
				float width = imageView.getScale() * imageView.getImageWidth();
				float height = imageView.getScale()
						* imageView.getImageHeight();
				// Logger.LOG("onTouchEvent", "width=" + width + ",height="
				// + height + ",screenWidth="
				// + PictureViewActivity.screenWidth + ",screenHeight="
				// + PictureViewActivity.screenHeight);
				if ((int) width <= ShowImageActivity.screenWidth
						&& (int) height <= ShowImageActivity.screenHeight)// 如果图片当前大小<屏幕大小，判断边界
				{
					Logger.logd("break -------");
					break;
				}
				float v[] = new float[9];
				Matrix m = imageView.getImageMatrix();
				m.getValues(v);
				float top = v[Matrix.MTRANS_Y];
				float bottom = top + height;
				Logger.logd("top = "+top+"  bottom= "+bottom+" height = "+height);
				if (top < 0 && bottom < ShowImageActivity.screenHeight) {
//					imageView.postTranslateDur(-top, 200f);
					imageView.postTranslateDur(ShowImageActivity.screenHeight
							- bottom, 200f);
				}
				if (top > 0 && bottom > ShowImageActivity.screenHeight) {
//					imageView.postTranslateDur(PictureViewActivity.screenHeight
//							- bottom, 200f);
					imageView.postTranslateDur(-top, 200f);
				}
				
				float left =v[Matrix.MTRANS_X];
				float right = left + width;
				Logger.logd("left = "+left+"  right= "+right+" width = "+width);
				if(left<0 && right< ShowImageActivity.screenWidth){
//					imageView.postTranslateXDur(-left, 200f);
					imageView.postTranslateXDur(ShowImageActivity.screenWidth
							- right, 200f);
				}
				if(left>0 && right>ShowImageActivity.screenWidth){
//					imageView.postTranslateXDur(PictureViewActivity.screenWidth
//							- right, 200f);
					imageView.postTranslateXDur(-left, 200f);
				}
			}
			break;
		}
		return super.onTouchEvent(event);
	}
	
	int kEvent = KEY_INVALID; //invalid
	public static final int KEY_INVALID = -1;
}
