package com.xhj.pm.ui.view;

import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;

public class MyInputConnection extends BaseInputConnection{
	
	private InputListener mListener;

    public MyInputConnection(View targetView, boolean fullEditor,InputListener listener) {
    	super(targetView, fullEditor);
    	this.mListener=listener;
    }
    
    @Override
    public boolean deleteSurroundingText(int beforeLength, int afterLength) {
    	if (beforeLength == 1 && afterLength == 0) {
            // backspace
            return super.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                && super.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
        }

        return super.deleteSurroundingText(beforeLength, afterLength);
    }
    
    @Override
    public boolean commitText(CharSequence text, int newCursorPosition) {
    	int l=text.length();
    	for(int i=0;i<l;i++){
    		super.commitText(String.valueOf(text.charAt(i)), newCursorPosition);
    	}
    	return true;
    	//return super.commitText(text, newCursorPosition);
    }
    
    public static interface InputListener{
    	public void onCommitText(String text,int newCursorPosition);
    }
}
