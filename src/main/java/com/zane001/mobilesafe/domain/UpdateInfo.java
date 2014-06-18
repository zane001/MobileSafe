package com.zane001.mobilesafe.domain;

/**
 * Created by zane001 on 2014/6/11.
 */
public class UpdateInfo {
    private String version; //服务器端的版本号
    private String description; //升级提示
    private String apkUrl; //apk的下载地址

    public String getApkUrl() {
        return apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
