package com.zhj.safeguard.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.zhj.safeguard.R;
import com.zhj.safeguard.utils.PackageInfoUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
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
        new Thread(new CheckVersionTask()).start();
    }

    //加载到主页
    private void load2home() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
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
            HttpClient client = AndroidHttpClient.newInstance("zhj", SplashActivity.this);//获取客户端
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
                        message.sendToTarget(); //消息发一个请求
                        message.what = SHOW_UPDATE_DIALOG;


                    } else {//不需要更新,跳到主页
                        load2home();
                    }

                } else {
                    //访问失败
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
        String target = new File(Environment.getExternalStorageDirectory(),
                apkName).getAbsolutePath(); //下载后文件存放的本地地址
        utils.download(mUrl, target, new RequestCallBack<File>() {
            @Override
            public void onSuccess(ResponseInfo<File> responseInfo) {
                //下载成功后的回调
                dialog1.dismiss();//消失
                
            }

            @Override
            public void onFailure(HttpException e, String s) {

                dialog1.dismiss();
                load2home();
            }
        });
    }

}
