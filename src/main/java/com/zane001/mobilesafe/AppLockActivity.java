package com.zane001.mobilesafe;

import android.app.Activity;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zane001.mobilesafe.db.dao.AppLockDao;
import com.zane001.mobilesafe.domain.AppInfo;
import com.zane001.mobilesafe.engine.AppInfoProvider;

import java.util.List;

/**
 * Created by zane001 on 2014/7/20.
 */
public class AppLockActivity extends Activity {

    private ListView lv_applock;    //展示手机中的所有应用
    private LinearLayout ll_loading;    //ProgressBar和TextView对应的父控件
    private AppInfoProvider provider;   //获取手机中已安装的应用程序
    private List<AppInfo> appInfos;     //存放当前手机上所有应用程序的信息
    private AppLockDao dao;             //操作存放已锁定的应用程序的数据库
    private List<String> lockedPackNames;    //存放所有已经被锁定的应用程序的包名信息

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
        provider = new AppInfoProvider(this);
        lv_applock = (ListView) findViewById(R.id.lv_applock);
        ll_loading = (LinearLayout) findViewById(R.id.ll_applock_loading);
        dao = new AppLockDao(this);
        lockedPackNames = dao.findAll();
        ll_loading.setVisibility(View.VISIBLE);
        new Thread() {
            @Override
            public void run() {
                appInfos = provider.getInstalledApps();
                handler.sendEmptyMessage(0);
            }
        }.start();

        lv_applock.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppInfo appInfo = (AppInfo) lv_applock.getItemAtPosition(position);
                String packName = appInfo.getPackName();
                ImageView iv = (ImageView) view.findViewById(R.id.iv_applock_status);
                TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                        0, Animation.RELATIVE_TO_SELF, 0.2F, Animation.RELATIVE_TO_SELF,
                        0, Animation.RELATIVE_TO_SELF, 0);
                ta.setDuration(200);
                if(lockedPackNames.contains(packName)) {    //判断当前的item是否处于锁定状态
//                    dao.delete(packName);
                    //利用内容提供者来观察数据库中的数据变化
                    Uri uri = Uri.parse("content://com.zane001.applock/DELETE");
                    getContentResolver().delete(uri, null, new String[]{packName});
                    iv.setImageResource(R.drawable.unlock);
                    lockedPackNames.remove(packName);
                } else {    //没有被锁定
//                    dao.add(packName);
                    Uri uri = Uri.parse("content://com.zane001.applock/ADD");
                    ContentValues values = new ContentValues();
                    values.put("packName", packName);
                    getContentResolver().insert(uri, values);
                    iv.setImageResource(R.drawable.lock);
                    lockedPackNames.add(packName);
                }
                view.startAnimation(ta);
            }
        });
    }

    private class AppLockAdapter extends BaseAdapter {  //自定义适配器对象

        @Override
        public int getCount() {
            return appInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return appInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder holder;
            if(convertView == null) {   //复用历史缓存的View对象
                view = View.inflate(getApplicationContext(), R.layout.app_lock_item, null);
                holder = new ViewHolder();
                holder.iv_icon = (ImageView) view.findViewById(R.id.iv_applock_icon);
                holder.iv_status = (ImageView) view.findViewById(R.id.iv_applock_status);
                holder.tv_name = (TextView) view.findViewById(R.id.tv_applock_appname);
                view.setTag(holder);
            } else {    //为View做一个标记，以便复用
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            //获取到当前的应用程序对象
            AppInfo appInfo = appInfos.get(position);
            holder.iv_icon.setImageDrawable(appInfo.getAppIcon());
            holder.tv_name.setText(appInfo.getAppName());
            if(lockedPackNames.contains(appInfo.getPackName())) {
                holder.iv_status.setImageResource(R.drawable.lock);
            } else {
                holder.iv_status.setImageResource(R.drawable.unlock);
            }
            return view;
        }
    }

    //View对应的View对象只会在堆中存在一份，所有Item共用该View
    public static class ViewHolder {
        ImageView iv_icon;
        ImageView iv_status;
        TextView tv_name;
    }
}
