package com.example.wang.imitation_weichat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.wang.dao.MyFriendsDao;
import com.example.wang.utils.ToastUtils;
import com.example.wang.view.TopBar;

/**
 * Created by wang on 16-4-16.
 */
public class FriendDetail extends BaseActivity {

    private String friend_id;
    private String friend_name;
    private String friend_signature;
    private TextView friend_id1;
    private TextView friend_name1;
    private TextView friend_signature1;
    private Button chat;
    private Button delete;
    private TopBar topBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frienddetail);
        initView();
        initData();
        initListener();
    }
    private void initView(){
        topBar = (TopBar) findViewById(R.id.friend_detail_topbar);
        friend_id1 = (TextView) findViewById(R.id.friend_id);
        friend_name1 = (TextView) findViewById(R.id.friend_name);
        friend_signature1 = (TextView) findViewById(R.id.friend_signature);
        chat = (Button) findViewById(R.id.btn_chatWithFriend);
        delete = (Button) findViewById(R.id.btn_deleteFriend);
    }
    private void initData(){
        Intent intent = getIntent();
        friend_id = intent.getStringExtra("friend_id");
        friend_name = intent.getStringExtra("friend_name");
        friend_signature = intent.getStringExtra("friend_signature");
        friend_id1.setText(friend_id);
        friend_name1.setText(friend_name);
        friend_signature1.setText(friend_signature==null?"未填写":friend_signature);
    }
    private void  initListener(){
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到chat界面
                Intent intent = new Intent(FriendDetail.this,DialogueActivity.class);
                intent.putExtra("chat_id",friend_id);
                FriendDetail.this.startActivity(intent);
            }
        });
        topBar.setBack_imgListener(this);
        //删除好友
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyFriendsDao.deleteMyFriend(friend_id);
                ToastUtils.showToast("删除成功");
                FriendDetail.this.finish();
            }
        });
    }
}
