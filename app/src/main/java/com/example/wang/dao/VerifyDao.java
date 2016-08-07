package com.example.wang.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by wang on 16-4-15.
 */
public class VerifyDao extends BaseDao {

   public static void insertVerify(String sender_id,String content){
       /*ContentValues verify = new ContentValues();
       verify.put("sender_id",sender_id);
       verify.put("content", content);*/
       SQLiteDatabase sqLiteDatabase = createGroupDatabase();
       sqLiteDatabase.execSQL("insert into waitForVerify(sender_id,content) values (?,?);",new String[]{sender_id,content});
       //内容观察者
       registerContentResolver("waitForVerify");
   }
    public  static void  disposeVerify(String sender_id){
        SQLiteDatabase sqLiteDatabase = createGroupDatabase();
        sqLiteDatabase.execSQL("delete from waitForVerify where sender_id = ?", new String[]{sender_id});
        registerContentResolver("waitForVerify");
    }
    public static int selectAllVerifyNum(){
        SQLiteDatabase sqLiteDatabase = createGroupDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from waitForVerify", null);
        return cursor.getCount();
    }
    public static Cursor getAllVerifyInfo() {
        SQLiteDatabase sqLiteDatabase = createGroupDatabase();
        return sqLiteDatabase.rawQuery("select waitForVerify_id as _id , sender_id , content from waitForVerify", null);
    }
}
