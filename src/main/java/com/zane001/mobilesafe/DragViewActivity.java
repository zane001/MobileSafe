package com.zane001.mobilesafe;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by zane001 on 2014/7/18.
 */
public class DragViewActivity extends Activity {

    protected static final String TAG = "DragViewActivity";
    private ImageView iv_drag_view;
    private TextView tv_drag_view;
    private int windowHeight;   //定义屏幕的高度
    private int windowWidth;    //定义屏幕的宽度
    private SharedPreferences sp;   //用于存储View的位置信息
    private long firstClickTime;    //记录“双击居中”时的第一次单击时间

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drag_view);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        iv_drag_view = (ImageView) findViewById(R.id.iv_drag_view);
        tv_drag_view = (TextView) findViewById(R.id.tv_drag_view);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        windowHeight = dm.heightPixels;
        windowWidth = dm.widthPixels;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv_drag_view.getLayoutParams();
        params.leftMargin = sp.getInt("lastX", 0);
        params.topMargin = sp.getInt("lastY", 0);
        iv_drag_view.setLayoutParams(params);

        iv_drag_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "被单击。。。");
                if (firstClickTime > 0) {
                    long secondClickTime = System.currentTimeMillis();
                    if (secondClickTime - firstClickTime < 500) {
                        Log.i(TAG, "被双击。。。");
                        firstClickTime = 0;
                        int right = iv_drag_view.getRight();
                        int left = iv_drag_view.getLeft();
                        int iv_width = right - left;    //计算出View的长度
                        int iv_left = windowWidth / 2 - iv_width / 2;
                        int iv_right = windowWidth / 2 + iv_width / 2;
                        iv_drag_view.layout(iv_left, iv_drag_view.getTop(), iv_right, iv_drag_view.getBottom());
                        SharedPreferences.Editor editor = sp.edit();
                        int lastX = iv_drag_view.getLeft();
                        int lastY = iv_drag_view.getTop();
                        editor.putInt("lastX", lastX);
                        editor.putInt("lastY", lastY);
                        editor.commit();
                    }
                }
                firstClickTime = System.currentTimeMillis();    //解决伪双击问题
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500);
                            firstClickTime = 0;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });

        iv_drag_view.setOnTouchListener(new View.OnTouchListener() {
            int startX; // 记录起始的X坐标
            int startY;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.i(TAG, "摸到");
                        startX = (int) motionEvent.getRawX();
                        startY = (int) motionEvent.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int x = (int) motionEvent.getRawX();
                        int y = (int) motionEvent.getRawY();
                        int tv_height = tv_drag_view.getBottom() - tv_drag_view.getTop();
                        if (y > (windowHeight / 2)) {
                            tv_drag_view.layout(tv_drag_view.getLeft(), 60,
                                    tv_drag_view.getRight(), 60 + tv_height);
                        } else {
                            tv_drag_view.layout(tv_drag_view.getLeft(),
                                    windowHeight - 120 - tv_height,
                                    tv_drag_view.getRight(), windowHeight - 120);
                        }
                        int dx = x - startX;    //计算出View在屏幕x轴方向上被移动的距离
                        int dy = y - startY;
                        int t = iv_drag_view.getTop();  //计算出被拖动的view距离窗体上下左右的距离
                        int b = iv_drag_view.getBottom();
                        int l = iv_drag_view.getLeft();
                        int r = iv_drag_view.getRight();
                        int newT = t + dy;
                        int newB = b + dy;
                        int newL = l + dx;
                        int newR = r + dx;
                        //通过对刚移动结束的View距离手机屏幕的大小判断，避免View移除屏幕
                        if (newL < 0 || newT < 0 || newB > windowHeight || newR > windowWidth) {
                            break;
                        }
                        iv_drag_view.layout(newL, newT, newR, newB);    //将移动后的view在窗体上重新显示
                        startX = (int) motionEvent.getRawX();
                        startY = (int) motionEvent.getRawY();   //更新
                        Log.i(TAG, "移动");
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.i(TAG, "松手");
                        SharedPreferences.Editor editor = sp.edit();
                        int lastX = iv_drag_view.getLeft();
                        int lastY = iv_drag_view.getTop();
                        editor.putInt("lastX", lastX);
                        editor.putInt("lastY", lastY);
                        editor.commit();
                        break;
                }
                return false;   //保证onClick能被响应
            }
        });
    }
}
