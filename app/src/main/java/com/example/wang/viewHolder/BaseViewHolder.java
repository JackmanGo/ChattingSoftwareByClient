package com.example.wang.viewHolder;

import android.view.View;

/**
 * Created by wang on 16-1-28.
 */
public abstract class BaseViewHolder<T> {
    private View view;
    private T detailInfos;
    public BaseViewHolder(){
        view = initView();
        view.setTag(this);
    }
    public void setDetailInfos(T detailInfos) {
        this.detailInfos = detailInfos;
        refreshView(detailInfos);
    }

    public View getView() {
        return view;
    }

    public abstract View initView();
    public abstract View refreshView(T detailInfos);
}
