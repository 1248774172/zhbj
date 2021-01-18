package com.xiaoer.zhbj.menu.impl;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.xiaoer.zhbj.MainActivity;
import com.xiaoer.zhbj.R;
import com.xiaoer.zhbj.entity.NewsMenu;
import com.xiaoer.zhbj.menu.BaseMenuDetailPager;
import com.xiaoer.zhbj.pager.impl.NewsDetailTabPager;
import com.xiaoer.zhbj.pager.impl.NewsPager;

import java.util.ArrayList;

/**
 * 菜单新闻详情页
 */
public class NewsDetailPager extends BaseMenuDetailPager {

    private ViewPager tab_view_pager;
    //页签数据
    private ArrayList<NewsMenu.NewsTabData> mNewsTabData;
    //页签的pager
    private ArrayList<NewsDetailTabPager> mNewsDetailTabPagers;
    private TabLayout tab_layout;

    public NewsDetailPager(Activity activity, ArrayList<NewsMenu.NewsTabData> newsMenu) {
        super(activity);
        mNewsTabData = newsMenu;
    }

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.pager_news_menu_detail, null);
        tab_layout = view.findViewById(R.id.tab_layout);
        tab_view_pager = view.findViewById(R.id.tab_view_pager);
        return view;
    }

    @Override
    public void initData() {
        mNewsDetailTabPagers = new ArrayList<>();
        for(int i = 0; i < mNewsTabData.size();i++){
            //添加页签
            tab_layout.addTab(tab_layout.newTab().setText(mNewsTabData.get(i).title));
            //新建页签对象
            mNewsDetailTabPagers.add(new NewsDetailTabPager(mActivity,mNewsTabData.get(i)));
        }

        tab_view_pager.setAdapter(new MyAdapter());
        //初始化第一个页签
        mNewsDetailTabPagers.get(0).initData();

        tab_view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                for(int i = 0; i<mNewsDetailTabPagers.size();i++){
                    if (i == position){
                        //选中哪个页签 调用哪个页签的initData方法 优化
                        mNewsDetailTabPagers.get(position).initData();
                    }else {
                        mNewsDetailTabPagers.get(position).onDestroy();
                    }
                }

                //当不是第一个页签的时候 禁用侧边栏的滑动 只能靠按钮打开
                MainActivity mainActivity = (MainActivity) mActivity;
                NewsPager newsPager = mainActivity.getContentFragment().getNewsPager();
                final DrawerLayout drawerLayout = newsPager.drawer_layout;
                if(position != 0){
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    newsPager.tb_main.setNavigationOnClickListener(new View.OnClickListener() {
                        @SuppressLint("RtlHardcoded")
                        @Override
                        public void onClick(View v) {
                            drawerLayout.openDrawer(Gravity.LEFT);
                        }
                    });
                }else {
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        //将tabLayout和viewPager联动
        tab_layout.setupWithViewPager(tab_view_pager,false);
    }

    @Override
    public void onDestroy() {

    }

    public class MyAdapter extends PagerAdapter{

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mNewsTabData.get(position).title;
        }

        @Override
        public int getCount() {
            return mNewsTabData.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            NewsDetailTabPager newsDetailTabPager = mNewsDetailTabPagers.get(position);
            View view = newsDetailTabPager.mRootView;
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View)object);
        }
    }
}
