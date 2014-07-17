package com.zane001.mobilesafe.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zane001.mobilesafe.R;
import com.zane001.mobilesafe.db.dao.NumberAddressDao;

/**
 * Created by zane001 on 2014/7/13.
 */
public class ShowCallLocationService extends Service {

    private TelephonyManager tm;
    private WindowManager windowManager;
    private MyPhoneListener listener;   //电话状态改变的监听器
    private SharedPreferences sp;

    private static final int[] bgs = {R.drawable.call_locate_white, R.drawable.call_locate_orange,
                                      R.drawable.call_locate_blue, R.drawable.call_locate_green,
                                      R.drawable.call_locate_gray};

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sp = getSharedPreferences("config", MODE_PRIVATE);
        listener = new MyPhoneListener();
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
    }

    private class MyPhoneListener extends PhoneStateListener {
        private View view;

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    String address = NumberAddressDao.getAddress(incomingNumber);   //自定义Toast
                    view = View.inflate(getApplicationContext(), R.layout.show_address, null);
                    LinearLayout ll = (LinearLayout) view.findViewById(R.id.ll_show_address);
                    int which = sp.getInt("which", 0);
                    ll.setBackgroundResource(bgs[which]);
                    TextView tv = (TextView) view.findViewById(R.id.tv_show_address);
                    tv.setText(address);
                    final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                    params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    params.width = WindowManager.LayoutParams.WRAP_CONTENT;
                    params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                            | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    params.format = PixelFormat.TRANSLUCENT;
                    params.type = WindowManager.LayoutParams.TYPE_TOAST;
                    windowManager.addView(view, params);    //将Toast及其参数挂载到窗体上,不会自动消失
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    if(view != null) {
                        windowManager.removeView(view);
                        view = null;
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    if(view != null) {
                        windowManager.removeView(view);
                        view = null;
                    }
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    @Override
    public void onDestroy() {   //取消电话状态的监听
        super.onDestroy();
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);
        listener = null;
    }
}
