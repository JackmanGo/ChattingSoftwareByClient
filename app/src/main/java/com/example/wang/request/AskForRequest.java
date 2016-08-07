package com.example.wang.request;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wang on 16-3-29.
 */
public class AskForRequest implements Request {

   private  Map<String,String> map = new HashMap<String,String>();

    public AskForRequest(String sender,String receiver,String content){
        map.put("action","ask_for");
        map.put("UUID",java.util.UUID.randomUUID().toString());
        map.put("sender",sender);
        map.put("receiver",receiver);
        if(content!=null){
            map.put("content",content);
        }else{
            map.put("content","请求添加我为好友");
        }
        map.put("type", "request");
    }




    @Override
    public String getData() {

        return new Gson().toJson(map);
    }
}
