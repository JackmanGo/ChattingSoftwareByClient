package com.example.wang.request;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuthRequest implements Request {
	private Map<String, String> map = new HashMap<String, String>();

	public AuthRequest(String sender,boolean isAuthRequest) {
		if(isAuthRequest) {
			map.put("type", "request");
		}else {
			map.put("type","reconnection");
		}
		map.put("sequence", UUID.randomUUID().toString());
		map.put("action", "auth");
		map.put("sender", sender);
	}

	@Override
	public String getData() {
		return new Gson().toJson(map);
	}

}
