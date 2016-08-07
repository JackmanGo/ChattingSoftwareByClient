package com.example.wang.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by wang on 16-4-26.
 */
public class NetWorkBroadcastReceiver extends BroadcastReceiver{
    public static final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    @Override
    public void onReceive(Context context, Intent intent) {
        /**
         * 具体的实现在HomeActivity中
         */



       /* String action = intent.getAction();
        Log.i("TAG", "PfDataTransReceiver receive action " + action);
        if(TextUtils.equals(action,CONNECTIVITY_CHANGE_ACTION)){//网络变化的时候会发送通知
            Log.i("TAG", "网络变化了");
            //判断此时的网络情况
            ConnectivityManager mConnMgr = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            //如果mConnMgr为null说明无网络
            if (mConnMgr != null){
                NetworkInfo aActiveInfo = mConnMgr.getActiveNetworkInfo(); // 获取活动网络连接信息
                if(aActiveInfo!=null) {
                    Log.i("TAG", "网络OK");
                    if(NetWorkInfos.NOW_NETWORK_STATIC!=null&&NetWorkInfos.NOW_NETWORK_STATIC!=NetWorkInfos.NETWORKISFAILE){
                        HomeActivity.reConnection();
                    }
                    //网络通畅，向服务器发送请求保持连接
                    NetWorkInfos.NOW_NETWORK_STATIC = NetWorkInfos.NETWORKISOK;
                }else{
                    Log.i("TAG", "网络Failed");
                    NetWorkInfos.NOW_NETWORK_STATIC=NetWorkInfos.NETWORKISFAILE;
                }
            }
            return;
        }
        */
    }
}
