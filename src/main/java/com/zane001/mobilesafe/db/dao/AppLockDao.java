package com.zane001.mobilesafe.db.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zane001.mobilesafe.db.AppLockDBOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zane001 on 2014/7/23.
 */
public class AppLockDao {
    private AppLockDBOpenHelper helper;

    public AppLockDao(Context context) {
        helper = new AppLockDBOpenHelper(context);
    }

    public boolean find(String packName) {
        boolean result = false;
        SQLiteDatabase db = helper.getReadableDatabase();
        if(db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from applock where packName=?", new String[]{packName});
            if(cursor.moveToFirst()) {
                result = true;
            }
            cursor.close();
            db.close();
        }
        return result;
    }

    /**
     * 添加一条锁定的程序包名
     */
    public boolean add(String packName) {
        if(find(packName)) {    //先查询数据库中是否存在该条数据，防止重复添加
            return false;
        }
        SQLiteDatabase db = helper.getWritableDatabase();
        if(db.isOpen()) {
            db.execSQL("insert into applock(packName) values(?)", new Object[]{packName});
            db.close();
        }
        return find(packName);
    }

    public void delete(String packName) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if(db.isOpen()) {
            db.execSQL("delete from applock where packName=?", new Object[]{packName});
            db.close();
        }
    }

    /**
     * 查找全部被锁定的应用包名
     * @return
     */
    public List<String> findAll() {
        List<String> packNames = new ArrayList<String>();
        SQLiteDatabase db = helper.getReadableDatabase();
        if(db.isOpen()) {
            Cursor cursor = db.rawQuery("select packName from applock", null);
            while (cursor.moveToNext()) {
                packNames.add(cursor.getString(0));
            }
            cursor.close();
            db.close();
        }
        return packNames;
    }
}
