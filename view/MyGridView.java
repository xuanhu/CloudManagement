package com.tg.cloudmanagement.view;

import android.app.Activity;
import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.GridView;

import com.tg.cloudmanagement.net.HttpTools;
import com.tg.cloudmanagement.net.MessageHandler;
import com.tg.cloudmanagement.net.MessageHandler.ResponseListener;
import com.tg.cloudmanagement.net.ResponseData;

/**
 * @Description: 解决在scrollview中只显示第一行数据的问题
 * @author http://blog.csdn.net/finddreams
 */
public class MyGridView extends GridView implements ResponseListener {
    public interface NetworkRequestListener {
        public void onRequest(MessageHandler msgHand);

        public void onSuccess(MyGridView gridView, Message msg, String response);
    }
    private NetworkRequestListener requestListener;
    private boolean isLoadding = false;
    private MessageHandler msgHandler;
    private Activity mActivity;

    public MyGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MyGridView(Context context) {
        super(context);
        initView(context);
    }

    public MyGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context con) {
        mActivity = (Activity) con;
        msgHandler = new MessageHandler(con);
        msgHandler.setResponseListener(this);
    }

    public void loaddingData() {
        if (!isLoadding) {
            if (requestListener != null) {
                isLoadding = true;
                requestListener.onRequest(msgHandler);
            }
        }
    }

    public void setNetworkRequestListener(NetworkRequestListener l) {
        requestListener = l;
    }

	/*@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}*/

    @Override
    public void onRequestStart(Message msg, String hintString) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        // TODO Auto-generated method stub
        String Content = HttpTools.getContentString(jsonString);
        if (Content != null) {
            ResponseData data = HttpTools.getResponseData(Content);
            if (data == null || data.length == 0) {
                isLoadding = false ;
            } else {
                if (requestListener != null) {
                    requestListener.onSuccess(this, msg, jsonString);
                }
            }
        }
    }

    @Override
    public void onFail(Message msg, String hintString) {

    }
}

