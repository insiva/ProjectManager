package com.xhj.pm.utils;

import java.io.File;

import android.content.Context;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class XHttpUtils {
		private HttpUtils mHttpUtils;
		private static XHttpUtils mInstance=null;
		private static int REQUEST_THREAD_POOL_SIZE=5;
		
		private XHttpUtils(){
			this.mHttpUtils=new HttpUtils();
			this.mHttpUtils.configRequestThreadPoolSize(REQUEST_THREAD_POOL_SIZE);
		}
		
		public ResponseStream syncGet(String url) throws HttpException{
			return this.mHttpUtils.sendSync(HttpMethod.GET, url);
		}
		
		public ResponseStream syncPost(String url, RequestParams params) throws HttpException{
			return this.mHttpUtils.sendSync(HttpMethod.POST, url, params);
		}
		
		public static void init(Context context){
			XHttpUtils.mInstance=new XHttpUtils();
		}

		public static String httpGet(String url) {
			String result = null;
			try {
				ResponseStream rs = XHttpUtils.mInstance.syncGet(url);
				result = Utils.inputStream2String(rs);
			} catch (Exception e) {
				e.printStackTrace();
				result = null;
			}
			return result;
		}

		public static String httpPost(String url, RequestParams params) {
			String result = null;
			try {
				ResponseStream rs = XHttpUtils.mInstance.syncPost(url, params);
				result = Utils.inputStream2String(rs);
			} catch (Exception e) {
				e.printStackTrace();
				result = null;
			}
			return result;
		}
		
		public static HttpHandler<File> download(String url, String target,
	            boolean autoResume, boolean autoRename, RequestCallBack<File> callback) {
			Utils.deleteFile(target);
			return XHttpUtils.mInstance.mHttpUtils.download(url, target, autoResume, autoRename, callback);
		}
		
	}

