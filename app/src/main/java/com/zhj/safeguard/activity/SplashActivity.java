package com.zhj.safeguard.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.zhj.safeguard.R;
import com.zhj.safeguard.utils.Constants;
import com.zhj.safeguard.utils.PackageInfoUtils;
import com.zhj.safeguard.utils.PreferenceUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class SplashActivity extends Activity {

    private static final String TAG = "SplashActivity";
    private static final int SHOW_UPDATE_DIALOG = 100;
    private static final int REQUEST_INSTALL = 101;
    private static final int SHOW_ERROR = 120332;
    private TextView mTvVersion;
    private String mDesc; //描述信息
    private String mUrl; //最新安装包地址
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_UPDATE_DIALOG:
                    showUpdateDialog();
                    break;
                case SHOW_ERROR:
                    Toast.makeText(SplashActivity.this, "" + msg.obj, Toast.LENGTH_SHORT).show();
                    load2home();
                default:
                    break;
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mTvVersion = (TextView) findViewById(R.id.splash_tv_version);
        //显示版本号
        mTvVersion.setText("版本号：" + PackageInfoUtils.getVersionName(this));
        //去检测版本
        checkVersion();


    }

    private void checkVersion() {
        //判断更新的标记->持久化存储
        boolean autoUpdate = PreferenceUtils.getBoolean(this, Constants.AUTOUPDATE);
        if (autoUpdate) {
            new Thread(new CheckVersionTask()).start();
        } else {
            load2home();
        }
    }

    //加载到主页
    private void load2home() {
//        try {
//            Thread.sleep(2500);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        //延时操作
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2500);

    }

    private class CheckVersionTask implements Runnable {


        @Override
        public void run() {
            //1.去服务器获取最新版本
            String uri = "http://192.168.56.1:8080/update.json";
            /*---------------update.json---------------------*/
            //{"versionCode":2,"desc":"修复了bug","url":"http://192.168.56.1:8080/safe2.apk"}
            //高版本SDK api23不能直接使用HttpClient,需导入org.apache.http.legacy.jar

//            /*----------------方式一--------------------*/
//
//            try {//建立client,请求，执行请求得到响应
//                HttpClient client = new DefaultHttpClient();
//                HttpGet request = new HttpGet(uri);
//                HttpResponse response = client.execute(request);
//                int statusCode = response.getStatusLine().getStatusCode();//状态码
//                if (statusCode ==200){
//                    HttpEntity entity = response.getEntity();//响应对象的数据实体
//                    InputStream is = entity.getContent();  //获得输入流
//                    String result = StreamTool.readStrem(is); //输入流转字符串
//                    is.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }


            /*==================方式二====================*/
            AndroidHttpClient client = AndroidHttpClient.newInstance("zhj", SplashActivity.this);//获取客户端
            //设置参数
            HttpParams params = client.getParams(); //获得参数
            HttpConnectionParams.setConnectionTimeout(params, 5000);//连接超时
            //响应超时
            HttpConnectionParams.setSoTimeout(params, 5000);
            //创建请求
            HttpGet get = new HttpGet(uri);
            //获得响应
            try {
                HttpResponse response = client.execute(get);
                //获得响应码
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    //访问成功
                    String json = EntityUtils.toString(response.getEntity(), "utf-8");
                    Log.d(TAG, "json" + json);
                    //解析json
                    JSONObject jsonObj = new JSONObject(json);
                    int netCode = jsonObj.getInt("versionCode");//服务器最新版本号
                    mDesc = jsonObj.getString("desc");
                    mUrl = jsonObj.getString("url");
                    //本地版本号
                    int localcode = PackageInfoUtils.getVersionCode(SplashActivity.this);

                    if (netCode > localcode) {
                        //需要更新 ,提示用户更新

                        Message message = mHandler.obtainMessage();
                        message.what = SHOW_UPDATE_DIALOG;
                        message.sendToTarget(); //消息发一个请求


                    } else {//不需要更新,跳到主页
                        Message message = mHandler.obtainMessage();
                        message.what = SHOW_ERROR;
                        message.obj = "error:100124";
                        message.sendToTarget(); //消息发一个请求
                    }

                } else {
                    //访问失败
                    load2home();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Message message = mHandler.obtainMessage();
                message.what = SHOW_ERROR;
                message.obj = "error:100123";
                message.sendToTarget(); //消息发一个请求
            } catch (JSONException e) {
                e.printStackTrace();
                Message message = mHandler.obtainMessage();
                message.what = SHOW_ERROR;
                message.obj = "error:100121";
                message.sendToTarget(); //消息发一个请求
            } finally {
                client.close();  //关闭客户端
            }
            /*=================方式三okhttp=====================*/
//            OkHttpClient client = new OkHttpClient();
//            Request request = new Request.Builder()
//                    .get()
//                    .url(uri)
//                    .build();
//            Response response = null;
//            try {
//                response = client.newCall(request).execute();
//                if (response.isSuccessful()) {
//                    Log.d(TAG, "打印GET响应的数据：" + response.body().string());
//                } else {
//                    Message message = mHandler.obtainMessage();
//                     message.what = SHOW_ERROR;
//                    message.obj = "error:100121";
//                    message.sendToTarget(); //消息发一个请求
//                }
//            } catch (IOException e) {
//                Message message = mHandler.obtainMessage();
//                e.printStackTrace();
//                message.what = SHOW_ERROR;
//                message.obj = "error:100120";
//                message.sendToTarget(); //消息发一个请求
//            }
        }
    }

    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);  //v4
        builder.setCancelable(false); //点击旁边不可取消
        builder.setTitle("版本更新提醒");//标题
        builder.setMessage(mDesc); //服务器获取,描述信息
        builder.setPositiveButton("立刻升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //下载最新版本,提示安装
                downloadNewVersion();

            }
        });
        builder.setNegativeButton("稍后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                load2home();
            }
        });
        builder.show();
    }

    //下载最新版本
    private void downloadNewVersion() {
        //弹出diolog下载apk
        final ProgressDialog dialog1 = new ProgressDialog(this);
        dialog1.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); //设置样式
        dialog1.setCancelable(false);
        dialog1.show();
        //下载apk
        HttpUtils utils = new HttpUtils();
        String apkName = System.currentTimeMillis() + ".apk"; //文件名称
        final File file = new File(Environment.getExternalStorageDirectory(), apkName);

        final String target = file.getAbsolutePath(); //下载后文件存放的本地地址
        utils.download(mUrl, target, new RequestCallBack<File>() {
            @Override
            public void onSuccess(ResponseInfo<File> responseInfo) {
                //下载成功后的回调
                dialog1.dismiss();//消失
                //提示安装
//                <activity android:name=".PackageInstallerActivity"
//                android:configChanges="orientation|keyboardHidden"
//                android:theme="@style/TallTitleBarTheme">
//                <intent-filter>
//                <action android:name="android.intent.action.VIEW" />
//                <category android:name="android.intent.category.DEFAULT" />
//                <data android:scheme="content" />
//                <data android:scheme="file" />
//                <data android:mimeType="application/vnd.android.package-archive" />
//                </intent-filter>
//                </activity>
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                startActivityForResult(intent, REQUEST_INSTALL);
            }

            /**
             * 进度显示的回调。
             * @param total 下载文件大小
             * @param current 当前下载位置
             * @param isUploading
             */
            @Override
            public void onLoading(long total, long current, boolean isUploading) {

                dialog1.setProgress((int) current);
                dialog1.setMax((int) total);
            }

            @Override
            public void onFailure(HttpException e, String s) {

                dialog1.dismiss();
                load2home();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_INSTALL) {
            switch (resultCode) {
                case Activity.RESULT_OK:

                    break;
                case Activity.RESULT_CANCELED:
                    load2home();
                    break;
                default:
                    break;
            }
        }
    }
}
