package com.example.wang.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by wang on 16-1-30.
 */
public class DialoguesDao extends BaseDao{
    public static void insertDialogue(String chat_id,String content,String type){
        ContentValues dialogues = new ContentValues();
        dialogues.put("dialogues_name",chat_id);
        dialogues.put("content",content);
        dialogues.put("time",System.currentTimeMillis());
        dialogues.put("type", type);
        dialogues.put("isread","0");
        SQLiteDatabase db = createGroupDatabase();
        db.insert("dialogues",null,dialogues);
        registerContentResolver("dialogues");
    }
    //点击进入对话后，即标识为全部已读
    public static void readAllMessage(String chat_id){
        //获取数据库或创建后获取
        SQLiteDatabase db = createGroupDatabase();
        db.execSQL("update dialogues set isread = ? where dialogues_name = ?", new String[]{"1", chat_id});
        //发通知
        registerContentResolver("dialogues");
    }
    //查询此会话中的未读数   0表示未读
    public static int unreadsum(String chat_id){
        SQLiteDatabase db = createGroupDatabase();
        Cursor cursor =  db.rawQuery("select count(*) as sum from dialogues where isread = ? and dialogues_name = ? and type = ?;", new String[]{"0",chat_id,"response"});
        cursor.moveToNext();
        return   Integer.parseInt(cursor.getString(cursor.getColumnIndex("sum")));
    }
    public static Cursor getDialogueCursor(String dialogues_id){
        //获取数据库或创建后获取
        SQLiteDatabase db = createGroupDatabase();
        Cursor cursor =  db.rawQuery("select dialogues_id as _id,content as content,time as time,type as type from dialogues where dialogues_name=?",new String[]{dialogues_id});
        return  cursor;
    }
}
