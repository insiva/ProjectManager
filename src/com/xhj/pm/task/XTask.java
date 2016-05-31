package com.xhj.pm.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import android.os.Handler;
import android.os.Message;

public abstract class XTask implements Runnable {
	private static final int MSG_COMPLETE=0x003;
	private static final int THREAD_POOL_SIZE=5;
	
	private static ExecutorService mThreadPool=Executors.newFixedThreadPool(THREAD_POOL_SIZE);
	
	private MyHandler mHandler;
	
	public XTask(){
		this.mHandler=new MyHandler(this);
	}
	
	public abstract void doInBackground();
	
	@Override
	public final void run() {
		this.doInBackground();
		this.mHandler.sendEmptyMessage(MSG_COMPLETE);
	}

	public void execute(){
		this.onPreExecute();
		mThreadPool.execute(this);
	}
	
	public void onPreExecute(){
		
	}
	
	public void onPostExecute(){
		
	}
	
	private static class MyHandler extends Handler{
		private XTask mTask;
		
		public MyHandler(XTask task){
			this.mTask=task;
		}
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(MSG_COMPLETE==msg.what){
				this.mTask.onPostExecute();
			}
		}
	}
	
	public static void execute(Runnable command){
		mThreadPool.execute(command);
	}
}
