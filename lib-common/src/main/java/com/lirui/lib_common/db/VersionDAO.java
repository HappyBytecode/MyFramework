package com.lirui.lib_common.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lirui.lib_common.version.bean.VersionBean;

/**
 * Created by long on 2016/5/26.
 * 数据库访问实现类
 */
public class VersionDAO implements IVersionDAO {

    private DbHelper mDbHelper = null;


    private VersionDAO() {
        // 创建数据库
        mDbHelper = new DbHelper();
    }

    private static class HolderClass {
        private static final VersionDAO instance = new VersionDAO();
    }

    public static VersionDAO getInstance() {
        return HolderClass.instance;
    }

    @Override
    public void insert(VersionBean info) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.insert(DbHelper.VERSION_TABLE_NAME, null, info.toValues());
    }

    @Override
    public VersionBean query(int versionCode) {
        VersionBean VersionBean = null;
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Cursor cursor = db.query(DbHelper.VERSION_TABLE_NAME, null, "versionCode = ?", new String[]{String.valueOf(versionCode)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String filePath = cursor.getString(cursor.getColumnIndex("filePath"));
            String time = cursor.getString(cursor.getColumnIndex("time"));
            String updateMsg = cursor.getString(cursor.getColumnIndex("updateMsg"));
            int isMustUpdate = cursor.getInt(cursor.getColumnIndex("isMustUpdate"));
            VersionBean = new VersionBean(versionCode, updateMsg, isMustUpdate == 1, filePath, time);
        }
        if (cursor != null) {
            cursor.close();
        }
        return VersionBean;
    }

    @Override
    public boolean isExists(int versionCode) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Cursor cursor = db.query(DbHelper.VERSION_TABLE_NAME, null, "versionCode = ?", new String[]{String.valueOf(versionCode)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            cursor.close();
            return true;
        } else {
            if (cursor != null) {
                cursor.close();
            }
            return false;
        }
    }
}
