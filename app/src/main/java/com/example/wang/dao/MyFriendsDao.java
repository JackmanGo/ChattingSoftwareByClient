package com.example.wang.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.wang.utils.InfoUtils;
import com.example.wang.utils.ToastUtils;
import com.example.wang.utils.UiUtils;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang on 16-4-16.
 */
public class MyFriendsDao extends BaseDao {

    private static OkHttpClient mOkHttpClient = UiUtils.getOkHttpClient();
    private static RequestBody formBody;

    public static void getAllMyFriendsFromServer() {
        //访问网络
        //创建请求参数
        try {
            formBody = new FormEncodingBuilder()
                    .add("login_id", URLEncoder.encode(InfoUtils.getUsername(), "UTF-8"))
                    .build();
        }catch (Exception e){}
//创建一个Request
        final Request request = new Request.Builder()
                .url(InfoUtils.GETAllFriend_URL).post(formBody)
                .build();
//new call
        final Call call = mOkHttpClient.newCall(request);
//异步请求,加入调度
        call.enqueue(new Callback() {

            private List<String> allFriend = new ArrayList<String>();
            private JSONObject jsonObject;

            @Override
            public void onFailure(Request request, IOException e) {
                UiUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToast("网络异常!");
                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {
                 String result_json = response.body().string();
                try {
                    jsonObject = new JSONObject(result_json);
                    JSONArray jsonArray  =  jsonObject.getJSONArray("info");
                    for(int i=0;i<jsonArray.length();i++){
                        allFriend.add(jsonArray.getString(i));
                    }
                    //序列化保存好友信息(更新本地数据库)
                    Cursor cursor = getAllMyFriendsCursor();
                    if(cursor.getCount()==0) {
                        for(String friend_id:allFriend) {
                            saveInSQLite(friend_id);
                        }
                    }else {
                     //保持本地客户端的好友列表与服务器的一致
                        for(String friend : allFriend){
                           if(verifyExistInSQLite(friend).getCount()==0){
                                //服务器存在而客户端不存在。插入客户端
                               saveInSQLite(friend);
                           }
                        }
                    }
                }
                 catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private static void saveInSQLite(String friends_id){
        SQLiteDatabase sqLiteDatabase = createGroupDatabase();
        sqLiteDatabase.execSQL("insert into myFriends (friend_id) values (?)",new String[]{friends_id});
        //发通知
        registerContentResolver("myFriends");
    }
    public static Cursor getAllMyFriendsCursor(){
        SQLiteDatabase sqLiteDatabase = createGroupDatabase();
        return sqLiteDatabase.rawQuery("select myFriends_id as _id,friend_id from myFriends", null);
    }
    public static  void deleteMyFriend(String friends_id){
        SQLiteDatabase sqLiteDatabase = createGroupDatabase();
        sqLiteDatabase.execSQL("delete from myFriends where friend_id =?",new String[]{friends_id});
        //删除好友后。chat也应该被删除
        ChatsDao.deleteChat(friends_id);
        //发通知
        registerContentResolver("myFriends");
    }
    private static  Cursor  verifyExistInSQLite(String friends_id){
        SQLiteDatabase sqLiteDatabase = createGroupDatabase();
        return sqLiteDatabase.rawQuery("select * from myFriends where friend_id = ?", new String[]{friends_id});
    }
}
