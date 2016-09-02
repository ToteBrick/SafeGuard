package com.zhj.safeguard.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;
import android.util.Log;

import com.zhj.safeguard.R;
import com.zhj.safeguard.utils.Constants;
import com.zhj.safeguard.utils.PreferenceUtils;

public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiver";


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "收到短信 ");

        boolean protecting = PreferenceUtils.getBoolean(context, Constants.SJFD_PROTECTING);
        if (!protecting) {
            return;
        }
        //检测短信内容，如果内容有特殊指令,处理防盗逻辑
        Object[] objs = (Object[]) intent.getExtras().get("pdus");
        for (Object obj :
                objs) {
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);
            String body = sms.getMessageBody();//短信内容
            //短信发送者
            String address = sms.getOriginatingAddress();

            //判断发送者是否是安全号码
            String number = PreferenceUtils.getString(context, Constants.SJFD_NUMBER);
            if (address.equals(number)) {
                if ("#*location*#".equals(body)) {
                    // GPS:
                    Log.d(TAG, "获取位置");
                    // 获取GPS位置
                    Intent service = new Intent(context, GPSService.class);
                    context.startService(service);
                    abortBroadcast();
                } else if ("#*alarm*#".equals(body)) {
                    // 报警音乐
                    Log.d(TAG, "报警音乐 ");

                    MediaPlayer player = MediaPlayer.create(context, R.raw.alarm);
//                  player.setLooping(true);//循环播放
                    player.setLooping(false);
                    player.setVolume(1.0f, 1.0f);
                    player.start();
                    abortBroadcast();
                } else if ("#*lockscreen*#".equals(body)) {
                    // 远程锁屏
                    Log.d(TAG, "远程锁屏 ");

                    DevicePolicyManager dpm = (DevicePolicyManager) context
                            .getSystemService(Context.DEVICE_POLICY_SERVICE);
                    dpm.resetPassword("123", 0);
                    dpm.lockNow();

                    abortBroadcast();
                } else if ("#*wipedata*#".equals(body)) {
                    // 远程擦除数据
                    Log.d(TAG, "远程擦除数据");

                    DevicePolicyManager dpm = (DevicePolicyManager) context
                            .getSystemService(Context.DEVICE_POLICY_SERVICE);

//                    dpm.wipeData(0);  //太暴力，屏蔽为妙
                    abortBroadcast();
                }
            }

        }
    }
}
