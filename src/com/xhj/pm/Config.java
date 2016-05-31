package com.xhj.pm;

import com.xhj.pm.utils.Utils;
import com.xhj.pm.utils.XHttpUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.Preference;
import android.preference.PreferenceManager;

public class Config {
	public static String MAC=null;
	public static Context APP_CONTEXT=null;
	private static String mToken=null;
	private static final String TOKEN_KEY="TOKEN";
	
	public static void init(Context context){
		APP_CONTEXT=context;
		readDeviceConfiguration();
		readPreference();
		
		initOthers();
	}
	
	private static void initOthers(){
		Utils.init(APP_CONTEXT);
		XHttpUtils.init(APP_CONTEXT);
	}
	
	private static void readDeviceConfiguration(){
		WifiManager wifi = (WifiManager) APP_CONTEXT
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		 MAC = info.getMacAddress();
	}
	
	private static void readPreference(){
		SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(APP_CONTEXT);
		if(sp.contains(TOKEN_KEY)){
			Config.mToken=sp.getString(TOKEN_KEY, null);
		}
	}
	
	public static String getToken(){
		return mToken;
	}
	
	public static void setToken(String token){
		Config.mToken=token;
		SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(APP_CONTEXT);
		sp.edit().putString(TOKEN_KEY, token).commit();
	}
}
