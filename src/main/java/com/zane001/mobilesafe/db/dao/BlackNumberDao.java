package com.zane001.mobilesafe.db.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.zane001.mobilesafe.db.BlackNumberDBOpenHelper;
import com.zane001.mobilesafe.domain.BlackNumber;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zane001 on 2014/7/30.
 */
public class BlackNumberDao {
    private BlackNumberDBOpenHelper helper;

    public BlackNumberDao(Context context) {
        helper = new BlackNumberDBOpenHelper(context);
    }

    public boolean find(String number) {
        boolean result = false;
        SQLiteDatabase db = helper.getReadableDatabase();
        if(db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from blacknumber where number=?", new String[]{number});
            if(cursor.moveToFirst()) {
                result = true;
            }
            cursor.close();
            db.close();
        }
        return result;
    }

    public int findNumberMode(String number) {
        //拦截模式有3种，0代表拦截短信，1代表拦截电话，2代表拦截短信和电话，默认为-1，表示没有拦截
        int result = -1;
        SQLiteDatabase db = helper.getReadableDatabase();
        if(db.isOpen()) {
            Cursor cursor = db.rawQuery("select mode from blacknumber where number=?", new String[]{number});
            if(cursor.moveToFirst()) {
                result = cursor.getInt(0);
            }
            cursor.close();
            db.close();
        }
        return result;
    }

    /**
     * 添加一条黑名单号码
     */
    public boolean add(String number, String mode) {
        //首先判断数据库是否存在该号码
        if(find(number)) return false;  //如果存在，直接停止该方法的执行
        SQLiteDatabase db = helper.getWritableDatabase();
        if(db.isOpen()) {
            db.execSQL("insert into blacknumber(number, mode) values(?, ?)", new Object[]{number, mode});
            db.close();
        }
        return find(number);
    }

    public void delete(String number) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if(db.isOpen()) {
            db.execSQL("delete from blacknumber where number=?", new String[]{number});
            db.close();
        }
    }

    /**
     * 更改黑名单号码
     */
    public void update(String oldNumber, String newNumber, String mode) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if(db.isOpen()) {
            if(TextUtils.isEmpty(newNumber)) {
                newNumber = oldNumber;  //用户并没有修改号码
            }
            db.execSQL("update blacknumber set number=?, mode=? where number=?", new Object[]{newNumber, mode, oldNumber});
            db.close();
        }
    }

    /**
     * 查找全部的黑名单号码
     * @return
     */
    public List<BlackNumber> findAll() {
        List<BlackNumber> numbers = new ArrayList<BlackNumber>();
        SQLiteDatabase db = helper.getReadableDatabase();
        if(db.isOpen()) {
            Cursor cursor = db.rawQuery("select number, mode from blacknumber", null);
            while(cursor.moveToNext()) {
                BlackNumber blackNumber = new BlackNumber();
                blackNumber.setNumber(cursor.getString(0));
                blackNumber.setMode(cursor.getInt(1));
                numbers.add(blackNumber);
                blackNumber = null;
            }
            cursor.close();
            db.close();
        }
        return numbers;
    }
}
