package com.example.wang.utils;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.wang.imitation_weichat.BaseApplication;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

/**
 * Created by wang on 16-1-21.
 */
public class UiUtils {
    //创建okHttpClient对象
    static  OkHttpClient okHttpClient = new OkHttpClient();
    //获取context
    public static Context getContext(){
        return BaseApplication.getApplication();
    }
    //获取okHttpClient对象
    public static OkHttpClient getOkHttpClient(){
        okHttpClient.setConnectTimeout(2000,TimeUnit.SECONDS);
        return okHttpClient;
    }
    //执行在主线程中
    public static void runOnUiThread(Runnable runnable){
        //根据当前是在主线程还是子线程执行不同的操作
        if(android.os.Process.myTid()==BaseApplication.getMainID()){
            //在主线程中直接运行即可
            runnable.run();
        }else{
            //说明在子线程中,通过handler的post来执行
            BaseApplication.getHandler().post(runnable);
        }
    }
    //隐藏键盘
    public static void cancelKeyBoard(EditText editText){
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(),0);
    }
}
