package com.zane001.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zane001.mobilesafe.domain.UpdateInfo;
import com.zane001.mobilesafe.engine.UpdateInfoParser;
import com.zane001.mobilesafe.utils.DownloadUtil;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class SplashActivity extends Activity {

    private TextView tv_splash_version;
    private UpdateInfo info;
    private static final int GET_INFO_SUCCESS = 10;
    private static final int SERVER_ERROR = 11;
    private static final int SERVER_URL_ERROR = 12;
    private static final int PROTOCOL_ERROR = 13;
    private static final int IO_ERROR = 14;
    private static final int XML_PARSE_ERROR = 15;
    private static final int DOWNLOAD_SUCCESS = 16;
    private static final int DOWNLOAD_ERROR = 17;
    protected static final String TAG = "SplashActivity";
    private long startTime;
    private RelativeLayout rl_splash;
    private long endTime;
    private ProgressDialog pd;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SERVER_ERROR:
                    Toast.makeText(getApplicationContext(), "服务器内部异常", Toast.LENGTH_SHORT).show();
                    loadMainUI();
                    break;
                case SERVER_URL_ERROR:
                    Toast.makeText(getApplicationContext(), "服务器路径不正确", Toast.LENGTH_SHORT).show();
                    loadMainUI();
                    break;
                case PROTOCOL_ERROR:
                    Toast.makeText(getApplicationContext(), "协议不支持", Toast.LENGTH_SHORT).show();
                    loadMainUI();
                    break;
                case IO_ERROR:
                    Toast.makeText(getApplicationContext(), "I/O错误", Toast.LENGTH_SHORT).show();
                    loadMainUI();
                    break;
                case XML_PARSE_ERROR:
                    Toast.makeText(getApplicationContext(), "XML解析错误", Toast.LENGTH_SHORT).show();
                    loadMainUI();
                    break;
                case GET_INFO_SUCCESS:
                    String serverVersion = info.getVersion();  //获得服务器的版本号
                    String currentVersion = getVersion();   //获得本地的版本号
                    if(currentVersion.equals(serverVersion)) {
                        Log.i(TAG, "版本号相同，进入主界面");
                        loadMainUI();
                    } else {
                        Log.i(TAG, "版本号不同，升级对话框");
                        showUpdateDialog();
                    }
                    break;
                case DOWNLOAD_SUCCESS:
                    Log.i(TAG, "文件下载成功");
                    File file = (File) msg.obj;
                    installApk(file);
                    break;
                case DOWNLOAD_ERROR:
                    Toast.makeText(getApplicationContext(), "下载数据异常", Toast.LENGTH_SHORT).show();
                    loadMainUI();
                    break;
            }
        }
    };

    /**
     * 加载主界面
     */
    private void loadMainUI() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 安装apk文件
     * @param file
     */
    protected void installApk(File file) {
        Intent intent = new Intent();   //隐式意图，效率较显式意图低
        intent.setAction("android.intent.action.VIEW"); //设置意图的动作
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive"); //同时设置意图的数据和类型
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        rl_splash = (RelativeLayout) findViewById(R.id.rl_splash);
        tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
        tv_splash_version.setText("版本号：" + getVersion());
        AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
        aa.setDuration(2000);
        rl_splash.setAnimation(aa);
        new Thread(new CheckVersionTask()).start();
    }

    /**
     * 联网检查本地的版本号和服务器端的版本号是否一致
     * @return
     */
    private class CheckVersionTask implements Runnable {
        @Override
        public void run() {
            startTime = System.currentTimeMillis();
            Message msg = Message.obtain();
            try {
                String serverUrl = getResources().getString(R.string.serverUrl);
                URL url = new URL(serverUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                int code = conn.getResponseCode();
                if(code == 200) {
                    InputStream is = conn.getInputStream();
                    info = UpdateInfoParser.getUpdateInfo(is);
                    endTime = System.currentTimeMillis();
                    long resultTime = endTime - startTime;
                    if(resultTime < 2000) {
                        try {
                            Thread.sleep(2000 - resultTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    msg.what = GET_INFO_SUCCESS;
                    handler.sendMessage(msg); //忘记写，死活弹不出升级对话框。。。
                } else {
                    msg.what = SERVER_ERROR;
                    handler.sendMessage(msg);
                    endTime = System.currentTimeMillis();
                    long resultTime = endTime - startTime;
                    if(resultTime < 2000) {
                        try {
                            Thread.sleep(2000 - resultTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                msg.what = SERVER_URL_ERROR;
                handler.sendMessage(msg);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                msg.what = XML_PARSE_ERROR;
                handler.sendMessage(msg);
            } catch (ProtocolException e) {
                e.printStackTrace();
                msg.what = PROTOCOL_ERROR;
                handler.sendMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
                msg.what = IO_ERROR;
                handler.sendMessage(msg);
            }
        }
    }

    /**
     * 显示升级提示的对话框
     * @return
     */
    protected void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(getResources().getDrawable(R.drawable.notification));   //升级图标
        builder.setTitle("升级温馨提示");
        builder.setMessage(info.getDescription());  //升级提示内容
        pd = new ProgressDialog(SplashActivity.this);   //创建下载进度条
        pd.setMessage("正在下载。。。");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);   //水平下载进度条
        builder.setPositiveButton("升级", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.i(TAG, "升级，下载" + info.getApkUrl());
                if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                    pd.show();  //判断SDCard是否存在
                    new Thread() {  //开启子线程下载

                        @Override
                        public void run() {
                            String path = info.getApkUrl();
                            String filename = DownloadUtil.getFileName(path);
                            File file = new File(Environment.getExternalStorageDirectory(), filename);
                            file = DownloadUtil.getFile(path, file.getAbsolutePath(), pd);
                            if(file != null) {
                                Message msg = Message.obtain();
                                msg.what = DOWNLOAD_SUCCESS;
                                msg.obj = file;
                                handler.sendMessage(msg);
                            } else {
                                Message msg = Message.obtain();
                                msg.what = DOWNLOAD_ERROR;
                                handler.sendMessage(msg);
                            }
                            pd.dismiss();
                        }
                    }.start();
                } else {
                    Toast.makeText(getApplicationContext(), "sd卡不可用", Toast.LENGTH_SHORT).show();
                    loadMainUI();   //进入程序主界面
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                loadMainUI();
            }
        });
        builder.create().show();
    }
    private String getVersion() {
        PackageManager pm = this.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "包名未找到";
        }
    }


}
