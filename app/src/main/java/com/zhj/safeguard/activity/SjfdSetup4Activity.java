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

public class SjfdSetup4Activity extends SjfdBaseSetupActivity {

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

		initEvnet();
	}

	private void initView() {
		mRlAdmin = (RelativeLayout) findViewById(R.id.setup4_rl_admin);
		mIvAdmin = (ImageView) findViewById(R.id.setup4_iv_admin);

		// 让imageview显示正确的状态
		ComponentName who = new ComponentName(SjfdSetup4Activity.this,
				SafeAdminReceiver.class);
		mIvAdmin.setImageResource(mDpm.isAdminActive(who) ? R.mipmap.admin_activated
				: R.mipmap.admin_inactivated);
	}

	private void initEvnet() {
		mRlAdmin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 如果没有激活，去激活，已经激活就取消
				ComponentName who = new ComponentName(SjfdSetup4Activity.this,
						SafeAdminReceiver.class);
				if (mDpm.isAdminActive(who)) {
					// 已经激活就取消
					mDpm.removeActiveAdmin(who);

					// 显示
					mIvAdmin.setImageResource(R.mipmap.admin_inactivated);
				} else {
					// 没有激活，去激活
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
		if (requestCode == REQUEST_CODE_ENABLE_ADMIN) {
			switch (resultCode) {
				case Activity.RESULT_OK:
					// 用户激活
					mIvAdmin.setImageResource(R.mipmap.admin_activated);
					break;
				case Activity.RESULT_CANCELED:
					break;
				default:
					break;
			}
		}
	}

	// public void clickPre(View view) {
	// // 跳转
	// Intent intent = new Intent(this, SjfdSetup3Activity.class);
	// startActivity(intent);
	//
	// // 动画
	// // enterAnim: 进入的动画, 让进入的activity做动画
	// // exitAnim: 退出的动画,让退出的activity做动画
	// overridePendingTransition(R.anim.pre_enter, R.anim.pre_exit);
	//
	// finish();
	// }

	// public void clickNext(View view) {
	// // 跳转
	// Intent intent = new Intent(this, SjfdSetup5Activity.class);
	// startActivity(intent);
	//
	// // 动画
	// // enterAnim: 进入的动画, 让进入的activity做动画
	// // exitAnim: 退出的动画,让退出的activity做动画
	// overridePendingTransition(R.anim.next_enter, R.anim.next_exit);
	//
	// finish();
	// }

	@Override
	protected boolean doNext() {

		// 如果激活了设备管理员，才可以下一步
		ComponentName who = new ComponentName(this, SafeAdminReceiver.class);
		if (!mDpm.isAdminActive(who)) {
			Toast.makeText(this, "要开启手机防盗，必须激活设备管理员", Toast.LENGTH_SHORT)
					.show();

			return true;
		}

		// 跳转
		Intent intent = new Intent(this, SjfdSetup5Activity.class);
		startActivity(intent);

		return false;
	}

	@Override
	protected boolean doPre() {
		// 跳转
		Intent intent = new Intent(this, SjfdSetup3Activity.class);
		startActivity(intent);

		return false;
	}

}