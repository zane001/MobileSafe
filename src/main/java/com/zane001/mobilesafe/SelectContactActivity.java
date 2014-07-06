package com.zane001.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.zane001.mobilesafe.domain.ContactInfo;
import com.zane001.mobilesafe.engine.ContactInfoProvider;

import java.util.List;

/**
 * Created by zane001 on 2014/7/5.
 */
public class SelectContactActivity extends Activity {
    private ListView lv_select_contact;
    private ContactInfoProvider provider;
    private List<ContactInfo> infos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_contact);
        lv_select_contact = (ListView) findViewById(R.id.lv_select_contact);
        provider = new ContactInfoProvider(this);
        infos = provider.getContactInfos();
        lv_select_contact.setAdapter(new ContactAdapter());
        lv_select_contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ContactInfo info = (ContactInfo) lv_select_contact.getItemAtPosition(i);
                String number = info.getPhone();
                Intent data = new Intent();
                data.putExtra("number", number);
                setResult(0, data);
                finish();
            }
        });
    }

    private class ContactAdapter extends BaseAdapter {
        public int getCount() {
            return infos.size();
        }

        public Object getItem(int position) {
            return infos.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int positon, View convertView, ViewGroup parent) {
            ContactInfo info = infos.get(positon);
            TextView tv = new TextView(getApplicationContext());
            tv.setTextSize(24);
            tv.setTextColor(Color.WHITE);
            tv.setText(info.getName() + "\n" + info.getPhone());
            return tv;
        }
    }
}
