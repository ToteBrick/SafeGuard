package com.zhj.safeguard.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.zhj.safeguard.R;
import com.zhj.safeguard.view.shimmer.Shimmer;
import com.zhj.safeguard.view.shimmer.ShimmerTextView;

public class HomeActivity extends AppCompatActivity {

    private Shimmer shimmer;
    private ShimmerTextView mHomeTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        init();
        initView();
        initData();
        initListner();
    }

    private void init() {

    }

    private void initView() {
        mHomeTitle = (ShimmerTextView) findViewById(R.id.home_tv_title);
        if (shimmer != null && shimmer.isAnimating()) {
            shimmer.cancel();
        } else {
            shimmer = new Shimmer();
            shimmer.start(mHomeTitle);
        }

    }

    private void initData() {

    }

    private void initListner() {

    }

}
