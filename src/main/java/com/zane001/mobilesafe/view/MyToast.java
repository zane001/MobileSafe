package com.zane001.mobilesafe.view;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zane001.mobilesafe.R;

/**
 * Created by zane001 on 2014/8/29.
 */

/**
 * 自定义Toast
 */
public class MyToast {
    public static void showToast(Context context, String text) {
        Toast toast = new Toast(context);
        View view = View.inflate(context, R.layout.my_toast, null);
        TextView tv = (TextView) view.findViewById(R.id.tv_toast);
        tv.setText(text);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
