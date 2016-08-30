package com.zhj.safeguard.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.zhj.safeguard.R;
import com.zhj.safeguard.utils.PackageInfoUtils;

public class SplashActivity extends Activity {

    private TextView  mTvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mTvVersion = (TextView) findViewById(R.id.splash_tv_version);
        mTvVersion.setText("版本号："+ PackageInfoUtils.getVersionName(this));
    }
}
