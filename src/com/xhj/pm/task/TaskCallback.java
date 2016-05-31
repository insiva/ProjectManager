package com.xhj.pm.task;


import org.json.JSONObject;

public interface TaskCallback {
	public void onPreExecuteTask();
	public void onPostExecute(int status,String info,JSONObject data);
	public void doInBackground(int status,String info,JSONObject data);
}
