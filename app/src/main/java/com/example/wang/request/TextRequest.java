package com.example.wang.request;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TextRequest implements Request {
	private Map<String, String> map = new HashMap<String, String>();

	public TextRequest(String sender, String receiver,
			String content) {
		map.put("type", "request");
		map.put("sequence", UUID.randomUUID().toString());
		map.put("action", "text");
		map.put("sender", sender);
		map.put("receiver", receiver);
		map.put("content", content);
	}

	@Override
	public String getData() {
		return new Gson().toJson(map);
	}

}
