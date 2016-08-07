package com.example.wang.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.wang.utils.ToastUtils;

/**
 * Created by wang on 16-1-27.
 */
public abstract class PushDataBroadcastReceiver extends BroadcastReceiver {
    public static final String  ACTION = "com.bigchat.receiver";
    public static final String DATA_KEY = "receiver_info";

    @Override
    public void onReceive(Context context, Intent intent) {
       String data =   intent.getStringExtra(DATA_KEY);
        //具体实现在HomeActivity中
    }
}
