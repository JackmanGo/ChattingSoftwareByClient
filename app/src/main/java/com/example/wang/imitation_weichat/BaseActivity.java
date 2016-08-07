package com.example.wang.imitation_weichat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by wang on 16-1-21.
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseApplication)getApplication()).addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((BaseApplication)getApplication()).removeActivity(this);
    }
}
