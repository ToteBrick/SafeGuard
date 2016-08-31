package com.zhj.safeguard.activity;


import android.content.Intent;
import android.os.Bundle;

import com.zhj.safeguard.R;

public class SjfdSetup1Activity extends SjfdBaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sjfd_setup1);
	}
	

	@Override
	protected boolean doNext() {
		Intent intent = new Intent(this, SjfdSetup2Activity.class);
		startActivity(intent);
		return false;
	}


	@Override
	protected boolean doPre() {
		return true;
	}

}
