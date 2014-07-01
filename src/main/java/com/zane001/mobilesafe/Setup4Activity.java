package com.zane001.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

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
