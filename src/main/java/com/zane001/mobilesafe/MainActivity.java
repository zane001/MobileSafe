package com.zane001.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.zane001.mobilesafe.adapter.MainAdapter;

/**
 * Created by zane001 on 2014/6/17.
 */
public class MainActivity extends Activity {

    private GridView gv_main;   //显示主界面中的各个模块
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        gv_main = (GridView) findViewById(R.id.gv_main);
        gv_main.setAdapter(new MainAdapter(this));
        gv_main.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: //手机防盗
                        Intent lostProtectedIntent = new Intent(MainActivity.this, LostProtectedActivity.class);
                        startActivity(lostProtectedIntent);
                        break;
                    case 8: //设置中心
                        Intent settingIntent = new Intent(MainActivity.this, SettingCenterActivity.class);
                        startActivity(settingIntent);
                        break;
                }
            }
        });
    }
}
