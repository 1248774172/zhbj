package com.xiaoer.zhbj.entity;

import java.util.ArrayList;

/**
 * 新闻页网页数据
 */
public class NewsMenu {

    public int retcode;
    public ArrayList<NewsMenuData> data;
    public ArrayList<Integer> extend;

    public class NewsMenuData {
        public int id;
        public String title;
        public int type;

        public ArrayList<NewsTabData> children;

        @Override
        public String toString() {
            return "NewsMenuData{" +
                    "id=" + id +
                    ", title='" + title + '\'' +
                    ", type=" + type +
                    ", children=" + children +
                    '}';
        }
    }

    // 页签的对象
    public static class NewsTabData {
        public int id;
        public String title;
        public int type;
        public String url;

        @Override
        public String toString() {
            return "NewsTabData{" +
                    "id=" + id +
                    ", title='" + title + '\'' +
                    ", type=" + type +
                    ", url='" + url + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "NewsMenu{" +
                "retcode=" + retcode +
                ", data=" + data +
                ", extend=" + extend +
                '}';
    }
}
