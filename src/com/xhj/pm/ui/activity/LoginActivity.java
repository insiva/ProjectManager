package com.xhj.pm.ui.activity;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.xhj.pm.Config;
import com.xhj.pm.Constant;
import com.xhj.pm.R;
import com.xhj.pm.model.User;
import com.xhj.pm.task.AbsAsyncTask;
import com.xhj.pm.ui.activity.base.AbsActivity;
import com.xhj.pm.ui.view.LockableLayout;
import com.xhj.pm.utils.Utils;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AbsActivity implements OnClickListener{
	static final String LOGIN_URL="%s?apiname=login&user_id=%s&user_pwd=%s&mac=%s";///Constant.USER_API_URL
	
	
	@ViewInject(R.id.lockLayout)
	private LockableLayout lockLayout;
	@ViewInject(R.id.etUserId)
	private EditText etUserId;
	@ViewInject(R.id.etPassword)
	private EditText etPassword;
	@ViewInject(R.id.btnLogin)
	private Button btnLogin;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		ViewUtils.inject(this);
		this.btnLogin.setOnClickListener(this);
	}
	
	public static void startActivity(Context ctx){
		Intent intent=new Intent(ctx,LoginActivity.class);
		ctx.startActivity(intent);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnLogin:
			this.login();
			break;

		default:
			break;
		}
	}
	
	private void login(){
		LoginTask task=new LoginTask();
		task.execute();
	}
	
	private class LoginTask extends AbsAsyncTask{
		User mLoginer;
		
		LoginTask(){
			super();
			String user_id=Utils.urlEncode(etUserId.getText().toString());
			String user_pwd=Utils.urlEncode(etPassword.getText().toString());
			String mac=Utils.urlEncode(Config.MAC);
			this.mUrl=String.format(LOGIN_URL, Constant.USER_API_URL,user_id,user_pwd,mac);
		}
		
		@Override
		public void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			lockLayout.lock();
		}
		
		@Override
		public void doInBackground() {
			super.doInBackground();
			if(this.mStatus==Constant.RESULT_STATUS_OK){
				mLoginer=User.parse(this.mData);
			}
		}
		
		@Override
		public void onPostExecute() {
			// TODO Auto-generated method stub
			super.onPostExecute();
			if(this.mStatus==Constant.RESULT_STATUS_OK){
				Config.setToken(mLoginer.mToken);
				String host=Utils.getHost(this.mUrl);
				Utils.setCookie(LoginActivity.this, host, Constant.TOKEN_KEY, mLoginer.mToken);
				Utils.setCookie(LoginActivity.this, host, Constant.SDK_VERSION_KEY, Integer.toString(Build.VERSION.SDK_INT));
				MainActivity.startActivity(LoginActivity.this);
				LoginActivity.this.finish();
			}else{
				Utils.toast(this.mInfo);
			}
			lockLayout.unlock();
		}
	}
}
