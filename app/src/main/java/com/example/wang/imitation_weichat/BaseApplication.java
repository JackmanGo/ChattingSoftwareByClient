package com.example.wang.imitation_weichat;

import android.app.Application;
import android.os.Handler;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by wang on 16-1-21.
 */
public class BaseApplication extends Application {
    private static Application myApplication;
    private static int mainID;
    private static Handler handler;
    //LinkedList适合增删
    private List<BaseActivity> activitys = new LinkedList<BaseActivity>();
    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
        mainID = android.os.Process.myTid();
        handler = new Handler();
    }
    //获取context
    public static Application getApplication(){
       return  myApplication;
   }
    //获取主线程
    public static int getMainID(){
        return mainID;
    }
    //获取Handler
    public static Handler getHandler() {
        return handler;
    }

    public void addActivity(BaseActivity baseActivity) {
       activitys.add(baseActivity);
    }

    public void removeActivity(BaseActivity baseActivity) {
        activitys.remove(baseActivity);
    }
    //退出应用时
    public void closeApplication(){
      ListIterator<BaseActivity> listIterator = activitys.listIterator();
        while (listIterator.hasNext()){
            BaseActivity baseActivity = listIterator.next();
            baseActivity.finish();
        }
    }
}
