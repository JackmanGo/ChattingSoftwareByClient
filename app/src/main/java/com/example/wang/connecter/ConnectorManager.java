package com.example.wang.connecter;


import android.util.Log;

import com.example.wang.request.AuthRequest;
import com.example.wang.request.Request;

import java.io.IOException;

/**
 * Created by wang on 16-1-28.
 */
public class ConnectorManager implements Connector.ConnectorListener {
    private static ConnectorManager instance;
    private Connector connector;
    private ConnectorListener listener;

    public static ConnectorManager getInstance() {
        if (instance == null) {
            synchronized (ConnectorManager.class) {
                if (instance == null) {
                    instance = new ConnectorManager();
                }
            }
        }
        return instance;
    }
    private ConnectorManager() {

    }
    //认证
    public void connnect(AuthRequest auth) throws IOException {
        connector = new Connector();
        connector.setConnectorListener(this);
        connector.connect();
        //System.out.println(auth.getData());
        Log.d("socketLog",auth.getData());
        connector.auth(auth.getData());
    }
    //发送消息
    public void putRequest(Request request) {
        connector.putRequest(request.getData());
    }
    @Override
    public void pushData(String data) {
          if(listener!=null){
              listener.pushData(data);
          }
    }
    public void setConnectorListener(ConnectorListener listener) {
        this.listener = listener;
    }

    public interface ConnectorListener {
        void pushData(String data);
    }
}
