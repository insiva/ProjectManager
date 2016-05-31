package com.xhj.pm.model;

import org.json.JSONObject;

public class User extends AbsModel{

	public String mUserId;
	public String mUserPwd;
	public String mRealname;
	public String mToken;
	public String mMac;
	
	public User(){
		super();
	}
	
	public static User parse(JSONObject json){
		User u=new User();
		u.mGuid=json.optInt("guid");
		u.mCreatetime=json.optString("create_time");
		u.mUserId=json.optString("user_id");
		u.mToken=json.optString("token");
		u.mRealname=json.optString("realname");
		return u;
	}
}
