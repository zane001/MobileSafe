package com.zane001.mobilesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

import com.zane001.mobilesafe.R;
import com.zane001.mobilesafe.db.dao.BlackNumberDao;
import com.zane001.mobilesafe.engine.GPSInfoProvider;


/**
 * Created by zane001 on 2014/7/6.
 */
public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiver";
    private SharedPreferences sp;
    private BlackNumberDao dao;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "收到短信");
        dao = new BlackNumberDao(context);
        sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        String safeNumber = sp.getString("safeNumber", "");
        Object[] objects = (Object[]) intent.getExtras().get("pdus");
        DevicePolicyManager dm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName mAdminName = new ComponentName(context, MyAdmin.class);
        for(Object obj : objects) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
            String sender = smsMessage.getOriginatingAddress(); //获取发信人的地址
            //判断短信号码是否是黑名单中的号码与是否短信拦截
            int result = dao.findNumberMode(sender);
            if(result == 1 || result == 2) {
                Log.i(TAG, "拦截黑名单中的短信");
                abortBroadcast();
            }
            String body = smsMessage.getMessageBody();  //获取短信内容
            if("#*location*#".equals(body)) {
                Log.i(TAG, "发送位置信息");
                String lastLocation = GPSInfoProvider.getInstance(context).getLocation();
                if(!TextUtils.isEmpty(lastLocation)) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(safeNumber, null, lastLocation, null, null);
                }
                abortBroadcast();
            } else if("#*alarm*#".equals(body)) {
                Log.i(TAG, "播放报警音乐");
                MediaPlayer player = MediaPlayer.create(context, R.raw.alarm);
                player.setVolume(1.0f, 1.0f);
                player.start();
                abortBroadcast();
            } else if("#*wipedata*#".equals(body)) {
                Log.i(TAG, "清除数据");
                if(dm.isAdminActive(mAdminName)) {
                    dm.wipeData(0);
                }
                abortBroadcast();
            } else if("#*lockscreen*#".equals(body)) {
                Log.i(TAG, "远程锁定屏幕");
                if(dm.isAdminActive(mAdminName)) {
                    dm.resetPassword("123456", 0);
                    dm.lockNow();
                }
                abortBroadcast();
            }
         }
    }
}
