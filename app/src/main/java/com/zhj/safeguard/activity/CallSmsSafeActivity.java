package com.zhj.safeguard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.zhj.safeguard.R;

public class CallSmsSafeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_sms_safe);
    }

    public void clickAdd(View v){
        Intent intent = new Intent(CallSmsSafeActivity.this,BlackListEditActivity.class);
        startActivity(intent);
    }
}
