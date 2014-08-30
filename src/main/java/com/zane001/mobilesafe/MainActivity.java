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
                    case 1: //通信卫士
                        Intent callSmsIntent = new Intent(MainActivity.this, CallSmsSafeActivity.class);
                        startActivity(callSmsIntent);
                        break;
                    case 2: //软件管理
                        Intent appManagerIntent = new Intent(MainActivity.this, AppManagerActivity.class);
                        startActivity(appManagerIntent);
                        break;
                    case 3: //进程管理
                        Intent taskManagerIntent = new Intent(MainActivity.this, TaskManagerActivity.class);
                        startActivity(taskManagerIntent);
                        break;
                    case 4: //流量管理
                        Intent trafficInfoIntent = new Intent(MainActivity.this, TrafficInfoActivity.class);
                        startActivity(trafficInfoIntent);
                        break;
                    case 5: //手机杀毒
                        Intent antiVirusIntent = new Intent(MainActivity.this, AntiVirusActivity.class);
                        startActivity(antiVirusIntent);
                        break;
                    case 6: //缓存清理
                        Intent cleanCacheIntent = new Intent(MainActivity.this, CleanCacheActivity.class);
                        startActivity(cleanCacheIntent);
                        break;
                    case 7: //高级工具
                        Intent atoolsIntent = new Intent(MainActivity.this, AtoolsActivity.class);
                        startActivity(atoolsIntent);
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
