package com.example.wang.utils;

import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * Created by wang on 16-1-27.
 */
public class InfoUtils {
    public static String SOCKET_IP = "192.168.2.103:8080";
    public static String SOCKET_CHANNEL="192.168.2.103";
    public static int SOCKET_PORT = 10000;
    public static String LOGIN_URL = "http://"+SOCKET_IP+"/login";
    public static String REGISTER_URL = "http://"+SOCKET_IP+"/register";
    public static String SEARCH_URL="http://"+SOCKET_IP+"/findOtherUser";
    public static String AgreeFriend_URL = "http://"+SOCKET_IP+"/agreeAddFriend";
    public static String GETAllFriend_URL = "http://"+SOCKET_IP+"/getAllFriends";
    public static String getUsername(){
        SharedPreferences sharedPreferences =  UiUtils.getContext().getSharedPreferences("login_info",UiUtils.getContext().MODE_PRIVATE);
        String save_username =  sharedPreferences.getString("username",null);
        return save_username;
    }
    public static void saveOrupdateLoginInfo(String username){
        SharedPreferences sharedPreferences =  UiUtils.getContext().getSharedPreferences("login_info",UiUtils.getContext().MODE_PRIVATE);
        String save_username =  sharedPreferences.getString("username",null);
        if(TextUtils.isEmpty(save_username)||!save_username.equals(username)){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("username",username);
            editor.commit();
        }
    }
}
