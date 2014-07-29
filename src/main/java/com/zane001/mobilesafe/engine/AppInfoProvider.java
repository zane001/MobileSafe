package com.zane001.mobilesafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.zane001.mobilesafe.domain.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zane001 on 2014/7/23.
 */
public class AppInfoProvider {
    private PackageManager pm;
    public AppInfoProvider(Context context) {
        pm = context.getPackageManager();
    }

    public List<AppInfo> getInstalledApps() {
        //包括那些被卸载的但是没有清除数据的应用
        List<PackageInfo> packageInfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        List<AppInfo> appInfos = new ArrayList<AppInfo>();
        for(PackageInfo info : packageInfos) {
            AppInfo appInfo = new AppInfo();
            appInfo.setPackName(info.packageName);
            appInfo.setVersion(info.versionName);
            appInfo.setAppName(info.applicationInfo.loadLabel(pm).toString());
            appInfo.setAppIcon(info.applicationInfo.loadIcon(pm));
            appInfo.setUserApp(filterApp(info.applicationInfo));
            appInfos.add(appInfo);
            appInfo = null;
        }
        return appInfos;
    }

    public boolean filterApp(ApplicationInfo info) {
        if((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {   //可升级的系统应用
            return true;
        } else if((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {    //用户的应用
            return true;
        }
        return false;
    }
}
