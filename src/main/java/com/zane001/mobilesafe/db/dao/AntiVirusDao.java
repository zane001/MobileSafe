package com.zane001.mobilesafe.db.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by zane001 on 2014/8/30.
 */
public class AntiVirusDao {
    private Context context;
    public AntiVirusDao(Context context) {
        this.context = context;
    }

    public String getVirusInfo(String md5) {
        String result = null;
        String path = "/data/data/com.zane001.mobilesafe/files/antivirus.db";
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        if(db.isOpen()) {
            Cursor cursor = db.rawQuery("select desc from datable where md5=?", new String[]{md5});
            if(cursor.moveToFirst()) {
                result = cursor.getString(0);
            }
            cursor.close();
            db.close();
        }
        return result;
    }
}
