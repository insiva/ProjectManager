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
		if(TextUtils.isEmpty(Config.getToken())||TextUtils.isEmpty(Utils.getCookie(this, Utils.getHost(Constant.HOST), "token"))){
			LoginActivity.startActivity(this);
		}else{
			MainActivity.startActivity(this);
		}
		this.finish();
	}
}
