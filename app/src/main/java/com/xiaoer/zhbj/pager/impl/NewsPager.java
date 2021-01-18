package com.xiaoer.zhbj.pager.impl;

import android.app.Activity;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.xiaoer.zhbj.entity.GlobalPath;
import com.xiaoer.zhbj.entity.NewsMenu;
import com.xiaoer.zhbj.fragment.LeftMenuFragment;
import com.xiaoer.zhbj.menu.BaseMenuDetailPager;
import com.xiaoer.zhbj.menu.impl.InteractMenuDetailPager;
import com.xiaoer.zhbj.menu.impl.NewsDetailPager;
import com.xiaoer.zhbj.menu.impl.PhotosDetailPager;
import com.xiaoer.zhbj.menu.impl.TopicMenuDetailPager;
import com.xiaoer.zhbj.pager.BasePager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 新闻中心
 */
public class NewsPager extends BasePager {

    private ArrayList<BaseMenuDetailPager> mBaseMenuDetailPagers;
    public NewsPager(Activity activity, Fragment fragment) {
        super(activity, fragment);
    }

    @Override
    public void initData() {
//        getDataFromService();
        getDataFromFile();

    }

    /**
     * 设置新闻中心具体显示那个页面
     */
    public void setDetailPager(int position){
        //将新闻中心的frameLayout填充要展示的具体页面
        BaseMenuDetailPager baseMenuDetailPager = mBaseMenuDetailPagers.get(position);
        fl_pager_contain.removeAllViews();
        fl_pager_contain.addView(baseMenuDetailPager.mRootView);

        baseMenuDetailPager.initData();
    }

    /**
     * 从本地文件获取数据
     */
    private void getDataFromFile() {
        FileReader fileReader = null;
        StringBuilder stringBuilder;
        BufferedReader bufferedReader = null;
        try {
            File file = new File(GlobalPath.assetsPath, "categories.json");
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            String s;
            stringBuilder = new StringBuilder();
            while ((s = bufferedReader.readLine()) != null) {
                stringBuilder.append(s);
            }
            processData(stringBuilder.toString().trim());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null)
                    bufferedReader.close();
                if (fileReader != null)
                    fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * @param string 解析json数据
     */
    private void processData(String string) {
        Gson gson = new Gson();
        NewsMenu newsMenu = gson.fromJson(string, NewsMenu.class);
//        Log.e(TAG, "processData: "+newsMenu );

        //给侧边栏设置数据
        LeftMenuFragment leftMenuFragment = mMainActivity.getLeftMenuFragment();
        leftMenuFragment.setLv_leftMenu(newsMenu.data);

        //初始化4个详情页
        mBaseMenuDetailPagers = new ArrayList<>();
        mBaseMenuDetailPagers.add(new NewsDetailPager(mMainActivity,newsMenu.data.get(0).children));
        mBaseMenuDetailPagers.add(new TopicMenuDetailPager(mMainActivity));
        mBaseMenuDetailPagers.add(new PhotosDetailPager(mMainActivity));
        mBaseMenuDetailPagers.add(new InteractMenuDetailPager(mMainActivity));
        //默认显示第一个页面
        fl_pager_contain.removeAllViews();
        fl_pager_contain.addView(mBaseMenuDetailPagers.get(0).mRootView);
        mBaseMenuDetailPagers.get(0).initData();
    }

    //    private void getDataFromService() {
//        OkHttpClient okHttpClient = new OkHttpClient();
//        Request build = new Request.Builder()
//                .get()
//                .url("")
//                .build();
//        Call call = okHttpClient.newCall(build);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                assert response.body() != null;
//                processData(response.body().string());
//            }
//        });
//    }
}
