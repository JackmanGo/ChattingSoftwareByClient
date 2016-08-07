package com.example.wang.dao;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.wang.sql.MySqliteHelper;
import com.example.wang.utils.UiUtils;

/**
 * Created by wang on 16-1-30.
 */
public class BaseDao {
    public static SQLiteDatabase createGroupDatabase(){
        MySqliteHelper mySqliteHelper = new MySqliteHelper(UiUtils.getContext(),1);
        SQLiteDatabase db = mySqliteHelper.getWritableDatabase();
        return db;
    }
    //自定义内容观察者
    public static void registerContentResolver(String table){
     UiUtils.getContext().getContentResolver().notifyChange(Uri.parse(table),null);
    }
}
