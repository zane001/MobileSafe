package com.zane001.mobilesafe;

import android.app.Activity;
import android.app.ActivityManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zane001.mobilesafe.domain.ProcessInfo;
import com.zane001.mobilesafe.engine.ProcessInfoProvider;
import com.zane001.mobilesafe.view.MyToast;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by zane001 on 2014/8/28.
 */
public class TaskManagerActivity extends Activity implements View.OnClickListener {

    private ListView lv_user;   //显示用户进程
    private ListView lv_system;   //显示系统进程
    private boolean showUserApp;    //判断当前显示的是用户进程还是系统进程
    private Button bt_user, bt_system;
    private TextView tv_header; //为系统进程添加一个Item的提示
    private UserAdapter userAdapter;
    private SystemAdapter systemAdapter;
    private ProcessInfoProvider provider;  //获取手机中的进程
    private List<ProcessInfo> userProcessInfos;   //存放用户进程的集合
    private List<ProcessInfo> systemProcessInfos; //存放系统进程的集合

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_manager);
        //默认情况下，显示的是用户进程列表
        showUserApp = true;
        provider = new ProcessInfoProvider(this);
        userProcessInfos = new ArrayList<ProcessInfo>();
        systemProcessInfos = new ArrayList<ProcessInfo>();
        List<ProcessInfo> runningProcessInfos = provider.getProcessInfos();
        //将获取到的所有进程进行分类存储
        for (ProcessInfo info : runningProcessInfos) {
            if (info.isUserProcess()) {
                userProcessInfos.add(info);
            } else {
                systemProcessInfos.add(info);
            }
        }
        //用户进程的ListView以及点击事件的处理
        lv_user = (ListView) findViewById(R.id.lv_userTask);
        lv_user.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox cb = (CheckBox) view.findViewById(R.id.cb_task_manager);
                ProcessInfo info = (ProcessInfo) lv_user.getItemAtPosition(position);
                //判断是否是手机安全卫士本身，不允许kill
                if (info.getPackName().equals(getPackageName())) {
                    return;
                }
                if (info.isChecked()) {
                    info.setChecked(false);
                    cb.setChecked(false);
                } else {
                    info.setChecked(true);
                    cb.setChecked(true);
                }
            }
        });
        //系统进程的ListView以及点击事件的处理
        lv_system = (ListView) findViewById(R.id.lv_systemTask);
        lv_system.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view instanceof TextView) {  //不响应TextView
                    return;
                }
                CheckBox cb = (CheckBox) view.findViewById(R.id.cb_task_manager);
                ProcessInfo info = (ProcessInfo) lv_system.getItemAtPosition(position);
                if (info.isChecked()) {
                    info.setChecked(false);
                    cb.setChecked(false);
                } else {
                    info.setChecked(true);
                    cb.setChecked(true);
                }
            }
        });
        //为“用户进程”的按钮注册一个监听器
        bt_user = (Button) findViewById(R.id.bt_user);
        bt_user.setOnClickListener(this);
        bt_user.setBackgroundDrawable(getResources().getDrawable(R.drawable.bt_pressed));
        //为“系统进程”的按钮注册一个监听器
        bt_system = (Button) findViewById(R.id.bt_system);
        bt_system.setOnClickListener(this);
        bt_system.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_normal));
        //默认显示的是用户进程的列表
        lv_system.setVisibility(View.GONE);
        userAdapter = new UserAdapter();
        lv_user.setAdapter(userAdapter);
        tv_header = new TextView(getApplicationContext());
        tv_header.setText("Kill系统进程会导致系统的不稳定");
        tv_header.setBackgroundColor(Color.RED);
        lv_system.addHeaderView(tv_header); //将提示添加进系统进程对应的ListView
        systemAdapter = new SystemAdapter();
        lv_system.setAdapter(systemAdapter);
    }

    //用户进程ListView的适配器
    private class UserAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return userProcessInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return userProcessInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                view = View.inflate(getApplicationContext(), R.layout.task_manager_item, null);
                holder.iv_icon = (ImageView) view.findViewById(R.id.iv_task_manager_icon);
                holder.tv_name = (TextView) view.findViewById(R.id.tv_task_manager_name);
                holder.tv_mem = (TextView) view.findViewById(R.id.tv_task_manager_mem);
                holder.cb = (CheckBox) view.findViewById(R.id.cb_task_manager);
                view.setTag(holder);
            } else {
                view = convertView; //复用缓存
                holder = (ViewHolder) view.getTag();
            }
            ProcessInfo info = userProcessInfos.get(position);
            if (info.getPackName().equals(getPackageName())) {   //隐藏手机安全卫士的选择框
                holder.cb.setVisibility(View.INVISIBLE);
            } else {
                holder.cb.setVisibility(View.VISIBLE);
            }
            holder.iv_icon.setImageDrawable(info.getIcon());
            holder.tv_name.setText(info.getAppName());
            holder.tv_mem.setText(Formatter.formatFileSize(getApplicationContext(), info.getMemSize()));
            holder.cb.setChecked(info.isChecked()); //手动控制Checkbox的状态
            return view;
        }
    }

    /**
     * 系统进程ListView的适配器
     */
    private class SystemAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return systemProcessInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return systemProcessInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder holder;
            if (convertView == null) {
                view = View.inflate(getApplicationContext(), R.layout.task_manager_item, null);
                holder = new ViewHolder();
                holder.iv_icon = (ImageView) view.findViewById(R.id.iv_task_manager_icon);
                holder.tv_name = (TextView) view.findViewById(R.id.tv_task_manager_name);
                holder.tv_mem = (TextView) view.findViewById(R.id.tv_task_manager_mem);
                holder.cb = (CheckBox) view.findViewById(R.id.cb_task_manager);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            ProcessInfo info = systemProcessInfos.get(position);
            holder.iv_icon.setImageDrawable(info.getIcon());
            holder.tv_name.setText(info.getAppName());
            holder.tv_mem.setText(Formatter.formatFileSize(getApplicationContext(), info.getMemSize()));
            holder.cb.setChecked(info.isChecked());
            return view;
        }
    }

    private class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_mem;
        CheckBox cb;
    }

    /**
     * 响应用户进程、系统进程按钮的单击事件
     *
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_user:
                if (tv_header != null) {
                    lv_system.removeHeaderView(tv_header);
                    tv_header = null;
                }
                showUserApp = true;
                bt_user.setBackgroundDrawable(getResources().getDrawable(R.drawable.bt_pressed));
                bt_system.setBackgroundDrawable(getResources().getDrawable(R.drawable.bt_normal));
                lv_user.setVisibility(View.VISIBLE);    //此时用户进程列表可见
                lv_system.setVisibility(View.INVISIBLE);
                break;
            case R.id.bt_system:
                showUserApp = false;
                bt_user.setBackgroundDrawable(getResources().getDrawable(R.drawable.bt_normal));
                bt_system.setBackgroundDrawable(getResources().getDrawable(R.drawable.bt_pressed));
                lv_user.setVisibility(View.INVISIBLE);    //此时系统进程列表可见
                lv_system.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * 全选按钮的单击事件
     */
    public void selectAll(View view) {
        if (showUserApp) {
            for (ProcessInfo info : userProcessInfos) {
                info.setChecked(true);
                userAdapter.notifyDataSetChanged();
            }
        } else {
            for (ProcessInfo info : systemProcessInfos) {
                info.setChecked(true);
                systemAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 一键清理的单击事件
     */
    public void oneKeyClear(View view) {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        int count = 0;  //计数要kill多少进程
        long memSize = 0;   //占用内存大小
        List<ProcessInfo> killedProcessInfo = new ArrayList<ProcessInfo>(); //重新定义一个集合，存放被kill的进程
        if (showUserApp) {   //用户进程
            for (ProcessInfo info : userProcessInfos) { //遍历集合时不能进行移除操作
                if (info.isChecked()) {
                    count++;
                    memSize += info.getMemSize();
                    am.killBackgroundProcesses(info.getPackName());
                    killedProcessInfo.add(info);
                }
            }
        } else {    //系统进程
            for (ProcessInfo info : systemProcessInfos) {
                if (info.isChecked()) {
                    count++;
                    memSize += info.getMemSize();
                    am.killBackgroundProcesses(info.getPackName());
                    killedProcessInfo.add(info);
                }
            }
        }
        //迭代被Kill的进程，判断哪个集合包含该进程
        for (ProcessInfo info : killedProcessInfo) {
            if (info.isUserProcess()) {
                if (userProcessInfos.contains(info)) {
                    userProcessInfos.remove(info);
                }
            } else {
                if (systemProcessInfos.contains(info)) {
                    systemProcessInfos.remove(info);
                }
            }
        }
        //更新数据显示
        if (showUserApp) {
            userAdapter.notifyDataSetChanged();
        } else {
            systemAdapter.notifyDataSetChanged();
        }
        MyToast.showToast(this, "杀死了" + count + "个进程,释放了" + Formatter.formatFileSize(this, memSize) + "内存");
    }
}