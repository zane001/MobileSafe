package com.zane001.mobilesafe.domain;

import android.graphics.drawable.Drawable;

/**
 * Created by zane001 on 2014/7/23.
 */
public class AppInfo {
    private String packName;    //应用的包名
    private String version;     //应用的版本
    private String appName;     //应用的名称
    private Drawable appIcon;   //应用的图标
    private boolean userApp;    //是否属于用户的程序

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public boolean isUserApp() {
        return userApp;
    }

    public void setUserApp(boolean userApp) {
        this.userApp = userApp;
    }
}
