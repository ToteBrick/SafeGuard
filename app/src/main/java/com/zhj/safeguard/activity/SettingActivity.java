package com.zhj.safeguard.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.zhj.safeguard.R;
import com.zhj.safeguard.service.CallSmsService;
import com.zhj.safeguard.utils.Constants;
import com.zhj.safeguard.utils.PreferenceUtils;
import com.zhj.safeguard.utils.ServiceStateUtils;
import com.zhj.safeguard.view.SettingItemView;


public class SettingActivity extends Activity {
    private SettingItemView mSivAutoUpdate; //自动更新
    private SettingItemView mSivCallSmsSafe; //骚扰拦截
    private SettingItemView mSivNumberAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        initEvent();
    }

    private void initView() {
        mSivAutoUpdate = (SettingItemView) findViewById(R.id.setting_auto_update);
        mSivCallSmsSafe = (SettingItemView) findViewById(R.id.setting_siv_callsmssafe);
        mSivNumberAddress = (SettingItemView) findViewById(R.id.setting_siv_number_address);
        // 获得当前数据
        mSivAutoUpdate.setToggleState(PreferenceUtils.getBoolean(this,
                Constants.AUTOUPDATE, true));

    }

    @Override
    protected void onStart() {
        super.onStart();
    //     回显服务状态

     //    显示骚扰拦截服务状态
        mSivCallSmsSafe.setToggleState(ServiceStateUtils.isRunning(
                getApplicationContext(), CallSmsService.class));

//        // 显示归属地服务状态
//        mSivNumberAddress.setToggleState(ServiceStateUtils.isRunning(
//                getApplicationContext(), NumberAddressService.class));
    }

    private void initEvent() {
        //自动更新服务
        mSivAutoUpdate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // ui图标开的时候关，关的时候开
                mSivAutoUpdate.toggle();
                // 业务逻辑：下次进入不检测网络-》打标记-》持久化存储
                PreferenceUtils.putBoolean(SettingActivity.this, "autoupdate",
                        mSivAutoUpdate.getToggleState());

            }
        });
        //骚扰拦截服务
        mSivCallSmsSafe.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 如果服务是开启就关闭，关闭就开启
                Intent intent = new Intent(SettingActivity.this,
                        CallSmsService.class);
                mSivCallSmsSafe.toggle();
                if (ServiceStateUtils.isRunning(getApplicationContext(),
                        CallSmsService.class)) {
                    stopService(intent);
                } else {
                    startService(intent);
                }
            }
        });
        //号码归属地服务
        mSivNumberAddress.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 如果服务是开启就关闭，关闭就开启
//                Intent intent = new Intent(SettingActivity.this,
//                        NumberAddressService.class);
                mSivNumberAddress.toggle();
//                if (ServiceStateUtils.isRunning(getApplicationContext(),
//                        NumberAddressService.class)) {
//                    stopService(intent);
//                } else {
//                    startService(intent);
//                }
            }
        });
    }
}
