package com.zane001.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.zane001.mobilesafe.domain.AppInfo;
import com.zane001.mobilesafe.engine.AppInfoProvider;
import com.zane001.mobilesafe.utils.DensityUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zane001 on 2014/8/24.
 */
public class AppManagerActivity extends Activity implements View.OnClickListener {
    protected static final int LOAD_APP_FINISH = 50;
    private static final String TAG = "AppManagerActivity";
    private TextView tv_appmanager_mem_avail;   //显示手机可用内存
    private TextView tv_appmanager_sd_avail;    //显示SDCard可用内存
    private ListView lv_appmanager; //展示用户程序、系统程序
    private LinearLayout ll_appmanager_loading;
    private PackageManager pm;
    private List<AppInfo> appInfos; //存放手机中的所有应用程序
    private List<AppInfo> userAppInfos;
    private List<AppInfo> systemAppInfos;
    //PopupWindow中ContentView对应的3个控件
    private LinearLayout ll_uninstall;
    private LinearLayout ll_start;
    private LinearLayout ll_share;
    private PopupWindow popupWindow;
    private String clickedPackName;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_APP_FINISH:
                    ll_appmanager_loading.setVisibility(View.INVISIBLE);
                    lv_appmanager.setAdapter(new AppManagerAdapter());
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_manager);
        tv_appmanager_mem_avail = (TextView) findViewById(R.id.tv_appmanager_mem_avail);
        tv_appmanager_sd_avail = (TextView) findViewById(R.id.tv_appmanager_sd_avail);
        lv_appmanager = (ListView) findViewById(R.id.lv_appmanager);
        ll_appmanager_loading = (LinearLayout) findViewById(R.id.ll_appmanager_loading);
        pm = getPackageManager();
        tv_appmanager_sd_avail.setText("SD卡可用: " + getAvailSDSize());
        tv_appmanager_mem_avail.setText("内存可用: " + getAvailROMSize());
        //加载所有应用程序的数据
        fillData();
        //为ListView设置点击事件
        lv_appmanager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dismissPopupWindow();
                View contentView = View.inflate(getApplicationContext(), R.layout.popup_item, null);
                ll_uninstall = (LinearLayout) contentView.findViewById(R.id.ll_popup_uninstall);
                ll_share = (LinearLayout) contentView.findViewById(R.id.ll_popup_share);
                ll_start = (LinearLayout) contentView.findViewById(R.id.ll_popup_start);
                ll_uninstall.setOnClickListener(AppManagerActivity.this);
                ll_start.setOnClickListener(AppManagerActivity.this);
                ll_start.setOnClickListener(AppManagerActivity.this);
                LinearLayout ll_popup_container = (LinearLayout) contentView.findViewById(R.id.ll_popup_container);
                ScaleAnimation sa = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f);
                sa.setDuration(300);
                Object obj = lv_appmanager.getItemAtPosition(position);
                if (obj instanceof AppInfo) { //obj如果是AppInfo的一个实例
                    AppInfo appInfo = (AppInfo) obj;
                    clickedPackName = appInfo.getPackName();
                    if (appInfo.isUserApp()) {
                        ll_uninstall.setTag(true);
                    } else {
                        ll_uninstall.setTag(false); //如果是系统程序，不允许卸载
                    }
                } else {
                    return;
                }
                //获取到当前Item距离顶部、底部的距离
                int top = view.getTop();
                int bottom = view.getBottom();
                //指定PopupWindow的窗体大小
                popupWindow = new PopupWindow(contentView, DensityUtil.dip2px(getApplicationContext(), 200), bottom - top + DensityUtil.dip2px(getApplicationContext(), 20));
                //设置PopupWindow的背景图片
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                //获取到Item在窗体中显示的位置
                int[] location = new int[2];
                view.getLocationInWindow(location);
                popupWindow.showAtLocation(view, Gravity.TOP | Gravity.LEFT, location[0] + 20, location[1]);
                ll_popup_container.startAnimation(sa);
            }
        });

        /**
         * 用户滑动窗体时，需要关闭已经存在的PopupWindow
         */
        lv_appmanager.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {
                dismissPopupWindow();
            }
        });
    }

    /**
     * 将手机中的应用程序全部获取出来
     */
    private void fillData() {
        ll_appmanager_loading.setVisibility(View.VISIBLE);
        new Thread() {
            @Override
            public void run() {
                AppInfoProvider provider = new AppInfoProvider(AppManagerActivity.this);
                appInfos = provider.getInstalledApps();
                initAppInfo();
                //向主线程发送消息
                Message msg = Message.obtain();
                msg.what = LOAD_APP_FINISH;
                handler.sendMessage(msg);
            }
        }.start();
    }

    /**
     * 初始化系统程序和用户程序
     */
    protected void initAppInfo() {
        systemAppInfos = new ArrayList<AppInfo>();
        userAppInfos = new ArrayList<AppInfo>();
        for (AppInfo appInfo : appInfos) {
            if (appInfo.isUserApp()) {
                userAppInfos.add(appInfo);
            } else {
                systemAppInfos.add(appInfo);
            }
        }
    }

    @Override
    protected void onDestroy() {
        dismissPopupWindow();
        super.onDestroy();
    }

    /**
     * 适配器对象
     */
    private class AppManagerAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return userAppInfos.size() + 1 + systemAppInfos.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            if (position == 0) { //对应“用户程序”的条目
                return position;
            } else if (position <= userAppInfos.size()) {
                int newPosition = position - 1;
                return userAppInfos.get(newPosition);
            } else if (position == userAppInfos.size() + 1) {    //对应“系统程序”的条目
                return position;
            } else {
                int newPosition = position - userAppInfos.size() - 2;
                return systemAppInfos.get(newPosition);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position == 0) {
                TextView tv = new TextView(getApplicationContext());
                tv.setTextSize(20);
                tv.setText("用户程序(" + userAppInfos.size() + ")");
                return tv;
            } else if (position <= userAppInfos.size()) {
                int newPosition = position - 1;
                View view;
                ViewHolder holder;
                //复用历史缓存，此处需要判断缓存View对象的类型，不能复用TextView
                if (convertView == null || convertView instanceof TextView) {
                    view = View.inflate(getApplicationContext(), R.layout.app_manager_item, null);
                    holder = new ViewHolder();
                    holder.iv_icon = (ImageView) view.findViewById(R.id.iv_app_manager_icon);
                    holder.tv_name = (TextView) view.findViewById(R.id.tv_app_manager_appName);
                    holder.tv_version = (TextView) view.findViewById(R.id.tv_app_manager_appVersion);
                    view.setTag(holder);
                } else {
                    view = convertView;
                    holder = (ViewHolder) view.getTag();
                }
                //为用户应用程序适配数据
                AppInfo appInfo = userAppInfos.get(newPosition);
                holder.iv_icon.setImageDrawable(appInfo.getAppIcon());
                holder.tv_name.setText(appInfo.getAppName());
                holder.tv_version.setText("版本号：" + appInfo.getVersion());
                return view;
            } else if (position == (userAppInfos.size() + 1)) {
                TextView tv = new TextView(getApplicationContext());
                tv.setTextSize(20);
                tv.setText("系统程序(" + systemAppInfos.size() + ")");
                return tv;
            } else {
                int newPosition = position - userAppInfos.size() - 2;
                View view;
                ViewHolder holder;
                //复用历史缓存
                if (convertView == null || convertView instanceof TextView) {
                    view = View.inflate(getApplicationContext(), R.layout.app_manager_item, null);
                    holder = new ViewHolder();
                    holder.iv_icon = (ImageView) view.findViewById(R.id.iv_app_manager_icon);
                    holder.tv_name = (TextView) view.findViewById(R.id.tv_app_manager_appName);
                    holder.tv_version = (TextView) view.findViewById(R.id.tv_app_manager_appVersion);
                    view.setTag(holder);
                } else {
                    view = convertView;
                    holder = (ViewHolder) view.getTag();
                }
                //为系统应用程序适配数据
                AppInfo appInfo = systemAppInfos.get(newPosition);
                holder.iv_icon.setImageDrawable(appInfo.getAppIcon());
                holder.tv_name.setText(appInfo.getAppName());
                holder.tv_version.setText("版本号：" + appInfo.getVersion());
                return view;
            }
        }

        /**
         * 屏蔽掉两个TextView的单击事件
         */
        @Override
        public boolean isEnabled(int position) {
            if (position == 0 || position == userAppInfos.size() + 1) {
                return false;
            }
            return super.isEnabled(position);
        }
    }

    //用static修饰保证只存在一份
    private static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_version;
    }

    /**
     * 获取SDCard的可用内存大小
     */
    private String getAvailSDSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long availableBlocks = stat.getAvailableBlocks();
        long blockSize = stat.getBlockSize();
        long availSDSize = availableBlocks * blockSize;
        return Formatter.formatFileSize(this, availSDSize);
    }

    /**
     * 获取系统的可用内存大小
     */
    private String getAvailROMSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long availableBlocks = stat.getAvailableBlocks();
        long blockSize = stat.getBlockSize();
        return Formatter.formatFileSize(this, availableBlocks * blockSize);
    }

    /**
     * 单击下一个Item的时候，要关闭上一个PopupWindow
     */
    private void dismissPopupWindow() {
        if(popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_popup_start:
                startApplication();
                break;
            case R.id.ll_popup_share:
                shareApplication();
                break;
            case R.id.ll_popup_uninstall:
                boolean result = (Boolean)v.getTag();
                if(result) {
                    uninstallApplication();
                } else {
                    Toast.makeText(this, "系统程序不能被卸载", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * 分享一个应用程序
     */
    private void shareApplication() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.SEND");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("text/plain");
        intent.putExtra("subject", "分享的标题");
        intent.putExtra("sms_body", "推荐您使用一款软件" + clickedPackName);
        intent.putExtra(Intent.EXTRA_TEXT, "extra_text");
        startActivity(intent);
    }

    /**
     * 卸载一个应用程序
     */
    private void uninstallApplication() {
        dismissPopupWindow();
        Intent intent = new Intent();
        intent.setAction("android.intent.action.DELETE");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse("package:" + clickedPackName));
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1) {
            fillData();
            tv_appmanager_mem_avail.setText("内存可用：" + getAvailROMSize());
            tv_appmanager_sd_avail.setText("SD卡可用：" + getAvailSDSize());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 开启一个应用程序
     */
    private void startApplication() {
        dismissPopupWindow();
        Intent intent = new Intent();
        PackageInfo packageInfo;
        try {
            //只解析Activity对应的节点
            packageInfo = pm.getPackageInfo(clickedPackName, PackageManager.GET_ACTIVITIES);
            ActivityInfo[] activityInfos = packageInfo.activities;
            if(activityInfos != null && activityInfos.length > 0) {
                String className = activityInfos[0].name;
                intent.setClassName(clickedPackName, className);
                startActivity(intent);
            } else {
                Toast.makeText(this, "不能启动当前应用", Toast.LENGTH_SHORT).show();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
