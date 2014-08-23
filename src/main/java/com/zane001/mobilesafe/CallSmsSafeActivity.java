package com.zane001.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zane001.mobilesafe.db.dao.BlackNumberDao;
import com.zane001.mobilesafe.domain.BlackNumber;

import java.util.List;

/**
 * Created by zane001 on 2014/7/31.
 */
public class CallSmsSafeActivity extends Activity {
    public static final String TAG = "CallSmsSafeActivity";
    protected static final int LOAD_DATA_FINISH = 40;
    private ListView lv_call_sms_safe;
    private BlackNumberDao dao;
    private List<BlackNumber> blackNumbers;
    private BlackNumberAdapter adapter;
    private LinearLayout ll_call_sms_safe_loading;  //ProgressBar控件的父控件，用于控制子控件的显示
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_DATA_FINISH:
                    ll_call_sms_safe_loading.setVisibility(View.INVISIBLE);
                    adapter = new BlackNumberAdapter();
                    lv_call_sms_safe.setAdapter(adapter);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_sms_safe);
        ll_call_sms_safe_loading = (LinearLayout) findViewById(R.id.ll_call_sms_safe_loading);
        dao = new BlackNumberDao(this);
        lv_call_sms_safe = (ListView) findViewById(R.id.lv_call_sms_safe);
        ll_call_sms_safe_loading.setVisibility(View.VISIBLE);
        //1.为lv_call_sms_safe注册一个上下文菜单
        registerForContextMenu(lv_call_sms_safe);

        new Thread() {
            @Override
            public void run() {
                blackNumbers = dao.findAll();
                Message msg = Message.obtain();
                msg.what = LOAD_DATA_FINISH;
                handler.sendMessage(msg);
            }
        }.start();
    }

    //2.重写创建上下文菜单的方法
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //设置长按Item后要显示的布局
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.call_sms_safe_menu, menu);
    }

    //3.响应上下文菜单的点击事件
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = (int) info.id;
        switch(item.getItemId()) {
            case R.id.item_delete:
                Log.i(TAG, "删除黑名单记录");
                deleteBlackNumber(position);
                return true;
            case R.id.item_update:
                Log.i(TAG, "更新黑名单记录");
                updateBlackNumber(position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    /**
     *更新黑名单号码
     *
     */
    private void updateBlackNumber(int position) {
        showBlackNumberDialog(1, position);
    }

    /**
     * 删除一条黑名单号码
     */
    private  void deleteBlackNumber(int position) {
        BlackNumber blackNumber = (BlackNumber) lv_call_sms_safe.getItemAtPosition(position);
        String number = blackNumber.getNumber();
        dao.delete(number);
        blackNumbers.remove(blackNumber);
        adapter.notifyDataSetChanged();
    }

    private class BlackNumberAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return blackNumbers.size();
        }

        @Override
        public Object getItem(int i) {
            return blackNumbers.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder holder;
            //复用历史缓存的View对象
            if(convertView == null) {
                Log.i(TAG, "创建新的View对象");
                view = View.inflate(getApplicationContext(), R.layout.call_sms_item, null);
                holder = new ViewHolder();
                holder.tv_number = (TextView) view.findViewById(R.id.tv_call_sms_item_number);
                holder.tv_mode = (TextView) view.findViewById(R.id.tv_call_sms_item_mode);
                view.setTag(holder);    //把控件id的引用放在view对象中
            } else {
                view = convertView;
                Log.i(TAG, "使用历史缓存的View对象");
                holder = (ViewHolder) view.getTag();
            }
            BlackNumber blackNumber = blackNumbers.get(position);
            holder.tv_number.setText(blackNumber.getNumber());
            int mode = blackNumber.getMode();
            if(mode == 0) {
                holder.tv_mode.setText("电话拦截");
            } else if(mode == 1) {
                holder.tv_mode.setText("短信拦截");
            } else {
                holder.tv_mode.setText("全部拦截");
            }
            return view;
        }
    }

    private static class ViewHolder {   //在栈中只存在一份
        TextView tv_number;
        TextView tv_mode;
    }

    /**
     * 添加一条黑名单号码
     */
    public void addBlackNumber(View view) {
        showBlackNumberDialog(0, 0);
    }

    /**
     * 添加或者修改黑名单的对话框
     */
    private void showBlackNumberDialog(final int flag, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = View.inflate(this, R.layout.add_black_number, null);
        final EditText et_number = (EditText) dialogView.findViewById(R.id.et_add_black_number);
        final CheckBox cb_phone = (CheckBox) dialogView.findViewById(R.id.cb_block_phone);
        final CheckBox cb_sms = (CheckBox) dialogView.findViewById(R.id.cb_block_sms);
        TextView tv_title = (TextView) dialogView.findViewById(R.id.tv_black_number_title);
        if(flag == 1) { //修改黑名单数据
            tv_title.setText("修改");
            BlackNumber blackNumber = (BlackNumber) lv_call_sms_safe.getItemAtPosition(position);
            String oldNumber = blackNumber.getNumber();
            et_number.setText(oldNumber);   //回显将要修改的号码
            int mode = blackNumber.getMode();
            if(mode == 0) {
                cb_phone.setChecked(true);
                cb_sms.setChecked(false);
            } else if(mode == 1) {
                cb_phone.setChecked(false);
                cb_sms.setChecked(true);
            } else {
                cb_phone.setChecked(true);
                cb_sms.setChecked(true);
            }
        }
        builder.setView(dialogView);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String number = et_number.getText().toString().trim();
                if(flag == 1 && dao.find(number)) { //如果要修改的号码已经存在
                    Toast.makeText(getApplicationContext(), "要修改的号码已经存在", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(number)) {
                    return;
                } else {
                    boolean result = false;
                    BlackNumber blackNumber = new BlackNumber();
                    blackNumber.setNumber(number);
                    //电话和短信都选择，拦截模式为2
                    if(cb_phone.isChecked() && cb_sms.isChecked()) {
                        if(flag == 0) { //添加
                            result = dao.add(number, "2");
                            blackNumber.setMode(2);
                        } else {    //修改
                            BlackNumber blackNumber1 = (BlackNumber) lv_call_sms_safe.getItemAtPosition(position);
                            dao.update(blackNumber1.getNumber(), number, "2");
                            blackNumber1.setMode(2);
                            blackNumber1.setNumber(number);
                            adapter.notifyDataSetChanged();
                        }
                    } else if(cb_phone.isChecked()) {   //拦截电话，模式为0
                        if(flag == 0) {
                            result = dao.add(number, "0");
                            blackNumber.setMode(0);
                        } else {
                            BlackNumber blackNumber1 = (BlackNumber) lv_call_sms_safe.getItemAtPosition(position);
                            dao.update(blackNumber1.getNumber(), number, "0");
                            blackNumber1.setMode(0);
                            blackNumber1.setNumber(number);
                            adapter.notifyDataSetChanged();
                        }
                    } else if(cb_sms.isChecked()) { //拦截短信，模式为1
                        if(flag == 0) {
                            result = dao.add(number, "1");
                            blackNumber.setMode(1);
                        } else {
                            BlackNumber blackNumber1 = (BlackNumber) lv_call_sms_safe.getItemAtPosition(position);
                            dao.update(blackNumber1.getNumber(), number, "1");
                            blackNumber1.setMode(1);
                            blackNumber1.setNumber(number);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "拦截模式不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(result) {    //添加或者修改数据成功
                        blackNumbers.add(blackNumber);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }
}
