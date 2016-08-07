package com.example.wang.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.wang.imitation_weichat.R;
import com.example.wang.utils.UiUtils;

/**
 * Created by wang on 16-1-21.
 */
public class TopBar extends RelativeLayout{

    private ImageView back_img;
    private TextView title_name;
    public ImageView actionView;
    private Context context;
    private TypedArray typedArray;
    private int action_imgID;

    public TopBar(Context context) {
        super(context, null);
    }

    public TopBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.TopBarAttr);
        init();
    }
    private void init(){
        View view = View.inflate(UiUtils.getContext(), R.layout.top_bar,this);
        back_img = (ImageView) view.findViewById(R.id.back_button);
        title_name = (TextView) view.findViewById(R.id.title_name);
        actionView = (ImageView) view.findViewById(R.id.bar_action);
        //获取自定义属性
        String titleName = typedArray.getString(R.styleable.TopBarAttr_title_name);
        if(!TextUtils.isEmpty(titleName)){
            setTitle_name(titleName);
        }
        boolean isBackShow = typedArray.getBoolean(R.styleable.TopBarAttr_show_back,true);
        setBack_imgIsShow(isBackShow);
        boolean isActionShow = typedArray.getBoolean(R.styleable.TopBarAttr_show_action,true);
        setAction_imgIsShow(isActionShow);
        action_imgID = typedArray.getResourceId(R.styleable.TopBarAttr_action_img, -1);
        //销毁TypedArray对象
        typedArray.recycle();
    }

    public void setTitle_name(String new_title_name){
        title_name.setText(new_title_name);
    }
    public void setBack_imgIsShow(boolean isShow){
      if(!isShow){
          back_img.setVisibility(View.GONE);
      }
    }
    public void setAction_imgIsShow(boolean isShow){
       if(!isShow){
           actionView.setVisibility(View.GONE);
       }
    }
    public void setSrc(int id){
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),id);
        actionView.setImageBitmap(bitmap);
    }
    public void setBack_imgListener(final Activity activity){
       back_img.setOnClickListener(new OnClickListener() {
           @Override
           public void onClick(View view) {
              activity.finish();
           }
       });
    }
}
