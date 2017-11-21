package com.tg.cloudmanagement.view.dialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.tg.cloudmanagement.R;
import com.tg.cloudmanagement.base.BaseActivity;
import com.tg.cloudmanagement.inter.ResultCallBack;
import com.tg.cloudmanagement.log.Logger;
import com.tg.cloudmanagement.net.HttpTools;
import com.tg.cloudmanagement.object.SlideItemObj;
import com.tg.cloudmanagement.util.Tools;
import com.tg.cloudmanagement.view.CameraView;
import com.tg.cloudmanagement.view.RotateProgress;
import com.tg.cloudmanagement.view.spinnerwheel.SlideSelectorView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class DialogFactory implements ResultCallBack{
	public static final int PIC_PHOTO_BY_ALBUM = Integer.MAX_VALUE;
	public static final int PIC_PHOTO_BY_CAMERA = PIC_PHOTO_BY_ALBUM -1;
	private String PHOTO_NAME = "wisdomPark.jpg";
	private String TAKE_PHOTO_PATH = "";
	private static DialogFactory factory = null;
	private boolean isStorageMemory = false;
	private String cropPath;
	private CameraView cameraView;
	private int groupPosition;
	private int childPosition;
	private int position;
	
	private AlertDialog dialog;
	private Activity dialogActivity;
	
	private AlertDialog photoDialog;
	private BaseActivity photoDialogActivity;
	
	private AlertDialog selectorDialog;
	private Activity selectorDialogActivity;
	
	private AlertDialog transitionDialog;
	private Activity transitionDialogActivity;
	
	private AlertDialog msgDialog;
	private Activity msgDialogActivity;
	
	private TextView msgTvContent;
	private Button msgBtnOk;
	private OnClickListener msgOkListener;
	
	private TextView tvMsg;
	private RotateProgress progressBar;
	private TextView tvContent;
	private OnClickListener okListener;
	private OnClickListener cancelListener;
	
	private Button btnOk;
	private Button btnCancel;
	
	private SlideSelectorView slideSelectorView;
	private DialogFactory(){
	}
	
	public static DialogFactory getInstance(){
		if(factory == null){
			factory = new DialogFactory();
		}
		return factory;
	}
	public void showTransitionDialog(final Activity activity, String text, final Object tag,final int requestCode) {
		if (transitionDialog == null || transitionDialogActivity != activity) {
			transitionDialogActivity = activity;
			DisplayMetrics metrics = Tools.getDisplayMetrics(activity);
			transitionDialog = new AlertDialog.Builder(activity).create();
			transitionDialog.setCanceledOnTouchOutside(false);
			transitionDialog.setCancelable(true);
			transitionDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					if(requestCode == HttpTools.POST_IMAG){//上传照片
						HttpTools.cancelPost();
					}else{
						if(tag != null){
							HttpTools.cancelRequest(tag);
						}
					}
					if(activity instanceof BaseActivity){
						((BaseActivity)activity).onCancel(tag, requestCode);
					}
				}
			});
			transitionDialog.show();
			Window window = transitionDialog.getWindow();
			LinearLayout layout = (LinearLayout) LayoutInflater.from(activity)
					.inflate(R.layout.transition_dialog_layout, null);
			tvMsg = (TextView) layout.findViewById(R.id.dialog_hint);
			progressBar = (RotateProgress) layout.findViewById(R.id.progressBar);
			window.setContentView(layout);
			WindowManager.LayoutParams p = window.getAttributes();
			p.width = ((int) (metrics.widthPixels - 80 * metrics.density));
			p.height = (int) (120 * metrics.density);
			window.setAttributes(p);
		}
		if(TextUtils.isEmpty(text)){
			tvMsg.setVisibility(View.GONE);
		}else{
			tvMsg.setText(text);
			tvMsg.setVisibility(View.VISIBLE);
		}
		progressBar.setVisibility(View.VISIBLE);
		transitionDialog.show();
	}
	
	public void  showDialog(Activity activity, final OnClickListener okL,final OnClickListener cancelL,String content,String ok, String cancel) {
		this.okListener = okL;
		this.cancelListener = cancelL;
		if(dialog == null || dialogActivity != activity){
			dialogActivity = activity;
			DisplayMetrics metrics = Tools.getDisplayMetrics(activity);
			dialog = new AlertDialog.Builder(activity).create();
			dialog.setCancelable(false);
			Window window = dialog.getWindow();
			dialog.show();
			LinearLayout layout = (LinearLayout) LayoutInflater.from(activity)
					.inflate(R.layout.custom_dialog_layout, null);
			tvContent = (TextView) layout.findViewById(R.id.dialog_msg);
			btnOk = (Button) layout.findViewById(R.id.btn_ok);
			btnCancel = (Button) layout.findViewById(R.id.btn_cancel);
			btnOk.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(okListener != null){
						okListener.onClick(v);
					}
					dialog.dismiss();
				}
			});
			btnCancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(cancelListener != null){
						cancelListener.onClick(v);
					}
					dialog.dismiss();
				}
			});
			window.setContentView(layout);
			WindowManager.LayoutParams p = window.getAttributes();
			p.width = ((int) (metrics.widthPixels - 80 * metrics.density));
			//p.height = (int) (120 * metrics.density);
			window.setAttributes(p);
		}
		tvContent.setText(content);
		if(ok == null){
			ok = "确定";
		}
		if(cancel == null){
			cancel = "取消";
		}
		btnOk.setText(ok);
		btnCancel.setText(cancel);
		dialog.show();
		
	}
	
	public void hideTransitionDialog(){
		if(transitionDialogActivity == null || transitionDialogActivity.isFinishing()){
			return;
		}
		if(transitionDialog != null && transitionDialog.isShowing()){
			progressBar.setVisibility(View.INVISIBLE);
			try{
				transitionDialog.dismiss();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public void showSelectorDialog(final BaseActivity activity, String title,
			ArrayList<SlideItemObj> list1, ArrayList<SlideItemObj> list2,
			final SlideSelectorView.OnCompleteListener l, boolean sort) {
		if (selectorDialog == null || selectorDialogActivity != activity) {
			selectorDialogActivity = activity;
			selectorDialog = new AlertDialog.Builder(activity).create();
			selectorDialog.setCanceledOnTouchOutside(true);
			selectorDialog.show();
			Window window = selectorDialog.getWindow();
			slideSelectorView = new SlideSelectorView(
					activity);
			
			slideSelectorView
					.setOnCancelListener(new SlideSelectorView.OnCancelListener() {
						@Override
						public void onCancel() {
							selectorDialog.dismiss();
						}
					});
			
			window.setContentView(slideSelectorView);
			WindowManager.LayoutParams p = window.getAttributes();
			DisplayMetrics metrics = Tools.getDisplayMetrics(activity);
			p.width = metrics.widthPixels;
			p.gravity = Gravity.BOTTOM;
			window.setAttributes(p);
		} else {
			selectorDialog.show();
		}
		slideSelectorView.setNeedSort(sort);
		slideSelectorView.setTitle(title);
		slideSelectorView.setList(list1, list2);
		slideSelectorView
		.setOnCompleteListener(new SlideSelectorView.OnCompleteListener() {
			@Override
			public void onComplete(SlideItemObj item1,
					SlideItemObj item2) {
				// TODO Auto-generated method stub
				selectorDialog.dismiss();
				if (l != null) {
					l.onComplete(item1, item2);
				}
			}
		});
	}
	
	protected void getImageFromAlbum() {
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");// 相片类型
		photoDialogActivity.startActivityForResult(intent, PIC_PHOTO_BY_ALBUM);
	}

	protected void getImageFromCamera() {
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		//M兆
		int maxMemorySize = maxMemory/(1024*1024);
    	Logger.logd("maxMemorySize = "+maxMemorySize+"Mb");
		if (android.os.Build.VERSION.SDK_INT <= 10 || maxMemorySize <= 32) {
			isStorageMemory = true;
			Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			photoDialogActivity.startActivityForResult(cameraintent,
					PIC_PHOTO_BY_CAMERA);
			Logger.logd("no SD card");
		} else {
			isStorageMemory = false;
			FileOutputStream out = null;
			try {
				out = photoDialogActivity.openFileOutput(PHOTO_NAME, Context.MODE_WORLD_WRITEABLE);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				if(out != null){
					out.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			TAKE_PHOTO_PATH = photoDialogActivity.getFilesDir().getAbsolutePath()+File.separator+PHOTO_NAME;
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			// 指定调用相机拍照后照片的储存路径
			//intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0); 
			intent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(new File(TAKE_PHOTO_PATH)));
			photoDialogActivity.startActivityForResult(intent,
					PIC_PHOTO_BY_CAMERA);
		}
	}
	
	public void showPhotoSelector(BaseActivity activity, CameraView cv,
			String cropPath, int position,int groupPosition,int childPosition){
		this.cropPath = cropPath;
		this.groupPosition = groupPosition;
		this.childPosition = childPosition;
		this.position = position;
		cameraView = cv;
		if (cv != null) {
			groupPosition = cv.getGroupPosition();
			childPosition = cv.getChildPosition();
			position = cv.getPosition();
			groupPosition = cv.getGroupPosition();
		}
		if (photoDialog == null || photoDialogActivity != activity) {
			photoDialogActivity = activity;
			photoDialogActivity.setResultCallBack(this);
			photoDialog = new AlertDialog.Builder(photoDialogActivity).create();
			photoDialog.show();
			View v = LayoutInflater.from(photoDialogActivity).inflate(
					R.layout.set_photo_dialog_layout, null);
			v.findViewById(R.id.take_photo).setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							getImageFromCamera();
							photoDialog.dismiss();
						}
					});
			v.findViewById(R.id.choose_photo).setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							getImageFromAlbum();
							photoDialog.dismiss();
						}
					});
			v.findViewById(R.id.cancel).setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							photoDialog.dismiss();
						}
					});
			Window window = photoDialog.getWindow();
			WindowManager.LayoutParams p = window.getAttributes();
			DisplayMetrics metrics = Tools.getDisplayMetrics(activity);
			p.width = metrics.widthPixels;
			p.gravity = Gravity.BOTTOM;
			window.setAttributes(p);
			window.setContentView(v);
		}
		photoDialog.show();
	}

	@Override
	public void onResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case PIC_PHOTO_BY_ALBUM:
			if (resultCode != Activity.RESULT_OK) {
				return;
			}
			Uri uri = data.getData();
			if (uri != null) {
				String filePath = Tools.getPathByUri(photoDialogActivity, uri);
				if (filePath == null) {
					if (data != null && data.getExtras() != null) {
						Bitmap btm = data.getExtras().getParcelable("data");
						Tools.compressImage(photoDialogActivity, btm, cropPath);
					}
				} else {
					recycleBitmap(data);
					// startCropPhoto(uri);
					Tools.saveImageToPath(photoDialogActivity, filePath,
							cropPath);
				}
				if (photoDialogActivity != null) {
					photoDialogActivity.returnData(cameraView,
							cameraView != null ? cameraView.getState() : null,
							groupPosition, childPosition, position,
							Tools.getSmallBitmap(cropPath), cropPath);
				}
			} else {
				if (data != null && data.getExtras() != null) {
					Bitmap btm = data.getExtras().getParcelable("data");
					Tools.compressImage(photoDialogActivity, btm, cropPath);
					if (photoDialogActivity != null) {
						photoDialogActivity.returnData(cameraView,
								cameraView != null ? cameraView.getState()
										: null, groupPosition, childPosition,
								position, Tools.getSmallBitmap(cropPath),
								cropPath);
					}
				}
			}
			break;
		case PIC_PHOTO_BY_CAMERA:
			if (resultCode != Activity.RESULT_OK) {
				return;
			}
			if(isStorageMemory){
				Bitmap btm = data.getExtras().getParcelable("data");
				Tools.compressImage(photoDialogActivity, btm, cropPath);
			}else{
				Tools.saveImageToPath(photoDialogActivity, TAKE_PHOTO_PATH,
						cropPath);
			}
			if (photoDialogActivity != null) {
				photoDialogActivity.returnData(cameraView,
						cameraView != null ? cameraView.getState()
								: null, groupPosition, childPosition,
								position, Tools.getSmallBitmap(cropPath),
								cropPath);
			}
			// startCropPhoto(Uri.fromFile(new File(takePhotoPath)));
			break;
		/*case GET_PHOTO_BY_CROP:
			if (resultCode != Activity.RESULT_OK) {
				return;
			}
			if (photoDialogActivity != null) {
				photoDialogActivity.returnData(cameraView,
						cameraView != null ? cameraView.getState() : null,
						groupPosition, childPosition, position,
						Tools.getSmallBitmap(cropPath), cropPath);
			}
			break;*/
		}
	}
	
	public void recycleBitmap(Bitmap btm) {
		if (btm != null && !btm.isRecycled()) {
			btm.recycle();
			btm = null;
		}
	}


	public void recycleBitmap(Intent data) {
		if (data != null && data.getExtras() != null) {
			Bitmap btm = data.getExtras().getParcelable("data");
			recycleBitmap(btm);
			btm = null;
		}
	}
	
	public void showMessageDialog(Activity activity, String content, String ok,
			OnClickListener okL) {
		this.msgOkListener = okL;
		if (msgDialog == null || msgDialogActivity != activity) {
			msgDialogActivity = activity;
			DisplayMetrics metrics = Tools.getDisplayMetrics(activity);
			msgDialog = new AlertDialog.Builder(activity).create();
			msgDialog.setCancelable(false);
			Window window = msgDialog.getWindow();
			msgDialog.show();
			LinearLayout layout = (LinearLayout) LayoutInflater.from(activity)
					.inflate(R.layout.msg_dialog_layout, null);
			msgTvContent = (TextView) layout.findViewById(R.id.dialog_msg);
			msgBtnOk = (Button) layout.findViewById(R.id.btn_ok);
			msgBtnOk.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (msgOkListener != null) {
						msgOkListener.onClick(v);
					}
					msgDialog.dismiss();
				}
			});
			window.setContentView(layout);
			WindowManager.LayoutParams p = window.getAttributes();
			p.width = ((int) (metrics.widthPixels - 80 * metrics.density));
			// p.height = (int) (120 * metrics.density);
			window.setAttributes(p);
		}
		if (content != null) {
			msgTvContent.setText(content);
		}
		if (ok != null) {
			msgBtnOk.setText(ok);
		}
		msgDialog.show();
	}
}
