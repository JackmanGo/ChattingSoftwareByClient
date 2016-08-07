package com.example.wang.imitation_weichat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wang.utils.InfoUtils;
import com.example.wang.utils.ToastUtils;
import com.example.wang.utils.UiUtils;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by wang on 16-3-19.
 */
public class SearchFriend extends BaseActivity {


    private EditText search_name_in_search;
    private TextView search_user_id;
    private LinearLayout linearlayout_searchfriend;
    private String search_username;
   private JSONObject jsonObject = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchfriend_activity);
        initView();
        initListener();
    }
    public void initView(){
        search_name_in_search = (EditText) findViewById(R.id.search_name_in_search);
        search_user_id = (TextView) findViewById(R.id.search_user_id);
        linearlayout_searchfriend = (LinearLayout) findViewById(R.id.linearlayout_searchfriend);
    }
    private void initListener(){
        //监听输入的内容
        search_name_in_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // ToastUtils.showToast(charSequence.toString());
                //当监听到用户的输入后显示下面的一个布局并把接收到的信息设置给下面的一个TextView
                linearlayout_searchfriend.setVisibility(View.VISIBLE);
                search_username = charSequence.toString();
                search_user_id.setText(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //监听搜索的点击事件
        linearlayout_searchfriend.setOnClickListener(new View.OnClickListener() {

            private OkHttpClient okHttpClient;

            @Override
            public void onClick(View view){
                //发送网络请求
                okHttpClient = UiUtils.getOkHttpClient();
                try {
                    //创建请求参数
                    RequestBody formBody = new FormEncodingBuilder()
                            .add("user_id", URLEncoder.encode(search_username, "UTF-8"))
                            .build();
                    //创建一个Request
                    final Request request = new Request.Builder()
                            .url(InfoUtils.SEARCH_URL).post(formBody)
                            .build();
                    //new call
                    Call call = okHttpClient.newCall(request);
                    //请求加入调度
                    call.enqueue(new Callback(){

                        @Override
                        public void onFailure(Request request, IOException e) {
                            UiUtils.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtils.showToast("网络异常!!!");
                                }
                            });
                        }
                        @Override
                        public void onResponse(Response response) throws IOException {
                            String result_json = response.body().string();

                            try {
                                jsonObject = new JSONObject(result_json);
                                final boolean result = jsonObject.getBoolean("result");
                                    UiUtils.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(!result){
                                                ToastUtils.showToast("查询不到该用户");
                                            }else{
                                                Intent intent = new Intent(SearchFriend.this,UserDetail.class);
                                                try {
                                                    intent.putExtra("user_id",jsonObject.getString("user_id"));
                                                    intent.putExtra("user_name",jsonObject.getString("user_name"));
                                                    intent.putExtra("user_signature",jsonObject.getString("user_signature"));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                SearchFriend.this.startActivity(intent);
                                            }
                                        }
                                    });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
