package com.tg.cloudmanagement.inter;

import com.tg.cloudmanagement.net.MessageHandler;
import com.tg.cloudmanagement.view.spinnerwheel.WheelVerticalView;

import android.os.Message;


public interface NetworkRequestListener {
	public void onRequest(MessageHandler msgHand);
	public void onSuccess(WheelVerticalView wheelView, Message msg, String response);
	public void onFail(Message msg, String message);
}
