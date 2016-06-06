package com.xhj.pm.ui.view;


import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.xhj.pm.utils.Utils;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ZoomButtonsController;

@SuppressLint("SetJavaScriptEnabled")
public class XWebView  extends WebView {
	
	static final String JS_OBJ_NAME="JsWvObj";
	static final int MSG_SHOW_DATE_PICKER=0x001;

    private ProgressBar pbLoading;
    private TextView tvTitle;
    private Context mContext;
    private JsObj mJsObj;

	//private MyWebViewClient mWebViewClient;
	public String mCurrentUrl;
	MyDatePickerDialog mMyDpDialog;
	
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
    
    private void init(){
        this.setWebChromeClient(new MyWebChromeClient());
		this.setWebViewClient(new MyWebViewClient());
		this.getSettings().setJavaScriptEnabled(true);
		this.getSettings().setAppCacheEnabled(false);
		//this.getSettings().setSupportZoom(true);
		//this.getSettings().setBuiltInZoomControls(false);
		//this.getSettings().setBuiltInZoomControls(true);   
		//this.getSettings().setUseWideViewPort(true);
		//this.setZoomControlGone(this);
		this.mJsObj=new JsObj();
		this.addJavascriptInterface(this.mJsObj, JS_OBJ_NAME);
    }
    
    private void setZoomControlGone(View view) {  
        Class classType;  
        Field field;  
        try {  
            classType = WebView.class;  
            field = classType.getDeclaredField("mZoomButtonsController");  
            field.setAccessible(true);  
            ZoomButtonsController mZoomButtonsController = new ZoomButtonsController(view);  
            mZoomButtonsController.getZoomControls().setVisibility(View.GONE);  
            try {  
                field.set(view, mZoomButtonsController);  
            } catch (IllegalArgumentException e) {  
                e.printStackTrace();  
            } catch (IllegalAccessException e) {  
                e.printStackTrace();  
            }  
        } catch (SecurityException e) {  
            e.printStackTrace();  
        } catch (NoSuchFieldException e) {  
            e.printStackTrace();  
        }  
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

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        @SuppressWarnings("deprecation")
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
		
		@Override
		public void onScaleChanged(WebView view, float oldScale, float newScale) {
			// TODO Auto-generated method stub
			super.onScaleChanged(view, oldScale, newScale);
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
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			//Utils.toast(this.mObjId);
			String u=String.format("javascript:onDateSelected('%s','%d-%d-%d')",mObjId, year,monthOfYear+1,dayOfMonth);
			 XWebView.this.loadUrl(u);  
		}

	}
}
