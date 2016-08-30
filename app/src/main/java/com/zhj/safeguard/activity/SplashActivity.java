package com.zhj.safeguard.activity;

import android.app.Activity;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.zhj.safeguard.R;
import com.zhj.safeguard.utils.PackageInfoUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class SplashActivity extends Activity {

    private static final String TAG ="SplashActivity" ;
    private TextView mTvVersion;

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

    private class CheckVersionTask implements Runnable {

        @Override
        public void run() {
            //1.去服务器获取最新版本
            String uri = "http://192.168.56.1:8080/update.json";
            //高版本api23不能直接使用HttpClient,需导入org.apache.http.legacy.jar

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
                    Log.d(TAG,"json"+json);
                } else {
                    //访问失败
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
