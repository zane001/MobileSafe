package com.zane001.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.List;

/**
 * Created by zane001 on 2014/7/20.
 */
public class AppLockActivity extends Activity {

    private ListView lv_applock;
    private LinearLayout ll_loading;
    private AppInfoProvider provider;
    private List<AppInfo> appInfos;
    private AppLockDao dao;
    private List<String> lockedPackNames;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            ll_loading.setVisibility(View.INVISIBLE);
            lv_applock.setAdapter(new AppLockAdapter());
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_lock);
    }
}
