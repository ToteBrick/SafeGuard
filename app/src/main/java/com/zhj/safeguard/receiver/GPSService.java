package com.zhj.safeguard.receiver;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.zhj.safeguard.utils.Constants;
import com.zhj.safeguard.utils.Logger;
import com.zhj.safeguard.utils.ModifyOffset;
import com.zhj.safeguard.utils.PreferenceUtils;

import org.json.JSONObject;

import java.io.InputStream;

public class GPSService extends Service {

    protected static final String TAG = "GPSService";
    private LocationManager mLm;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "GPS服务开启");

        mLm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // 开启GPS位置请求
        String provider = LocationManager.GPS_PROVIDER;
        long minTime = 0;// 多久更新一次gps位置
        float minDistance = 0;// 移动多少距离

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLm.requestLocationUpdates(provider, minTime, minDistance, mListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.d(TAG, "GPS服务结束");

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLm.removeUpdates(mListener);
    }

    private LocationListener mListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onLocationChanged(Location location) {
            // 位置发送改变时的回调

            float accuracy = location.getAccuracy();// gps精确度
            double altitude = location.getAltitude();// 海拔
            double latitude = location.getLatitude();// 纬度
            double longitude = location.getLongitude();// 经度

            Log.d(TAG, "经度：" + longitude);
            Log.d(TAG, "纬度：" + latitude);

            final String number = PreferenceUtils.getString(GPSService.this,
                    Constants.SJFD_NUMBER);

            final double[] loc = getLocation(longitude, latitude);

            // 去网络获取具体位置信息
            // 接口地址：http://lbs.juhe.cn/api/getaddressbylngb
            // 支持格式：JSON/XML
            // 请求方式：GET
            // 请求示例：http://lbs.juhe.cn/api/getaddressbylngb?lngx=116.407431&lngy=39.914492
            // 请求参数：
            // 名称 类型 必填 说明
            // lngx String Y google地图经度 (如：119.9772857)
            // lngy String Y google地图纬度 (如：27.327578)
            // dtype String N 返回数据格式：json或xml,默认json

            HttpUtils utils = new HttpUtils();
            String url = "http://lbs.juhe.cn/api/getaddressbylngb?lngx="
                    + longitude + "&lngy=" + latitude;
            utils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {

                @Override
                public void onFailure(HttpException e, String msg) {
                    // 访问接口失败

                    // 发送位置短信给安全号码
                    SmsManager sm = SmsManager.getDefault();
                    sm.sendTextMessage(number, null, "longitude(" + loc[0]
                            + "),latitude(" + loc[1] + ")", null, null);

                    // 停止服务
                    stopSelf();
                }

                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    // 成功

                    // 解析结果json
                    try {
                        JSONObject jsonObject = new JSONObject(
                                responseInfo.result);

                        JSONObject rowObj = jsonObject.getJSONObject("row");
                        JSONObject resultObj = rowObj.getJSONObject("result");
                        String address = resultObj
                                .getString("formatted_address");

                        // 发送位置短信给安全号码
                        SmsManager sm = SmsManager.getDefault();

                        sm.sendTextMessage(number, null, "location from net",
                                null, null);

                        Logger.d(TAG, "address : " + address);
                        sm.sendTextMessage(number, null, address
                                + "  longitude(" + loc[0] + "),latitude("
                                + loc[1] + ")", null, null);

                        // 停止服务
                        stopSelf();

                    } catch (Exception e) {
                        e.printStackTrace();

                        // 发送位置短信给安全号码
                        SmsManager sm = SmsManager.getDefault();
                        sm.sendTextMessage(number, null, "longitude(" + loc[0]
                                + "),latitude(" + loc[1] + ")", null, null);

                        // 停止服务
                        stopSelf();
                    }

                }
            });

        }
    };

    private double[] getLocation(double x, double y) {
        InputStream is = null;
        try {
            is = getAssets().open("axisoffset.dat");
            ModifyOffset instance = ModifyOffset.getInstance(is);

            ModifyOffset.PointDouble pt = new ModifyOffset.PointDouble(x, y);
            ModifyOffset.PointDouble s2c = instance.s2c(pt);
            return new double[]{s2c.x, s2c.y};
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }
}
