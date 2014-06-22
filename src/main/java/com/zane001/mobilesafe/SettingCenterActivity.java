package com.zane001.mobilesafe;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

/**
 * Created by zane001 on 2014/6/19.
 */
public class SettingCenterActivity extends Activity {

    private SharedPreferences sp;   //用于存储是否开启自动更新的boolean值
    private TextView tv_setting_autoUpdate_status; //自动更新的显示文字
    private CheckBox cb_setting_autoUpdate; //是否开启自动更新

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.setting_center);
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        cb_setting_autoUpdate = (CheckBox) findViewById(R.id.cb_setting_autoUpdate);
        tv_setting_autoUpdate_status = (TextView) findViewById(R.id.tv_setting_autoUpdate_status);
        boolean autoUpdate = sp.getBoolean("autoUpdate", true); //初始化，默认开启更新
        if(autoUpdate) {
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
                if(isChecked) {
                    tv_setting_autoUpdate_status.setText("自动更新已经开启");
                    tv_setting_autoUpdate_status.setTextColor(Color.MAGENTA);
                } else {
                    tv_setting_autoUpdate_status.setText("自动更新已经关闭");
                    tv_setting_autoUpdate_status.setTextColor(Color.RED);
                }
            }
        });
    }
}
