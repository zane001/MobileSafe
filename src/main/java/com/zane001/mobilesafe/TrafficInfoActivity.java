package com.zane001.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zane001.mobilesafe.domain.TrafficInfo;
import com.zane001.mobilesafe.engine.TrafficInfoProvider;

import java.util.List;

/**
 * Created by zane001 on 2014/8/29.
 */
public class TrafficInfoActivity extends Activity {
    private ListView lv;
    private TrafficInfoProvider provider;
    private LinearLayout ll_loading;
    private List<TrafficInfo> trafficInfos;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            ll_loading.setVisibility(View.INVISIBLE);
            lv.setAdapter(new TrafficAdapter());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.traffic_info);
        lv = (ListView) findViewById(R.id.lv_traffic_manager);
        provider = new TrafficInfoProvider(this);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        ll_loading.setVisibility(View.VISIBLE);
        new Thread(){
            @Override
            public void run() {
                trafficInfos = provider.getTrafficInfos();
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    private class TrafficAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return trafficInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return trafficInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder holder = new ViewHolder();
            TrafficInfo info = trafficInfos.get(position);
            //复用缓存的View
            if(convertView == null) {
                view = View.inflate(getApplicationContext(), R.layout.traffic_item, null);
                holder.iv_icon = (ImageView) view.findViewById(R.id.iv_traffic_icon);
                holder.tv_name = (TextView) view.findViewById(R.id.tv_traffic_name);
                holder.tv_rx = (TextView) view.findViewById(R.id.tv_traffic_rx);
                holder.tv_tx = (TextView) view.findViewById(R.id.tv_traffic_tx);
                holder.tv_total = (TextView) view.findViewById(R.id.tv_traffic_total);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            holder.iv_icon.setImageDrawable(info.getIcon());
            holder.tv_name.setText(info.getAppName());
            long rx = info.getRx();
            long tx = info.getTx();
            if(rx < 0) {
                rx = 0;
            }
            if(tx < 0) {
                tx = 0;
            }
            holder.tv_rx.setText(Formatter.formatFileSize(getApplicationContext(), rx));
            holder.tv_tx.setText(Formatter.formatFileSize(getApplicationContext(), tx));
            long total = rx + tx;
            holder.tv_total.setText(Formatter.formatFileSize(getApplicationContext(), total));
            return view;
        }
    }

    private static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_rx;
        TextView tv_tx;
        TextView tv_total;
    }
}
