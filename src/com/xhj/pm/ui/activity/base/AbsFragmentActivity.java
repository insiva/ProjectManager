package com.xhj.pm.ui.activity.base;


import java.util.Iterator;
import java.util.Stack;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.umeng.analytics.MobclickAgent;

public abstract class AbsFragmentActivity extends FragmentActivity {

	public static final int REQUEST_CODE_LOGOUT = 0x301;
	public static Stack<Activity> mActivityStack = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(null);
		if (mActivityStack == null) {
			mActivityStack = new Stack<Activity>();
		}
		//mActivityStack.push(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mActivityStack != null && mActivityStack.size() > 0) {
			//mActivityStack.pop();
		}
	}
}
