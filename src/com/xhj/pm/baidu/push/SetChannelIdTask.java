package com.xhj.pm.baidu.push;

import com.xhj.pm.Config;
import com.xhj.pm.Constant;
import com.xhj.pm.utils.XHttpUtils;

public class SetChannelIdTask extends Thread {
	private static final String SET_CHANNEL_ID_URL = "%s?apiname=setChannelId&token=%s&channel_id=%s";
	
	private String mUrl;
	
	public SetChannelIdTask(String channelId){
		this.mUrl=String.format(SET_CHANNEL_ID_URL, Constant.USER_API_URL,Config.getToken(),channelId);
	}
	
	 @Override
	public void run() {
		 XHttpUtils.httpGet(this.mUrl);
	}
}
