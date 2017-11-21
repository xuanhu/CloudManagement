package com.tg.cloudmanagement;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import com.tg.cloudmanagement.MyBrowserActivity;
import com.tg.cloudmanagement.R;
import com.tg.cloudmanagement.net.HttpTools;
import com.tg.cloudmanagement.net.ResponseData;
import com.tg.cloudmanagement.util.IsTodayutil;
import com.tg.cloudmanagement.util.Tools;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
/**
 * 弹出提现新消息页面
 * @author Administrator
 *
 */
public class AlertActivity extends Activity implements OnClickListener {
	public static String SERVERMESSAGE="serverMessage";
	private String serverMessage;
	private TextView tvTitle,tvTime,tvcontext;
	private TextView btnSubmit;
	private ImageView  ivClose;
	private Vibrator vibrator;
	private String weiappname,content,homePushTime,btime,keystr;
	private int ringerMode;
	private int MUTE = 0;
	private int VIBRATE = 1;
	private int SOUND = 2;
	private MediaPlayer player;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_alert);
		Intent intent = getIntent();
		if(intent != null){
			serverMessage= intent.getStringExtra(SERVERMESSAGE);
		}
		AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        ringerMode = am.getRingerMode();//0为静音，1为震动，2为响铃
        if (ringerMode == MUTE) {//静音
            //无需任何操作
        }
        if (ringerMode == VIBRATE) {//振动
        	 setVibration();
        }
        if (ringerMode == SOUND) {//声音
        	 setVibration();
        	 setVoice();
        }
		initView();
	}


	/**
	 * 播放声音
	 * 
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	private MediaPlayer setVoice(){
		Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		player = new MediaPlayer();
		try {
			player.setDataSource(this, alert);
			final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			if (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) != 0) {
				player.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
				player.setLooping(false);
				player.prepare();
				player.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return player;
	}

	/**
	 * 设置振动
	 */
	private void setVibration() {
		vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(new long[] {100,2000,}, -1);
	}
	/**
	 *初始化
	 */
	private void initView() {
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTime = (TextView) findViewById(R.id.tv_time);
		tvcontext = (TextView) findViewById(R.id.tv_content);
		btnSubmit = (TextView) findViewById(R.id.btn_submit);
		ivClose = (ImageView) findViewById(R.id.iv_close);
		btnSubmit.setOnClickListener(this);
		ivClose.setOnClickListener(this);
		/**
		 * 解析json数据，写入弹出框
		 */
		String jsonString = HttpTools.getContentString(serverMessage);
		if(jsonString != null){
			ResponseData data = HttpTools.getResponseData(jsonString);
			weiappname= data.getString("weiappname");
			content= data.getString("content");
			keystr= data.getString("keystr");
			homePushTime= data.getString("homePushTime");
			btime= data.getString("btime");
		}
		tvTitle.setText(weiappname);
		tvcontext.setText(content);
		if(homePushTime != null){
			IsTodayutil isTodayUtil = new IsTodayutil();
			try {
				boolean istoday = isTodayUtil.IsToday(homePushTime);
				if(istoday){//表示是今天（只显示时间或者刚刚）
					Date dt = new Date();
					Long time = dt.getTime();//当前时间
					long servicetime = Tools.dateString2Millis(homePushTime);//获取到的时间
					long timestamp = time - servicetime;//当前时间和服务时间差
					long minutes = 10*60*1000;//10分钟转换为多少毫秒
					if( timestamp > minutes){
						String nowTime = Tools.getSecondToString(servicetime);
						tvTime.setText(nowTime);
					}else {
						tvTime.setText("刚刚");
					}
				}else{
					Date dt = new Date();
					Long time = dt.getTime();//当前时间
					String newYear = Tools.getSimpleDateToString(time);//
					String serviceYear = homePushTime.substring(0,homePushTime.indexOf(" "));
					if(newYear.substring(0,4).equals(serviceYear.substring(0,4))){//表示消息时间是今年
						tvTime.setText(serviceYear.substring(5,serviceYear.length()));
					}else{
						tvTime.setText(serviceYear);
					}
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Tools.saveDateInfo(this,btime);
		//取消该实例的震动
		if(vibrator != null){
			vibrator.cancel(); 
		}
		if(player != null){
			player.stop();
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_submit:
			Tools.saveDateInfo(this,btime);
			Intent intent = new Intent(this,MyBrowserActivity.class);
			intent.putExtra(MyBrowserActivity.KEY_URL,keystr);
			startActivity(intent);
			finish();
			//取消该实例的震动
			if(vibrator != null){
				vibrator.cancel(); 
			} 
			if(player != null){
				player.stop();
			}
			break;
		case R.id.iv_close:
			Tools.saveDateInfo(this,btime);
			finish();
			//取消该实例的震动
			if(vibrator != null){
				vibrator.cancel(); 
			}
			if(player != null){
				player.stop();
			}
			break;
		}
	}

}
