package com.zane001.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by zane001 on 2014/7/1.
 */
public class Setup3Activity extends Activity {

    private EditText et_setup3_number;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup3);
        et_setup3_number = (EditText) findViewById(R.id.et_setup3_number);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        String number = sp.getString("safeNumber", "");
        et_setup3_number.setText(number);
    }

    public void selectContact(View view) {
        Intent intent = new Intent(this, SelectContactActivity.class);
        startActivityForResult(intent, 0);  //激活一个带返回值的Activity，要求SelectContactActivity返回数据
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data != null) {
            String number = data.getStringExtra("number");  //获取到返回的数据
            et_setup3_number.setText(number);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void next(View view) {
        String number = et_setup3_number.getText().toString().trim();
        if(TextUtils.isEmpty(number)) {
            Toast.makeText(this, "安全号码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("safeNumber", number);
        editor.commit();
        Intent intent = new Intent(this, Setup4Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
    }

    public void pre(View view) {
        Intent intent = new Intent(this, Setup2Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
    }
}
