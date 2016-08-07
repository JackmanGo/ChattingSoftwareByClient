package com.example.wang.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by wang on 16-1-30.
 */
public class ChatsDao extends BaseDao {

    public static void insertChats(String sender,String content,String time){
        SQLiteDatabase db = createGroupDatabase();
        Cursor cursor =  db.rawQuery("select * from chats where chats_id = ?", new String[]{sender});
        if(cursor.getCount()==0){
            ContentValues chats = new ContentValues();
            chats.put("chats_id",sender);
            chats.put("content", content);
            chats.put("time",time);
            db.insert("chats", null, chats);
        }else{
            db.execSQL("update chats set content = ?,time = ? where chats_id = ?",new String[]{content,time,sender});
        }
       registerContentResolver("chats");
    }

    public static Cursor getChatCursor(){
        //获取数据库或创建后获取
        SQLiteDatabase db = createGroupDatabase();
        Cursor cursor =  db.rawQuery("select chats_id as _id,content as content,time as time from chats",null);
        return  cursor;
    }
    //查询所有的未读数
    public static String getAllUnRead(){
        int allunread = 0;
        SQLiteDatabase db = createGroupDatabase();
        Cursor cursor = db.rawQuery("select chats_id from chats",null);
        while (cursor.moveToNext()){
             allunread+=DialoguesDao.unreadsum(cursor.getString(0));
        }
        return  allunread+"";
    }
    public static boolean isInChat(String friend_id){
        SQLiteDatabase sqLiteDatabase = createGroupDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select content from chats where chats_id = ?", new String[]{friend_id});
        if(cursor.getCount()!=0){
            return true;
        }
        return false;
    }

    public static void deleteChat(String friend_id) {
        SQLiteDatabase sqLiteDatabase = createGroupDatabase();
        sqLiteDatabase.execSQL("delete from chats where chats_id=?", new String[]{friend_id});
        registerContentResolver("chats");
    }
}
