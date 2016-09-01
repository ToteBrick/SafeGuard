package com.zhj.safeguard.activity;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

import com.zhj.safeguard.R;


public abstract class SjfdBaseActivity extends Activity {

	protected static final String TAG = "SjfdBaseActivity";
	private GestureDetector mGestureDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		mGestureDetector = new GestureDetector(this,
				new SimpleOnGestureListener() {
					// onfling 惯性滑动调用的方法
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						// e1 start; e2 end ;velocity:速率
						// 如果x1<x2从左向右滑动
						float x1 = e1.getX();
						float x2 = e2.getX();
						float y1 = e1.getY();
						float y2 = e2.getY();
						Log.d(TAG, "velocityX：" + velocityX);
						Log.d(TAG, "速度太慢");
						// 方向限制
						if (Math.abs(y2 - y1) > Math.abs(x2 - x1)) {
							return false;
						}
						// 速度限制
						if (Math.abs(velocityX) < 100) {
							Log.d(TAG, "y方向运动");
							return false;
						}

						if (x1 < x2) {
							Log.d(TAG, "进入上一页");
							// doPre();
							performPre();
							return true;
						} else {
							Log.d(TAG, "进入下一页");
							// doNext();
							performNext();
							return true; // false 不继续分发消费

						}

					}
				});
	}

	private void performPre() {
		if(doPre()){
			return;
		}
		// enterAnima进入activity动画,exitAnim退出activity动画
		overridePendingTransition(R.anim.pre_enter, R.anim.pre_exit);
		finish();
	}

	private void performNext() {
		if(doNext()){
			return;
		}
		// enterAnima进入activity动画,exitAnim退出activity动画
		overridePendingTransition(R.anim.next_enter, R.anim.next_exit);
		finish();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 手势识别器分析touch
		mGestureDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	// 相同操作
	public void clickNext(View v) {
		performNext();
	}

	protected abstract boolean doNext();
	//执行上一个页面操作。如果返回true，不往下执行。
	protected abstract boolean doPre();

	public void clickPre(View v) {
		performPre();
	}
}
