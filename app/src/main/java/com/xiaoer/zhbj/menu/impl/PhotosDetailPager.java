package com.xiaoer.zhbj.menu.impl;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.xiaoer.zhbj.menu.BaseMenuDetailPager;

public class PhotosDetailPager extends BaseMenuDetailPager {
    public PhotosDetailPager(Activity activity) {
        super(activity);
    }

    @Override
    public View initView() {
        TextView view = new TextView(mActivity);
        view.setText("菜单详情页-组图");
        view.setTextColor(Color.RED);
        view.setTextSize(22);
        view.setGravity(Gravity.CENTER);
        return view;
    }

    @Override
    public void initData() {

    }

    @Override
    public void onDestroy() {

    }
}
