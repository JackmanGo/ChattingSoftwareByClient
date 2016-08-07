package com.example.wang.imitation_weichat;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.wang.connecter.ConnectorManager;
import com.example.wang.request.AskForRequest;
import com.example.wang.service.CoreService;
import com.example.wang.utils.InfoUtils;
import com.example.wang.view.TopBar;

/**
 * Created by wang on 16-3-27.
 */
public class UserDetail extends BaseActivity {

    private TextView tv_user_id;
    private TextView tv_user_name;
    private TextView tv_user_signature;
    private Button btn_addFriend;
    private Intent intent;
    private String user_id;
    private TopBar topBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userdetail);
        intent = getIntent();
        init();
        initListener();
    }
    private void init(){
        topBar = (TopBar) findViewById(R.id.user_detail_topbar);
        tv_user_id = (TextView) findViewById(R.id.user_id);
        tv_user_name = (TextView) findViewById(R.id.user_name);
        tv_user_signature = (TextView) findViewById(R.id.user_signature);
        btn_addFriend = (Button) findViewById(R.id.btn_addFriend);
        user_id = intent.getStringExtra("user_id");
        tv_user_id.setText(user_id);
        tv_user_name.setText(intent.getStringExtra("user_name") == null ? "未填写" : intent.getStringExtra("user_name"));
        tv_user_signature.setText(intent.getStringExtra("user_signature")==null?"无":intent.getStringExtra("user_signature"));
    }
    private void initListener(){
        btn_addFriend.setOnClickListener(new View.OnClickListener() {

            private String ask_for_infos;

            @Override
            public void onClick(View view) {
                //弹出一个输入验证信息的对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(UserDetail.this);
                builder.setTitle("请输入验证信息:");
                AlertDialog dialog = builder.create();
                View dialog_view = View.inflate(UserDetail.this,R.layout.dialog_askfor_addfriend,null);
                dialog.setView(dialog_view);
                dialog.show();
                Button ask_for_button = (Button) dialog_view.findViewById(R.id.ask_for_button);
               final  EditText  ask_for_edit = (EditText) dialog_view.findViewById(R.id.ask_for_infos);

                ask_for_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ask_for_infos = ask_for_edit.getText().toString();
                        AskForRequest askForRequest = new AskForRequest(InfoUtils.getUsername(),user_id,ask_for_infos);
                        //设置监听到数据的事件
                        ConnectorManager.getInstance().setConnectorListener(new CoreService());
                        //发送消息给要申请的用户
                        ConnectorManager.getInstance().putRequest(askForRequest);
                        //结束该Activity
                        UserDetail.this.finish();
                    }
                });
            }
        });
        topBar.setBack_imgListener(this);
    }
}
