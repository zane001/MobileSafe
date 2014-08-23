package com.zane001.mobilesafe.service;

import android.app.Service;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.zane001.mobilesafe.db.dao.BlackNumberDao;

import java.lang.reflect.Method;

/**
 * Created by zane001 on 2014/8/23.
 */
public class CallFirewallService extends Service {

    public static final String TAG = "CallFirewallService";
    private TelephonyManager tm;
    private MyPhoneListener listener;
    private BlackNumberDao dao;

    @Override
    public void onCreate() {
        super.onCreate();
        dao = new BlackNumberDao(this);
        listener = new MyPhoneListener();   //注册系统电话状态改变的监听器
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private class MyPhoneListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch(state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    int mode = dao.findNumberMode(incomingNumber);
                    if(mode == 0 || mode == 2) {
                        Log.i(TAG, "挂断电话");
                        endCall(incomingNumber);
                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:   //接通状态
                    break;
            }
        }
    }

    //取消电话状态的监听
    @Override
    public void onDestroy() {
        super.onDestroy();
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);
        listener = null;
    }

    /**
     * 挂断电话
     * 需要复制2个aidl文件
     * 添加权限 android.permission.CALL_PHONE
     * @param incomingNumber
     */
    public void endCall(String incomingNumber) {
        try {
            //使用反射获取系统的service方法
            Method method = Class.forName("android.os.ServiceManager").getMethod("getService", String.class);
            IBinder binder = (IBinder) method.invoke(null, new Object[] {TELEPHONY_SERVICE});
            ITelephony telephony = ITelephony.Stub.asInterface(binder);
            telephony.endCall();    //异步方法，会开启一个新的线程将呼入的号码存入数据库
//            deleteCallLog(incomingNumber);
            //注册一个内容观察者，观察uri数据的变化
            getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, true, new MyObserver(new Handler(), incomingNumber));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 定义自己的内容观察者
     */
    private class MyObserver extends ContentObserver {
        private String incomingNumber;

        public MyObserver(Handler handler, String incomingNumber) {
            super(handler);
            this.incomingNumber = incomingNumber;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            deleteCallLog(incomingNumber);
            getContentResolver().unregisterContentObserver(this);   //停止数据的观察
        }
    }

    /**
     * 删除呼叫记录
     * @param incomingNumber
     */
    private void deleteCallLog(String incomingNumber) {
        Uri uri = Uri.parse("content://call_log/calls");
        Cursor cursor = getContentResolver().query(uri, new String[]{"_id"}, "number=?", new String[]{incomingNumber}, null);
        while(cursor.moveToNext()) {
            String id = cursor.getString(0);
            getContentResolver().delete(uri, "_id=?", new String[]{id});
        }
        cursor.close();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
