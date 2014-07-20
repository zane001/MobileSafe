package com.zane001.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.zane001.mobilesafe.db.dao.CommonNumDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zane001 on 2014/7/20.
 */
public class CommonNumActivity extends Activity {
    protected static final String TAG = "CommonNumActivity";
    private ExpandableListView elv_common_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_num);
        elv_common_num = (ExpandableListView) findViewById(R.id.elv_common_num);
        elv_common_num.setAdapter(new CommonNumberAdapter());
        elv_common_num.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                TextView tv = (TextView)v;
                String number = tv.getText().toString().split("\n")[1];
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + number));
                startActivity(intent);
                return false;
            }
        });
    }

    private class CommonNumberAdapter extends BaseExpandableListAdapter {

        private List<String> groupNames;    //将数据从数据库一次性取出，存入缓存中，避免对数据库的频繁操作
        private Map<Integer, List<String>> childrenCache;
        public CommonNumberAdapter() {
            childrenCache = new HashMap<Integer, List<String>>();
        }
        @Override
        public int getGroupCount() {
            return CommonNumDao.getGroupCount();
        }

        @Override
        public int getChildrenCount(int i) {
            return CommonNumDao.getChildrenCount(i);
        }

        @Override
        public Object getGroup(int i) {
            return null;
        }

        @Override
        public Object getChild(int i, int i2) {
            return null;
        }

        @Override
        public long getGroupId(int i) {
            return i;
        }

        @Override
        public long getChildId(int i, int i2) {
            return i2;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
            TextView tv;
            if(view == null) {
                tv = new TextView(getApplicationContext());
            } else {
                tv = (TextView) view;   //复用缓存，使用缓存的View对象
            }
            tv.setTextSize(28);
            tv.setTextColor(Color.MAGENTA);
            if(groupNames != null) {
                tv.setText("" + groupNames.get(i));
            } else {
                groupNames = CommonNumDao.getGroupNames();
                tv.setText("" + groupNames.get(i));
            }
            return tv;
        }

        @Override
        public View getChildView(int i, int i2, boolean b, View view, ViewGroup viewGroup) {
            TextView tv;
            if(view == null) {
                tv = new TextView(getApplicationContext());
            } else {
                tv = (TextView) view;
            }
            tv.setTextSize(20);
            tv.setTextColor(Color.BLUE);
            String result = null;
            if(childrenCache.containsKey(i)) {
                result = childrenCache.get(i).get(i2);
            } else {
                List<String> results = CommonNumDao.getChildNameByPosition(i);
                childrenCache.put(i, results);
                result = results.get(i2);
            }
            tv.setText(result);
            return tv;
        }

        @Override
        public boolean isChildSelectable(int i, int i2) {   //每个分组的子孩子都能响应到单击事件
            return true;
        }
    }
}
