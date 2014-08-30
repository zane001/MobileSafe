package com.zane001.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zane001.mobilesafe.db.dao.AntiVirusDao;
import com.zane001.mobilesafe.utils.Md5Encoder;

import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import android.widget.Toast;

/**
 * Created by zane001 on 2014/8/30.
 */
public class AntiVirusActivity extends Activity {
    protected static final int SCAN_NOT_VIRUS = 90;
    protected static final int FIND_VIRUS = 91;
    protected static final int SCAN_FINISH = 92;
    private ImageView iv_scan;
    private PackageManager pm;
    private AntiVirusDao dao;
    private ProgressBar progressBar;
    private TextView tv_scan_status;
    private LinearLayout ll_scan_status;
    private List<PackageInfo> virusPackInfos;
    RotateAnimation ra;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            PackageInfo info = (PackageInfo) msg.obj;
            switch (msg.what) {
                case SCAN_NOT_VIRUS:
                    TextView tv = new TextView(getApplicationContext());
                    tv.setText("扫描" + info.applicationInfo.loadLabel(pm) + "安全");
                ll_scan_status.addView(tv, 0);  //添加到控件最上面
                break;
                case FIND_VIRUS:
                    virusPackInfos.add(info);
                    break;
                case SCAN_FINISH:
                    iv_scan.clearAnimation();
                    if(virusPackInfos.size() == 0) {
                        Toast.makeText(getApplicationContext(), "扫描完毕，您的手机很安全", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anti_virus);
        pm = getPackageManager();
        dao = new AntiVirusDao(this);
        virusPackInfos = new ArrayList<PackageInfo>();
        iv_scan = (ImageView) findViewById(R.id.iv_scan);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tv_scan_status = (TextView) findViewById(R.id.tv_scan_status);
        ll_scan_status = (LinearLayout) findViewById(R.id.ll_scan_status);
        ra = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 1.0F, Animation.RELATIVE_TO_SELF, 1.0F);
        ra.setDuration(1000);
        ra.setRepeatCount(Animation.INFINITE);  //一直旋转
        ra.setRepeatMode(Animation.RESTART);    //一个回合后，重新旋转
    }

    public void kill(View v) {
        ra.reset();
        iv_scan.startAnimation(ra);
        new Thread() {
            @Override
            public void run() {
                List<PackageInfo> packageInfos = pm.getInstalledPackages(PackageManager.GET_SIGNATURES);
                progressBar.setMax(packageInfos.size());
                int count = 0;
                for(PackageInfo info : packageInfos) {
                    String md5 = Md5Encoder.encode(info.signatures[0].toCharsString());
                    String result = dao.getVirusInfo(md5);
                    if(result == null) {    //当前遍历的应用不是病毒
                        Message msg = Message.obtain();
                        msg.what = SCAN_NOT_VIRUS;
                        msg.obj = info;
                        handler.sendMessage(msg);
                    } else {    //当前应用发现病毒
                        Message msg = Message.obtain();
                        msg.what = FIND_VIRUS;
                        msg.obj = info;
                        handler.sendMessage(msg);
                    }
                    count ++;
                    try {
                        Thread.sleep(20);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    progressBar.setProgress(count);
                }
                //遍历结束
                Message msg = Message.obtain();
                msg.what = SCAN_FINISH;
                handler.sendMessage(msg);
            }
        }.start();
    }

    /**
     * 一键清理按钮
     */
    public void clean(View v) {
        if(virusPackInfos.size() > 0) {
            for(PackageInfo info : virusPackInfos) {
                String packName = info.packageName;
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DEFAULT);
                intent.setData(Uri.parse("package:" + packName));
                startActivity(intent);
            }
        } else {
            return;
        }
    }
}
