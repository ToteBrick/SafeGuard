package com.zhj.safeguard.activity;


import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.zhj.safeguard.R;
import com.zhj.safeguard.receiver.SafeAdminReceiver;

public class SjfdSetup4Activity extends SjfdBaseActivity {
	protected static final int REQUEST_CODE_ENABLE_ADMIN = 100;
	private DevicePolicyManager mDpm;
	private RelativeLayout mRlAdmin;
	private ImageView mIvAdmin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sjfd_setup4);

		mDpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		initView();

		initEvent();

	}

	private void initView() {
		mRlAdmin = (RelativeLayout) findViewById(R.id.setup4_rl_admin);
		mIvAdmin = (ImageView) findViewById(R.id.setup4_iv_admin);
		ComponentName who = new ComponentName(SjfdSetup4Activity.this,
				SafeAdminReceiver.class);
		mIvAdmin.setImageResource((mDpm.isAdminActive(who)) ? R.mipmap.admin_activated
				: R.mipmap.admin_inactivated);

	}

	private void initEvent() {
		mRlAdmin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				ComponentName who = new ComponentName(SjfdSetup4Activity.this,
						SafeAdminReceiver.class);
				if (mDpm.isAdminActive(who)) {
					mDpm.removeActiveAdmin(who);
					mIvAdmin.setImageResource(R.mipmap.admin_inactivated);
				} else {
					// 没有激活，去激活
					mIvAdmin.setImageResource(R.mipmap.admin_activated);
					Intent intent = new Intent(
							DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
					intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, who);
					intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
							"激活一键锁屏");
					startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_ENABLE_ADMIN) {
			switch (resultCode) {
			case Activity.RESULT_OK:
				mIvAdmin.setImageResource(R.mipmap.admin_activated);
//				 mDpm.lockNow();
				break;
			case Activity.RESULT_CANCELED:
//				 finish();
				break;

			default:
				break;
			}
		}
	}

	/*
	 * public void clickNext(View v) { Intent intent = new Intent(this,
	 * SjfdSetup5Activity.class); startActivity(intent); //
	 * enterAnima进入activity动画,exitAnim退出activity动画
	 * overridePendingTransition(R.anim.next_enter, R.anim.next_exit); finish();
	 * }
	 */

	/*
	 * public void clickPre(View v) { Intent intent = new Intent(this,
	 * SjfdSetup3Activity.class); startActivity(intent); //
	 * enterAnima进入activity动画,exitAnim退出activity动画
	 * overridePendingTransition(R.anim.pre_enter, R.anim.pre_exit); finish(); }
	 */

	@Override
	protected boolean doNext() {
		//  如果激活了设备管理员，才能下一步
		ComponentName who = new ComponentName(this, SafeAdminReceiver.class);
		if (!mDpm.isAdminActive(who)) {
			// 要开启手机防盗，必须开启设备管理员
			Toast.makeText(this, "要开启手机防盗，必须开启设备管理员", 0).show();
			return true;
		}
		Intent intent = new Intent(this, SjfdSetup5Activity.class);
		startActivity(intent);
		return false;
	}

	@Override
	protected boolean doPre() {
		Intent intent = new Intent(this, SjfdSetup3Activity.class);
		startActivity(intent);
		return false;
	}
}
