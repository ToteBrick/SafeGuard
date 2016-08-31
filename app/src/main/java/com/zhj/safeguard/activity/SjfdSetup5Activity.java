package com.zhj.safeguard.activity;


import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import com.zhj.safeguard.R;
import com.zhj.safeguard.utils.Constants;
import com.zhj.safeguard.utils.PreferenceUtils;

public class SjfdSetup5Activity extends SjfdBaseActivity {

	private CheckBox mCbProtecting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sjfd_setup5);
		mCbProtecting = (CheckBox) findViewById(R.id.setup5_cb_protecting);

		mCbProtecting.setChecked(PreferenceUtils.getBoolean(
				getApplicationContext(), Constants.SJFD_PROTECTING));
		mCbProtecting.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// checkbox选中或未选中的回调
				PreferenceUtils.putBoolean(getApplicationContext(),
						Constants.SJFD_PROTECTING, isChecked);
			}
		});
	}

	/*
	 * public void clickNext(View v) { Intent intent = new Intent(this,
	 * SjfdActivity.class); startActivity(intent); //
	 * enterAnima进入activity动画,exitAnim退出activity动画
	 * overridePendingTransition(R.anim.next_enter, R.anim.next_exit); finish();
	 * }
	 */

	/*
	 * public void clickPre(View v) { Intent intent = new Intent(this,
	 * SjfdSetup4Activity.class); startActivity(intent); //
	 * enterAnima进入activity动画,exitAnim退出activity动画
	 * overridePendingTransition(R.anim.pre_enter, R.anim.pre_exit); finish(); }
	 */

	@Override
	protected boolean doNext() {

		if (!mCbProtecting.isChecked()) {
			Toast.makeText(SjfdSetup5Activity.this, "需要勾选开启防盗保护", 0).show();
			return true;
		}
		Intent intent = new Intent(this, SjfdActivity.class);
		startActivity(intent);
		return false;
	}

	@Override
	protected boolean doPre() {
		Intent intent = new Intent(this, SjfdSetup4Activity.class);
		startActivity(intent);
		return false;
	}
}
