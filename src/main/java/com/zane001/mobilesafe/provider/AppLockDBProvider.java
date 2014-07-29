package com.zane001.mobilesafe.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import com.zane001.mobilesafe.db.dao.AppLockDao;

/**
 * Created by zane001 on 2014/7/26.
 */
public class AppLockDBProvider extends ContentProvider {

    private static final int ADD = 1;
    private static final int DELETE = 2;
    private AppLockDao dao;
    public static UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        matcher.addURI("com.zane001.applock", "ADD", ADD);
        matcher.addURI("com.zane001.applock", "DELETE", DELETE);
    }

    @Override
    public boolean onCreate() {
        dao = new AppLockDao(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings2, String s2) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int result = matcher.match(uri);
        if(result == ADD) {
            String packName = contentValues.getAsString("packName");
            dao.add(packName);
            //发布内容的变化通知
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        int result = matcher.match(uri);
        if(result == DELETE) {
            dao.delete(strings[0]);
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
