package com.zane001.mobilesafe.engine;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;

import com.zane001.mobilesafe.domain.TrafficInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zane001 on 2014/8/29.
 */
public class TrafficInfoProvider {
    private Context context;
    private PackageManager pm;

    public TrafficInfoProvider(Context context) {
        this.context = context;
        pm = context.getPackageManager();
    }

    /**
     * 返回所有具有互联网访问权限的应用程序的流量信息
     */
    public List<TrafficInfo> getTrafficInfos() {
        List<PackageInfo> packageInfos = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        List<TrafficInfo> trafficInfos = new ArrayList<TrafficInfo>();
        for(PackageInfo info : packageInfos) {
            String[] permissions = info.requestedPermissions;
            if(permissions != null && permissions.length > 0) {
                for(String permission : permissions) {
                    if("android.permission.INTERNET".equals(permission)) {
                        TrafficInfo trafficInfo = new TrafficInfo();
                        trafficInfo.setPackName(info.packageName);
                        trafficInfo.setIcon(info.applicationInfo.loadIcon(pm));
                        trafficInfo.setAppName(info.applicationInfo.loadLabel(pm).toString());
                        int uid = info.applicationInfo.uid;
                        trafficInfo.setRx(TrafficStats.getUidRxBytes(uid));
                        trafficInfo.setTx(TrafficStats.getUidTxBytes(uid));
                        trafficInfos.add(trafficInfo);
                        trafficInfo = null;
                        break;  //如果找到有Internet权限，就跳出
                    }
                }
            }
        }
        return trafficInfos;
    }
}
