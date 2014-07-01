package com.zane001.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by zane001 on 2014/7/1.
 */
public class Setup2Activity extends Activity implements View.OnClickListener {
    private RelativeLayout rl_setup2_bind;
    private ImageView iv_setup2_bind_status;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup2);
        rl_setup2_bind = (RelativeLayout) findViewById(R.id.rl_setup2_bind);
        rl_setup2_bind.setOnClickListener(this);
        iv_setup2_bind_status = (ImageView) findViewById(R.id.iv_setup2_bind_status);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        String simSerial = sp.getString("simSerial", ""); //初始化，判断sim卡是否被绑定
        if (TextUtils.isEmpty(simSerial)) {
            iv_setup2_bind_status.setImageResource(R.drawable.switch_off_normal);
        } else {
            iv_setup2_bind_status.setImageResource(R.drawable.switch_on_normal);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_setup2_bind:
                String simSerial = sp.getString("simSerial", "");
                if (TextUtils.isEmpty(simSerial)) {  //SIM卡未绑定
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("simSerial", getSimSerial());
                    editor.commit();
                    iv_setup2_bind_status.setImageResource(R.drawable.switch_on_normal);
                } else {    //SIM卡已经绑定
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("simSerial", "");
                    editor.commit();
                    iv_setup2_bind_status.setImageResource(R.drawable.switch_off_normal);
                }
                break;
        }
    }

    /**
     * 获取手机的SIM卡的串号
     */
    private String getSimSerial() {
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        return tm.getSimSerialNumber();
    }

    /**
     * 单击“下一步”所要执行的方法
     */
    public void next(View view) {
        Intent intent = new Intent(this, Setup3Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
    }

    /**
     * 单击“上一步”所要执行的方法
     */
    public void pre(View view) {
        Intent intent = new Intent(this, Setup1Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
    }
}
