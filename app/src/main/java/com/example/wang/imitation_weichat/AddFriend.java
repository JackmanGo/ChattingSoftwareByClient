package com.example.wang.imitation_weichat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.wang.view.TopBar;

/**
 * Created by wang on 16-3-19.
 */
public class AddFriend extends BaseActivity {

    private TopBar topBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriend);
        initView();
    }
    private void initView(){
       TextView search_others = (TextView) findViewById(R.id.search_others);
        topBar = (TopBar) findViewById(R.id.addfriend_topbar);
        search_others.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddFriend.this, SearchFriend.class);
                AddFriend.this.startActivity(intent);
            }
        });
        topBar.setBack_imgListener(this);
    }
}
