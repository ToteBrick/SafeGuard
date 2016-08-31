package com.zhj.safeguard.view;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.widget.TextView;

public class FocusTextView extends TextView {

	public FocusTextView(Context context) {
		this(context,null);
		
	}

	public FocusTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		/*android:singleLine="true"
                android:ellipsize="marquee"
                android:marqueeRepeatLimit="marquee_forever"
                android:focusable="true" 
                android:focusableInTouchMode="true"*/
		setSingleLine();
		setEllipsize(TruncateAt.MARQUEE);
		setMarqueeRepeatLimit(-1); //-1无穷多次
		setFocusable(true);
		setFocusableInTouchMode(true);
		
	}
	/*@Override
	public boolean isFocused() {
		
		return true; //做的欺骗，焦点未改变时有用
		
	}*/
	
	@Override
	protected void onFocusChanged(boolean focused, int direction,
			Rect previouslyFocusedRect) {
		if(focused){
		super.onFocusChanged(focused, direction, previouslyFocusedRect);
		}
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		if(hasWindowFocus){
		super.onWindowFocusChanged(hasWindowFocus);
		}
	}
}
