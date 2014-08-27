package com.zane001.mobilesafe.utils;

import android.content.Context;

/**
 * Created by zane001 on 2014/8/27.
 * 手机分辨率dp和px的互相转换
 */
public class DensityUtil {
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
