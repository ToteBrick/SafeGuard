package com.zhj.safeguard.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zhj.safeguard.R;
import com.zhj.safeguard.utils.Constants;
import com.zhj.safeguard.utils.PreferenceUtils;

public class SjfdSetup3Activity extends SjfdBaseActivity {

	private static final int REQUEST_CODE_CONTACT = 100;
	private EditText mEtNumber;
	private Button mBtnSelected;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sjfd_setup3);

		initView();

		initEvent();
	}

	private void initEvent() {
		mBtnSelected.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 跳转到联系人界面
				Intent intent = new Intent(SjfdSetup3Activity.this,
						ContactSelectedActivity2.class);
				startActivityForResult(intent, REQUEST_CODE_CONTACT);
			}
		});
	}

	private void initView() {
		mEtNumber = (EditText) findViewById(R.id.setup3_et_number);
		mBtnSelected = (Button) findViewById(R.id.setup3_btn_slected);
		// 如果设置过安全号码，要回显
		String number = PreferenceUtils.getString(getApplicationContext(),
				Constants.SJFD_NUMBER);
		if (!TextUtils.isEmpty(number)) {
			mEtNumber.setText(number);
			mEtNumber.setSelection(number.length());
		}
	}

	/*
	 * public void clickNext(View v) { Intent intent = new Intent(this,
	 * SjfdSetup4Activity.class); startActivity(intent); //
	 * enterAnima进入activity动画,exitAnim退出activity动画
	 * overridePendingTransition(R.anim.next_enter, R.anim.next_exit); finish();
	 * }
	 */

	/*
	 * public void clickPre(View v) { Intent intent = new Intent(this,
	 * SjfdSetup2Activity.class); startActivity(intent); //
	 * enterAnima进入activity动画,exitAnim退出activity动画
	 * overridePendingTransition(R.anim.pre_enter, R.anim.pre_exit); finish(); }
	 */

	@Override
	protected boolean doNext() {
		// 校验输入框是否设置安全号码
		// 如果没设置，提醒设置，不让进入下一页
		String number = mEtNumber.getText().toString().trim();
		if (TextUtils.isEmpty(number)) {
			Toast.makeText(getApplicationContext(), "如果要开启防盗保护，必须设置安全号码", Toast.LENGTH_SHORT)
					.show();
			return true;
		}
		// 如果有，跳转到下一页，并且存储安全号码
		PreferenceUtils.putString(getApplicationContext(),
				Constants.SJFD_NUMBER, number);
		Intent intent = new Intent(this, SjfdSetup4Activity.class);
		startActivity(intent);
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_CONTACT) {

			switch (resultCode) {
			case Activity.RESULT_OK:
				String number = data
						.getStringExtra(ContactSelectedActivity.KEY_NUMBER);
				mEtNumber.setText(number);
				mEtNumber.setSelection(number.length());
				break;

			default:
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected boolean doPre() {
		Intent intent = new Intent(this, SjfdSetup2Activity.class);
		startActivity(intent);
		return false;

	}
}
