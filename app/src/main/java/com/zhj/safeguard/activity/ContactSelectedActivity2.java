package com.zhj.safeguard.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zhj.safeguard.R;
import com.zhj.safeguard.bean.ContactBean;
import com.zhj.safeguard.utils.ContactUtils;


public class ContactSelectedActivity2 extends Activity {
	public static final String KEY_NUMBER = "number";
	private ListView mListView;
	private ContactSelectedAdapter mAdapter;
	private ProgressBar mProgressBar;
	private Cursor mCursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_selectled);
		initView();
		initData();
		initEvent();
	}

	private void initEvent() {

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//改变游标指向,选中点击的item.
				mCursor.moveToPosition(position);
				ContactBean bean = ContactUtils.getContactBean(mCursor);
				
				Intent data = new Intent();
				data.putExtra(KEY_NUMBER, bean.number);
				setResult(Activity.RESULT_OK, data);
				// 当前界面结束
				finish();
				
			}
		});
	}

	private void initData() {
		mAdapter = new ContactSelectedAdapter(this, null);
		mListView.setAdapter(mAdapter); // adapter-->list<data>-->data
		// is decided by
		// view

		mProgressBar.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {

			

			@Override
			public void run() {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
				mCursor = ContactUtils
						.getAllCursor(ContactSelectedActivity2.this);
				runOnUiThread(new Runnable() {// runnable在主线程执行
					public void run() {
						mProgressBar.setVisibility(View.GONE);
						
						
						// 改变游标
						mAdapter.changeCursor(mCursor);
					}
				});
			}
		}).start();

	}

	private void initView() {
		mListView = (ListView) findViewById(R.id.cs_listview);
		mProgressBar = (ProgressBar) findViewById(R.id.cs_progressbar);
	}

	private class ContactSelectedAdapter extends CursorAdapter {//针对大数据

		public ContactSelectedAdapter(Context context, Cursor c) {
			super(context, c);

		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			// itemview第一次创建时调用
			return View.inflate(ContactSelectedActivity2.this,
					R.layout.item_contacted_selected, null);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			// 绑定数据时调用,显示的view
			// cursor 自动move到position
			ImageView ivIcon = (ImageView) view
					.findViewById(R.id.item_cs_iv_icon);
			TextView tvName = (TextView) view
					.findViewById(R.id.item_cs_tv_name);
			TextView tvNumber = (TextView) view
					.findViewById(R.id.item_cs_tv_number);

			// 设置数据
			ContactBean bean = ContactUtils.getContactBean(cursor);
			tvName.setText(bean.name);
			tvNumber.setText(bean.number);

			Bitmap icon = ContactUtils.getContactIcon(ContactSelectedActivity2.this,
					bean.contactId);
			
			if (icon!=null) {
				ivIcon.setImageBitmap(icon);
			}
		}

	}

}
