package com.xiaoer.zhbj.entity;

import java.util.ArrayList;

public class TabDetailBean {
    public NewsTab data;

    public class NewsTab {
        public String more;
        public ArrayList<NewsData> news;
        public ArrayList<TopNews> topnews;
    }

    // 新闻列表对象
    public static class NewsData {
        public int id;
        public String listimage;
        public String pubdate;
        public String title;
        public String type;
        public String url;
    }

    // 头条新闻
    public static class TopNews {
        public int id;
        public String topimage;
        public String pubdate;
        public String title;
        public String type;
        public String url;
    }
}
