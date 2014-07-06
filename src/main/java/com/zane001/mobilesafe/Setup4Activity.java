package com.zane001.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.zane001.mobilesafe.receiver.MyAdmin;

/**
 * Created by zane001 on 2014/7/1.
 */
public class Setup4Activity extends Activity {

    private CheckBox cb_setup4_protect;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup4);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        cb_setup4_protect = (CheckBox) findViewById(R.id.cb_setup4_protect);
        boolean protecting = sp.getBoolean("protecting", false);    //默认防盗没有开启
        cb_setup4_protect.setChecked(protecting);
        cb_setup4_protect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SharedPreferences.Editor editor = sp.edit();
                if (isChecked) {
                    editor.putBoolean("protecting", true);
                    cb_setup4_protect.setText("防盗保护已经开启");
                } else {
                    editor.putBoolean("protecting", false);
                    cb_setup4_protect.setText("防盗保护没有开启");
                }
                editor.commit();
            }
        });
    }

    public void activeDeviceAdmin(View view) {
        //创建一个与MyAdmin相关联的组件
        ComponentName mAdminName = new ComponentName(this, MyAdmin.class);
        DevicePolicyManager dm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        if(!dm.isAdminActive(mAdminName)) { //判断组件是否获得了超级管理员的权限，如果没有，则添加权限
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
            startActivity(intent);
        }
    }

    public void next(View view) {
        if (!cb_setup4_protect.isChecked()) { //如果防盗保护没有开启
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("温馨提示");
            builder.setMessage("手机防盗极大地保护了您的手机安全，强烈建议开启");
            builder.setPositiveButton("开启", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    cb_setup4_protect.setChecked(true);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean("isSetup", true);
                    editor.commit();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean("isSetup", true);
                    editor.commit();
                }
            });
            builder.create().show();
            return;
        }
        //设置向导已经完成，在用户下次进入时判断，如果为true，说明已经设置过。
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("isSetup", true);
        editor.commit();
        Intent intent = new Intent(this, LostProtectedActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
    }

    public void pre(View view) {
        Intent intent = new Intent(this, Setup3Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
    }
}
