package com.zane001.mobilesafe.domain;

import android.graphics.drawable.Drawable;

/**
 * Created by zane001 on 2014/8/29.
 */
public class ProcessInfo {
    private String packName;    //应用程序包名
    private Drawable icon;  //图标
    private long memSize;   //占用内存大小，单位byte
    private boolean isUserProcess;  //是否用户进程
    private String appName; //应用程序名称
    private boolean isChecked;  //是否被选中
    private int pid;    //进程的pid

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public long getMemSize() {
        return memSize;
    }

    public void setMemSize(long memSize) {
        this.memSize = memSize;
    }

    public boolean isUserProcess() {
        return isUserProcess;
    }

    public void setUserProcess(boolean isUserProcess) {
        this.isUserProcess = isUserProcess;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }
}
