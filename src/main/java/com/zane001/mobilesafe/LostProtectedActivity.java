package com.zane001.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zane001.mobilesafe.utils.Md5Encoder;

/**
 * Created by zane001 on 2014/6/22.
 */
public class LostProtectedActivity extends Activity implements OnClickListener {

    private SharedPreferences sp;
    private EditText et_first_dialog_pwd;
    private EditText et_first_dialog_pwd_confirm;
    private Button bt_first_dialog_ok;
    private Button bt_first_dialog_cancel;  //第一次进入“手机防盗”界面

    private EditText et_normal_dialog_pwd;
    private Button bt_normal_dialog_ok;
    private Button bt_normal_dialog_cancel; //非第一次进入“手机防盗”的界面

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        if(isSetupPwd()) {
            showNormalEntryDialog();
        } else {
            showFirstEntryDialog();
        }
    }

    /**
     *   第一次进入“手机防盗”时要显示的对话框
     */
    private void showFirstEntryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.first_entry_dialog, null);
        et_first_dialog_pwd = (EditText) view.findViewById(R.id.et_first_dialog_pwd);
        et_first_dialog_pwd_confirm = (EditText) view.findViewById(R.id.et_first_dialog_pwd_confirm);
        bt_first_dialog_ok = (Button) view.findViewById(R.id.bt_first_dialog_ok);
        bt_first_dialog_cancel = (Button) view.findViewById(R.id.bt_first_dialog_cancel);
        bt_first_dialog_cancel.setOnClickListener(this);
        bt_first_dialog_ok.setOnClickListener(this);
        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }

    /**
     * 当设置过密码之后，正常进入“手机防盗”时的对话框
     */
    private void showNormalEntryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {    //如果点击“取消”，则进入主界面
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                finish();
            }
        });
        View view = View.inflate(this, R.layout.normal_entry_dialog, null);
        et_normal_dialog_pwd = (EditText) view.findViewById(R.id.et_normal_dialog_pwd);
        bt_normal_dialog_ok = (Button) view.findViewById(R.id.bt_normal_dialog_ok);
        bt_normal_dialog_cancel = (Button) view.findViewById(R.id.bt_normal_dialog_cancel);
        bt_normal_dialog_ok.setOnClickListener(this);
        bt_normal_dialog_cancel.setOnClickListener(this);
        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }

    /**
     * 判断用户是否设置密码
     */
    private boolean isSetupPwd() {
        String savedPwd = sp.getString("password", "");
        if(TextUtils.isEmpty(savedPwd)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_first_dialog_cancel:   //第一次进入“手机防盗”，弹出的对话框，对按钮事件的处理
                dialog.cancel();
                finish();
                break;
            case R.id.bt_first_dialog_ok:
                String pwd = et_first_dialog_pwd.getText().toString().trim();
                String pwd_confirm = et_first_dialog_pwd_confirm.getText().toString().trim();
                //判断两次输入的密码是否为空
                if(TextUtils.isEmpty(pwd) || TextUtils.isEmpty(pwd_confirm)) {
                    Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                //判断两次输入的密码是否相同
                if(pwd.equals(pwd_confirm)) {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("password", Md5Encoder.encode(pwd));
                    editor.commit();
                    dialog.dismiss();
                    finish();
                } else {
                    Toast.makeText(this, "两次密码不相同", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
            case R.id.bt_normal_dialog_cancel: //非第一次进入“手机防盗”界面
                dialog.cancel();
                finish();
                break;
            case R.id.bt_normal_dialog_ok:
                String userEntryPwd = et_normal_dialog_pwd.getText().toString().trim();
                if(TextUtils.isEmpty(userEntryPwd)) {
                    Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                String savedPwd = sp.getString("password", "");
                //先将用户输入的密码进行Md5加密，然后和存储的加密密码进行对比
                if(savedPwd.equals(Md5Encoder.encode(userEntryPwd))) {
                    //Toast.makeText(this, "密码正确", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return; //加载主界面
                } else {
                    Toast.makeText(this, "密码不正确", Toast.LENGTH_SHORT).show();
                    return;
                }
        }
    }
}
