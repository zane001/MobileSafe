package com.zane001.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zane001.mobilesafe.service.ShowCallLocationService;
import com.zane001.mobilesafe.service.WatchDogService1;
import com.zane001.mobilesafe.utils.ServiceStatusUtil;

/**
 * Created by zane001 on 2014/6/19.
 */
public class SettingCenterActivity extends Activity implements View.OnClickListener {

    private SharedPreferences sp;   //用于存储是否开启自动更新的boolean值
    private TextView tv_setting_autoUpdate_status; //自动更新的显示文字
    private CheckBox cb_setting_autoUpdate; //是否开启自动更新

    private TextView tv_setting_show_location_status;   //来电归属地
    private CheckBox cb_setting_show_location;
    private RelativeLayout rl_setting_show_location;
    private Intent showLocationIntent;
    private RelativeLayout rl_setting_change_bg;    //"来电归属地风格设置"控件的父控件

    private TextView tv_setting_show_bg;
    private RelativeLayout rl_setting_change_location;

    private RelativeLayout rl_setting_applock;
    private CheckBox cb_setting_applock;    //程序锁开关
    private TextView tv_setting_applock_status;
    private Intent watchDogIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.setting_center);
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        cb_setting_autoUpdate = (CheckBox) findViewById(R.id.cb_setting_autoUpdate);
        tv_setting_autoUpdate_status = (TextView) findViewById(R.id.tv_setting_autoUpdate_status);
        boolean autoUpdate = sp.getBoolean("autoUpdate", true); //初始化，默认开启更新

        if (autoUpdate) {
            tv_setting_autoUpdate_status.setText("自动更新已经开启");
            cb_setting_autoUpdate.setChecked(true);
        } else {
            tv_setting_autoUpdate_status.setText("自动更新已经关闭");
            cb_setting_autoUpdate.setChecked(false);
        }
        cb_setting_autoUpdate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SharedPreferences.Editor editor = sp.edit();    //获取编辑器
                editor.putBoolean("autoUpdate", isChecked); //持久化存储
                editor.commit();    //将数据提交
                if (isChecked) {
                    tv_setting_autoUpdate_status.setText("自动更新已经开启");
                    tv_setting_autoUpdate_status.setTextColor(Color.MAGENTA);
                } else {
                    tv_setting_autoUpdate_status.setText("自动更新已经关闭");
                    tv_setting_autoUpdate_status.setTextColor(Color.RED);
                }
            }
        });
        tv_setting_show_location_status = (TextView) findViewById(R.id.tv_setting_show_location_status);
        cb_setting_show_location = (CheckBox) findViewById(R.id.cb_setting_show_location);
        rl_setting_show_location = (RelativeLayout) findViewById(R.id.rl_setting_show_location);
        showLocationIntent = new Intent(this, ShowCallLocationService.class);
        rl_setting_show_location.setOnClickListener(this);

        //归属地显示背景
        rl_setting_change_bg = (RelativeLayout) findViewById(R.id.rl_setting_change_bg);
        tv_setting_show_bg = (TextView) findViewById(R.id.tv_setting_show_bg);
        rl_setting_change_bg.setOnClickListener(this);

        //归属地显示位置
        rl_setting_change_location  = (RelativeLayout) findViewById(R.id.rl_setting_change_location);
        rl_setting_change_location.setOnClickListener(this);

        //程序锁
        rl_setting_applock = (RelativeLayout) findViewById(R.id.rl_setting_applock);
        cb_setting_applock = (CheckBox) findViewById(R.id.cb_setting_applock);
        tv_setting_applock_status = (TextView) findViewById(R.id.tv_setting_applock_status);
        watchDogIntent = new Intent(this, WatchDogService1.class);
        rl_setting_applock.setOnClickListener(this);

        boolean applock = sp.getBoolean("applock", false); //初始化，默认关闭程序锁
        if (applock) {
            tv_setting_applock_status.setText("程序锁已经开启");
            cb_setting_applock.setChecked(true);
        } else {
            tv_setting_applock_status.setText("程序锁已经关闭");
            cb_setting_applock.setChecked(false);
        }
        cb_setting_applock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("applock", isChecked);
                editor.commit();
                if (isChecked) {
                    tv_setting_applock_status.setText("程序锁服务已经开启");
                    tv_setting_applock_status.setTextColor(Color.MAGENTA);
                } else {
                    tv_setting_applock_status.setText("程序锁服务没有开启");
                    tv_setting_applock_status.setTextColor(Color.MAGENTA);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        if(ServiceStatusUtil.isServiceRunning(this, "com.zane001.mobilesafe.service.ShowCallLocationService")) {
            cb_setting_show_location.setChecked(true);
            tv_setting_show_location_status.setText("来电归属地显示已经开启");
        } else {
            cb_setting_show_location.setChecked(false);
            tv_setting_show_location_status.setText("来电归属地显示已经关闭");
        }
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_setting_show_location:
                if (cb_setting_show_location.isChecked()) {
                    tv_setting_show_location_status.setText("来电归属地显示没有开启");
                    stopService(showLocationIntent);
                    cb_setting_show_location.setChecked(false);
                } else {
                    tv_setting_show_location_status.setText("来电归属地显示已经开启");
                    startService(showLocationIntent);
                    cb_setting_show_location.setChecked(true);
                }
                break;
            case R.id.rl_setting_change_bg:
                showChooseBgDialog();
                break;
            case R.id.rl_setting_change_location:
                Intent changeLocationIntent = new Intent(this, DragViewActivity.class);
                startActivity(changeLocationIntent);
                break;
            case R.id.rl_setting_applock:
                if(cb_setting_applock.isChecked()) {
                    tv_setting_applock_status.setText("程序锁服务没有开启");
                    stopService(watchDogIntent);
                    cb_setting_applock.setChecked(false);
                } else {
                    tv_setting_applock_status.setText("程序锁服务已经开启");
                    startService(watchDogIntent);
                    cb_setting_applock.setChecked(true);
                }
                break;
        }
    }
    /**
     * 更改背景颜色的对话框
     */
    private void showChooseBgDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.notification);
        builder.setTitle("归属地提示框风格");
        final String[] items = {"半透明", "活力橙", "卫士蓝", "苹果绿", "金属灰"};
        int which = sp.getInt("which", 0);
        builder.setSingleChoiceItems(items, which, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("which", i);
                editor.commit();
                tv_setting_show_bg.setText(items[i]);
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.create().show();    //创建并显示对话框
    }
}
