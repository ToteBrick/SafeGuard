package com.zhj.safeguard.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.zhj.safeguard.DB.BlackDao;
import com.zhj.safeguard.R;
import com.zhj.safeguard.bean.BlackBean;

public class BlackListEditActivity extends Activity implements View.OnClickListener {

    public static final String ACTION_ADD = "add";
    public static final String ACTION_UPDATE = "update";

    public static final String KEY_NUMBER = "number";
    public static final String KEY_TYPE = "type";
    public static final String KEY_POSITION = "position";

    private Button mBtnOk;
    private Button mBtnCancel;
    private EditText mEtInput;
    private RadioGroup mRgType;
    private TextView mTvTitle;

    private BlackDao mDao;

    private boolean isUpdate = false;
    private int mCurrrentPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacklist);

        mDao = new BlackDao(this);

        // 初始化view
        initView();

        // event
        initEvent();
    }

    private void initView() {
        mTvTitle = (TextView) findViewById(R.id.bl_tv_title);
        mBtnOk = (Button) findViewById(R.id.bl_btn_ok);
        mBtnCancel = (Button) findViewById(R.id.bl_btn_cancel);
        mEtInput = (EditText) findViewById(R.id.bl_et_input);
        mRgType = (RadioGroup) findViewById(R.id.bl_rg_type);

        // 获得intent判断是添加还是更新
        Intent intent = getIntent();
        String action = intent.getAction();
        if (ACTION_UPDATE.equals(action)) {
            // 更新
            isUpdate = true;
            mCurrrentPosition = intent.getIntExtra(KEY_POSITION, -1);

            // 1. title显示
            mTvTitle.setText("更新黑名单");

            // 2. button显示
            mBtnOk.setText("更新");

            // 3. 输入框
            mEtInput.setEnabled(false);
            String number = intent.getStringExtra(KEY_NUMBER);
            mEtInput.setText(number);

            // 4. radioGroup选中
            int type = intent.getIntExtra(KEY_TYPE, -1);
            int id = -1;
            switch (type) {
                case BlackBean.TYPE_CALL:
                    id = R.id.bl_rb_call;
                    break;
                case BlackBean.TYPE_SMS:
                    id = R.id.bl_rb_sms;
                    break;
                case BlackBean.TYPE_ALL:
                    id = R.id.bl_rb_all;
                    break;
                default:
                    break;
            }
            mRgType.check(id);
        } else {
            // 添加
            isUpdate = false;
        }
    }

    private void initEvent() {
        mBtnOk.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnOk) {
            clickOk();
        } else if (v == mBtnCancel) {
            clickCancel();
        }
    }

    private void clickCancel() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    private void clickOk() {
        // 校验内容
        // 输入框
        String number = mEtInput.getText().toString().trim();

        if (TextUtils.isEmpty(number)) {
            Toast.makeText(this, "号码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // radiogroup
        int id = mRgType.getCheckedRadioButtonId();
        if (-1 == id) {
            Toast.makeText(this, "请选择拦截类型", Toast.LENGTH_SHORT).show();
            return;
        }

        int type = BlackBean.TYPE_CALL;
        switch (id) {
            case R.id.bl_rb_call:
                type = BlackBean.TYPE_CALL;
                break;
            case R.id.bl_rb_sms:
                type = BlackBean.TYPE_SMS;
                break;
            case R.id.bl_rb_all:
                type = BlackBean.TYPE_ALL;
                break;
            default:
                break;
        }

        if (isUpdate) {
            // 更新号码
            boolean update = mDao.update(number, type);
            if (update) {
                Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show();

                // 结果返回
                Intent data = new Intent();
                data.putExtra(KEY_TYPE, type);
                data.putExtra(KEY_POSITION, mCurrrentPosition);
                setResult(Activity.RESULT_OK, data);

            } else {
                Toast.makeText(this, "更新失败", Toast.LENGTH_SHORT).show();
            }
        } else {
            // 添加号码
            boolean add = mDao.add(number, type);
            if (add) {
                // 添加成功
                Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();

                // 结果返回
                Intent data = new Intent();
                data.putExtra(KEY_NUMBER, number);
                data.putExtra(KEY_TYPE, type);
                setResult(Activity.RESULT_OK, data);
            } else {
                // 添加失败
                Toast.makeText(this, "添加失败", Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }
}
