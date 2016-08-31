package com.zhj.safeguard.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.zhj.safeguard.R;
import com.zhj.safeguard.utils.Constants;
import com.zhj.safeguard.utils.PreferenceUtils;

public class SjfdSetup2Activity extends SjfdBaseActivity {

	private RelativeLayout mRlBind;
	private ImageView mIvBind;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sjfd_setup2);

		initView();

		initEvent();
	}

	private void initEvent() {

		mRlBind.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
				String simSerialNumber = tm.getSimSerialNumber();
				String sim = PreferenceUtils.getString(getApplicationContext(),
						Constants.SJFD_SIM);
				// ui:绑定就解绑，解绑就绑定
				if (TextUtils.isEmpty(sim)) {
					mIvBind.setImageResource(R.mipmap.lock);
					PreferenceUtils.putString(getApplicationContext(),
							Constants.SJFD_SIM, simSerialNumber);
				} else {
					mIvBind.setImageResource(R.mipmap.unlock);
					PreferenceUtils.putString(getApplicationContext(),
							Constants.SJFD_SIM, null);
				}

			}
		});
	}

	private void initView() {
		mRlBind = (RelativeLayout) findViewById(R.id.setup2_rl_bind);
		mIvBind = (ImageView) findViewById(R.id.setup2_iv_bind);
		String sim = PreferenceUtils.getString(getApplicationContext(),
				Constants.SJFD_SIM);

		mIvBind.setImageResource(TextUtils.isEmpty(sim) ? R.mipmap.unlock
				: R.mipmap.lock);

	}

	/*
	 * public void clickPre(View v) { Intent intent = new Intent(this,
	 * SjfdSetup1Activity.class); startActivity(intent); //
	 * enterAnima进入activity动画,exitAnim退出activity动画
	 * overridePendingTransition(R.anim.pre_enter, R.anim.pre_exit); finish(); }
	 */

	@Override
	protected boolean doNext() {

		String sim = PreferenceUtils.getString(getApplicationContext(),
				Constants.SJFD_SIM);
		// 如果要开启防盗保护，必须绑定sim卡。
		if (TextUtils.isEmpty(sim)) {
			Toast.makeText(getApplicationContext(), "如果要开启手机保护，必须绑定sim卡", 0)
					.show();
			return true;
		}
		Intent intent = new Intent(this, SjfdSetup3Activity.class);
		startActivity(intent);
		return false;
	}

	@Override
	protected boolean doPre() {
		Intent intent = new Intent(this, SjfdSetup1Activity.class);
		startActivity(intent);
		return false;
	}
}
