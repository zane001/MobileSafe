package com.zane001.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;

import com.zane001.mobilesafe.adapter.MainAdapter;

/**
 * Created by zane001 on 2014/6/17.
 */
public class MainActivity extends Activity {

    private GridView gv_main;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        gv_main = (GridView) findViewById(R.id.gv_main);
        gv_main.setAdapter(new MainAdapter(this));
    }
}
