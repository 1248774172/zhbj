package com.xiaoer.zhbj.pager.impl;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.tamsiree.rxkit.RxWebViewTool;
import com.tamsiree.rxui.activity.ActivityWebView;
import com.xiaoer.zhbj.R;
import com.xiaoer.zhbj.ShowNewsActivity;
import com.xiaoer.zhbj.Utils.SpKey;
import com.xiaoer.zhbj.Utils.SpUtil;
import com.xiaoer.zhbj.entity.GlobalPath;
import com.xiaoer.zhbj.entity.NewsMenu;
import com.xiaoer.zhbj.entity.TabDetailBean;
import com.xiaoer.zhbj.menu.BaseMenuDetailPager;
import com.xiaoer.zhbj.view.PullToRefreshListView;
import com.xiaoer.zhbj.view.TopNewsViewPager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import me.relex.circleindicator.CircleIndicator;

import static android.content.ContentValues.TAG;

/**
 * 菜单新闻详情页-10多个tab
 */
public class NewsDetailTabPager extends BaseMenuDetailPager {

    private static final int DATA_READY = 100;
    private static final int AUTO_NEXT_PAGE = 101;
    public NewsMenu.NewsTabData mNewsTabData;
    public PullToRefreshListView lv_list;
    //列表展示新闻
    private ArrayList<TabDetailBean.NewsData> mListNews;
    //头条新闻
    private ArrayList<TabDetailBean.TopNews> mTopNews;

    //轮播新闻
    private TopNewsViewPager vp_top_news;
    //轮播新闻圆点指示器
    private CircleIndicator mCircleIndicator;
    //轮播新闻标题
    private TextView tv_title;

    private Handler mAutoNextPageHandler;
    //每个页签
    private TabDetailBean mTabDetailBean;

    private ListNewsAdapter mListNewsAdapter;
    private TopNewsAdapter mTopNewsAdapter;

