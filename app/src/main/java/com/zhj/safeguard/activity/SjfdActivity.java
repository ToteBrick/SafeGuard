package com.zhj.safeguard.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhj.safeguard.R;
import com.zhj.safeguard.utils.Constants;
import com.zhj.safeguard.utils.PreferenceUtils;

public class SjfdActivity extends Activity {

	private TextView mTvSafeNumber;
	private RelativeLayout mRlProtecting, mSetup;
	private ImageView mIvProtecting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sjfd);

		initView();
		initEvent();
	}

	private void initView() {
		mTvSafeNumber = (TextView) findViewById(R.id.sjfd_safe_number);
		mRlProtecting = (RelativeLayout) findViewById(R.id.sjfd_rl_protecting);
		mIvProtecting = (ImageView) findViewById(R.id.sjfd_iv_protecting);
		mSetup = (RelativeLayout) findViewById(R.id.sjfd_rl_setup);
		// 显示安全号码
		String number = PreferenceUtils.getString(getApplicationContext(),
				Constants.SJFD_NUMBER);
		mTvSafeNumber.setText(number);

		// 显示当前状态
		boolean protecting = PreferenceUtils.getBoolean(
				getApplicationContext(), Constants.SJFD_PROTECTING);

		mIvProtecting.setImageResource(protecting ? R.mipmap.lock
				: R.mipmap.unlock);
	}

	private void initEvent() {
		mRlProtecting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean protecting = PreferenceUtils.getBoolean(
						getApplicationContext(), Constants.SJFD_PROTECTING);
				if (protecting) {
					PreferenceUtils.putBoolean(SjfdActivity.this,
							Constants.SJFD_PROTECTING, false);
				} else {
					PreferenceUtils.putBoolean(SjfdActivity.this,
							Constants.SJFD_PROTECTING, true);
				}
				mIvProtecting.setImageResource(protecting ? R.mipmap.unlock
						: R.mipmap.lock);
			}
		});

		mSetup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SjfdActivity.this,
						SjfdSetup1Activity.class);
				startActivity(intent);
				finish();
				//考虑优化可做动画
				overridePendingTransition(R.anim.next_enter, R.anim.next_exit);
			}
		});
	}

}
