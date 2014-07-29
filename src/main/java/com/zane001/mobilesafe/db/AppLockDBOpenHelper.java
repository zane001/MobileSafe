package com.zane001.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zane001 on 2014/7/23.
 */
public class AppLockDBOpenHelper extends SQLiteOpenHelper {

    public AppLockDBOpenHelper(Context context) {
        super(context, "applock.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table applock(_id integer primary key autoincrement, packName varchar(20))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }
}
