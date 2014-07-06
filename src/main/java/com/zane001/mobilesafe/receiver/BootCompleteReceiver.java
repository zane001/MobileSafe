package com.zane001.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by zane001 on 2014/7/6.
 */
public class BootCompleteReceiver extends BroadcastReceiver {
    private static final String TAG = "BootCompleteReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "手机重启了");
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        boolean protecting = sp.getBoolean("protecting", false);
        if(protecting) {
            String safeNumber = sp.getString("safaNumber", "");
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String realSim = tm.getSimSerialNumber();
            String savedSim = sp.getString("simSerial", "");
            if(!savedSim.equals(realSim)) {
                Log.i(TAG, "发送短信");
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(safeNumber, null, "sim card changed", null ,null);
            }
        }
    }
}
