package com.zane001.mobilesafe;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zane001.mobilesafe.utils.AssetCopyUtil;

import java.io.File;

/**
 * Created by zane001 on 2014/7/9.
 */
public class AtoolsActivity extends Activity implements View.OnClickListener {

    protected static final int COPY_SUCCESS = 30;
    protected static final int COPY_FAILED = 31;
    protected static final int COPY_COMMON_NUMBER_SUCCESS = 32;
    private TextView tv_atools_address_query;   //号码归属地
    private TextView tv_atools_common_num;  //常用号码
    private TextView tv_atools_applock; //程序锁
    private ProgressDialog pd;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            pd.dismiss();
            switch (msg.what) {
                case COPY_SUCCESS:
                    loadQueryUI();
                    break;
                case COPY_COMMON_NUMBER_SUCCESS:
                    loadCommNumUI();
                    break;
                case COPY_FAILED:
                    Toast.makeText(getApplicationContext(), "复制数据失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atools);
        tv_atools_address_query = (TextView) findViewById(R.id.tv_atools_address_query);
        tv_atools_address_query.setOnClickListener(this);
        tv_atools_common_num = (TextView) findViewById(R.id.tv_atools_common_num);
        tv_atools_common_num.setOnClickListener(this);
        tv_atools_applock = (TextView) findViewById(R.id.tv_atools_applock);
        tv_atools_applock.setOnClickListener(this);
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_atools_address_query:
                final File file = new File(getFilesDir(), "address.db");
                if(file.exists() && file.length() > 0) {
                    loadQueryUI();
                } else {
                    pd.show();
                    new Thread() {
                        public void run() {
                            AssetCopyUtil asu = new AssetCopyUtil(getApplicationContext());
                            boolean result = asu.copyFile("naddress.db", file, pd);
                            if(result) {
                                Message msg = Message.obtain();
                                msg.what = COPY_SUCCESS;
                                handler.sendMessage(msg);
                            } else {
                                Message msg = Message.obtain();
                                msg.what = COPY_FAILED;
                                handler.sendMessage(msg);
                            }
                        }
                    }.start();
                }
                break;
            case R.id.tv_atools_common_num:
                final File commonNumberFile = new File(getFilesDir(), "commonnum.db");
                if(commonNumberFile.exists() && commonNumberFile.length() > 0) {
                    loadCommNumUI();
                } else {
                    pd.show();
                    new Thread() {
                        @Override
                        public void run() {
                            AssetCopyUtil asu = new AssetCopyUtil(getApplicationContext());
                            boolean result = asu.copyFile("commonnum.db", commonNumberFile, pd);
                            if(result) {
                                Message msg = Message.obtain();
                                msg.what = COPY_COMMON_NUMBER_SUCCESS;
                                handler.sendMessage(msg);
                            } else {
                                Message msg = Message.obtain();
                                msg.what = COPY_FAILED;
                                handler.sendMessage(msg);
                            }
                        }
                    }.start();
                }
                break;
            case R.id.tv_atools_applock:
                Intent intent = new Intent(this, AppLockActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void loadQueryUI() {
        Intent intent = new Intent(this, NumberQueryActivity.class);
        startActivity(intent);
    }

    private void loadCommNumUI() {
        Intent intent = new Intent(this, CommonNumActivity.class);
        startActivity(intent);
    }
}
