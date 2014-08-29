package com.zane001.mobilesafe.domain;

import android.graphics.drawable.Drawable;

/**
 * Created by zane001 on 2014/8/29.
 */
public class TrafficInfo {
    private String packName;
    private String appName;
    private long tx;    //上传的数据
    private long rx;    //接收的数据
    private Drawable icon;

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public long getTx() {
        return tx;
    }

    public void setTx(long tx) {
        this.tx = tx;
    }

    public long getRx() {
        return rx;
    }

    public void setRx(long rx) {
        this.rx = rx;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
}
