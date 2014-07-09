package com.zane001.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zane001.mobilesafe.db.dao.NumberAddressDao;

/**
 * Created by zane001 on 2014/7/9.
 */
public class NumberQueryActivity extends Activity {

    private EditText et_number_query;
    private TextView tv_number_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.number_query);
        et_number_query = (EditText) findViewById(R.id.et_number_query);
        tv_number_address = (TextView) findViewById(R.id.tv_number_address);
    }

    public void query(View view) {
        String number = et_number_query.getText().toString().trim();
        if(TextUtils.isEmpty(number)) {
            Toast.makeText(this, "号码不能为空，请输入手机号", Toast.LENGTH_SHORT).show();
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            et_number_query.startAnimation(shake);
            return;
        } else {
            String address = NumberAddressDao.getAddress(number);
            tv_number_address.setText(address);
        }
    }
}
