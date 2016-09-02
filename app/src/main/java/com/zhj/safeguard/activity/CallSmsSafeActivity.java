package com.zhj.safeguard.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhj.safeguard.DB.BlackDao;
import com.zhj.safeguard.R;
import com.zhj.safeguard.bean.BlackBean;
import com.zhj.safeguard.utils.Logger;

import java.util.List;

public class CallSmsSafeActivity extends Activity implements
        AdapterView.OnItemClickListener {
    protected static final String TAG = "CallSmsSafeActivity";

    private static final int REQUEST_CODE_ADD = 100;
    private static final int REQUEST_CODE_UPDATE = 101;

    private ListView mListView;
    private LinearLayout mLLProgress;
    private ImageView mIvEmpty;

    private List<BlackBean> mDatas;
    private BlackDao mDao;

    private CallSmsAdapter mAdapter;

    private int pageSize = 20;
    private boolean isLoadingMore = false;
    private boolean hasMore = true;// 有更多数据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_sms_safe);

        mDao = new BlackDao(this);

        // 初始化view
        initView();

        // 加载数据
        initData();

        // 事件
        initEvent();
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.css_listview);
        mLLProgress = (LinearLayout) findViewById(R.id.css_progressbar);
        mIvEmpty = (ImageView) findViewById(R.id.css_iv_empty);
    }

    private void initData() {
        // 给listView加载数据
        mAdapter = new CallSmsAdapter();
        mListView.setAdapter(mAdapter);// adapter--->List<数据>--->Bean

        // 显示进度
        mLLProgress.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(1200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // list数据初始化 所有
                // mDatas = mDao.findAll();
                mDatas = mDao.findPart(pageSize, 0);

                // adapter更新
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        // 隐藏进度
                        mLLProgress.setVisibility(View.GONE);

                        mAdapter.notifyDataSetChanged();
                        mListView.setEmptyView(mIvEmpty);// 设置为空的时候的view
                    }
                });
            }
        }).start();

    }

    // @Override
    // protected void onStart() {
    // super.onStart();
    //
    // // 加载数据
    // initData();
    // }

    private void initEvent() {
        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 当 滚动状态改变时 的回调
                // view: mListView
                // scrollState: 当前的状态

                // SCROLL_STATE_IDLE = 0:闲置状态
                // SCROLL_STATE_TOUCH_SCROLL = 1 ： 触摸滚动状态
                // SCROLL_STATE_FLING = 2:惯性滑动

                // Logger.d(TAG, "onScrollStateChanged : " + scrollState);

                if (isLoadingMore) {
                    // 正在加载更多
                    return;
                }

                // 如果是闲置状态，并且最后一个是可见的，去加载更多的数据
                // int lastVisiblePosition =
                // mListView.getLastVisiblePosition();// 获得最后可见的position
                //
                // if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
                // && (lastVisiblePosition == mAdapter.getCount() - 1)) {
                //
                // Logger.d(TAG, "加载更多数据");
                //
                // // 显示进度
                // mLLProgress.setVisibility(View.VISIBLE);
                // isLoadingMore = true;
                // new Thread(new Runnable() {
                //
                // @Override
                // public void run() {
                //
                // // 去加载第n页
                // final List<BlackBean> list = mDao.findPart(
                // pageSize, mDatas.size());
                //
                // try {
                // Thread.sleep(1200);
                // } catch (InterruptedException e) {
                // e.printStackTrace();
                // }
                //
                // // list数据初始化 所有
                //
                // // adapter更新
                // runOnUiThread(new Runnable() {
                // @Override
                // public void run() {
                //
                // // 隐藏进度
                // mLLProgress.setVisibility(View.GONE);
                //
                // mDatas.addAll(list);
                //
                // // adapter 更新
                // mAdapter.notifyDataSetChanged();
                //
                // mListView.setEmptyView(mIvEmpty);// 设置为空的时候的view
                // }
                // });
                //
                // isLoadingMore = false;
                // }
                // }).start();
                // }

                loadMore();

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                // 当 滚动时的回调
                // firstVisibleItem: 第一个可见的item的position
                // visibleItemCount:可见的item的数量
                // totalItemCount:item的总数量

                // Logger.d(TAG, "onScroll : " + firstVisibleItem + " "
                // + visibleItemCount + " " + totalItemCount);

                // 如果滑动到最后一个是可见的，去加载更多的数据

                // loadMore();
            }
        });
    }

    public void clickAdd(View view) {
        Intent intent = new Intent(this, BlackListEditActivity.class);
        intent.setAction(BlackListEditActivity.ACTION_ADD);
        startActivityForResult(intent, REQUEST_CODE_ADD);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

        BlackBean bean = mDatas.get(position);

        Intent intent = new Intent(this, BlackListEditActivity.class);
        intent.setAction(BlackListEditActivity.ACTION_UPDATE);
        intent.putExtra(BlackListEditActivity.KEY_NUMBER, bean.number);
        intent.putExtra(BlackListEditActivity.KEY_POSITION, position);
        intent.putExtra(BlackListEditActivity.KEY_TYPE, bean.type);
        startActivityForResult(intent, REQUEST_CODE_UPDATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_ADD) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    // 获得添加的number和type
                    String number = data
                            .getStringExtra(BlackListEditActivity.KEY_NUMBER);
                    int type = data.getIntExtra(BlackListEditActivity.KEY_TYPE, -1);

                    // 添加到mDatas中
                    BlackBean object = new BlackBean();
                    object.number = number;
                    object.type = type;
                    mDatas.add(object);

                    // adapter更新
                    mAdapter.notifyDataSetChanged();
                    break;
                case Activity.RESULT_CANCELED:
                    break;
                default:
                    break;
            }
        } else if (requestCode == REQUEST_CODE_UPDATE) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    int type = data.getIntExtra(BlackListEditActivity.KEY_TYPE, -1);
                    int position = data.getIntExtra(
                            BlackListEditActivity.KEY_POSITION, -1);

                    // 那条数据变化
                    BlackBean bean = mDatas.get(position);
                    bean.type = type;

                    // adapter更新
                    mAdapter.notifyDataSetChanged();
                    break;
                case Activity.RESULT_CANCELED:
                    break;
                default:
                    break;
            }
        }
    }

    private void loadMore() {
        // 没有更多数据
        if (!hasMore) {
            return;
        }

        if (mDatas == null) {
            return;
        }

        if (isLoadingMore) {
            return;
        }

        int lastVisiblePosition = mListView.getLastVisiblePosition();// 获得最后可见的position

        if (lastVisiblePosition == mAdapter.getCount() - 1) {

            // 加载更多
            Logger.d(TAG, "加载更多数据");

            // 显示进度
            mLLProgress.setVisibility(View.VISIBLE);
            isLoadingMore = true;
            new Thread(new Runnable() {

                @Override
                public void run() {

                    // 去加载第n页
                    final List<BlackBean> list = mDao.findPart(pageSize,
                            mDatas.size());

                    try {
                        Thread.sleep(1200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // list数据初始化 所有

                    // adapter更新
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            // 隐藏进度
                            mLLProgress.setVisibility(View.GONE);

                            if (list.size() < 20) {
                                // 没有更多数据
                                hasMore = false;

                                Toast.makeText(getApplicationContext(),
                                        "没有更多数据", Toast.LENGTH_SHORT).show();
                            }

                            mDatas.addAll(list);

                            // adapter 更新
                            mAdapter.notifyDataSetChanged();

                            mListView.setEmptyView(mIvEmpty);// 设置为空的时候的view

                            isLoadingMore = false;
                        }
                    });

                }
            }).start();
        }
    }

    private class CallSmsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mDatas != null) {

                // if (mDatas.size() != 0) {
                // mIvEmpty.setVisibility(View.GONE);
                // } else {
                // mIvEmpty.setVisibility(View.VISIBLE);
                // }

                return mDatas.size();
            }

            // mIvEmpty.setVisibility(View.VISIBLE);

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
                // 没有复用
                // 1. 给convertView加载布局
                convertView = View.inflate(CallSmsSafeActivity.this,
                        R.layout.item_css_callsms, null);
                // 2. 新建holder
                holder = new ViewHolder();
                // 3. 给convertView设置标记
                convertView.setTag(holder);
                // 4. 给holder去找view
                holder.ivDelete = (ImageView) convertView
                        .findViewById(R.id.item_css_iv_delete);
                holder.tvNumber = (TextView) convertView
                        .findViewById(R.id.item_css_tv_number);
                holder.tvType = (TextView) convertView
                        .findViewById(R.id.item_css_tv_type);
            } else {
                // 已经复用
                holder = (ViewHolder) convertView.getTag();
            }

            // 给holder的view设置数据
            final BlackBean bean = mDatas.get(position);

            String text = "";
            switch (bean.type) {
                case BlackBean.TYPE_CALL:
                    text = "电话拦截";
                    break;
                case BlackBean.TYPE_SMS:
                    text = "短信拦截";
                    break;
                case BlackBean.TYPE_ALL:
                    text = "电话+短信拦截";
                    break;
                default:
                    break;
            }
            holder.tvNumber.setText(bean.number);
            holder.tvType.setText(text);

            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    boolean delete = mDao.delete(bean.number);
                    if (delete) {
                        // 数据库删除成功
                        Toast.makeText(CallSmsSafeActivity.this, "删除成功",
                                Toast.LENGTH_SHORT).show();

                        // 从内存中移除数据
                        mDatas.remove(bean);

                        // 加载多一条
                        List<BlackBean> list = mDao.findPart(1, mDatas.size());
                        mDatas.addAll(list);

                        // UI更新
                        mAdapter.notifyDataSetChanged();
                    } else {
                        // 数据库删除失败
                        Toast.makeText(CallSmsSafeActivity.this, "删除失败",
                                Toast.LENGTH_SHORT).show();
                    }

                }
            });

            return convertView;
        }
    }

    private class ViewHolder {
        TextView tvNumber;
        TextView tvType;
        ImageView ivDelete;
    }
}
