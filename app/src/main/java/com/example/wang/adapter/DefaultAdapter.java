package com.example.wang.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by wang on 16-1-28.
 */
public class DefaultAdapter<T> extends BaseAdapter {
    private List<T> listData;
    public DefaultAdapter(List<T> list){
        super();
        this.listData = list;
    }
    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int i) {
        return listData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }
}
