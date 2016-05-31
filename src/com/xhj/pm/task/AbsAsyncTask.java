package com.xhj.pm.task;


import org.json.JSONObject;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.xhj.pm.Constant;
import com.xhj.pm.utils.XHttpUtils;

public class AbsAsyncTask extends XTask {
	protected String mUrl;
	protected int mStatus=Constant.RESULT_STATUS_ERROR;
	protected String mInfo=null;
	protected JSONObject mData=null;
	protected TaskCallback mCallback;
	protected HttpMethod mHttpMethod=HttpMethod.GET;
	protected RequestParams mParams=null;
	protected boolean mIsRunning;
	
	public AbsAsyncTask(){
		
	}
	
	public AbsAsyncTask(String url){
		this(url, null);
	}
	
	public AbsAsyncTask(String url,TaskCallback callback){
		this.mUrl=url;
		this.mCallback=callback;
	}
	
	@Override
	public void onPreExecute() {
		super.onPreExecute();
		this.mIsRunning=true;
		if(this.mCallback!=null){
			this.mCallback.onPreExecuteTask();
		}
	}

	@Override
	public void doInBackground() {
		String result=null;
		if(this.mHttpMethod==HttpMethod.GET||null==this.mParams){
			result=XHttpUtils.httpGet(this.mUrl);
		}else if(this.mHttpMethod==HttpMethod.POST){
			result=XHttpUtils.httpPost(this.mUrl, this.mParams);
		}
		try {
			JSONObject jsonObject=new JSONObject(result);
			this.mStatus=jsonObject.optInt(Constant.JSON_TAG_RESULT_STATUS, Constant.RESULT_STATUS_ERROR);
			this.mData=jsonObject.optJSONObject(Constant.JSON_TAG_RESULT_DATA);//jsonObject.optString(Constant.JSON_TAG_RESULT_DATA, null);
			this.mInfo=jsonObject.optString(Constant.JSON_TAG_RESULT_INFO,null);
		} catch (Exception e) {
			this.mStatus=Constant.RESULT_STATUS_ERROR;
			this.mData=null;
			e.printStackTrace();
		}
		if(this.mCallback!=null){
			this.mCallback.doInBackground(this.mStatus, this.mInfo,this.mData);
		}
	}
	
	@Override
	public void onPostExecute() {
		super.onPostExecute();
		this.mIsRunning=false;
		if(this.mCallback!=null){
			this.mCallback.onPostExecute(this.mStatus, this.mInfo,this.mData);
		}
	}
	
	public final boolean isRunning(){
		return this.mIsRunning;
	}
	
	public int getStatus(){
		return this.mStatus;
	}
}
