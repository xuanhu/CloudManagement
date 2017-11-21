package com.tg.cloudmanagement;

import com.tg.cloudmanagement.R;
import com.tg.cloudmanagement.base.BaseActivity;

import android.os.Bundle;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;
/**
 * 关于app
 * 
 * @author Administrator
 * 
 */
public class AboutUsActivity extends BaseActivity {
	private TextView tvVersionShort;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tvVersionShort=(TextView) findViewById(R.id.tv_versionShort);
		SharedPreferences sharedPreferences= getSharedPreferences("versions",0);
		String versionShort =sharedPreferences.getString("versionShort", "");
		tvVersionShort.setText("V"+versionShort);
	}

	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		return getLayoutInflater().inflate(R.layout.activity_about_us, null);
	}

	@Override
	public String getHeadTitle() {
		// TODO Auto-generated method stub
		return "关于力和物业";
	}

}
