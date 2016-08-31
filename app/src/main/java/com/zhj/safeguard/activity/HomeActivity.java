package com.zhj.safeguard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nineoldandroids.animation.ObjectAnimator;
import com.zhj.safeguard.R;
import com.zhj.safeguard.bean.HomeBean;
import com.zhj.safeguard.view.shimmer.Shimmer;
import com.zhj.safeguard.view.shimmer.ShimmerTextView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ImageView mIv_logo;
    private GridView mGradview;
    private List<HomeBean> mDatas;
    private Shimmer shimmer;
    private ShimmerTextView mHomeTitle;
    private final static String[] TITLES = new String[]{"手机防盗", "骚扰拦截",
            "软件管家", "进程管理", "流量统计", "手机杀毒", "缓存清理", "常用工具"};
    private final static String[] DESCS = new String[]{"远程定位手机", "全面拦截骚扰",
            "管理您的软件", "管理运行进程", "流量一目了然", "病毒无处藏身", "系统快如火箭", "工具大全"};
    private final static int[] ICONS = new int[]{R.mipmap.sjfd,
            R.mipmap.srlj, R.mipmap.rjgj, R.mipmap.jcgl, R.mipmap.lltj,
            R.mipmap.sjsd, R.mipmap.hcql, R.mipmap.cygj};
    private static final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initView();
        //让logo动
        logoAnimator();
        initData();
        initListner();
    }


    private void initView() {

        mIv_logo = (ImageView) findViewById(R.id.home_iv_logo);
        mGradview = (GridView) findViewById(R.id.home_gridview);
        mHomeTitle = (ShimmerTextView) findViewById(R.id.home_tv_title);
        if (shimmer != null && shimmer.isAnimating()) {
            shimmer.cancel();
        } else {
            shimmer = new Shimmer();
            shimmer.start(mHomeTitle);
        }

    }

    private void logoAnimator() {
//        mIv_logo.setRotationY();
        ObjectAnimator animator = ObjectAnimator.ofFloat(mIv_logo, "rotationY", 0, 45, 120, 180, 270, 360);
        animator.setDuration(2000);
        animator.setRepeatMode(ObjectAnimator.REVERSE);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.start();
    }

    private void initData() {

        mDatas = new ArrayList<HomeBean>();
        for (int i = 0; i < ICONS.length; i++) {
            HomeBean bean = new HomeBean();
            bean.icon = ICONS[i];
            bean.title = TITLES[i];
            bean.desc = DESCS[i];
            mDatas.add(bean);
        }
        mGradview.setAdapter(new HomeAdapter());
    }

    private void initListner() {
        mGradview.setOnItemClickListener(this);
    }

    public void clickSetting(View v) {
        Intent intent = new Intent(HomeActivity.this,SettingActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    class HomeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mDatas != null) {
                return mDatas.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (mDatas != null) {
                return mDatas.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(HomeActivity.this,
                        R.layout.item_home, null);
            }
            // 初始化view
            ImageView ivIcon = (ImageView) convertView
                    .findViewById(R.id.item_home_iv_icon);
            TextView tvTitle = (TextView) convertView
                    .findViewById(R.id.item_home_tv_title);
            TextView tvDesc = (TextView) convertView
                    .findViewById(R.id.item_home_tv_desc);

            // show data
            HomeBean bean = mDatas.get(position);
            ivIcon.setImageResource(bean.icon);
            tvTitle.setText(bean.title);
            tvDesc.setText(bean.desc);
            return convertView;
        }
    }

}
