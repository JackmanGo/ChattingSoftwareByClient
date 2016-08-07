package com.example.wang.connecter;


import com.example.wang.utils.InfoUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by wang on 16-1-27.
 */
public class Connector {
    private String userID;
    private ConnectorListener listener;
    private Socket socket;
    //初始化一个队列
    private ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(8);
    public Connector() {

    }
    //把通信分成发送消息是一个线程
    //接收消息是一个线程
    public void connect() throws IOException {
        // 三次握手
        if (socket == null || socket.isClosed()) {
            socket = new Socket(InfoUtils.SOCKET_IP, InfoUtils.SOCKET_PORT);
        }
        //发送消息
       new Thread(new SendMessage()).start();
        //接收消息
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    InputStream inputStream = socket.getInputStream();
                    final byte[] buff = new byte[1024];
                    int len = 0;
                    //接收服务器的消息
                    while ((len = inputStream.read(buff)) > 0) {
                        userID = new String(buff, 0, len);
                        if (listener != null) {
                            listener.pushData(userID);
                        }
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }.start();
    }
    //用来处理发生消息
    public class SendMessage implements Runnable{
        @Override
        public void run() {
            try {
                OutputStream os = socket.getOutputStream();
                while (true){
                    String content = queue.take();
                    os.write(content.getBytes());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //认证信息
    public void auth(String authInfos){
        putRequest(authInfos);
    }
    public void putRequest(String sendMessage){
        try {
            System.out.println(sendMessage);
            queue.put(sendMessage);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //断开连接
    public void disConnect(){
        if(socket!=null){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            socket = null;
        }
    }
    public void setConnectorListener(ConnectorListener listener) {
        this.listener = listener;
    }

    public interface ConnectorListener {
        void pushData(String data);
    }
}
