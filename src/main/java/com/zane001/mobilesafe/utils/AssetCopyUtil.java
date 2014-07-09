package com.zane001.mobilesafe.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zane001 on 2014/7/9.
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
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
