package com.xiaoer.zhbj.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaoer.zhbj.R;
import com.xiaoer.zhbj.entity.GlobalPath;
import com.xiaoer.zhbj.entity.ToolBarConfig;
import com.xiaoer.zhbj.pager.BasePager;
import com.xiaoer.zhbj.pager.impl.GovernmentPager;
import com.xiaoer.zhbj.pager.impl.HomePager;
import com.xiaoer.zhbj.pager.impl.NewsPager;
import com.xiaoer.zhbj.pager.impl.SettingPager;
import com.xiaoer.zhbj.pager.impl.SmartServicePager;
import com.xiaoer.zhbj.view.NoScrollViewPager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ContentFragment extends BaseFragment {

    //底部页签
    private BottomNavigationView bnv_bottom;
    //页签详情页
    private ArrayList<BasePager> mBasePagers;
    //侧边栏
    private NoScrollViewPager nvp_contain;
    private ArrayList<ToolBarConfig> mToolBarConfigArrayList;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if(mMyAdapter == null)
                mMyAdapter = new MyAdapter();

            //手动初始化主页面的数据
            initPage(0);

            nvp_contain.setAdapter(mMyAdapter);
        }
    };
    private MyAdapter mMyAdapter;
    //页签详细内容
    public FrameLayout fl_pager_contain;

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_content, null);

        bnv_bottom = view.findViewById(R.id.bnv_bottom);
        nvp_contain = view.findViewById(R.id.nvp_contain);
        fl_pager_contain = view.findViewById(R.id.fl_pager_contain);

        //设置底部BottomNavigationView
        bnv_bottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                refreshItemIcon();
                int i = 0;
                switch (menuItem.getItemId()) {
                    case R.id.bt_home:
                        menuItem.setIcon(R.drawable.home_press);
                        i = 0;
                        break;
                    case R.id.bt_news:
                        menuItem.setIcon(R.drawable.newscenter_press);
                        i = 1;
                        break;
                    case R.id.bt_service:
                        menuItem.setIcon(R.drawable.smartservice_press);
                        i = 2;
                        break;
                    case R.id.bt_gov:
                        menuItem.setIcon(R.drawable.govaffairs_press);
                        i = 3;
                        break;
                    case R.id.bt_set:
                        menuItem.setIcon(R.drawable.setting_press);
                        i = 4;
                        break;
                }
                nvp_contain.setCurrentItem(i,false);
                return true;
            }
        });
        bnv_bottom.setItemIconTintList(null);
        return view;
    }

    @Override
    public void initData() {

        //获取每个page的配置信息
        getToolBarConfigFromFile();

        mBasePagers = new ArrayList<>();
        mBasePagers.add(new HomePager(mActivity,this));
        mBasePagers.add(new NewsPager(mActivity,this));
        mBasePagers.add(new SmartServicePager(mActivity,this));
        mBasePagers.add(new GovernmentPager(mActivity,this));
        mBasePagers.add(new SettingPager(mActivity,this));


        //当某个页面被展示的时候再调用此页面的initData方法 防止viewpager预加载左右页面造成卡顿
        nvp_contain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                initPage(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



    }

    /**
     * @param position 初始化每个page
     */
    private void initPage(int position) {
        BasePager basePager = mBasePagers.get(position);
        ToolBarConfig toolBarConfig = mToolBarConfigArrayList.get(position);
        //初始化page数据
        basePager.initData();
        //设置page中的toolbar颜色
        String backgroundColor = toolBarConfig.backgroundColor;
        int color = Color.parseColor(backgroundColor);
        basePager.initStatusBar(color);
        //设置page中的toolbar标题
        basePager.setToolBarTitle(toolBarConfig.name);
        //设置page中侧边栏能否使用
        basePager.setLeftMenuState(toolBarConfig.canOpenLeftMenu);


    }

    /**
     * 从文件中获取每个page的配置信息
     */
    private void getToolBarConfigFromFile() {
        File file = new File(GlobalPath.assetsPath,"ToolBarConfig.json");
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader(file);
            bufferedReader =  new BufferedReader(fileReader);
            String s;
            StringBuilder stringBuilder = new StringBuilder();
            while ((s = bufferedReader.readLine()) != null){
                stringBuilder.append(s);
            }
            String result = stringBuilder.toString().trim();
            processDara(result);

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
                try {
                    if(fileReader!=null)
                        fileReader.close();
                    if(bufferedReader!=null)
                        bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

    }

    /**
     * @param json 解析json数据
     */
    private void processDara(String json) {
        Gson gson = new Gson();
        mToolBarConfigArrayList = gson.fromJson(json,
                new TypeToken<ArrayList<ToolBarConfig>>(){}.getType());
        mHandler.sendEmptyMessage(0);
    }

    public class MyAdapter extends PagerAdapter{
        @Override
        public int getCount() {
            return mBasePagers.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            BasePager basePager = mBasePagers.get(position);
            View view = basePager.mRootView;
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

    /**
     * 未选中时加载默认的图片
     */
    public void refreshItemIcon() {
        MenuItem item_home = bnv_bottom.getMenu().findItem(R.id.bt_home);
        item_home.setIcon(R.drawable.home);
        MenuItem item_news = bnv_bottom.getMenu().findItem(R.id.bt_news);
        item_news.setIcon(R.drawable.newscenter);
        MenuItem item_set = bnv_bottom.getMenu().findItem(R.id.bt_set);
        item_set.setIcon(R.drawable.setting);
        MenuItem item_gov = bnv_bottom.getMenu().findItem(R.id.bt_gov);
        item_gov.setIcon(R.drawable.govaffairs);
        MenuItem item_service = bnv_bottom.getMenu().findItem(R.id.bt_service);
        item_service.setIcon(R.drawable.smartservice);
    }

    /**
     * @return 获取新闻中心pager
     */
    public NewsPager getNewsPager(){
        return (NewsPager) mBasePagers.get(1);
    }

}
