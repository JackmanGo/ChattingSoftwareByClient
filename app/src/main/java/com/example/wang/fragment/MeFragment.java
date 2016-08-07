package com.example.wang.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.wang.imitation_weichat.BaseApplication;
import com.example.wang.imitation_weichat.R;
import com.example.wang.utils.InfoUtils;
import com.example.wang.utils.UiUtils;

/**
 * Created by wang on 16-1-20.
 */
public class MeFragment extends BaseFragment {

    private View view;
    private Button btn_exitlogin;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.me_fra,null);
        initView();
        initData();
        initListener();
        return view;
    }
    private void initView(){
        btn_exitlogin = (Button) view.findViewById(R.id.btn_exitlogin);
    }
    private void initData(){
        TextView tv_my_id= (TextView) view.findViewById(R.id.tv_my_id);
        tv_my_id.setText("巨信号:"+InfoUtils.getUsername());
    }
    private void initListener(){
         btn_exitlogin.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 SharedPreferences login_info = UiUtils.getContext().getSharedPreferences("login_info", UiUtils.getContext().MODE_PRIVATE);
                 login_info.edit().clear().commit();
                 BaseApplication baseApplication = (BaseApplication) getActivity().getApplication();
                 baseApplication.closeApplication();
             }
         });
    }
}
