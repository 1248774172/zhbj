package com.xiaoer.zhbj.pager.impl;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.xiaoer.zhbj.pager.BasePager;

public class HomePager extends BasePager {


    public HomePager(Activity activity, Fragment fragment) {
        super(activity, fragment);
    }

    @Override
    public void initData() {
//        setToolBarTitle("首页");
//        setLeftMenuState(false);

//        initStatusBar(R.color.toolbarColor1);

        // 要给帧布局填充布局对象
        TextView view = new TextView(mMainActivity);
        view.setText("1");
        view.setTextColor(Color.RED);
        view.setTextSize(22);
        view.setGravity(Gravity.CENTER);

        fl_pager_contain.addView(view);
    }


}
