package com.tg.cloudmanagement;

import im.fir.sdk.FIR;
import im.fir.sdk.VersionCheckCallback;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.tg.cloudmanagement.application.CloudManagementApplication;
import com.tg.cloudmanagement.base.BaseActivity;
import com.tg.cloudmanagement.database.SharedPreferencesTools;
import com.tg.cloudmanagement.object.ViewConfig;
import com.tg.cloudmanagement.updateapk.ApkInfo;
import com.tg.cloudmanagement.updateapk.UpdateManager;
import com.tg.cloudmanagement.view.MessageArrowView;
import com.tg.cloudmanagement.view.MessageArrowView.ItemClickListener;
import com.tg.cloudmanagement.view.dialog.DialogFactory;

import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
/**
 * 更多设置
 * @author Administrator
 *
 */
public class SettingActivity extends BaseActivity implements ItemClickListener {
	private MessageArrowView mineInfoZone;
	private LinearLayout llExit;
	private RelativeLayout rlUpApk;
	private String firToken = "64a33a0c08c19980142d4f42e1df05d0";// fir.im 的用户
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}
	/**
	 * 初始化控件
	 */
	private void initView() {
		mineInfoZone=(MessageArrowView) findViewById(R.id.mine_info_zone);
		llExit = (LinearLayout) findViewById(R.id.exit);
		rlUpApk = (RelativeLayout) findViewById(R.id.rl_upApk);
		rlUpApk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/**
				 * 版本检测更新
				 */
				FIR.checkForUpdateInFIR(firToken, new VersionCheckCallback() {
					@Override
					public void onSuccess(String versionJson) {
						try {
							JSONObject obj = new JSONObject(versionJson);
							String apkVersion = obj.getString("versionShort");
							int apkCode = obj.getInt("version");
							String apkSize = obj.getString("updated_at");
							String apkName = obj.getString("name");
							String downloadUrl = obj.getString("installUrl");
							String apkLog = obj.getString("changelog");
							ApkInfo apkinfo = new ApkInfo(downloadUrl, apkVersion,apkSize, apkCode, apkName, apkLog);
							if (apkinfo != null) {
								SharedPreferences mySharedPreferences= getSharedPreferences("versions",0);
								SharedPreferences.Editor editor = mySharedPreferences.edit();
								editor.putString("versionShort", apkVersion);
								editor.commit(); 
								UpdateManager manager = new UpdateManager(SettingActivity.this,false);
								// 检查软件更新
								manager.checkUpdate(apkinfo);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}
		});
		llExit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {// 退出登录
				DialogFactory.getInstance().showDialog(SettingActivity.this, new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						SharedPreferencesTools.clearUserId(SettingActivity.this);
						CloudManagementApplication.gotoLoginActivity(SettingActivity.this);
					}
				}, null, "确定要退出账号吗", null, null);
				
			}
		});
		mineInfoZone.setItemClickListener(this);
		ArrayList<ViewConfig> list = new ArrayList<ViewConfig>();
		ViewConfig viewConfig = new ViewConfig("关于力和物业", "", true);
		list.add(viewConfig);
		viewConfig = new ViewConfig("意见反馈", "", true);
		list.add(viewConfig);
		viewConfig = new ViewConfig("修改密码", "", true);
		list.add(viewConfig);
		/*viewConfig = new ViewConfig("退出登录", "", true);
		list.add(viewConfig);*/
		mineInfoZone.setData(list);
	}
	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		return getLayoutInflater().inflate(R.layout.activity_setting, null);
	}

	@Override
	public String getHeadTitle() {
		// TODO Auto-generated method stub
		return "更多设置";
	}
	
	@Override
	public void onItemClick(MessageArrowView mv, View v, int position) {
		// TODO Auto-generated method stub
		if (mv == mineInfoZone) {
			if (position == 0) {// 关于app
				startActivity(new Intent(this,AboutUsActivity.class));
			}else if(position == 1){// 意见反馈
				startActivity(new Intent(this,OpinionActivity.class));
			}else if(position == 2){// 修改密码
				startActivity(new Intent(this,ModifiedPasswordActivity.class));
			}
		}
	}
}
