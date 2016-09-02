package com.zhj.safeguard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nineoldandroids.animation.ObjectAnimator;
import com.zhj.safeguard.R;
import com.zhj.safeguard.bean.HomeBean;
import com.zhj.safeguard.utils.Constants;
import com.zhj.safeguard.utils.Logger;
import com.zhj.safeguard.utils.PreferenceUtils;
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
        Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                //手机防盗
                performSjfd();
                break;
            case 1:
                //骚扰拦截
                performSrlj();
                break;
            default:
                break;
        }

    }


    // 进入手机防盗界面
    private void performSjfd() {
        // 第一次使用此功能-->没有设置过密码
        String pwd = PreferenceUtils.getString(this, Constants.SJFD_PWD);
        if (TextUtils.isEmpty(pwd)) {
            // 弹出对话，提示设置密码
            Logger.d(TAG, "提示设置密码");
            showInitPwdDialog();
        } else {
            // 弹出对话，提示输入密码
            Logger.d(TAG, "提示输入密码");
            showEnterPwdDailog();
        }

    }
    //骚扰拦截
    private void performSrlj() {
        Intent intent = new Intent(this, CallSmsSafeActivity.class);
        startActivity(intent);
    }

    private void showEnterPwdDailog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // view
        View view = View.inflate(this, R.layout.dialog_enter_pwd, null);
        builder.setView(view);

        Button btnOk = (Button) view.findViewById(R.id.dialog_btn_ok);
        Button btnCancel = (Button) view.findViewById(R.id.dialog_btn_cancel);
        final EditText etPwd = (EditText) view.findViewById(R.id.dialog_et_pwd);

        final AlertDialog dialog = builder.show();

        btnOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 非空校验
                String pwd = etPwd.getText().toString().trim();
                if (TextUtils.isEmpty(pwd)) {
                    Toast.makeText(getApplicationContext(), "密码不能为空",
                            Toast.LENGTH_SHORT).show();
                    etPwd.requestFocus();// 获得焦点
                    return;
                }

                // 密码的校验
                String localPwd = PreferenceUtils.getString(HomeActivity.this,
                        Constants.SJFD_PWD);
                if (!pwd.equals(localPwd)) {
                    Toast.makeText(getApplicationContext(), "密码错误",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // 隐藏dialog
                dialog.dismiss();

                // 获取标记,开启防盗保护的标记
                boolean flag = PreferenceUtils.getBoolean(
                        getApplicationContext(), Constants.SJFD_PROTECTING);

                // 判断是否有开启防盗保护
                if (flag) {
                    // 进入手机防盗界面
                    Logger.d(TAG, "进入手机防盗界面");

                    Intent intent = new Intent(HomeActivity.this,
                            SjfdActivity.class);
                    startActivity(intent);
                } else {
                    // 进入手机防盗设置界面
                    Logger.d(TAG, "进入设置向导界面");
                    Intent intent = new Intent(HomeActivity.this,
                            SjfdSetup1Activity.class);
                    startActivity(intent);
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 隐藏dialog
                dialog.dismiss();
            }
        });
    }

    private void showInitPwdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // view
        View view = View.inflate(this, R.layout.dialog_init_pwd, null);
        builder.setView(view);

        Button btnOk = (Button) view.findViewById(R.id.dialog_btn_ok);
        Button btnCancel = (Button) view.findViewById(R.id.dialog_btn_cancel);
        final EditText etPwd = (EditText) view.findViewById(R.id.dialog_et_pwd);
        final EditText etConfirm = (EditText) view
                .findViewById(R.id.dialog_et_confirm);

        final AlertDialog dialog = builder.show();

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击确定按钮
                // 非空校验
                String pwd = etPwd.getText().toString().trim();
                if (TextUtils.isEmpty(pwd)) {
                    Toast.makeText(getApplicationContext(), "密码不能为空",
                            Toast.LENGTH_SHORT).show();
                    etPwd.requestFocus();// 获得焦点
                    return;
                }

                // 非空校验
                String confirm = etConfirm.getText().toString().trim();
                if (TextUtils.isEmpty(confirm)) {
                    Toast.makeText(getApplicationContext(), "确认密码不能为空",
                            Toast.LENGTH_SHORT).show();
                    etConfirm.requestFocus();// 获得焦点
                    return;
                }

                // 两次密码必须一致
                if (!confirm.equals(pwd)) {
                    Toast.makeText(getApplicationContext(), "两次密码不一致",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // 密码要存储
                PreferenceUtils.putString(getApplicationContext(),
                        Constants.SJFD_PWD, pwd);

                // 隐藏dialog
                dialog.dismiss();

                // 进入设置向导界面
                Logger.d(TAG, "进入设置向导界面");
                Intent intent = new Intent(HomeActivity.this,
                        SjfdSetup1Activity.class);
                startActivity(intent);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 隐藏dialog
                dialog.dismiss();
            }
        });
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
