package com.example.wang.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wang.imitation_weichat.R;


/**
 * Created by wang on 16-1-18.
 */
public class ButtomRadioButton extends LinearLayout{

    private Context context;
    private ImageView im_radio_button;
    private TextView tv_radio_button;
    private TextView tv_noreadmessage;

    public ButtomRadioButton(Context context) {
        this(context, null);
    }

    public ButtomRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }
    //初始化各个布局
    private void initView(){
        View view = View.inflate(getContext(), R.layout.buttom_radio, this);
        im_radio_button = (ImageView) view.findViewById(R.id.im_radio_button);
        tv_radio_button = (TextView) view.findViewById(R.id.tv_radio_button);
        tv_noreadmessage = (TextView) view.findViewById(R.id.tv_noreadmessage);
    }
    public  void  setImageBackgroundResource(int backgroundResourceId){
        im_radio_button.setBackgroundResource(backgroundResourceId);
    }
    public  void setText(String name){
        tv_radio_button.setText(name);
    }

    public void setNoReadNum(int number) {
        if (number == 0) {
            tv_noreadmessage.setVisibility(GONE);
        } else {
            tv_noreadmessage.setVisibility(VISIBLE);
            tv_noreadmessage.setText(number + "");
        }
    }
}
