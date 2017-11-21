package com.tg.cloudmanagement;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.tg.cloudmanagement.base.BaseActivity;
import com.tg.cloudmanagement.view.dialog.DialogFactory;
import com.tg.cloudmanagement.view.dialog.ToastFactory;
import com.tg.cloudmanagement.zxing.camera.CameraManager;
import com.tg.cloudmanagement.zxing.decoding.CaptureActivityHandler;
import com.tg.cloudmanagement.zxing.decoding.InactivityTimer;
import com.tg.cloudmanagement.zxing.view.ViewfinderView;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
/**
 * 扫描二维码页面
 * @author Administrator
 *
 */
public class MipcaActivityCapture extends BaseActivity implements Callback ,OnClickListener{
	private static URL url;
	private static HttpURLConnection con;
	private static int state = -1;
	public static final String KEY_TEXT1 = "text1";
	public static final String KEY_TEXT2 = "text2";
	public static final String TEXT_OPEN = "打开闪光灯";
	public static final String TEXT_CLOSE = "关闭闪光灯";
	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.50f;
	private boolean vibrate;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ViewUtil.addTopView(getApplicationContext(), this,
		// R.string.scan_card);
		CameraManager.init(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		Intent data = getIntent();
		if (data != null) {
			String text1 = data.getStringExtra(KEY_TEXT1);
			String text2 = data.getStringExtra(KEY_TEXT2);
			if (!TextUtils.isEmpty(text1)) {
				viewfinderView.setText1(text1);
			}
			if (!TextUtils.isEmpty(text2)) {
				viewfinderView.setText2(text2);
			}
		}
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (viewfinderView != null) {
			viewfinderView.setPause(false);
		}
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		if (CameraManager.get().isFlashOpen()) {
			headView.setRightText(TEXT_CLOSE);
		} else {
			headView.setRightText(TEXT_OPEN);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;

	}

	@Override
	protected void onPause() {
		super.onPause();
		pause();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	/**
	 * 解码开门
	 * 
	 * @param result
	 * @param barcode
	 */
	public void handleDecode(Result result, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		String resultString = result.getText();
		if (TextUtils.isEmpty(resultString)) {
			showFailMessage("Scan failed!");
		} else {
			/*RequestConfig config = new RequestConfig(this,
					HttpTools.SET_OPEN_DOOR, "请求开门");
			config.handler = mHand;
			HttpTools.httpPost("set.open.door", config, new RequestParams(
					"uid", UserInfo.userId).put("code", resultString));*/
			Intent intent = new Intent(this,MyBrowserActivity.class);
			String url = isConnect(resultString);
			if(url == null || "".equals(url)){
				intent.putExtra(MyBrowserActivity.KEY_TITLE, "社区通");
				intent.putExtra(MyBrowserActivity.KEY_HTML_TEXT, resultString);
			}else{
				intent.putExtra(MyBrowserActivity.KEY_URL,url);
			}
			startActivity(intent);
		}
		 MipcaActivityCapture.this.finish();
	}
	/**
	 * 功能：检测当前URL是否可连接或是否有效, 描述：最多连接网络 5 次, 如果 5 次都不成功，视为该地址不可用
	 * 
	 * @param urlStr 指定URL网络地址
	 * @return URL
	 */
	public static synchronized String isConnect(String urlStr) {
		Pattern pattern = Pattern
				.compile("((http[s]{0,1}|ftp)://[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)|((www.)|[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)");
		Matcher matcher = pattern.matcher(urlStr);
		StringBuffer buffer = new StringBuffer();
		while (matcher.find()) {
			buffer.append(matcher.group());
		}
		return buffer.toString();
	}
	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		// TODO Auto-generated method stub
		super.onSuccess(msg, jsonString, hintString);
		ToastFactory.showToast(this, "开门成功");
		finish();
	}

	@Override
	public void onFail(Message msg, String hintString) {
		// TODO Auto-generated method stub
		DialogFactory.getInstance().hideTransitionDialog();
		showFailMessage(hintString);
	}

	private void showFailMessage(String msg) {
		/*Intent intent = new Intent(this, MessageDialogActivity.class);
		if (TextUtils.isEmpty(msg)) {
			msg = "开门失败";
		}
		intent.putExtra(MessageDialogActivity.KEY_TEXT, msg);
		startActivity(intent);*/
	}

	private void pause() {
		if (viewfinderView != null) {
			viewfinderView.setPause(true);
		}
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
		if (CameraManager.get().isFlashOpen()) {
			headView.setRightText(TEXT_CLOSE);
		} else {
			headView.setRightText(TEXT_OPEN);
		}
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			ToastFactory.showToast(this, "摄像头打开失败，请在应用管理选择允许打开摄像头");
			finish();
			return;
		} catch (RuntimeException e) {
			ToastFactory.showToast(this, "摄像头打开失败，请在应用管理选择允许打开摄像头");
			finish();
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		@Override
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	@Override
	public View getContentView() {
		return getLayoutInflater().inflate(
				R.layout.activity_mipca_activity_capture, null);
	}

	@Override
	public String getHeadTitle() {
		headView.setRightText(TEXT_OPEN);
		headView.setRightTextColor(getResources().getColor(R.color.white));
		headView.setListenerRight(this);
		return "扫一扫";
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		CameraManager.get().openOrCloseFlash();
		if (CameraManager.get().isFlashOpen()) {
			headView.setRightText(TEXT_CLOSE);
		} else {
			headView.setRightText(TEXT_OPEN);
		}
	}

}

