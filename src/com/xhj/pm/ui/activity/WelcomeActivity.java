package com.xhj.pm.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;

import com.xhj.pm.Config;
import com.xhj.pm.Constant;
import com.xhj.pm.R;
import com.xhj.pm.ui.activity.base.AbsActivity;
import com.xhj.pm.utils.Utils;

public class WelcomeActivity  extends AbsActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		String host=Utils.getHost(Constant.HOST);
		String sdkVersion=Utils.getCookie(this,host, Constant.SDK_VERSION_KEY);
		String token=Utils.getCookie(this,host, Constant.TOKEN_KEY);
		if(TextUtils.isEmpty(sdkVersion)||
		TextUtils.isEmpty(token)){
			Utils.setCookie(this, host, Constant.SDK_VERSION_KEY, null);
			Utils.setCookie(this, host, Constant.TOKEN_KEY, null);
		}
		token=Utils.getCookie(this,host, Constant.TOKEN_KEY);
		if(TextUtils.isEmpty(Config.getToken())||TextUtils.isEmpty(token)){
			LoginActivity.startActivity(this);
		}else{
			MainActivity.startActivity(this);
		}
		this.finish();
	}
}
