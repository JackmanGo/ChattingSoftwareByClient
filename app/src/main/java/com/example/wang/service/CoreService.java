package com.example.wang.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.wang.broadcastReceiver.PushDataBroadcastReceiver;
import com.example.wang.connecter.ConnectorManager;
import com.example.wang.request.AuthRequest;
import com.example.wang.utils.InfoUtils;
import com.example.wang.utils.UiUtils;

import java.io.IOException;

/**
 * Created by wang on 16-1-27.
 */
public class CoreService extends Service implements ConnectorManager.ConnectorListener {
    ConnectorManager connectorManager;
    @Override
    public void onCreate() {
        super.onCreate();
        connectorManager = ConnectorManager.getInstance();
        //设置监听
        connectorManager.setConnectorListener(CoreService.this);
        //开启线程链接服务器
        keepSocketWithService(true);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MiddleMan();
    }
    //发送广播
    @Override
    public void pushData(String data) {
        Intent intent = new Intent();
        intent.setAction(PushDataBroadcastReceiver.ACTION);
        intent.putExtra(PushDataBroadcastReceiver.DATA_KEY,data);
        UiUtils.getContext().sendBroadcast(intent);
    }
    //连接客户端与服务器
    public  void  keepSocketWithService( final boolean isAutoRequest){
        new Thread(new Runnable() {
            @Override
            public void run() {
                   /* String save_username = InfoUtils.getUsername();
                    AuthRequest authRequest = new AuthRequest(save_username);
                    connectorManager.connnect(authRequest);*/
        String save_username = InfoUtils.getUsername();
        AuthRequest authRequest = null;
        //根据是用户第一次登录还是重新连接来构造不同的AutoRequest
        if(isAutoRequest) {
            authRequest = new AuthRequest(save_username,isAutoRequest);
        }else {
            authRequest = new AuthRequest(save_username,isAutoRequest);
        }
        try {
            connectorManager.connnect(authRequest);
        }catch (IOException e){}
            }
        }).start();
    }

    class MiddleMan extends Binder implements ServiceMethod{

        @Override
        public void keepSocketWithService(boolean isAutoRequest) {
            CoreService.this.keepSocketWithService(isAutoRequest);
        }
    }
}
