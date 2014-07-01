package com.zane001.mobilesafe.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zane001.mobilesafe.R;

/**
 * Created by zane001 on 2014/6/17.
 */
public class MainAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private String newName; //用于替换“手机防盗”的新标题
    private static final int[] icons = {R.drawable.widget01, R.drawable.widget02,
            R.drawable.widget03, R.drawable.widget04, R.drawable.widget05,
            R.drawable.widget06, R.drawable.widget07, R.drawable.widget08, R.drawable.widget09};
    private static final String[] names = {"手机防盗", "通信卫士", "软件管理", "进程管理", "流量统计",
            "手机杀毒", "系统优化", "高级工具", "设置中心"};

    public MainAdapter(Context context) {
        this.context = context; //获得系统的布局填充器
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        newName = sp.getString("newName", "");
    }

    @Override
    public int getCount() {
        return names.length;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.main_item, null);
        TextView tv_name = (TextView) view.findViewById(R.id.iv_main_item_name);
        ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_main_item_icon);
        tv_name.setText(names[i]);
        iv_icon.setImageResource(icons[i]);
        if(i == 0) {
            if(!TextUtils.isEmpty(newName)) {
                tv_name.setText(newName);
            }
        }
        return view;
    }
}
