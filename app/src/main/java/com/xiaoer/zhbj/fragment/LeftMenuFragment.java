package com.xiaoer.zhbj.fragment;

import android.annotation.SuppressLint;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.drawerlayout.widget.DrawerLayout;

import com.xiaoer.zhbj.MainActivity;
import com.xiaoer.zhbj.R;
import com.xiaoer.zhbj.entity.NewsMenu;
import com.xiaoer.zhbj.pager.impl.NewsPager;

import java.util.ArrayList;

public class LeftMenuFragment extends BaseFragment {

    private ListView lv_leftMenu;
    private ArrayList<NewsMenu.NewsMenuData> data;
    //默认当前页为第一个
    private int currentPage = 0;

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_leftmenu, null);
        lv_leftMenu = view.findViewById(R.id.lv_leftMenu);
        return view;
    }

    @Override
    public void initData() {

    }

    /**
     * @param data 数据
     *             给侧边栏设置具体数据 填充listView
     */
    public void setLv_leftMenu(ArrayList<NewsMenu.NewsMenuData> data){
        this.data = data;
        //填充listView
        final MyAdapter myAdapter = new MyAdapter();
        lv_leftMenu.setAdapter(myAdapter);

        currentPage = 0;

        lv_leftMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("RtlHardcoded")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentPage = position;
                myAdapter.notifyDataSetChanged();
                //设置详情页
                setCurrentPageDetail(position);
                //收起侧边栏
                DrawerLayout drawer_layout = mActivity.findViewById(R.id.drawer_layout);
                drawer_layout.closeDrawer(Gravity.LEFT);
            }
        });
    }

    /**
     * @param position 位置
     *                 设置当前详情页
     */
    private void setCurrentPageDetail(int position) {
        MainActivity mainActivity = (MainActivity) mActivity;
        NewsPager newsPager = mainActivity.getContentFragment().getNewsPager();
        newsPager.setDetailPager(position);
    }

    public class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public NewsMenu.NewsMenuData getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView == null) {
                convertView = View.inflate(getContext(), R.layout.list_item_left_menu, null);
                viewHolder  = new ViewHolder();
                viewHolder.tv_menu = convertView.findViewById(R.id.tv_menu);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.tv_menu.setText(getItem(position).title);
            if(currentPage == position) {
                viewHolder.tv_menu.setEnabled(true);
            }else {
                viewHolder.tv_menu.setEnabled(false);
            }

            return convertView;
        }
    }

    public static class ViewHolder{
        public TextView tv_menu;
    }
}
