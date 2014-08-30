package com.zane001.mobilesafe.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by zane001 on 2014/7/9.
 * 复制归属地数据库到手机
 */
public class AssetCopyUtil {
    private Context context;

    public AssetCopyUtil(Context context) {
        this.context = context;
    }

    public boolean copyFile(String srcFileName, File file, ProgressDialog pd) {
        try {
            AssetManager am = context.getAssets();
            InputStream is = am.open(srcFileName);
            int max = is.available(); //获取到该文件的最大字节数
            pd.setMax(max);     //设置进度条显示的最大进度
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len = 0;
            int progress = 0;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                progress += len;
                pd.setProgress(progress);
            }
            fos.flush();
            fos.close();
            is.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 从资产目录复制病毒数据库到手机
     *
     * @param context
     * @param fileName
     * @param destFileName
     * @param pd
     */
    public static File copyVirusDb(Context context, String fileName, String destFileName, ProgressDialog pd) {
        try {
            InputStream is = context.getAssets().open(fileName);
            int max = is.available();
            if (pd != null) {
                pd.setMax(max);
            }
            File file = new File(destFileName);
            OutputStream out = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len = 0;
            int total = 0;
            while ((len = is.read(buffer)) != -1) {
                out.write(buffer, 0, len);
                total += len;
                if (pd != null) {
                    pd.setProgress(total);
                }
            }
            out.flush();
            out.close();
            is.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