    //数据准备完成处理消息
    private Handler mHandler = new Handler(new Handler.Callback() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == DATA_READY) {//数据准备完毕 收到消息 设置数据适配器
                mListNewsAdapter = new ListNewsAdapter();
                lv_list.setAdapter(mListNewsAdapter);
                mTopNewsAdapter = new TopNewsAdapter();
                vp_top_news.setAdapter(mTopNewsAdapter);
                //头条新闻轮播图设置监听  更新新闻title
                vp_top_news.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                        //更新轮播新闻标题
                        tv_title.setText(mTopNews.get(position).title);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });
                // 更新第一个头条新闻标题
                tv_title.setText(mTopNews.get(0).title);
                //绑定原点指示器
                mCircleIndicator.setViewPager(vp_top_news);
                //开始轮播
                //避免重复发消息
                if (mAutoNextPageHandler == null) {
                    mAutoNextPageHandler = new Handler(new Handler.Callback() {
                        //自动轮播
                        @Override
                        public boolean handleMessage(@NonNull Message msg) {
//                            Log.e(TAG, "handleMessage: 收到消息开始轮播"+mNewsTabData.title);
                            //TODO handler内存溢出
                            if (msg.what == AUTO_NEXT_PAGE) {
                                int currentItem = vp_top_news.getCurrentItem();
                                if (currentItem == Objects.requireNonNull(vp_top_news.getAdapter()).getCount() - 1) {
                                    currentItem = 0;
                                } else {
                                    currentItem++;
                                }
                                vp_top_news.setCurrentItem(currentItem);
                                //重新发送消息 进行轮播
                                mAutoNextPageHandler.sendEmptyMessageDelayed(AUTO_NEXT_PAGE, 3000);

                                //点击viewpager的时候停止轮播
                                vp_top_news.setOnTouchListener(new View.OnTouchListener() {
                                    @Override
                                    public boolean onTouch(View v, MotionEvent event) {
                                        switch (event.getAction()) {
                                            //按下和滑动都停止轮播
                                            case MotionEvent.ACTION_DOWN:
                                            case MotionEvent.ACTION_MOVE:
                                                mAutoNextPageHandler.removeCallbacksAndMessages(null);
                                                break;
                                            //抬起和意外终止都继续轮播
                                            case MotionEvent.ACTION_UP:
                                            case MotionEvent.ACTION_CANCEL:
                                                mAutoNextPageHandler.sendEmptyMessageDelayed(AUTO_NEXT_PAGE, 3000);
                                                break;
                                        }
                                        return false;
                                    }
                                });
                            }
                            return true;
                        }
                    });
                    mAutoNextPageHandler.removeCallbacksAndMessages(null);
                    mAutoNextPageHandler.sendEmptyMessageDelayed(AUTO_NEXT_PAGE, 3000);
                }
                //下拉刷新
                lv_list.setRefreshListener(new PullToRefreshListView.OnRefreshListener() {
                    @Override
                    public boolean refreshListView() {
                        return getFirstData();
                    }

                    @Override
                    public boolean getMoreData() {
                        return getMoreDataFromFile();
                    }
                });

                //点击过的新闻置为灰色
                lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        position = position - lv_list.getHeaderViewsCount();
                        //点击过的直接设置为灰色
                        TextView tv_title = view.findViewById(R.id.tv_title);
                        tv_title.setTextColor(Color.GRAY);
                        //存储被点击新闻的id
                        String string = SpUtil.getString(mActivity, SpKey.READ_IDS, "");
                        StringBuilder stringBuilder = new StringBuilder(string);
                        if (!string.contains(mListNews.get(position).id + "")) {
                            //只有不包含 才追加
                            stringBuilder.append(mListNews.get(position).id).append(",");
                        }
                        SpUtil.putString(mActivity, SpKey.READ_IDS, stringBuilder.toString().trim());

                        //打开webView展示页面
                        String url = mListNews.get(position).url;
                        String path = serviceUrl2Assets(url);
                        Log.e(TAG, "onItemClick: " + path);
                        Intent intent = new Intent(mActivity, ShowNewsActivity.class);
                        intent.putExtra("url", path);
                        mActivity.startActivity(intent);
                    }
                });
            }
            return true;
        }
    });

    private String serviceUrl2Assets(String url) {
        String serviceUrl = "http://10.0.2.2:8080/zhbj";
        int i = url.indexOf(serviceUrl);
        String substring = url.substring(serviceUrl.length()-i);
        return "file:///android_asset/zhbj" + substring;
    }

    /**
     * 加载更多数据
     */
    private boolean getMoreDataFromFile() {
        if (!TextUtils.isEmpty(mTabDetailBean.data.more)) {
            String moreDataJson = getDataFromFile(mTabDetailBean.data.more);
            return processData(moreDataJson, true);
        }
        return false;
    }

    public NewsDetailTabPager(Activity activity, NewsMenu.NewsTabData newsTabData) {
        super(activity);
        mNewsTabData = newsTabData;
    }

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.pager_tab_detail, null);
        lv_list = view.findViewById(R.id.lv_list);
        //加载listView轮播图头部局
        View headView = View.inflate(mActivity, R.layout.list_item_header, null);
        vp_top_news = headView.findViewById(R.id.vp_top_news);
        mCircleIndicator = headView.findViewById(R.id.indicator);
        tv_title = headView.findViewById(R.id.tv_title);

        lv_list.addHeaderView(headView);
        return view;
    }

    @Override
    public void initData() {
        getFirstData();

    }

    /**
     * 获取第一页数据
     */
    private boolean getFirstData() {
        String dataFromFile = getDataFromFile(mNewsTabData.url);
        return processData(dataFromFile, false);
    }

    @Override
    public void onDestroy() {
        if (mAutoNextPageHandler != null) {
            mAutoNextPageHandler.removeCallbacksAndMessages(null);
            mAutoNextPageHandler = null;
        }
    }

    /**
     * 获取子标签的详细数据
     */
    private String getDataFromFile(String url) {
        File file = new File(GlobalPath.assetsPath, url);
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            String s;
            StringBuilder stringBuilder = new StringBuilder();
            while ((s = bufferedReader.readLine()) != null) {
                stringBuilder.append(s);
            }
            return stringBuilder.toString().trim();
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
        return null;
    }

    /**
     * @param trim json数据
     *             解析json
     */
    private boolean processData(String trim, boolean isMore) {
        Gson gson = new Gson();
        mTabDetailBean = gson.fromJson(trim, TabDetailBean.class);
        if (mTabDetailBean != null) {
            if (!isMore) {
                //如果不是加载更多  直接解析并创建数据
                //列表新闻数据
                mListNews = mTabDetailBean.data.news;
                //头条新闻数据
                mTopNews = mTabDetailBean.data.topnews;

                mHandler.sendEmptyMessage(DATA_READY);
            } else {
                //如果是加载更多  追加数据
                mListNews.addAll(mTabDetailBean.data.news);
                mTopNews.addAll(mTabDetailBean.data.topnews);
                if (mListNewsAdapter != null)
                    mListNewsAdapter.notifyDataSetChanged();
                if (mTopNewsAdapter != null)
                    mTopNewsAdapter.notifyDataSetChanged();
            }
            return true;
        }
        return false;
    }

    public class ListNewsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mListNews.size();
        }

        @Override
        public TabDetailBean.NewsData getItem(int position) {
            return mListNews.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(mActivity, R.layout.list_item_news, null);
                viewHolder = new ViewHolder();
                viewHolder.iv_icon = convertView.findViewById(R.id.iv_icon);
                viewHolder.tv_title = convertView.findViewById(R.id.tv_title);
                viewHolder.tv_date = convertView.findViewById(R.id.tv_date);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            TabDetailBean.NewsData item = getItem(position);
            viewHolder.tv_title.setText(item.title);
            viewHolder.tv_date.setText(item.pubdate);
            //如果看过这条新闻  置为灰色
            String string = SpUtil.getString(mActivity, SpKey.READ_IDS, "");
            if (string.contains(item.id + "")) {
//                Log.e(TAG, "getView: 看过"+item.title+item.id+",设置为灰色");
                viewHolder.tv_title.setTextColor(Color.GRAY);
            } else {
//                Log.e(TAG, "getView: 没看过"+item.title+item.id+",设置为黑色");
                //重新设置为黑色避免复用的时候文字还是灰色的
                viewHolder.tv_title.setTextColor(Color.BLACK);
            }

            String path = serviceUrl2FilePath(item.listimage);
            Glide.with(mActivity)
                    .load(path)
                    .placeholder(R.drawable.news_pic_default)
                    .into(viewHolder.iv_icon);


            return convertView;
        }

        public class ViewHolder {
            public TextView tv_title;
            public TextView tv_date;
            public ImageView iv_icon;
        }

    }

    public class TopNewsAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mTopNews.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            TabDetailBean.TopNews topNews = mTopNews.get(position);
            String path = serviceUrl2FilePath(topNews.topimage);
            //给viewpager填充一个imageView
            ImageView imageView = new ImageView(mActivity);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(mActivity)
                    .load(path)
                    .placeholder(R.drawable.news_pic_default)
                    .into(imageView);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

    /**
     * @param url 服务器地址
     * @return 本地文件地址
     * 将服务器地址转为本地文件地址
     */
    private String serviceUrl2FilePath(String url) {
        String serviceUrl = "http://10.0.2.2:8080/zhbj";
        int i = url.indexOf(serviceUrl);
        return GlobalPath.assetsPath + url.substring(serviceUrl.length() - i);
    }


}
