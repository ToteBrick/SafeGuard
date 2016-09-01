package com.zhj.safeguard.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.zhj.safeguard.utils.Constants;
import com.zhj.safeguard.utils.PreferenceUtils;

public class BootCompleteReceiver extends BroadcastReceiver {
    private static final String TAG = "BootCompleteReceiver";

    public BootCompleteReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"手机重启了");
        //判断是否开启防盗保护
        boolean protecting = PreferenceUtils.getBoolean(context, Constants.SJFD_PROTECTING);
        if (!protecting){
            return;
        }

        //检测sim卡是否变更,获取当前sim卡
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String currentSim = tm.getSimSerialNumber();
        //获得存储的sim
        boolean sim = PreferenceUtils.getBoolean(context, Constants.SJFD_SIM);
        if (currentSim.equals(sim)){ //如果一致，不发送报警短信
            Log.d(TAG, "onReceive: "+"没有被盗");
            return;
        }
        //lost
        String number = PreferenceUtils.getString(context, Constants.SJFD_NUMBER);
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(number,null,"The phone maybe be lost",null,null);

    }
}
