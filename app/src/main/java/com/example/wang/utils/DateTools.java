package com.example.wang.utils;

import android.text.format.DateFormat;
import android.text.format.DateUtils;

/**
 * Created by wang on 16-1-6.
 */
public class DateTools {
    public  static  String formatDate(long date){
        //使用系统工具类，判断是否是今天,如果是今天就格式化为时间。如果不是就格式化为年月日
        if(DateUtils.isToday(date)){
          return  DateFormat.getTimeFormat(UiUtils.getContext()).format(date);
        }else{
          return   DateFormat.getDateFormat(UiUtils.getContext()).format(date);
        }
    }
    public  static  String formatDate(String date){
        long long_date = Long.parseLong(date);
        //使用系统工具类，判断是否是今天,如果是今天就格式化为时间。如果不是就格式化为年月日
        if(DateUtils.isToday(long_date)){
            return  DateFormat.getTimeFormat(UiUtils.getContext()).format(long_date);
        }else{
            return   DateFormat.getDateFormat(UiUtils.getContext()).format(long_date);
        }
    }
}
