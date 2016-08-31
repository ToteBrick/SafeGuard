package com.zhj.safeguard.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhj.safeguard.R;

public class SettingItemView extends RelativeLayout {

	private TextView mTvTitle;
	private ImageView mIvIcon;
	private RelativeLayout mRoot;
	public final static int BACKGROUD_FIRST = 0;
	public final static int BACKGROUD_MIDDLE = 1;
	public final static int BACKGROUD_LAST = 2;

	private boolean isOpen = true;

	// new
	public SettingItemView(Context context) {
		this(context, null);
	}

	// layout
	public SettingItemView(Context context, AttributeSet set) {
		super(context, set);
		// 1,加载布局
		View.inflate(context, R.layout.view_setting_item, this);
		// 2,找控件
		mTvTitle = (TextView) findViewById(R.id.view_tv_title);
		mIvIcon = (ImageView) findViewById(R.id.view_iv_icon);
		// findViewById(R.id.view_tv_title);
		// 3，du 属性值
		TypedArray ta = context.obtainStyledAttributes(set,
				R.styleable.SettingItemView);
		// 4,读属性
		String sivTitle = ta.getString(R.styleable.SettingItemView_sivTitle);
		int backgroud = ta.getInt(R.styleable.SettingItemView_sivBackground,
				BACKGROUD_FIRST);
		boolean flag = ta.getBoolean(R.styleable.SettingItemView_sivToggle,
				true);
		mIvIcon.setVisibility(flag ? View.VISIBLE : View.GONE);
		// 5,设置值
		mTvTitle.setText(sivTitle);
		switch (backgroud) {
		case BACKGROUD_FIRST:

			findViewById(R.id.view_root).setBackgroundResource(
					R.drawable.setting_first_item_selector);
			break;
		case BACKGROUD_MIDDLE:

			findViewById(R.id.view_root).setBackgroundResource(
					R.drawable.setting_middle_item_selector);
			break;
		case BACKGROUD_LAST:

			findViewById(R.id.view_root).setBackgroundResource(
					R.drawable.setting_last_item_selector);
			break;
		default:
			break;
		}

		ta.recycle();

	}

	// 开的时候关，关的时候开
	public void toggle() {
		mIvIcon.setImageResource(isOpen ? R.mipmap.off : R.mipmap.on);
		isOpen = !isOpen;
	}

	// 获取开关状态
	public boolean getToggleState() {
		return isOpen;
	}

	// 设置开关状态
	public void setToggleState(boolean state) {
		this.isOpen = state;
		mIvIcon.setImageResource(isOpen ? R.mipmap.on : R.mipmap.off);
	}
}
