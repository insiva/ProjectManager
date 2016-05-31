package com.xhj.pm.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.xhj.pm.Constant;

import android.R.string;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.EditText;
import android.widget.Toast;

public class Utils {
	private static Context mContext = null;
	private static Random mRandom=null;

	public static void init(Context context) {
		Utils.mContext = context;
		Utils.mRandom=new Random(System.currentTimeMillis());
	}

	public static String getString(int resId) {
		return Utils.mContext.getString(resId);
	}

	public static int getColor(int resId) {
		Resources res = Utils.mContext.getResources();
		int color = res.getColor(resId);
		return color;
	}

	public static boolean isNotNull(String str) {
		/*
		if (str == null || "".equals(str)||"null".equals(str)) {
			return false;
		}
		return true;*/
		return !TextUtils.isEmpty(str);
	}

	public static boolean isNull2(String str) {
		return !Utils.isNotNull(str);
	}

	public static void deleteFile(String path) {
		File file = new File(path);
		Utils.deleteFile(file);
	}
	
	public static boolean fileExists(String path){
		File file=new File(path);
		return file.exists();
	}

	public static void deleteFile(File file) {
		if (file.exists()) {
			file.delete();
		}
	}


	public static void hideSoftInputFromWindow(Activity context, EditText et) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
	}

	public static void showSoftInput(final EditText et) {
		et.requestFocus();
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				InputMethodManager inputManager = (InputMethodManager) et
						.getContext().getSystemService(
								Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(et, 0);
			}
		}, 500);
	}
	
	public static void showSoftInputNow(final EditText et) {
		et.requestFocus();
		InputMethodManager inputManager = (InputMethodManager) et
				.getContext().getSystemService(
						Context.INPUT_METHOD_SERVICE);
		inputManager.showSoftInput(et, 0);
	}

	public static void openBrowser(String url) {
		Uri uri = Uri.parse(url);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		Utils.mContext.startActivity(intent);
	}

	public static String inputStream2String(InputStream in) throws IOException {
		StringBuffer out = new StringBuffer();
		byte[] b = new byte[4096];
		for (int n; (n = in.read(b)) != -1;) {
			out.append(new String(b, 0, n));
		}
		return out.toString();
	}

	public static String urlEncode(String input) {
		if (TextUtils.isEmpty(input)) {
			return "";
		}
		String result = "";
		try {
			result = java.net.URLEncoder.encode(input, Constant.UTF8);
		} catch (Exception e1) {
			e1.printStackTrace();
			result = "";
		}
		return result;
	}

	public static String urlDecode(String input) {
		if (TextUtils.isEmpty(input)) {
			return "";
		}
		String result = "";
		try {
			result = java.net.URLDecoder.decode(input, Constant.UTF8);
		} catch (Exception e1) {
			e1.printStackTrace();
			result = "";
		}
		return result;
	}

	public static void toast(int resId) {
		Utils.toast(Utils.getString(resId));
	}

	public static void toast(String text) {
		Toast toast = Toast.makeText(Utils.mContext, text, Toast.LENGTH_SHORT);
		toast.show();
	}

	public static final String md5(final String s) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest
					.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				String h = Integer.toHexString(0xFF & messageDigest[i]);
				while (h.length() < 2) {
					h = "0" + h;
				}
				hexString.append(h);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			Log.w("", e);
		}
		return "";
	}

	public static boolean isServiceRunning(String className) {
		boolean isRunning = false;

		ActivityManager activityManager = (ActivityManager) Utils.mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		// 获取所有的服务
		List<ActivityManager.RunningServiceInfo> services = activityManager
				.getRunningServices(Integer.MAX_VALUE);
		if (services != null && services.size() > 0) {
			for (ActivityManager.RunningServiceInfo service : services) {
				if (className.equals(service.service.getClassName())) {
					isRunning = true;
					break;
				}
			}
		}

		return isRunning;
	}
	
	public static void waitFor(IWaitFor callback,int times,long duration){
		for(int i=0;i<times;i++){
			if(callback.isSucces()){
				break;
			}else{
				try {
					Thread.sleep(duration);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	private static final int DEFAULT_WAIT_TIMES=30;
	private static final int DEFAULT_WAIT_DURATION=1000;
	public static void waitFor(IWaitFor callback){
		Utils.waitFor(callback,DEFAULT_WAIT_TIMES,DEFAULT_WAIT_DURATION);
	}
	
	public interface IWaitFor{
		public boolean isSucces();
	}
	
	public static boolean isNumber(String number){
		if(TextUtils.isEmpty(number)){
			return false;
		}
		return true; //number.matches("^[0-9*]*[1-9][0-9]*$");
	}
	
	public static boolean isPhoneNumber(String number){
		if(TextUtils.isEmpty(number)){
			return false;
		}
		return number.matches("^[0-9*]*[1-9][0-9]*$");
	}
	
	static long mLastTime=0;
	public static void printTime(){
		long time=System.currentTimeMillis();
		if (mLastTime==0) {
			Log.e("PRINT_TIME", String.format("%d", time));
		}else {
			Log.e("PRINT_TIME", String.format("%d,-------,%d", time,time-mLastTime));
		}
		mLastTime=time;
	}
	
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		float fSize = spValue * fontScale + 0.5f;
		return (int) fSize;
	}
	
	public static int random(int n){
		return Utils.mRandom.nextInt(n);
	}
	public static int random(){
		return Utils.mRandom.nextInt();
	}
	
	public static void setCookie(Context context,String url,String key,String value){
	    CookieSyncManager.createInstance(context);  
	    CookieManager cookieManager = CookieManager.getInstance();  
	    cookieManager.setCookie(url, String.format("%s=%s", key,value));              
	    CookieSyncManager.getInstance().sync(); 
	}
	
	public static String getCookie(Context context,String url,String key){
	    CookieSyncManager.createInstance(context);  
	    CookieManager cookieManager = CookieManager.getInstance();  
	    String c= cookieManager.getCookie(url);
	    return  c;
	}
	
	public static void removeCookie(Context context) {
	    CookieSyncManager.createInstance(context);  
	    CookieManager cookieManager = CookieManager.getInstance(); 
	    cookieManager.removeAllCookie();
	    CookieSyncManager.getInstance().sync();  
	}
	
	public static String getHost(String url){
		  if(url==null||url.trim().equals("")){
		   return "";
		  }
		  String host = "";
		  Pattern p =  Pattern.compile("(?<=//|)((\\w)+\\.)+\\w+");
		  Matcher matcher = p.matcher(url);  
		  if(matcher.find()){
		   host = matcher.group();  
		  }
		  return host;
		 }
}
