package com.zane001.mobilesafe;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zane001.mobilesafe.service.WatchDogService1;

/**
 * Created by zane001 on 2014/7/25.
 */
public class EnterPwdActivity extends Activity {

    private EditText et_password;
    private TextView tv_name;
    private ImageView iv_icon;
    private String packName;
    private Intent serviceIntent;   //用于启动看门狗服务的意图对象
    private IService iService;  //停止保护一个应用程序的接口
    private MyConn conn;    //连接服务时的一个对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_pwd);
        et_password = (EditText) findViewById(R.id.et_password);
        tv_name = (TextView) findViewById(R.id.tv_name);
        iv_icon = (ImageView) findViewById(R.id.iv_icon);
        Intent intent = getIntent();    //获取到激活当前Activity的意图
        packName = intent.getStringExtra("packName");
        serviceIntent = new Intent(this, WatchDogService1.class);
        conn = new MyConn();
        bindService(serviceIntent, conn, BIND_AUTO_CREATE); //绑定服务，非startService()
        try {
            PackageInfo info = getPackageManager().getPackageInfo(packName, 0);
            tv_name.setText(info.applicationInfo.loadLabel(getPackageManager()));
            iv_icon.setImageDrawable(info.applicationInfo.loadIcon(getPackageManager()));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private class MyConn implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            iService = (IService) service;  //向上转型
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);    //解除绑定
    }

    public void enterPassword(View view) {
        String pwd = et_password.getText().toString().trim();
        if(TextUtils.isEmpty(pwd)) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if(pwd.equals("123")) {
            //通知看门狗,临时的停止对packName的保护
            iService.callTempStopProtect(packName);
            finish();
        } else {
            Toast.makeText(this, "密码不正确", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    /**
     * 当进入到当前界面后，屏蔽掉Back键
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return true; //消费掉当前的Back键
        }
        return super.onKeyDown(keyCode, event);
    }
}
