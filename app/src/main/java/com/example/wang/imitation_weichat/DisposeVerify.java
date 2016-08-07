package com.example.wang.imitation_weichat;

import android.app.Activity;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.wang.dao.VerifyDao;
import com.example.wang.utils.InfoUtils;
import com.example.wang.utils.ToastUtils;
import com.example.wang.utils.UiUtils;
import com.example.wang.view.TopBar;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * Created by wang on 16-4-15.
 */
public class DisposeVerify extends BaseActivity {

    private Cursor cursor;
    private ListView lv_verify;
    private OkHttpClient mOkHttpClient;
    private TopBar topBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispose_verify);
        initData();
        initView();
        initListener();
    }
    private void initData(){
        cursor = VerifyDao.getAllVerifyInfo();
    }
    private void initView(){
        lv_verify = (ListView) findViewById(R.id.lv_verify);
        topBar = (TopBar) findViewById(R.id.disposeverify_topbar);
        VerifyAdapter verifyAdapter = new VerifyAdapter(this,cursor);
        lv_verify.setAdapter(verifyAdapter);
    }
    private void initListener(){
        mOkHttpClient = UiUtils.getOkHttpClient();
        //监听变化
        UiUtils.getContext().getContentResolver() .registerContentObserver(Uri.parse("content://waitForVerify"), true, new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                //cursorAdapter不能使用notifyDataSetChanged来实时更新，而使用cursor调用requery即可
                //messageAdapter.notifyDataSetChanged();
                cursor.requery();
            }
        });
        topBar.setBack_imgListener(this);
    }
    class VerifyAdapter extends CursorAdapter{
         public VerifyAdapter(Context context, Cursor c) {
             super(context, c);
         }


        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            View view = View.inflate(context,R.layout.item_dispose_verify,null);
            VerifyInfoHolder verifyInfoHolder = new VerifyInfoHolder();
            verifyInfoHolder.verify_sender_head_view = (ImageView) view.findViewById(R.id.verify_sender_head_view);
            verifyInfoHolder.verify_sender_id = (TextView) view.findViewById(R.id.verify_sender_id);
            verifyInfoHolder.verify_content = (TextView) view.findViewById(R.id.verify_content);
            verifyInfoHolder.verify_agree= (Button) view.findViewById(R.id.verify_agree);
            view.setTag(verifyInfoHolder);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            VerifyInfoHolder verifyInfoHolder = (VerifyInfoHolder) view.getTag();
            verifyInfoHolder.verify_sender_id.setText(cursor.getString(cursor.getColumnIndex("sender_id")));
            final String sender_id = verifyInfoHolder.verify_sender_id.getText().toString();
            verifyInfoHolder.verify_content.setText(cursor.getString(cursor.getColumnIndex("content")));
            verifyInfoHolder.verify_agree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //发送给服务器建立两种的好友关系
                    try{
                        //创建请求参数
                        RequestBody formBody = new FormEncodingBuilder()
                                .add("others_id", URLEncoder.encode(sender_id, "UTF-8"))
                                .add("login_id",URLEncoder.encode(InfoUtils.getUsername(),"UTF-8"))
                                .build();
                        //创建一个Request
                        final Request request = new Request.Builder()
                                .url(InfoUtils.AgreeFriend_URL).post(formBody)
                                .build();
                        //new call
                        Call call = mOkHttpClient.newCall(request);
                        //异步请求,加入调度
                        call.enqueue(new Callback() {

                            private boolean result;
                            private JSONObject jsonObject;

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
                                final String result_json = response.body().string();
                                try {
                                    jsonObject = new JSONObject(result_json);
                                    result = jsonObject.getBoolean("result");
                                }catch (Exception e){}
                                UiUtils.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                       if(result) {
                                           ToastUtils.showToast("添加成功");
                                       }else{
                                           ToastUtils.showToast("该用户已经是你的好友，不能重复添加。");
                                       }
                                        //删除waitForVerify表中已经处理的数据
                                        VerifyDao.disposeVerify(sender_id);
                                    }
                                });

                            }
                        });
                    }catch (Exception e){}
                }
            });
        }
    }
    static class VerifyInfoHolder{
        ImageView verify_sender_head_view;
        TextView verify_sender_id;
        TextView verify_content;
        Button verify_agree;
    }
}
