package com.zane001.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.zane001.mobilesafe.EnterPwdActivity;
import com.zane001.mobilesafe.IService;
import com.zane001.mobilesafe.db.dao.AppLockDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zane001 on 2014/7/25.
 */
public class WatchDogService1 extends Service {
    private static final String TAG = "WatchDogService";
    boolean flag;   //是否要停止看门狗服务
    private Intent pwdIntent;   //用于激活输入密码
    private List<String> lockPackNames; //已经被锁定的应用程序的包名存放在该集合缓存中
    private List<String> tempStopProtectPackNames;
    private MyBinder binder;
    private AppLockDao dao;
    private MyObserver observer;    //内容观察者
    private LockScreenReceiver receiver;    //锁屏的广播接收者

    //返回到EnterPwdActivity中的ServiceConnection对象中的onServiceConnected方法的第二个参数
    @Override
    public IBinder onBind(Intent intent) {
        binder = new MyBinder();
        return binder;
    }

    private class MyBinder extends Binder implements IService {
        public void callTempStopProtect(String packName) {
            tempStopProtect(packName);
        }
    }

    //临时停止保护一个被锁定的应用程序的方法
    public void tempStopProtect(String packName) {
        tempStopProtectPackNames.add(packName);
    }

    @Override
    public void onCreate() {
        Uri uri = Uri.parse("content://com.zane001.applock/");
        observer = new MyObserver(new Handler());
        getContentResolver().registerContentObserver(uri, true, observer);  //注册内容观察者
        //用代码动态注册一个广播接收者
        IntentFilter filter = new IntentFilter();
        filter.setPriority(1000);
        filter.addAction(Intent.ACTION_SCREEN_OFF); //要接收的动作
        receiver = new LockScreenReceiver();
        registerReceiver(receiver, filter);
        super.onCreate();
        dao = new AppLockDao(this);
        flag = true;
        tempStopProtectPackNames = new ArrayList<String>();
        lockPackNames = dao.findAll();
        pwdIntent = new Intent(this, EnterPwdActivity.class);
        pwdIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  //需要为该Activity创建一个任务栈
        new Thread(){
            @Override
            public void run() {
                while(flag) {
                    ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                    ActivityManager.RunningTaskInfo taskInfo = am.getRunningTasks(1).get(0);
                    String packName = taskInfo.topActivity.getPackageName();
                    Log.i(TAG, packName);
                    if(tempStopProtectPackNames.contains(packName)) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;   //重新执行while循环
                    }
                    //将任务栈顶的程序的包名信息存入意图中
                    pwdIntent.putExtra("packName", packName);
                    if(lockPackNames.contains(packName)) {
                        startActivity(pwdIntent);
                    }
                    try {
                        Thread.sleep(200);  //耗电，暂停200ms
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        flag = false;
        //将内容观察者反注册掉
        getContentResolver().unregisterContentObserver(observer);
        observer = null;
        //将广播接收者反注册掉
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    private class MyObserver extends ContentObserver {

        public MyObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            lockPackNames = dao.findAll();  //重新从数据库中获取数据
            super.onChange(selfChange);
        }
    }

    private class LockScreenReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "锁屏了");
            tempStopProtectPackNames.clear();   //清空集合，继续保护
        }
    }
}
