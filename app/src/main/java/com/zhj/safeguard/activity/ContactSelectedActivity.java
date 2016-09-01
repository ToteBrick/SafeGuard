package com.zhj.safeguard.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zhj.safeguard.R;
import com.zhj.safeguard.bean.ContactBean;
import com.zhj.safeguard.utils.ContactUtils;

import java.util.List;

public class ContactSelectedActivity extends Activity {
	public static final String KEY_NUMBER = "number";
	private ListView mListView;
	private List<ContactBean> mDatas;
	private ContactSelectedAdapter mAdapter;
	private ProgressBar mProgressBar;

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
				ContactBean bean = mDatas.get(position);
				Intent data = new Intent();
				data.putExtra(KEY_NUMBER, bean.number);
				setResult(Activity.RESULT_OK, data);
				// 当前界面结束
				finish();
			}
		});
	}

	private void initData() {
		mAdapter = new ContactSelectedAdapter();
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
				mDatas = ContactUtils
						.getAllContacts(ContactSelectedActivity.this);
				runOnUiThread(new Runnable() {// runnable在主线程执行
					public void run() {
						mProgressBar.setVisibility(View.GONE);
						// 更新adapter-->ui
						mAdapter.notifyDataSetChanged();
					}
				});
			}
		}).start();

	}

	private void initView() {
		mListView = (ListView) findViewById(R.id.cs_listview);
		mProgressBar = (ProgressBar) findViewById(R.id.cs_progressbar);
	}

	private class ContactSelectedAdapter extends BaseAdapter {

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

			ViewHolder holder = null;

			if (convertView == null) {
				// 1,加载布局
				convertView = View.inflate(ContactSelectedActivity.this,
						R.layout.item_contacted_selected, null);
				// 2,初始化holder
				holder = new ViewHolder();

				// 3,给convertview设置标记
				convertView.setTag(holder);
				// 4,给holder找view
				holder.ivIcon = (ImageView) convertView
						.findViewById(R.id.item_cs_iv_icon);
				holder.tvName = (TextView) convertView
						.findViewById(R.id.item_cs_tv_name);
				holder.tvNumber = (TextView) convertView
						.findViewById(R.id.item_cs_tv_number);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// 给view设置数据
			ContactBean bean = mDatas.get(position);
			holder.tvName.setText(bean.name);
			holder.tvNumber.setText(bean.number);

			// picture
			Bitmap icon = ContactUtils.getContactIcon(
					ContactSelectedActivity.this, bean.contactId);

			if (icon != null) {
				holder.ivIcon.setImageBitmap(icon);
			}

			return convertView;
		}
		/*
		 * if (convertView == null) {
		 * convertView.inflate(ContactSelectedActivity.this,
		 * R.layout.item_contacted_selected, null); }
		 * 
		 * ImageView ivIcon = (ImageView) convertView
		 * .findViewById(R.id.item_cs_iv_icon); TextView tvName = (TextView)
		 * convertView .findViewById(R.id.item_cs_tv_name); TextView tvNumber =
		 * (TextView) convertView .findViewById(R.id.item_cs_tv_number);
		 * 
		 * // 给view设置数据 ContactBean bean = mDatas.get(position);
		 * tvName.setText(bean.name); tvNumber.setText(bean.number);
		 * 
		 * // picture Bitmap icon = ContactUtils.getContactIcon(
		 * ContactSelectedActivity.this, bean.contactId);
		 * 
		 * if (icon != null) { ivIcon.setImageBitmap(icon); } return
		 * convertView; }
		 */

	}

	 class ViewHolder {

		ImageView ivIcon;
		TextView tvName, tvNumber;
	}

}
