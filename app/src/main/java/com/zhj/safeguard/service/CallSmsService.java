package com.zhj.safeguard.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.zhj.safeguard.DB.BlackDao;
import com.zhj.safeguard.bean.BlackBean;
import com.zhj.safeguard.utils.Logger;

import java.lang.reflect.Method;

public class CallSmsService extends Service {
    private static final String TAG = "CallSmsService";

    private SmsSafeReceiver mSmsReceiver;

    private BlackDao mDao;
    private TelephonyManager mTm;
    private CallSafeListener mCallListener;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Logger.d(TAG, "骚扰拦截服务开启");

        mDao = new BlackDao(this);

        mTm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        // 1. 拦截电话,注册
        mCallListener = new CallSafeListener();
        mTm.listen(mCallListener, PhoneStateListener.LISTEN_CALL_STATE);

        // 2. 拦截短信
        // 动态注册
        mSmsReceiver = new SmsSafeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(mSmsReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Logger.d(TAG, "骚扰拦截服务停止");

        // 取消注册
        unregisterReceiver(mSmsReceiver);

        // 取消注册
        mTm.listen(mCallListener, PhoneStateListener.LISTEN_NONE);
    }

    private class CallSafeListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, final String incomingNumber) {
            // 电话状态的回调
            // 1. state:
            // * @see TelephonyManager#CALL_STATE_IDLE: 闲置状态,挂机
            // * @see TelephonyManager#CALL_STATE_RINGING: 响铃状态
            // * @see TelephonyManager#CALL_STATE_OFFHOOK: 摘机状态，接听状态
            // 2. incomingNumber:拨入的电话号码

            if (state == TelephonyManager.CALL_STATE_RINGING) {
                // 判断号码是否是需要 拦截
                int type = mDao.findType(incomingNumber);
                if (type == BlackBean.TYPE_ALL || type == BlackBean.TYPE_CALL) {
                    // 拦截电话拨打
                    // 挂机--->endCall()

                    // ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));

                    // android.os.ServiceManager
                    try {
                        Class<?> clazz = Class
                                .forName("android.os.ServiceManager");

                        Method method = clazz.getDeclaredMethod("getService",
                                String.class);
                        IBinder binder = (IBinder) method.invoke(null,
                                Context.TELEPHONY_SERVICE);
                        ITelephony telephony = ITelephony.Stub
                                .asInterface(binder);
                            telephony.endCall();
                        // Thread.sleep(1000);//

                        // 删除电话记录
                        final ContentResolver cr = getContentResolver();
                        final Uri url = CallLog.Calls.CONTENT_URI;

                        // 监听内容改变
                        cr.registerContentObserver(url, true,
                                new ContentObserver(new Handler()) {
                                    @Override
                                    public void onChange(boolean selfChange) {

                                        String where = CallLog.Calls.NUMBER
                                                + "=?";
                                        String[] selectionArgs = new String[] { incomingNumber };
                                        cr.delete(url, where, selectionArgs);
                                    }
                                });
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }

    private class SmsSafeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            // 1. 解析短信
            Object[] objs = (Object[]) intent.getExtras().get("pdus");

            for (Object obj : objs) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);

                // 2. 判断是否是拦截的
                String address = sms.getOriginatingAddress();
                // 判断号码是否在数据库中
                int type = mDao.findType(address);

                if (type == BlackBean.TYPE_ALL || type == BlackBean.TYPE_SMS) {

                    Logger.d(TAG, "拦截" + address + "  " + sms.getMessageBody());

                    // 3.如果拦截
                    abortBroadcast();
                }
            }
        }
    }
}
