package com.example.wang.utils;

import android.widget.Toast;

/**
 * Created by wang on 16-1-21.
 */
public class ToastUtils {
     public static   void  showToast(String info){
       Toast.makeText(UiUtils.getContext(), info, Toast.LENGTH_SHORT).show();
     }

}
