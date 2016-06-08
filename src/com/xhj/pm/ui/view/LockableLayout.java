package com.xhj.pm.ui.view;

import com.xhj.pm.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

public class LockableLayout extends  FrameLayout {
	private static final int MESSAGE_WHAT_UNLOCK=0x002;
	private static final long DEFAULT_LOCK_DURATION_MILLI_SECONDS=15000;
	private boolean mLocked;
	private Context mContext;
	//private LockLayout mLockLayout;
	private boolean mTouchable;
	private ProgressBar pbWait;
	private FrameLayout.LayoutParams mPbParams;
	private View mPrompt;
	private BottomLayout flBottom;
	private FrameLayout.LayoutParams mBottomParams;
	private OnTimeOutUnLock mOnTimeOutUnLock;
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler=new Handler(){
		
		@Override
		public void handleMessage(Message msg){
			switch(msg.what){
			case MESSAGE_WHAT_UNLOCK:
				LockableLayout.this.unlock();
				if(LockableLayout.this.mOnTimeOutUnLock!=null){
					LockableLayout.this.mOnTimeOutUnLock.onTimeOutUnLock();
				}
				break;
			case 23:
				InputMethodManager  input=(InputMethodManager) LockableLayout.this.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				input.showSoftInput(LockableLayout.this.pbWait,0);
				break;
				default:
					break;
			}
		}
	};
	
	public LockableLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.init(context);
	}

	private void init(Context context) {
		this.mContext = context;
		this.mLocked = false;
		this.mTouchable=true;
		this.pbWait=new ProgressBar(this.mContext);
		this.mPbParams=new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		this.mPbParams.gravity=Gravity.CENTER;
		this.flBottom=new BottomLayout(this.mContext);
		this.mBottomParams=new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT);
		this.flBottom.setBackgroundColor(this.mContext.getResources().getColor(R.color.halfblack));
	}

	public boolean isLocked() {
		return this.mLocked;
	}

	public void lock() {
		this.lock(true);
	}
	
	public void lock(OnTimeOutUnLock onUnLock){
		this.mOnTimeOutUnLock=onUnLock;
		this.lock(true);
		this.unlock(DEFAULT_LOCK_DURATION_MILLI_SECONDS);
	}
	
	public void lock(boolean hasWaitBar){
		this.mLocked = true;
		this.mTouchable=false;
		this.flBottom.setVisibility(View.VISIBLE);
		this.pbWait.setVisibility(View.VISIBLE);
		if(this.flBottom.getParent()==null){
			this.addView(this.flBottom, this.mBottomParams);
		}
		if(!hasWaitBar){
			return;
		}
		if(this.pbWait.getParent()==null){
			this.flBottom.addView(this.pbWait, this.mPbParams);
		}
	}

	public void unlock() {
		this.unlock(0);
	}
	
	public void unlock(long delayMS){
		if(!this.mLocked){
			return;
		}
		if(delayMS<=0){
			this.mLocked = false;
			this.mTouchable=true;
			this.flBottom.setVisibility(View.GONE);
			if(this.flBottom!=null){
				this.removeView(this.flBottom);
			}
			if(this.pbWait.getParent()!=null){
				this.pbWait.setVisibility(View.GONE);
				this.flBottom.removeView(this.pbWait);
			}
			if(this.mPrompt!=null){
				this.mPrompt.setVisibility(View.GONE);
				this.flBottom.removeView(this.mPrompt);
				this.mPrompt=null;
			}
		}else{
			this.mHandler.sendEmptyMessageDelayed(MESSAGE_WHAT_UNLOCK, delayMS);
		}
	}
	
	public void lock(View prompt){
		this.mPrompt=prompt;
		this.mLocked = true;
		this.mTouchable=false;
		this.flBottom.setVisibility(View.VISIBLE);
		this.mPrompt.setVisibility(View.VISIBLE);
		if(this.flBottom.getParent()==null){
			this.addView(this.flBottom, this.mBottomParams);
		}
		if(this.mPrompt.getParent()==null){
			this.flBottom.addView(this.mPrompt, this.mPbParams);
		}
	}
	
	public void setTouchable(boolean touchable){
		this.mTouchable=touchable;
	}
	
	public boolean isTouchable(){
		return this.mTouchable;
	}
	
	private class BottomLayout extends FrameLayout{

		public BottomLayout(Context context) {
			super(context);
		}
		
		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouchEvent(MotionEvent event){
			return !mTouchable;
		}
	}
	
	public void setOnUnLock(OnTimeOutUnLock onUnLock){
		this.mOnTimeOutUnLock=onUnLock;
	}
	
	public interface OnTimeOutUnLock{
		public void onTimeOutUnLock();
	}
	
	public void recycle(){
		this.reset();
	}

	public void reset(){
		this.mOnTimeOutUnLock=null;
	}
	
}
