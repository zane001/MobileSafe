package com.zane001.mobilesafe.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.zane001.mobilesafe.R;
import com.zane001.mobilesafe.domain.ProcessInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zane001 on 2014/8/29.
 */
public class ProcessInfoProvider {
    private Context context;

    public ProcessInfoProvider(Context context) {
        this.context = context;
    }

    /**
     * 返回所有正在运行的程序信息
     */
    public List<ProcessInfo> getProcessInfos() {
        // 动态获取应用的进程信息
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = context.getPackageManager();    //静态获取手机的应用程序信息
        //返回所有正在运行的进程
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos = am.getRunningAppProcesses();
        //存放进程信息
        List<ProcessInfo> processInfos = new ArrayList<ProcessInfo>();
        for(ActivityManager.RunningAppProcessInfo info : runningAppProcessInfos) {
            ProcessInfo processInfo = new ProcessInfo();
            int pid = info.pid;
            String packName = info.processName;
            long memSize = am.getProcessMemoryInfo(new int[] {pid})[0].getTotalPrivateDirty() * 1024;
            processInfo.setPid(pid);
            processInfo.setPackName(packName);
            processInfo.setMemSize(memSize);
            ApplicationInfo applicationInfo = null;
            try {
                applicationInfo = pm.getApplicationInfo(packName, 0);
                if(filterApp(applicationInfo)) {    //判断是否属于第三方应用程序
                    processInfo.setUserProcess(true);
                } else {
                    processInfo.setUserProcess(false);
                }
                processInfo.setIcon(applicationInfo.loadIcon(pm));
                processInfo.setAppName(applicationInfo.loadLabel(pm).toString());
            } catch (PackageManager.NameNotFoundException e) {  //使用C语言实现的应用程序
                e.printStackTrace();
                processInfo.setUserProcess(false);
                processInfo.setIcon(context.getResources().getDrawable(R.drawable.ic_launcher));
                processInfo.setAppName(packName);
            }
            processInfos.add(processInfo);
            processInfo = null;
        }
        return processInfos;
    }

    /**
     * 第三方应用的过滤器
     */
    public boolean filterApp(ApplicationInfo info) {
        if((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
            return true;
        } else if((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
            return true;
        }
        return false;
    }
}
