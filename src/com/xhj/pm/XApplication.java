package com.xhj.pm;

import android.app.Application;
import android.content.Context;

public class XApplication  extends Application{
	private static Context mAppContext;
	
	@Override
	public void onCreate() {
		super.onCreate();
		XApplication.mAppContext=this;
		Config.init(this);
	}
	
	
	public static Context getContext(){
		return mAppContext;
	}
}
