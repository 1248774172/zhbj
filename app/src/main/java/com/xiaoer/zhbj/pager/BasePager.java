package com.xiaoer.zhbj.pager;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.xiaoer.zhbj.MainActivity;
import com.xiaoer.zhbj.R;
import com.zackratos.ultimatebarx.library.UltimateBarX;
import com.zackratos.ultimatebarx.library.bean.BarConfig;

public class BasePager {
    //当前页的viewpager
    public FrameLayout fl_pager_contain;
    //当前页的根view
    public View mRootView;
    public MainActivity mMainActivity;
    //当前页的标题栏
    public Toolbar tb_main;
    private TextView tv_title;
    public DrawerLayout drawer_layout;
    public ActionBarDrawerToggle mActionBarDrawerToggle;
    private NavigationView nv_left;

    private Fragment mFragment;

    public BasePager(Activity activity, Fragment fragment) {
        mMainActivity = (MainActivity) activity;
        mFragment = fragment;
        mRootView = initView();
    }

    public View initView() {
        View view = View.inflate(mMainActivity, R.layout.pager_base, null);
        fl_pager_contain = view.findViewById(R.id.fl_pager_contain);

        //获取toolbar、title及其layout父布局
        tb_main = view.findViewById(R.id.tb_main);
//        AppBarLayout abl_contain = view.findViewById(R.id.abl_contain);
        tv_title = tb_main.findViewById(R.id.tv_title);
        drawer_layout = mMainActivity.findViewById(R.id.drawer_layout);
        //获取navigationView
        nv_left = mMainActivity.findViewById(R.id.nv_left);
        //设置toolbar
        mMainActivity.setSupportActionBar(tb_main);

        //绑定toolbar和侧边栏
        mActionBarDrawerToggle = new ActionBarDrawerToggle(mMainActivity,
                drawer_layout, tb_main, R.string.open_left_menu, R.string.close_left_menu);

        UltimateBarX.addStatusBarTopPadding(tb_main);
//        UltimateBarX.addStatusBarTopPadding(nv_left);


        return view;
    }

    public void initData() {
    }

    /**
     * 设置状态栏颜色
     */
    public void initStatusBar(final int toolbarColor) {

        //侧边栏打开时 更新侧边栏颜色和当前页面的toolbar颜色一致
        final Fragment fragmentById = mMainActivity.getLeftMenuFragment();
        DrawerLayout drawerLayout = mMainActivity.findViewById(R.id.drawer_layout);
        setNavigationColor(toolbarColor, fragmentById);
        //设置侧边栏的打开时状态栏的具体状态
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                UltimateBarX.with(fragmentById)
                        .fitWindow(true)
                        .light(true)
                        .applyStatusBar();
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });

        //设置toolbar的颜色
        setToolBarColor(toolbarColor);

        BarConfig barConfig = new BarConfig()
                .fitWindow(false)
                .color(toolbarColor)
                .transparent()
                .light(true);
        //布局是否侵入状态栏（true 不侵入，false 侵入）
        // 状态栏背景颜色
        // light模式
        // 状态栏字体 true: 灰色，false: 白色 Android 6.0+
        // 导航栏按钮 true: 灰色，false: 白色 Android 8.0+
        UltimateBarX.with(mFragment)
                .config(barConfig)
                .applyStatusBar();
    }

    /**
     * @param toolbarColor 颜色int
     *                     设置侧边栏颜色
     */
    private void setNavigationColor(int toolbarColor, Fragment fragment) {

        assert fragment != null;
        UltimateBarX.with(fragment)
                .color(toolbarColor)
                .applyStatusBar();
        nv_left.setBackgroundColor(toolbarColor);
    }

    /**
     * @param toolbarColor 颜色 int
     *                     设置toolbar的颜色
     */
    public void setToolBarColor(int toolbarColor) {
        tb_main.setBackgroundColor(toolbarColor);
    }

    /**
     * @param title 标题
     *              给每个页面设置对应的标题
     */
    public void setToolBarTitle(String title) {
        if (title != null) {
            tb_main.setTitle("");
            tv_title.setText(title);
        }
    }

    /**
     * @param state 侧边栏是否能打开
     *              设置每个页面是否启用侧边栏
     */
    public void setLeftMenuState(boolean state) {
        if (state) {
            //启用侧边栏
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            //给toolbar添加打开关闭侧边栏的按钮
            mActionBarDrawerToggle.setDrawerIndicatorEnabled(true);

            if (mActionBarDrawerToggle != null)
                drawer_layout.addDrawerListener(mActionBarDrawerToggle);

        } else {
            //禁用侧边栏
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            //给toolbar移除打开关闭侧边栏的按钮
            mActionBarDrawerToggle.setDrawerIndicatorEnabled(false);

            if (mActionBarDrawerToggle != null)
                drawer_layout.removeDrawerListener(mActionBarDrawerToggle);
        }

        assert mActionBarDrawerToggle != null;
        mActionBarDrawerToggle.syncState();

    }

}
