package com.example.wang.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wang on 16-1-14.
 */
public class MySqliteHelper extends SQLiteOpenHelper {

    public MySqliteHelper(Context context,int version) {
        super(context,"weichatDB.db",null, version);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //保存会话记录
        //0表示未读，1表示已读
        sqLiteDatabase.execSQL("create table dialogues(dialogues_id integer primary key autoincrement,dialogues_name varchar(20),content varchar(100),time varchat(100),type varchar(20),isread varchar(10));");
        //聊天列表
        sqLiteDatabase.execSQL("create table chats(chats_id varchar(20) primary key ,content varchar(100),time varchat(100),unread_sum varchar(10));)");
        //添加好友信息表
        sqLiteDatabase.execSQL("create table waitForVerify(waitForVerify_id integer primary key autoincrement,sender_id varchar(20),content varchar(255))");
        //本地保存用户的好友信息
        sqLiteDatabase.execSQL("create table myFriends(myFriends_id integer primary key autoincrement,friend_id varchar(255));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
