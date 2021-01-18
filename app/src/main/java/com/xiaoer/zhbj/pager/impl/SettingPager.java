package com.xiaoer.zhbj.pager.impl;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.xiaoer.zhbj.pager.BasePager;

public class SettingPager extends BasePager {

    public SettingPager(Activity activity, Fragment fragment) {
        super(activity, fragment);
    }

    @Override
    public void initData() {
//        setToolBarTitle("设置");
//        setLeftMenuState(false);

//        initStatusBar(R.color.toolbarColor4);


        // 要给帧布局填充布局对象
        TextView view = new TextView(mMainActivity);
        view.setText("5");
        view.setTextColor(Color.RED);
        view.setTextSize(22);
        view.setGravity(Gravity.CENTER);

        fl_pager_contain.addView(view);
    }
}
