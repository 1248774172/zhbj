package com.xiaoer.zhbj.entity;

public class ToolBarConfig {

        public String name;
        public String backgroundColor;
        public boolean canOpenLeftMenu;

    @Override
    public String toString() {
        return "ToolBarConfig{" +
                "name='" + name + '\'' +
                ", backgroundColor='" + backgroundColor + '\'' +
                ", canOpenLeftMenu=" + canOpenLeftMenu +
                '}';
    }
}
