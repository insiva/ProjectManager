package com.xhj.pm.ui.view;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.xhj.pm.Constant;
import com.xhj.pm.ui.view.MyInputConnection.InputListener;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;

@SuppressLint("SetJavaScriptEnabled")
public class XWebView  extends WebView implements InputListener{
	
	static final String JS_OBJ_NAME="JsWvObj";
	static final int MSG_SHOW_DATE_PICKER=0x001;

    private ProgressBar pbLoading;
    private TextView tvTitle;
    private Context mContext;
    private JsObj mJsObj;

	//private MyWebViewClient mWebViewClient;
	public String mCurrentUrl;
	MyDatePickerDialog mMyDpDialog;
	
	@SuppressLint("HandlerLeak")
	Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			if (msg.what==MSG_SHOW_DATE_PICKER) {
				mMyDpDialog.show();
			}
		};
	};
	
    @SuppressWarnings("deprecation")
	public XWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext=context;
        pbLoading = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        pbLoading.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 5, 0, 0));
        addView(pbLoading);
        this.init();
    }
    
    public void setTvTitle(TextView tTitle){
    	this.tvTitle=tTitle;
    }
    
    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
    	InputConnection conn=null;
    	if(Build.VERSION.SDK_INT<=Constant.JELLY_BEN_SDK_VERSION){
    		conn=new MyInputConnection(this, false,this);
    		if(outAttrs!=null){
        		outAttrs.inputType=161;
    		}
    	}else{
    		conn=super.onCreateInputConnection(outAttrs);
    	}
        return conn;
    }
    
    private void init(){
        this.setWebChromeClient(new MyWebChromeClient());
		this.setWebViewClient(new MyWebViewClient());
		this.getSettings().setJavaScriptEnabled(true);
		this.getSettings().setAppCacheEnabled(false);
		this.mJsObj=new JsObj();
		this.addJavascriptInterface(this.mJsObj, JS_OBJ_NAME);
    }
    
    private class MyWebChromeClient extends android.webkit.WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
            	pbLoading.setVisibility(GONE);
            } else {
                if (pbLoading.getVisibility() == GONE)
                	pbLoading.setVisibility(VISIBLE);
                pbLoading.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }
        
        @Override
        public void onReceivedTitle(WebView view, String title) {
        	tvTitle.setText(title);
        }

        //on
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		LayoutParams lp = (LayoutParams) pbLoading.getLayoutParams();
        lp.x = l;
        lp.y = t;
        pbLoading.setLayoutParams(lp);
        super.onScrollChanged(l, t, oldl, oldt);
    }
    
	private class MyWebViewClient extends WebViewClient{
		public boolean shouldOverrideUrlLoading(WebView view, String url) { 

	        view.loadUrl(url);
	        mCurrentUrl=url;
	        return true; 

	    } 
		
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			//XWebView.this.setZoomControlGone(XWebView.this);
		}
	}
	
	public class JsObj extends Object{
		
		public JsObj(){
			mMyDpDialog=new MyDatePickerDialog();
		}
		
		@JavascriptInterface
		public void selectDate(final String objId,final String date){
			mMyDpDialog.mObjId=objId;
			mMyDpDialog.setDate(date);
			mHandler.sendEmptyMessage(MSG_SHOW_DATE_PICKER);
			//mDpDialog.show();
		}
	}
	
	public class MyDatePickerDialog implements OnDateSetListener{
		
		DatePickerDialog mDpDialog;
		String mObjId;

		public MyDatePickerDialog(){
			Calendar calendar = Calendar.getInstance(); 
			this.mDpDialog=new DatePickerDialog(mContext, this,
					calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		}
		
		@SuppressLint("SimpleDateFormat")
		public void setDate(final String date){
			Date d=new Date();
			if(!TextUtils.isEmpty(date)){
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟  
				try {
					d=sdf.parse(date);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
				Calendar calendar=Calendar.getInstance();
				calendar.setTime(d);
				mDpDialog.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		}
		
		public void show(){
			this.mDpDialog.show();
		}
		
		@SuppressLint("DefaultLocale")
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			//Utils.toast(this.mObjId);
			int m=monthOfYear+1;
			String ms=m>9?Integer.toString(m):("0"+Integer.toString(m));
			String ds=dayOfMonth>9?Integer.toString(dayOfMonth):("0"+Integer.toString(dayOfMonth));
			String u=String.format("javascript:onDateSelected('%s','%d-%s-%s')",mObjId, year,ms,ds);
			 XWebView.this.loadUrl(u);  
		}
	}

	@SuppressLint("DefaultLocale")
	@Override
	public void onCommitText(String text, int newCursorPosition) {
		String u=String.format("javascript:$.onCommitText('%s',%d)",text,newCursorPosition);
		 XWebView.this.loadUrl(u);  
		 this.onCreateInputConnection(null);
	}
}
