package com.xhj.pm.ui.activity.base;

import android.os.Bundle;

public abstract class AbsActivity extends AbsFragmentActivity {
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(null);
	}
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause(){
		super.onPause();
	}
}
