package com.zane001.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by zane001 on 2014/7/13.
 */
public class ServiceStatusUtil {
    public static boolean isServiceRunning(Context context, String serviceClassName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> infos = am.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo info : infos) {
            if (serviceClassName.equals(info.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
