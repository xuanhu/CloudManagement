package com.tg.cloudmanagement.inter;

import android.os.Handler;
import android.os.Message;

public interface OnLoadingListener<T>{
	public void onLoading(T t, Handler hand);
	public void refreshData(T t, boolean isLoadMore, Message msg, String response);
	public void onLoadingMore(T t, Handler hand, int pageIndex);
}