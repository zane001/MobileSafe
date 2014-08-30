package com.zane001.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by zane001 on 2014/8/30.
 */
public class CleanCacheActivity extends Activity {
    private ProgressBar pd;
    private TextView tv_clean_cache_status;
    private PackageManager pm;
    private List<String> cachePackNames;
    private LinearLayout ll_clean_cache;
    private Map<String, Long> cacheInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clean_cache);
        pd = (ProgressBar) findViewById(R.id.progressBar_clean);
        ll_clean_cache = (LinearLayout) findViewById(R.id.ll_clean_cache);
        tv_clean_cache_status = (TextView) findViewById(R.id.tv_clean_cache_status);
        pm = getPackageManager();
        scanPackages();
    }

    //扫描出带有缓存的应用程序
    private void scanPackages() {
        new AsyncTask<Void, Integer, Void>() {  //开启一个异步任务，扫描带有缓存的应用程序
            List<PackageInfo> packageInfos;

            @Override
            protected Void doInBackground(Void... voids) {
                int i = 0;
                for (PackageInfo info : packageInfos) {
                    String packName = info.packageName;
                    getSize(pm, packName);
                    i++;
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    publishProgress(i); //调用更新进度信息的方法
                }
                return null;
            }

            @Override
            protected void onPreExecute() { //在执行后台任务前，对UI做一些标记
                super.onPreExecute();
                cachePackNames = new ArrayList<String>();
                cacheInfo = new HashMap<String, Long>();
                packageInfos = pm.getInstalledPackages(0);
                pd.setMax(packageInfos.size());
                tv_clean_cache_status.setText("开始扫描。。。");
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                tv_clean_cache_status.setText("扫描完毕。。。" + "发现有" + cachePackNames.size() + "个缓存信息");
                for (final String packName : cachePackNames) {
                    View child = View.inflate(getApplicationContext(), R.layout.cache_item, null);
                    child.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (Build.VERSION.SDK_INT >= 9) {
                                //跳转至“清理缓存”的页面
                                Intent intent = new Intent();
                                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                intent.addCategory(Intent.CATEGORY_DEFAULT);
                                intent.setData(Uri.parse("package:" + packName));
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent();
                                intent.setAction("android.intent.action.VIEW");
                                intent.addCategory(Intent.CATEGORY_DEFAULT);
                                intent.addCategory("android.intent.category.VOICE_LAUNCH");
                                intent.putExtra("pkg", packName);
                                startActivity(intent);
                            }
                        }
                    });
                    //为child中的控件设置数据
                    ImageView iv_icon = (ImageView) child.findViewById(R.id.iv_cache_icon);
                    TextView tv_name = (TextView) child.findViewById(R.id.tv_cache_name);
                    TextView tv_size = (TextView) child.findViewById(R.id.tv_cache_size);
                    iv_icon.setImageDrawable(getApplicationIcon(packName));
                    tv_name.setText(getApplicationName(packName));
                    tv_size.setText("缓存大小：" + Formatter.formatFileSize(getApplicationContext(), cacheInfo.get(packName)));
                    ll_clean_cache.addView(child);
                }
            }

            @Override
            protected void onProgressUpdate(Integer... values) {    //直接将进度信息更新到UI上
                super.onProgressUpdate(values);
                pd.setProgress(values[0]);
                tv_clean_cache_status.setText("正在扫描" + values[0] + "条目");
            }

        }.execute();
    }

    /**
     * 通过反射的方式调用PackageManager中的方法
     */
    private void getSize(PackageManager pm, String packName) {
        //需要在清单文件中配置权限
        try {
            Method method = pm.getClass().getDeclaredMethod("getPackageSizeInfo", new Class[]{String.class, IPackageStatsObserver.class});
            method.invoke(pm, new Object[]{packName, new MyObserver(packName)});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MyObserver extends IPackageStatsObserver.Stub {
        private String packName;

        public MyObserver(String packName) {
            this.packName = packName;
        }
        @Override
        public void onGetStatsCompleted(PackageStats stats, boolean succeeded) {
            long cacheSize = stats.cacheSize;
//            long codeSize = stats.codeSize;
//            long dataSize = stats.dataSize;
            if(cacheSize > 0) {
                cachePackNames.add(packName);
                cacheInfo.put(packName, cacheSize);
            }
        }
    }

    /**
     * 获取到应用程序的名称
     */
    private String getApplicationName(String packName) {
        try {
            PackageInfo info = pm.getPackageInfo(packName, 0);
            return info.applicationInfo.loadLabel(pm).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return packName;
        }
    }

    /**
     * 获取到应用程序的图标
     */
    private Drawable getApplicationIcon(String packName) {
        try {
            PackageInfo info = pm.getPackageInfo(packName, 0);
            return info.applicationInfo.loadIcon(pm);
        } catch (Exception e) {
            e.printStackTrace();
            return getResources().getDrawable(R.drawable.ic_launcher);
        }
    }
}
