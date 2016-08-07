package com.example.wang.fragment;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.wang.imitation_weichat.HomeActivity;
import com.example.wang.imitation_weichat.R;
import com.example.wang.utils.UiUtils;

/**
 * Created by wang on 16-1-23.
 */
public class LogoFragment extends BaseFragment {
    private Button login_button;
    private Button register_button;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.logo_fra, null);
        init();
        initUI();
        initListener();
        return view;
    }
    private void init(){
        SharedPreferences sharedPreferences =  UiUtils.getContext().getSharedPreferences("login_info",UiUtils.getContext().MODE_PRIVATE);
        String save_username =  sharedPreferences.getString("username",null);
        if(!TextUtils.isEmpty(save_username)){
            Intent intent = new Intent(getActivity(), HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish();
        }
    }
    private void initUI() {
        login_button = (Button) view.findViewById(R.id.login_button);
        register_button = (Button) view.findViewById(R.id.register_button);
    }

    private void initListener() {
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(myClickButton!=null){
                   myClickButton.loginClick();
               }
            }
        });
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myClickButton!=null){
                    myClickButton.registerClick();
                }
            }
        });
    }
  //设置点击的处理
    private Click myClickButton;
    public void setMyClickButton(Click myClickButton){
        this.myClickButton = myClickButton;
    }
    public interface Click{
        void loginClick();
        void registerClick();
  }
}
