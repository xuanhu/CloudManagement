package com.tg.cloudmanagement.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.tg.cloudmanagement.R;
import com.tg.cloudmanagement.base.MyBaseAdapter;
import com.tg.cloudmanagement.inter.OnLoadingListener;
import com.tg.cloudmanagement.log.Logger;
import com.tg.cloudmanagement.net.HttpTools;
import com.tg.cloudmanagement.net.MessageHandler;
import com.tg.cloudmanagement.net.MessageHandler.ResponseListener;
import com.tg.cloudmanagement.util.Tools;
import com.tg.cloudmanagement.view.PullListView.ListViewTouchListener;
import com.tg.cloudmanagement.view.dialog.ToastFactory;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PullRefreshListView extends LinearLayout implements OnClickListener,ListViewTouchListener, ResponseListener{
	public static final int[] NOTHING = new int[] { 0 };
	public static final int HTTP_FRESH_CODE = 1000;
	public static final int HTTP_MORE_CODE = 2000;
	public static final int PAGER_SIZE = 10;
	public static final int REFRESH_SECONDS = 5 * 60 * 1000;
	private static final int FLAG_UP = 0;
	private static final int FLAG_DOWN = 1;
	public static final int STATE_IDLE = 0;
	public static final int STATE_PULLING = 1;
	public static final int STATE_LOADING = 2;
	public static final int LOADING_CODE = -3;
	public static final int LOADING_MORE_CODE = -4;
	public static final int MSG_TOUCH_UP = 0x10; 
	public static final int MSG_TO_IDLE = 0x11; 
	public static final int MSG_TO_LOADING = 0x12;
	public static final int MSG_LOADING = 0x13;
	public static final int MSG_LOADING_DONE = 0x14;
	public static final int MSG_LOADING_MORE = 0x15;
	public static final int MSG_LOADING_MORE_DONE = 0x16;
	public static final int MSG_CLEAR_LISTVIEW_STATE = 0x17;
	public static final String TEXT_PULL = "下拉刷新";
	public static final String TEXT_RELEASE = "松手立即刷新";
	public static final String TEXT_REDRESH = "正在刷新数据";
	public static final String TEXT_LOADING_MORE = "加载更多";
	public static final String TEXT_IS_LAST_ITEM = "已无更多内容";
	public static final String TEXT_EMPT = "刷新成功，暂无数据";
	public static final String TEXT_FRESH_FAIL = "刷新失败，请下拉刷新";
	public static final String TEXT_TO_FRESH = "请下拉刷新数据";
	public static final String TEXT_LOADING = "正在加载...";
	public static final String KEY_NAME_ORDER1 = "key_order1";
	public static final String KEY_NAME_ORDER2 = "key_order2";
	public static final String KEY_NAME_ORDER3 = "key_order3";
	public static final String KEY_NAME_ORDER4 = "key_order4";
	public static final String KEY_NAME_ORDER5 = "key_order5";
	public static final String KEY_NAME_ORDER6 = "key_order6";
	public static final String KEY_NAME_NEARBY_ORDER = "key_nearby_order";
	public static final String KEY_NAME_HISTORY_ORDER = "key_history_order";
	public static final String KEY_NAME_TRANSACTION1 = "key_transaction1";
	public static final String KEY_NAME_TRANSACTION2 = "key_transaction2";
	public static final String KEY_NAME_TRANSACTION3 = "key_transaction3";
	public static final String KEY_NAME_PRICE = "key_price";
	private int state = STATE_IDLE;
	private LinearLayout headLayout;
	private int headHeight;
	private PullListView listView;
	private ImageView imgArrow;
	private GifImageView imgProgress;
	private RotateAnimation animRotate;
	private RotateAnimation animRotate1;
	private int flag = FLAG_UP;
	private TextView headMsg;
	private View footView;
	private RotateProgress imgProgressMore;
	private TextView tvMoreMsg;
	private DecelerateInterpolator deceInterpolator = new DecelerateInterpolator();
	private boolean isLoadingMore = false;
	private MyBaseAdapter adapter;
	private boolean byUserScroll = false;
	private int oldY = -1;
	private int newY = -1;
	private int top = 0;
	private int bottom  = 0;
	private boolean slideEnd = true;
	private boolean enablePullRefresh = true;
	private boolean showNodataHint = true;
	private OnLoadingListener<PullRefreshListView> loadingListener;
	private Activity mActivity;
	//private TextView tvTime;
	private String keyName = null;
	private int pagerIndex = 2;
	private View contentLayout;
	private LinearLayout nodataLayout;
	private TextView tvHintNodata;
	private GifImageView imgLoaddingGif;
	private View loaddingLayout;
	private boolean pullFreshSuccess = false;
	private MessageHandler httpHand;
	private int listViewBackgroundRes;
	private boolean failClearData = false;
	private boolean enableMoreButton = true;
	private int minPageSize;
	public interface NetPullRefreshOnScroll{
		public void refreshOnScroll(AbsListView view, int firstVisibleItem,
                                    int visibleItemCount, int totalItemCount);
	}
	private NetPullRefreshOnScroll netPullRefreshOnScroll;
	private Handler hand = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
				case MSG_CLEAR_LISTVIEW_STATE:
					clearChildPressedState();
					break;
				case MSG_TOUCH_UP:
					if(getLocationY() >= bottom){
						//变为加载状态
						if(state != STATE_LOADING){
							hand.sendEmptyMessage(MSG_TO_LOADING);
						}
					}else{
						//变为闲置状态
						hand.sendEmptyMessage(MSG_TO_IDLE);
					}
					break;
				case MSG_TO_IDLE:
					slideEnd = false;
					state = STATE_PULLING;
					int temp = getLocationY() - 10;
					headMsg.setText(TEXT_PULL);
					if(temp <= top){
						temp = top;
						setLocationY(temp);
						hand.removeMessages(MSG_TO_IDLE);
						state = STATE_IDLE;
						slideEnd = true;
						break;
					}
					setLocationY(temp);
					hand.sendEmptyMessage(MSG_TO_IDLE);
					break;
				case MSG_TO_LOADING:
					slideEnd = false;
					state = STATE_PULLING;
					int temp1 = getLocationY() - 10;
					if(temp1 <= bottom){
						temp1 = bottom;
						setLocationY(temp1);
						hand.removeMessages(MSG_TO_LOADING);
						hand.sendEmptyMessage(MSG_LOADING);
						state = STATE_LOADING;
						slideEnd = true;
						break;
					}
					setLocationY(temp1);
					hand.sendEmptyMessage(MSG_TO_LOADING);
					break;
				case MSG_LOADING:
					state = STATE_LOADING;
					if(listView.getCount() > 0){
						listView.setSelectionFromTop(0, 0);
					}
					setLocationY(bottom);
					imgArrow.clearAnimation();
					imgArrow.setVisibility(View.GONE);
					imgProgress.setVisibility(View.VISIBLE);
					imgProgress.play(true);
					
					/*loaddingLayout.setVisibility(View.VISIBLE);
					imgLoaddingGif.play(true);*/
					headMsg.setText(TEXT_REDRESH);
					hand.removeMessages(MSG_LOADING_DONE);
					if(loadingListener != null){
						loadingListener.onLoading(PullRefreshListView.this,httpHand.getHandler());
					}else{
						hand.sendEmptyMessageDelayed(MSG_LOADING_DONE, 1000);
					}
					//请求网络数据
					break;
				case MSG_LOADING_DONE:
					imgProgress.setVisibility(View.GONE);
					
					/*imgLoaddingGif.play(false);
					loaddingLayout.setVisibility(View.GONE);*/
					imgArrow.clearAnimation();
					imgArrow.setVisibility(View.VISIBLE);
					hand.sendEmptyMessage(MSG_TO_IDLE);
					if(adapter != null){
						adapter.notifyDataSetChanged();
					}
					break;
				case MSG_LOADING_MORE_DONE:
					imgProgressMore.setVisibility(View.GONE);
					isLoadingMore = false;
					if(adapter != null){
						adapter.notifyDataSetChanged();
					}
					break;
				case MSG_LOADING_MORE:
					if(isLoadingMore || !slideEnd){
						return;
					}
					isLoadingMore = true;
					tvMoreMsg.setText(TEXT_LOADING);
					imgProgressMore.setVisibility(View.VISIBLE);
					if(loadingListener != null ){
						loadingListener.onLoadingMore(PullRefreshListView.this,httpHand.getHandler(),pagerIndex);
					}else{
						hand.sendEmptyMessageDelayed(MSG_LOADING_MORE_DONE, 1000);
					}
					break;
			}
		}
	};
	
	public boolean isPullFreshSuccess(){
		return pullFreshSuccess;
	}
	
	public void setFailClearData(boolean failClearData){
		this.failClearData = failClearData;
	}
	
	public void hideNodataHint(){
		showNodataHint = false;
	}
	/**
	 * 是否启用‘加载更多按钮’
	 */
	public void setEnableMoreButton(boolean enable){
		enableMoreButton = enable;
		if(!enableMoreButton){
			removeFooterView();
		}
	}
	
	public void setEnablePullRefresh(boolean enable){
		enablePullRefresh = enable;
		if(enablePullRefresh){
			if( footView != null ){
				if(adapter.getList().size() == 0){
					footView.setVisibility(View.GONE);
					footView.setPadding(0, -headHeight, 0, 0);  
				}else{
					footView.setVisibility(View.VISIBLE);
					footView.setPadding(0, 0, 0, 0);  
				}
			}
		}else{
			if( footView != null ){
				footView.setVisibility(View.GONE);
				footView.setPadding(0, -headHeight, 0, 0);  
			}
		}
	}
	
	public View getChildView(int position){
		int count = adapter.getCount();
		if(count == 0 || position < 0 || position >= count){
			return null;
		}
		int visibyFirst = listView.getFirstVisiblePosition();
		int visibyLast = listView.getLastVisiblePosition();
		if(position >= visibyFirst && position <= visibyLast){
			return listView.getChildAt(position - visibyFirst);
		}else{
			return null;
		}
	}
	
	public void freshTime(boolean fromDatabase){
		String time = "未知";
		if(!TextUtils.isEmpty(keyName)){
			if(fromDatabase){
				time = Tools.getSysMapStringValue(mActivity, keyName);
				if(TextUtils.isEmpty(time)){
					time = "未知";
				}
			}else{
				time = getCurrentTime();
				Tools.saveSysMap(mActivity, keyName, time);
			}
		}
	//	tvTime.setText("上次更新："+time);
	}
	
	public void setKeyName(String key){
		keyName = key;
		Logger.logd("keyName = "+keyName);
		freshTime(true);
	}
	
	public String getLastFreshTime(){
		return Tools.getSysMapStringValue(mActivity, keyName);
	}
	
	public boolean shouldFresh(){
		if(!pullFreshSuccess){
			return true;
		}
		String oldFreshTime = getLastFreshTime();
		if(TextUtils.isEmpty(oldFreshTime)){
			return true;
		}
		long lastMillis = Tools.dateString2Millis(oldFreshTime);
		long currentMllis = Tools.getCurrentMillis();
		if(currentMllis - lastMillis > REFRESH_SECONDS){
			return true;
		}
		return false;
	}
	
	public void performLoading(){
		if(state == STATE_IDLE){
			hand.sendEmptyMessage(MSG_LOADING);
		}
	}
	
	public void loadBySilent(){
		if(loadingListener != null){
			loadingListener.onLoading(this, httpHand.getHandler());
		}
	}
	
	public void setDivider(int resId){
		listView.setDivider(mActivity.getResources().getDrawable(resId));
	}
	
	public void setListViewId(int id){
		listView.setId(id);
	}
	
	public void setDividerHeight(int height){
		listView.setDividerHeight(height);
	}
	public void restoreToIDLE(){
		hand.sendEmptyMessage(MSG_LOADING_DONE);
	}
	
	public String getCurrentTime(){
		String time ="";
		Date date = Calendar.getInstance().getTime();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		time = format.format(date);
		return time;
	}
	
	public void enableLoadMore(){
		if( footView != null){
			tvMoreMsg.setText(TEXT_LOADING_MORE);
			footView.setEnabled(true);
		}
	}
	
	public void disenableLoadMore(String text){
		if(footView != null){
			tvMoreMsg.setText(text);
			footView.setEnabled(false);
		}
	}
	public void setMinPageSize(int size){
		minPageSize = size;
	}
	
	private void onSuccess(int size,Message msg,boolean isLoadingMore){
			Logger.logd("size = "+size);
		if(!isLoadingMore){//下拉刷新
			if(size >= minPageSize){
				showFooterView(null, true);
			}else{
				if(size == 0){
					hideFooterView();
				}else{
					showFooterView(TEXT_IS_LAST_ITEM, false);
				}
			}
			pagerIndex = 2;
			freshTime(false);
			pullFreshSuccess = true;
			if(loadingListener != null ){
				clearData();
				String response = null;
				Bundle data = msg.getData();
				if(data != null){
					response = data.getString(HttpTools.KEY_RESPONSE_MSG);
				}
				loadingListener.refreshData(PullRefreshListView.this,false,msg,response);
				adapter.notifyDataSetChanged();
			}
		}else{//加载更多
			if(size >= minPageSize){
				showFooterView(null, true);
				pagerIndex ++;
			}else{
				showFooterView(TEXT_IS_LAST_ITEM, false);
			}
			if(loadingListener != null ){
				String response = null;
				Bundle data = msg.getData();
				if(data != null){
					response = data.getString(HttpTools.KEY_RESPONSE_MSG);
				}
				loadingListener.refreshData(PullRefreshListView.this,true,msg,response);
				adapter.notifyDataSetChanged();
			}
		}
	}
	
	public void freshPullListView(){
		if(adapter.getCount() == 0){
			hideFooterView();
		}
	}
	
	public PullRefreshListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs);
		init(context,attrs);
	}

	public PullRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context,attrs);
	}

	public PullRefreshListView(Context context) {
		super(context);
		init(context,null);
	}
	
	@Override
	public Handler getHandler(){
		return hand;
	}
	
	public void setOnLoadingListener(OnLoadingListener<PullRefreshListView> l){
		loadingListener = l;
	}
	
	@Override
	public void setOnTouchListener(OnTouchListener l){
		listView.setOnTouchListener(l);
	}
	
	public void init(Context con ,AttributeSet attr){
		minPageSize = PAGER_SIZE; 
		mActivity = (Activity)con;
		httpHand = new MessageHandler(con);
		httpHand.setResponseListener(this);
		LayoutInflater inflater = LayoutInflater.from(con);
		inflater.inflate(R.layout.pull_refresh_view_layout, this);
		headLayout = (LinearLayout)findViewById(R.id.head_linear_layout);
		contentLayout = findViewById(R.id.content_layout);
		nodataLayout = (LinearLayout)findViewById(R.id.nodata_layout);
		tvHintNodata = (TextView)findViewById(R.id.tv_hint_nodata);
		loaddingLayout = findViewById(R.id.loadding_hint_layout);
		imgLoaddingGif = (GifImageView)findViewById(R.id.img_loadding_gif);
		listView = (PullListView)findViewById(R.id.pull_fresh_listView);
		imgArrow = (ImageView)findViewById(R.id.img);
		imgProgress = (GifImageView)findViewById(R.id.progressBar);
		headMsg = (TextView) findViewById(R.id.txt_msg);
		//tvTime = (TextView)findViewById(R.id.txt_time);
		animRotate = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF,
				0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animRotate.setDuration(200);
		animRotate.setFillAfter(true);
		animRotate1 = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF,
				0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animRotate1.setDuration(200);
		animRotate1.setFillAfter(true);
		listView.setListViewTouchListener(this);
		imgProgress.measure(0, 0);
		headLayout.measure(0, 0);
		headHeight = headLayout.getMeasuredHeight();
		bottom = headHeight;
		footView = inflater.inflate(R.layout.foot_view_layout, null);
		tvMoreMsg = (TextView)footView.findViewById(R.id.tv_foot_msg);
		imgProgressMore = (RotateProgress)footView.findViewById(R.id.progressBar_more);
		footView.setLayoutParams(new AbsListView.LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		footView.measure(0, 0);
		final int height = footView.getMeasuredHeight();
		imgProgressMore.setVisibility(View.GONE);
		footView.setOnClickListener(this);
		listView.addFooterView(footView);
		showFooterView(null, true);
		hideFooterView();
		listView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				if(scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
					byUserScroll = true;
				}else if(scrollState == OnScrollListener.SCROLL_STATE_IDLE){
					byUserScroll = false;
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (netPullRefreshOnScroll != null) {//获取onScroll里面的参数
					netPullRefreshOnScroll.refreshOnScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
				}
				int count = listView.getFooterViewsCount();
				if(count <= 0){
					return;
				}
				int count1 = listView.getChildCount();
				if(count1 < PullRefreshListView.PAGER_SIZE){
					return;
				}
				if(footView.isEnabled() && byUserScroll && !isLoadingMore && visibleItemCount > 0){
					int lastPosition = listView.getLastVisiblePosition();
					int listViewHeight = listView.getMeasuredHeight();
					int y = listView.getChildAt(count1 -1).getBottom();
					if(lastPosition == totalItemCount - 1 && y <= listViewHeight + height/3){
						hand.sendEmptyMessage(MSG_LOADING_MORE);
					}
				}
			}
		});
		initCustomAttrs(con,attr);
		if(listViewBackgroundRes == 0){
			listViewBackgroundRes = R.color.white;
		}
		nodataLayout.setBackgroundResource(listViewBackgroundRes);
		listView.setBackgroundResource(listViewBackgroundRes);
		setKeyName(con.getClass().getName());
	}
	public void setNetPullRefreshOnScroll(NetPullRefreshOnScroll l){
		netPullRefreshOnScroll = l;
	}
	public void initCustomAttrs(Context con, AttributeSet attr){
		if(attr != null){
			TypedArray typeArray = con.obtainStyledAttributes(attr, R.styleable.PullRefreshListView);
			if( typeArray.hasValue(R.styleable.PullRefreshListView_divider)){
				Drawable drawable = typeArray.getDrawable(R.styleable.PullRefreshListView_divider);
				listView.setDivider(drawable);
			}
			if( typeArray.hasValue(R.styleable.PullRefreshListView_dividerHeight)){
				int dividerHeight = (int)typeArray.getDimension(R.styleable.PullRefreshListView_dividerHeight, -1);
				if(dividerHeight > 0){
					setDividerHeight(dividerHeight);
				}
			}
			if(typeArray.hasValue(R.styleable.PullRefreshListView_background_color)){
				listViewBackgroundRes = typeArray.getResourceId(R.styleable.PullRefreshListView_background_color,R.color.white);
			}
			typeArray.recycle();
		}
	}
	public void setOnItemClickListener(OnItemClickListener l){
		listView.setOnItemClickListener(l);
	}
	
	public void addHeaderView(View headerView){
		headerView.setLayoutParams(new AbsListView.LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		listView.addHeaderView(headerView);
	}
	
	public void hideFooterView(){
		if(showNodataHint){
			listView.setBackgroundColor(0x00000000);
			nodataLayout.setVisibility(View.VISIBLE);
		}else{
			listView.setBackgroundResource(listViewBackgroundRes);
			nodataLayout.setVisibility(View.GONE);
		}
		if( footView != null ){
			footView.setEnabled(false);
			footView.setVisibility(View.GONE);
			footView.setPadding(0, -headHeight, 0, 0);  
		}
	}
	
	public void removeFooterView(){
		if( footView != null && listView.getFooterViewsCount() > 0 ){
			listView.removeFooterView(footView);
			footView = null;
		}
	}
	
	public void showFooterView(String str,boolean enable){
		listView.setBackgroundResource(listViewBackgroundRes);
		nodataLayout.setVisibility(View.GONE);
		if(footView != null ){
			footView.setVisibility(View.VISIBLE);
			footView.setPadding(0, 0, 0, 0); 
		}
		if(enable){
			enableLoadMore();
		}else{
			if(showNodataHint){
				disenableLoadMore(str);
			}else{
				hideFooterView();
			}
		}
		if(adapter != null){
			adapter.notifyDataSetChanged();
		}
	}
	
	public void clearChildPressedState(){
		Drawable selector = listView.getSelector();
		listView.setPressed(false);
		for(int i = 0; i < listView.getChildCount(); i++){
			listView.getChildAt(i).setPressed(false);
		}
		if( selector != null){
			selector.setState(NOTHING);
		}
	}
	
	public void setNodataText(String text){
		tvHintNodata.setText(text);
	}
	
	public void setSelectionFromTop(int index , int top){
		listView.setSelectionFromTop(index, top);
	}
	
	public void hideScrollBar(){
		listView.setVerticalScrollBarEnabled(false);
	}
	
	public void clearData(){
		if(adapter != null && adapter.getList() != null){
			adapter.getList().clear();
			adapter.notifyDataSetChanged();
		}
	}
	
	public List getList(){
		if(adapter != null){
			return adapter.getList();
		}
		return null;
	}
	
	public void notifyDataSetChanged(){
		if(adapter != null){
			adapter.notifyDataSetChanged();
		}
		int size = adapter.getCount();
		if(size >= minPageSize){
			showFooterView(null, true);
		}else{
			if(size == 0){
				hideFooterView();
			}else{
				showFooterView(TEXT_IS_LAST_ITEM, false);
			}
		}
	}
	
	public PullRefreshListView setAdapter(MyBaseAdapter adapter){
		this.adapter = adapter;
		int size = adapter.getCount();
		if(size >= minPageSize){
			showFooterView(null, true);
		}else{
			if(size == 0){
				hideFooterView();
			}else{
				showFooterView(TEXT_IS_LAST_ITEM, false);
			}
		}
		listView.setAdapter(adapter);
		return this;
	}
	public float getDistance(int margin){
		if(margin >= 10* bottom){
			return 0;
		}
		float k = margin * 1.0f;
		float y = k/(10* bottom);
		float x = deceInterpolator.getInterpolation(y);
		return 1 - x;
		
	}
	
	public void initAllData(){
		headMsg.setText(TEXT_PULL);
		state = STATE_IDLE;
		setLocationY(top);
	}
	
	public void setLocationY(int y){
		contentLayout.scrollTo(0,-y);
	}
	
	public int getLocationY(){
		return -contentLayout.getScrollY();
	}
	
	public boolean canPullToRefresh(int distance) {
		boolean result= false;
        View firstChild = listView.getChildAt(0); 
        if (firstChild != null) {  
            int firstVisiblePos = listView.getFirstVisiblePosition();  
            if (firstVisiblePos == 0 && firstChild.getTop() == 0) { 
            	if(getLocationY() <= top && distance <= 0){
            		setLocationY(top);
            		result = false;
            	}else{
            		result = true;
            	}
            }else{
            	result = false;
            }
        } else {  
            // 如果ListView中没有元素，也应该允许下拉刷新  
        	result = true;  
        }  
        return result;
    }

	public boolean canScrollUp(){
		if(state == STATE_LOADING  || getLocationY() == top){
			return true;
		}
		return false;		
	}
	
	public int getState(){
		return state;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(!isLoadingMore){
			hand.sendEmptyMessage(MSG_LOADING_MORE);
		}
	}

	public void setOldY( int y){
		oldY = y;
	}
	@Override
	public boolean onListViewTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if(!enablePullRefresh){
			return false;
		}
		int action = event.getAction();
		int distance = 0;
		if(!slideEnd){
			return true;
		}
		if(state != STATE_IDLE && state != STATE_PULLING){
			return false;
		}
		switch(action){
			case MotionEvent.ACTION_DOWN:
				newY = (int)event.getRawY();
				oldY = newY;
				break;
			case MotionEvent.ACTION_MOVE:
				newY = (int)event.getRawY();
				if(oldY < 0){
					oldY = newY;
					return true;
				}
				distance =  newY - oldY;
				oldY = newY;
				if(state == STATE_LOADING  && distance > 0){
					return true;
				}
				if(canPullToRefresh(distance)){
					state = STATE_PULLING;
					clearChildPressedState();
					int temp = 0;
					if(distance > 0 && getLocationY() > bottom){
						float x = getDistance(getLocationY() - bottom);
						temp = (int)(getLocationY()+x*distance);
					}else{
						temp = (int)(getLocationY()+distance);
					}
					if(temp <= top){
						temp = top;
					}
					setLocationY(temp);
					if(temp >= bottom){
						if(flag != FLAG_DOWN){
							flag = FLAG_DOWN;
							//imgArrow.clearAnimation();
							headMsg.setText(TEXT_RELEASE);
							imgArrow.startAnimation(animRotate);
						}
					}else{
						if(flag != FLAG_UP){
							flag = FLAG_UP;
							//imgArrow.clearAnimation();
							headMsg.setText(TEXT_PULL);
							imgArrow.startAnimation(animRotate1);
						}
					}
					return true;
				}else{
					return false;
				}
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				boolean result = touchUp();
				hand.sendEmptyMessageDelayed(MSG_CLEAR_LISTVIEW_STATE, 150);
				return result;
		}
		return false;
	}  
	
	public boolean touchUp(){
		oldY = -1;
		int y = getLocationY();
		if(y > top){
			if(y >= bottom){
				//变为加载状态
				if(state != STATE_LOADING){
					state = STATE_PULLING;
					hand.sendEmptyMessage(MSG_TO_LOADING);
				}else{
					return true;
				}
			}else{
				//变为闲置状态
				hand.sendEmptyMessage(MSG_TO_IDLE);
			}
			return true;
		}else{
			initAllData();
			return false;
		}
	}

	@Override
	public void onRequestStart(Message msg, String hintMsg) {
	}

	@Override
	public void onSuccess(Message msg, String jsonString,String message) {
		if(!enablePullRefresh){
			return;
		}
		boolean isLoadingMore = msg.arg1 == HTTP_MORE_CODE;
		int size = 0;
		String contentString = HttpTools.getContentString(jsonString);
		if( contentString !=null){
			 size = HttpTools.getDataCount(contentString);
		}else {
			 size = HttpTools.getDataCount(jsonString);
		}
		
		onSuccess(size,msg,isLoadingMore);
		if(isLoadingMore){
			hand.sendEmptyMessage(MSG_LOADING_MORE_DONE);
		}else{
			hand.sendEmptyMessage(MSG_LOADING_DONE);
		}
	}
	
	private void loadEmptyData(){
		if(adapter != null && adapter.getList() != null){
			adapter.getList().clear();
			notifyDataSetChanged();
		}
	}
	@Override
	public void onFail(Message msg, String message) {
		if(!enablePullRefresh){
			return;
		}
		ToastFactory.showToast(mActivity, message);
		boolean isLoadingMore = msg.arg1 == HTTP_MORE_CODE;
		if(msg.what == HttpTools.RESPONSE_ERROR){
			if(isLoadingMore){
				showFooterView("加载更多", true);
			}else{
				if(failClearData){
					loadEmptyData();
				}
			}
		}else{
			int size = 0;
			Bundle bundle = msg.getData();
			if(bundle != null){
				size = HttpTools.getDataCount(
						bundle.getString(HttpTools.KEY_RESPONSE_MSG));
			}
			onSuccess(size,msg,isLoadingMore);
			/*if(isLoadingMore){
				hideFooterView();
			}else{
				loadEmptyData();
			}*/
		}
		if(isLoadingMore){
			hand.sendEmptyMessage(MSG_LOADING_MORE_DONE);
		}else{
			hand.sendEmptyMessage(MSG_LOADING_DONE);
		}
	}
	
	public void setSelector(int resID){
		listView.setSelector(resID);
	}

}
