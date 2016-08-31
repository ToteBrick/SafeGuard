package com.zhj.safeguard.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

import com.zhj.safeguard.R;
import com.zhj.safeguard.utils.Logger;


public abstract class SjfdBaseSetupActivity extends Activity {

	protected static final String TAG = "SjfdBaseSetupActivity";

	private GestureDetector mDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mDetector = new GestureDetector(this, new SimpleOnGestureListener() {

			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {

				// e1: 开始的点
				// e2: 结束的点
				// velocityX: 速率，单位时间x方向移动的像素 px/时间
				// velocityY: 速率，单位时间y方向移动的像素 px/时间

				float x1 = e1.getX();// 获得开始点的x坐标
				float y1 = e1.getY();
				float x2 = e2.getX();// 获得结束点的x坐标
				float y2 = e2.getY();

				// 水平滑动的判断
				if (Math.abs(y2 - y1) > Math.abs(x2 - x1)) {
					// 垂直方向运动
					Logger.d(TAG, "垂直方向运动 ");
					return false;
				}

				// 速度限制
				if (Math.abs(velocityX) < 100) {
					Logger.d(TAG, "速度太慢 ");
					return false;
				}

				if (x1 < x2) {
					// 如果x1 < x2 从左往右滑动----> 进入上一页
					Logger.d(TAG, "进入上一页");

					performPre();

					return true;
				} else {
					// 如果x1 > x2 从右往左滑动----> 进入下一页
					Logger.d(TAG, "进入下一页");

					performNext();

					return true;
				}

				// 不继续分发消费
				// return false;
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 手势识别器分析touch
		mDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	/**
	 * 点击上一步的共同行为
	 * 
	 * @param view
	 */
	public void clickPre(View view) {
		performPre();
	}

	private void performPre() {
		// 1.不同点
		if (doPre()) {
			return;
		}

		// 2.共同点
		// 动画
		// enterAnim: 进入的动画, 让进入的activity做动画
		// exitAnim: 退出的动画,让退出的activity做动画
		overridePendingTransition(R.anim.pre_enter, R.anim.pre_exit);

		finish();
	}

	/**
	 * 点击下一步的共同行为
	 * 
	 * @param view
	 */
	public void clickNext(View view) {
		performNext();
	}

	private void performNext() {
		// #### 不同操作 ######
		if (doNext()) {
			return;
		}

		// #### 相同 ########
		// 动画
		// enterAnim: 进入的动画, 让进入的activity做动画
		// exitAnim: 退出的动画,让退出的activity做动画
		overridePendingTransition(R.anim.next_enter, R.anim.next_exit);

		finish();
	}

	/**
	 * 执行上一个页面操作，如果return true不往下执行
	 * 
	 * @return
	 */
	protected abstract boolean doPre();

	/**
	 * 执行下一个页面操作，如果return true不往下执行
	 * 
	 * @return
	 */
	protected abstract boolean doNext();
}
