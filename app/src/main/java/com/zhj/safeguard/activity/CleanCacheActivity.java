package com.zhj.safeguard.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhj.safeguard.R;
import com.zhj.safeguard.bean.CacheBean;
import com.zhj.safeguard.utils.Logger;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CleanCacheActivity extends Activity {

    protected static final String TAG = "CleanCacheActivity";
    private PackageManager mPm;

    private ImageView mIvIcon;
    private ImageView mIvLine;
    private ProgressBar mPbProgress;
    private TextView mTvName;
    private TextView mTvCacheSize;

    // 容器
    private RelativeLayout mRlScanContainer;
    private RelativeLayout mRlResultContainer;

    private Button mBtnScan;
    private TextView mTvResult;

    private ScanTask mTask;

    private int mCacheAppCount;// 有几个应用有缓存
    private int mCacheTotalSize;// 缓存的总大小

    private ListView mListView;
    private Button mBtnClean;

    private List<CacheBean> mDatas;

    private CleanCacheAdapter mAdapter;
    private ClearCacheObserver mClearCacheObserver;

    private boolean isRuning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean_cache);

        // 包管理器
        mPm = getPackageManager();
        mClearCacheObserver = new ClearCacheObserver();

        // 初始化view
        initView();

        initEvent();

        // 初始化数据
        initData();
    }

    private void initView() {
        mIvIcon = (ImageView) findViewById(R.id.cc_scan_iv_icon);
        mIvLine = (ImageView) findViewById(R.id.cc_scan_iv_line);
        mPbProgress = (ProgressBar) findViewById(R.id.cc_scan_pb_progress);
        mTvCacheSize = (TextView) findViewById(R.id.cc_scan_tv_cachesize);
        mTvName = (TextView) findViewById(R.id.cc_scan_tv_name);

        mRlResultContainer = (RelativeLayout) findViewById(R.id.cc_result_container);
        mRlScanContainer = (RelativeLayout) findViewById(R.id.cc_scan_container);

        mBtnScan = (Button) findViewById(R.id.cc_result_btn_scan);
        mTvResult = (TextView) findViewById(R.id.cc_result_tv_result);

        mListView = (ListView) findViewById(R.id.cc_listview);
        mBtnClean = (Button) findViewById(R.id.cc_btn_clean);
    }

    private void initEvent() {
        mBtnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScan();
            }
        });

        mBtnClean.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 一键清理所有的缓存
                // public abstract void freeStorageAndNotify(long
                // freeStorageSize, IPackageDataObserver observer);

                if (mCacheTotalSize <= 0) {
                    Toast.makeText(getApplicationContext(), "没有要清理的",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    Method method = mPm.getClass().getDeclaredMethod(
                            "freeStorageAndNotify", long.class,
                            IPackageDataObserver.class);

                    method.invoke(mPm, Long.MAX_VALUE, mClearCacheObserver);

                    // 重新扫描
                    startScan();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (AbsListView.OnScrollListener.SCROLL_STATE_IDLE == scrollState
                        && mListView.getFirstVisiblePosition() == 0) {
                    mBtnScan.setEnabled(true);

                    // 清理按钮可用
                    mBtnClean.setEnabled(true);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

            }
        });
    }

    private void initData() {
        // 给listView加载数据
        mAdapter = new CleanCacheAdapter();
        mListView.setAdapter(mAdapter);// -->list<CacheBean>

        startScan();
    }

    private void startScan() {
        mTask = new ScanTask();
        mTask.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();

        isRuning = false;
    }

    private class ScanTask extends AsyncTask<Void, CacheBean, Void> {
        private int progress;
        private int max;

        protected void onPreExecute() {
            isRuning = true;

            // 清理按钮不可用
            mBtnClean.setEnabled(false);

            // 清空数据
            mCacheAppCount = 0;
            mCacheTotalSize = 0;
            // 给list添加数据.ui更新
            if (mDatas == null) {
                mDatas = new ArrayList<CacheBean>();
            }
            mDatas.clear();

            // 显示扫描部分，隐藏结果部分
            mRlScanContainer.setVisibility(View.VISIBLE);
            mRlResultContainer.setVisibility(View.GONE);

            // 扫描线做动画
            TranslateAnimation animation = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, 0,
                    Animation.RELATIVE_TO_PARENT, 0,
                    Animation.RELATIVE_TO_PARENT, 0,
                    Animation.RELATIVE_TO_PARENT, 1);
            animation.setDuration(800);
            animation.setRepeatCount(Animation.INFINITE);
            animation.setRepeatMode(Animation.REVERSE);
            mIvLine.startAnimation(animation);
        }

        ;

        @Override
        protected Void doInBackground(Void... params) {

            // 获得所有的包
            List<PackageInfo> packages = mPm.getInstalledPackages(0);

            // 设置进度最大值
            max = packages.size();
            runOnUiThread(new Runnable() {  //低版本进度条可以直接在子线程更新，可以确定api 23 不行。
                @Override
                public void run() {
                    mPbProgress.setMax(max);
                }
            });

            for (PackageInfo pkg : packages) {
                if (!isRuning) {
                    break;
                }

                progress++;

                String packageName = pkg.packageName;
                // 获得具体的包的缓存信息
                // mPm.getPackageSizeInfo(packageName, mStatsObserver);

                try {
                    Method method = mPm.getClass().getMethod(
                            "getPackageSizeInfo", String.class,
                            IPackageStatsObserver.class);
                    method.setAccessible(true);
                    method.invoke(mPm, packageName, mStatsObserver);

                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        protected void onProgressUpdate(CacheBean... values) {
            CacheBean bean = values[0];

            mIvIcon.setImageDrawable(bean.icon);
            mPbProgress.setProgress(progress);
            mTvName.setText(bean.name);
            mTvCacheSize.setText("缓存大小:"
                    + Formatter.formatFileSize(getApplicationContext(),
                    bean.cacheSize));

            // 判断缓存的大小
            if (bean.cacheSize > 0) {
                mCacheAppCount++;
                mCacheTotalSize += bean.cacheSize;
            }

            // 让缓存的app排在最前面
            if (bean.cacheSize > 0) {
                mDatas.add(0, bean);
            } else {
                mDatas.add(bean);
            }
            mAdapter.notifyDataSetChanged();

            // ListView滑动到底部
            mListView.smoothScrollToPosition(mAdapter.getCount());
        }

        public void updateProgress(CacheBean bean) {
            publishProgress(bean);
        }

        @Override
        protected void onPostExecute(Void result) {
            // 显示结果部分，隐藏扫描部分
            mRlScanContainer.setVisibility(View.GONE);
            mRlResultContainer.setVisibility(View.VISIBLE);

            mTvResult.setText("总共有"
                    + mCacheAppCount
                    + "个有缓存，共"
                    + Formatter.formatFileSize(getApplicationContext(),
                    mCacheTotalSize));

            // listView滑动到顶部
            mListView.smoothScrollToPosition(0);

            // 设置扫描按钮不可用
            mBtnScan.setEnabled(false);
        }
    }

    private final IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub() {
        public void onGetStatsCompleted(PackageStats stats, boolean succeeded) {
            long cacheSize = stats.cacheSize;
            String packageName = stats.packageName;

            // Log.d(TAG, "package : " + packageName);
            // Log.d(TAG, "cacheSize : " + cacheSize);
            // Log.d(TAG, "---------------------------------");

            try {
                ApplicationInfo applicationInfo = mPm.getApplicationInfo(
                        packageName, 0);

                String name = applicationInfo.loadLabel(mPm).toString();
                Drawable icon = applicationInfo.loadIcon(mPm);

                CacheBean bean = new CacheBean();
                bean.cacheSize = cacheSize;
                bean.name = name;
                bean.icon = icon;
                bean.packageName = packageName;
                mTask.updateProgress(bean);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private class CleanCacheAdapter extends BaseAdapter {

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
                convertView = View.inflate(CleanCacheActivity.this,
                        R.layout.item_clean_cache, null);
                holder = new ViewHolder();
                convertView.setTag(holder);

                holder.ivClean = (ImageView) convertView
                        .findViewById(R.id.item_cc_iv_clean);
                holder.ivIcon = (ImageView) convertView
                        .findViewById(R.id.item_cc_iv_icon);
                holder.tvName = (TextView) convertView
                        .findViewById(R.id.item_cc_tv_name);
                holder.tvCacheSize = (TextView) convertView
                        .findViewById(R.id.item_cc_tv_cachesizes);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // 设置数据
            final CacheBean bean = mDatas.get(position);
            holder.ivIcon.setImageDrawable(bean.icon);
            holder.tvName.setText(bean.name);
            holder.tvCacheSize.setText("缓存大小:"
                    + Formatter.formatFileSize(getApplicationContext(),
                    bean.cacheSize));

            holder.ivClean.setVisibility(bean.cacheSize > 0 ? View.VISIBLE
                    : View.GONE);

            holder.ivClean.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // 清理缓存
                    /*
					 * mPm.deleteApplicationCacheFiles(bean.packageName,
					 * mClearCacheObserver);
					 */

                    // public abstract void deleteApplicationCacheFiles(String
                    // packageName,
                    // IPackageDataObserver observer);

                    // try {
                    // Method method = mPm.getClass().getDeclaredMethod(
                    // "deleteApplicationCacheFiles", String.class,
                    // IPackageDataObserver.class);
                    // method.invoke(mPm, bean.packageName,
                    // mClearCacheObserver);
                    // } catch (Exception e) {
                    // e.printStackTrace();
                    // }

                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + bean.packageName));
                    startActivity(intent);
                }
            });

            return convertView;
        }
    }

    private class ViewHolder {
        ImageView ivIcon;
        ImageView ivClean;
        TextView tvName;
        TextView tvCacheSize;
    }

    private class ClearCacheObserver extends IPackageDataObserver.Stub {
        public void onRemoveCompleted(final String packageName,
                                      final boolean succeeded) {
            Logger.d(TAG, "onRemoveCompleted: " + packageName + "  " + succeeded);
        }
    }
}
