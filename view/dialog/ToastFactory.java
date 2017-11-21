package com.tg.cloudmanagement.view.dialog;

import com.tg.cloudmanagement.R;
import com.tg.cloudmanagement.util.Tools;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ToastFactory {
	private static Toast toast;
	private static Toast bottomToast;
	private static TextView tv;
	private static TextView bottomTv;

	public static void showToast(Context con, String text) {
		if(TextUtils.isEmpty(text)){
			return;  
		}
		if (toast == null) {
			DisplayMetrics metrics = Tools.getDisplayMetrics(con);
			toast = new Toast(con);
			View v = LayoutInflater.from(con).inflate(R.layout.toast_layout,
					null);
			tv = (TextView) v.findViewById(R.id.message);
			LinearLayout.LayoutParams p = (LinearLayout.LayoutParams)tv.getLayoutParams();
			//p.width = (int) (metrics.widthPixels - 140 * metrics.density);
			//p.height = (int) (100 * metrics.density);
			p.width = LayoutParams.WRAP_CONTENT;
			p.height = LayoutParams.WRAP_CONTENT;
			tv.setMaxHeight((int) (200 * metrics.density));
			tv.setMaxWidth((int)(metrics.widthPixels - 80*metrics.density));
			tv.setMinHeight((int) (60 * metrics.density));
			tv.setMinWidth((int) (100 * metrics.density));
			tv.setLayoutParams(p);

			toast.setView(v);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.setDuration(Toast.LENGTH_SHORT);
		}
		tv.setText(text);
		toast.show();
	}
	
	public static void showBottomToast(Activity con, String text) {
		if (bottomToast == null) {
			DisplayMetrics metrics = Tools.getDisplayMetrics(con);
			bottomToast = new Toast(con);
			View v = LayoutInflater.from(con).inflate(R.layout.toast_bottom_layout,
					null);
			bottomTv = (TextView) v.findViewById(R.id.message);
			LinearLayout.LayoutParams p = (LinearLayout.LayoutParams)bottomTv.getLayoutParams();
			p.width = LayoutParams.WRAP_CONTENT;
			p.height = LayoutParams.WRAP_CONTENT;
			bottomTv.setMaxHeight((int) (200 * metrics.density));
			bottomTv.setMaxWidth((int)(metrics.widthPixels - 80*metrics.density));
			bottomTv.setLayoutParams(p);
			bottomToast.setView(v);
			bottomToast.setDuration(Toast.LENGTH_SHORT);
		}
		bottomTv.setText(text);
		bottomToast.show();
	}
}
