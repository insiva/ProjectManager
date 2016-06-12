package com.xhj.pm.ui.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.xhj.pm.Config;
import com.xhj.pm.Constant;
import com.xhj.pm.R;
import com.xhj.pm.task.AbsAsyncTask;
import com.xhj.pm.ui.activity.base.AbsActivity;
import com.xhj.pm.ui.view.LockableLayout;
import com.xhj.pm.ui.view.XWebView;
import com.xhj.pm.ui.view.popuptree.Node;
import com.xhj.pm.ui.view.popuptree.PopupTree;
import com.xhj.pm.ui.view.popuptree.PopupTreeAdapter;

@SuppressLint("DefaultLocale")
public class MainActivity extends AbsActivity implements OnClickListener {
	public static final String ACTION_NAME="com.matteo.pm.ui.activity.MainActivity";
	private static final String HOMPAGE_URL = Constant.MOBILE_PAGES_BASE_URL
			+ "Main.aspx?token=%s";
	private static final String NOTIFICATION_DETAIL_URL = Constant.MOBILE_PAGES_BASE_URL
			+ "notificationDetail.aspx?guid=%d";
	private static final String NAVIGATION_URL = "%s?encode=utf8&apiname=listNavigations";
	private static final String NOTIFICATION_GUID_KEY="notification_guid";
	@ViewInject(R.id.tvBack)
	private TextView tvBack;
	@ViewInject(R.id.tvNavigation)
	private TextView tvNavigation;
	@ViewInject(R.id.tvTitle)
	private TextView tvTitle;
	@ViewInject(R.id.wvMain)
	private XWebView wvMain;
	@ViewInject(R.id.lockLayout)
	private LockableLayout lockLayout;

	private PopupTree mNavigation;
	private NavigationAdapter mNavigationAdapter;

	private GetNavigationsTask mGetNavigationsTask;
	private List<Node> mNavigationNodes;
	private int mNavigationLevelNum;
	private NotificationReceiver mNotificationReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PushManager.startWork(getApplicationContext(),
				PushConstants.LOGIN_TYPE_API_KEY, Constant.BAIDU_API_KEY);
		
		setContentView(R.layout.activity_main);
		ViewUtils.inject(this);
		this.tvBack.setOnClickListener(this);
		this.tvNavigation.setOnClickListener(this);
		this.wvMain.setTvTitle(this.tvTitle);
		this.mGetNavigationsTask = new GetNavigationsTask();
		this.mGetNavigationsTask.execute();
		this.mNotificationReceiver=new NotificationReceiver();
		IntentFilter intentFilter=new IntentFilter(Constant.ACTION_NEW_NOTIFICATION);
		this.registerReceiver(this.mNotificationReceiver , intentFilter);
		this.init(this.getIntent());
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		this.init(intent);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		this.unregisterReceiver(this.mNotificationReceiver);
	}
	
	private void init(Intent intent){
		int guid=intent.getIntExtra(NOTIFICATION_GUID_KEY, 0);
		String url=String.format(HOMPAGE_URL, Config.getToken());
		if(guid>0){
			url=String.format(NOTIFICATION_DETAIL_URL, guid);
		}
		this.wvMain.loadUrl(url);
	}


	public static void startActivity(Context ctx) {
		Intent intent = new Intent(ctx, MainActivity.class);
		ctx.startActivity(intent);
	}
	
	public static void startActivityByNotification(Context context,int notificationGuid){
		Intent intent = new Intent(ACTION_NAME);
		intent.putExtra(NOTIFICATION_GUID_KEY, notificationGuid);
		context.startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tvBack:
			if (this.wvMain.canGoBack()) {
				this.wvMain.goBack();
			}
			break;
		case R.id.tvNavigation:
			this.showNavigation();
			break;

		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		if (!this.hideNavigation()) {
			if (this.wvMain.canGoBack()) {
				this.wvMain.goBack();
			} else {
				super.onBackPressed();
			}
		}
	}

	private void showNavigation() {
		if (this.mGetNavigationsTask.getStatus() != Constant.RESULT_STATUS_OK) {
			return;
		}
		if (this.mNavigation == null) {
			this.mNavigation = new PopupTree(this, this.lockLayout);
			this.mNavigationAdapter = new NavigationAdapter();
			this.mNavigation.setAdapter(this.mNavigationAdapter);
		}
		this.mNavigation.show(this.tvNavigation);
	}

	private boolean hideNavigation() {
		if (this.mNavigation != null && this.mNavigation.isShowing()) {
			this.mNavigation.dismiss();
			return true;
		}
		return false;
	}

	class NavigationAdapter extends PopupTreeAdapter {

		public NavigationAdapter() {
			super();
		}

		@Override
		public boolean onSelected(Node node, int... indexs) {
			if (node.hasChildren()) {
				return false;
			}
			if (!TextUtils.isEmpty(node.mValue)) {
				MainActivity.this.wvMain.loadUrl(node.mValue);
			}
			return true;
		}

		@Override
		public int getWindowWidth() {
			WindowManager wm = (WindowManager) MainActivity.this
					.getSystemService(Context.WINDOW_SERVICE);
			DisplayMetrics outMetrics = new DisplayMetrics();
			wm.getDefaultDisplay().getMetrics(outMetrics);
			int ln = this.getLevelNum();
			int w = (int) ((outMetrics.widthPixels * 0.95) / (ln == 1 ? 2 : ln));
			return w;
		}

		@Override
		public int getHeight() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getLevelNum() {
			return MainActivity.this.mNavigationLevelNum;
		}

		@Override
		public List<Node> getNodeList() {
			// TODO Auto-generated method stub
			return MainActivity.this.mNavigationNodes;
		}

	}

	private class GetNavigationsTask extends AbsAsyncTask {

		public GetNavigationsTask() {
			this.mUrl = String.format(NAVIGATION_URL, Constant.USER_API_URL);
			tvNavigation.setText("正在同步");
		}

		@Override
		public void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		public void doInBackground() {
			super.doInBackground();
			if (this.mStatus != Constant.RESULT_STATUS_OK) {
				return;
			}
			MainActivity.this.mNavigationNodes = MainActivity.this
					.parseNavigationJsonArray(this.mData
							.optJSONArray("children"));
			MainActivity.this.mNavigationLevelNum = this.mData
					.optInt("level_num");
		}

		@Override
		public void onPostExecute() {
			// TODO Auto-generated method stub
			super.onPostExecute();
			if (this.mStatus == Constant.RESULT_STATUS_OK) {
				tvNavigation.setText(R.string.navigation);
			} else {
				tvNavigation.setText("同步失败");
			}
		}
	}

	@SuppressWarnings("deprecation")
	private List<Node> parseNavigationJsonArray(JSONArray jsonArray) {
		if (jsonArray == null) {
			return null;
		}
		List<Node> ns = new ArrayList<Node>();
		int l = jsonArray.length();
		for (int i = 0; i < l; i++) {
			Node node = new Node();
			JSONObject jsonObject = jsonArray.optJSONObject(i);
			node.mName = java.net.URLDecoder.decode(jsonObject
					.optString("name"));
			node.mValue = jsonObject.optString("url");
			node.mChildren = this.parseNavigationJsonArray(jsonObject
					.optJSONArray("children"));
			ns.add(node);
		}
		return ns;
	}
	
	public class NotificationReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			int guid=intent.getIntExtra(NOTIFICATION_GUID_KEY, 0);
			if(guid>0){
				String url=String.format(NOTIFICATION_DETAIL_URL, guid);
				wvMain.loadUrl(url);
			}
		}
		
	}
}
