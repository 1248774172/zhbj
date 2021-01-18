package com.xiaoer.zhbj.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoer.zhbj.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PullToRefreshListView extends ListView {
    private static final int PULL_TO_FLASH = 100;
    private static final int FLASHING = 101;
    private static final int RELASE_TO_FLASH = 102;
    //下拉刷新的当前状态
    private int currentState = PULL_TO_FLASH;
    //上拉加载更多的当前状态
    private boolean isLoadingMore = false;

    private TextView tv_time;
    private ImageView iv_arrow;
    private ProgressBar pb_loading;
    private TextView tv_title;

    private int mHeaderMeasuredHeight;
    private View mHeaderView;

    float startX = -1;
    float startY = -1;
    private RotateAnimation mRotateAnimationUp;
    private RotateAnimation mRotateAnimationDown;
    private OnRefreshListener mOnRefreshListener;
    private View mFooterView;
    private int mFooterVIewHeight;

    public PullToRefreshListView(Context context) {
        super(context);
        init();
    }

    public PullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PullToRefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //初始化头部局
        initHeaderView();
        //初始化脚布局
        initFooterView();
        //初始化箭头动画
        initArrowAnimation();
    }

    /**
     * 初始化脚布局
     */
    private void initFooterView() {
        mFooterView = View.inflate(getContext(), R.layout.pull_to_refresh_footer, null);
        mFooterView.measure(0, 0);
        mFooterVIewHeight = mFooterView.getMeasuredHeight();
        mFooterView.setPadding(0, -mFooterVIewHeight, 0, 0);
        this.addFooterView(mFooterView);
        //下拉加载更多
        this.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //如果滑到了最后一个
                if (getLastVisiblePosition() == getCount() - 1) {
                    //展示脚布局
                    mFooterView.setPadding(0, 0, 0, 0);
                    //设置当前显示的条目为脚布局
                    setSelection(getCount() - 1);
                    //加载更多数据 isLoadingMore避免多次请求加载
                    if (mOnRefreshListener != null && !isLoadingMore) {
                        isLoadingMore = true;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                boolean moreData = mOnRefreshListener.getMoreData();
                                if(!moreData)
                                    Toast.makeText(getContext(),"没有更多数据了",Toast.LENGTH_SHORT).show();
                                //加载完隐藏头部局
                                mFooterView.setPadding(0, -mFooterVIewHeight, 0, 0);
                                isLoadingMore = false;
                            }
                        }, 1000);
                    }
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }

    /**
     * 初始化头部局
     */
    private void initHeaderView() {
        //初始化下拉刷新头部局
        mHeaderView = View.inflate(getContext(), R.layout.pull_to_refresh_header, null);
        tv_time = mHeaderView.findViewById(R.id.tv_time);
        iv_arrow = mHeaderView.findViewById(R.id.iv_arrow);
        pb_loading = mHeaderView.findViewById(R.id.pb_loading);
        tv_title = mHeaderView.findViewById(R.id.tv_title);
        //隐藏头部局
        mHeaderView.measure(0, 0);
        mHeaderMeasuredHeight = mHeaderView.getMeasuredHeight();
        mHeaderView.setPadding(0, -mHeaderMeasuredHeight, 0, 0);
        //给listView添加头部局
        addHeaderView(mHeaderView);
    }

    /**
     * 初始化箭头动画
     */
    public void initArrowAnimation() {
        //向下旋转
        mRotateAnimationUp = new RotateAnimation(
                0, 180,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        mRotateAnimationUp.setDuration(200);
        mRotateAnimationUp.setFillAfter(true);
        //向上旋转
        mRotateAnimationDown = new RotateAnimation(
                180, 0,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        mRotateAnimationDown.setDuration(200);
        mRotateAnimationDown.setFillAfter(true);
    }

    //重写onTouchEvent 实现下拉刷新 上拉加载更多
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = ev.getX();
                startY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                // 当用户按住头条新闻的viewpager进行下拉时,ACTION_DOWN会被viewpager消费掉,
                // 导致startY没有赋值,此处需要重新获取一下
                if (startX == -1 || startY == -1) {
                    startX = ev.getX();
                    startY = ev.getY();
                }
                int dX = (int) (ev.getX() - startX);
                int dY = (int) (ev.getY() - startY);

                //左右滑动不用处理
                if (Math.abs(dX) > Math.abs(dY))
                    return super.onTouchEvent(ev);
                //如果正在刷新时下拉 无动作
                if (currentState == FLASHING)
                    return super.onTouchEvent(ev);

                if (dY > 0) {
                    //下拉
                    int firstVisiblePosition = getFirstVisiblePosition();
                    if (firstVisiblePosition == 0) {
                        //下拉刷新

                        dY = (int) (Math.exp(-ev.getY() / startY / 40) * dY);
                        int paddingTop = dY - mHeaderMeasuredHeight;
                        mHeaderView.setPadding(0, paddingTop, 0, 0);
                        //如果下拉高度大于头部局高度 则开始刷新
                        if (paddingTop > 0 && currentState != RELASE_TO_FLASH) {
                            currentState = RELASE_TO_FLASH;
                            refreshState();

                        } else if (paddingTop < 0 && currentState != PULL_TO_FLASH) {
                            currentState = PULL_TO_FLASH;
                            refreshState();
                        }
                    }

                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                startX = -1;
                startY = -1;
                if (currentState == RELASE_TO_FLASH) {
                    //抬手时应该刷新  将当前状态置为刷新中 刷新数据
                    currentState = FLASHING;
                    //完全显示头部局
                    mHeaderView.setPadding(0, 0, 0, 0);
                    refreshState();

                    if (mOnRefreshListener != null) {
                        final boolean success = mOnRefreshListener.refreshListView();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (success) {
                                    //刷新成功
                                    Toast.makeText(getContext(), "刷新成功", Toast.LENGTH_SHORT).show();
                                    //更新刷新时间
                                    Date date = new Date();
                                    @SuppressLint("SimpleDateFormat")
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    String format = simpleDateFormat.format(date);
                                    tv_time.setText(format);
                                } else {
                                    //刷新失败
                                    Toast.makeText(getContext(), "服务器繁忙请稍候再试", Toast.LENGTH_SHORT).show();
                                }
                                //收起头部局
                                hideHeaderView();
                                //更改当前状态
                                currentState = PULL_TO_FLASH;
                                refreshState();
                            }
                        }, 1000);
                    }
                } else if (currentState == PULL_TO_FLASH) {
                    //抬手时不用刷新
                    hideHeaderView();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 根据当前状态更新头部局
     */
    private void refreshState() {
        iv_arrow.clearAnimation();
        switch (currentState) {
            case PULL_TO_FLASH:
                tv_title.setText("下拉刷新");
                pb_loading.setVisibility(INVISIBLE);
                iv_arrow.setVisibility(VISIBLE);
                iv_arrow.startAnimation(mRotateAnimationDown);
                break;
            case FLASHING:
                tv_title.setText("刷新中...");
//                iv_arrow.clearAnimation();
                pb_loading.setVisibility(VISIBLE);
                iv_arrow.setVisibility(INVISIBLE);
                break;
            case RELASE_TO_FLASH:
                tv_title.setText("松开刷新");
                pb_loading.setVisibility(INVISIBLE);
                iv_arrow.setVisibility(VISIBLE);
                iv_arrow.startAnimation(mRotateAnimationUp);
                break;
        }
    }

    /**
     * 隐藏头部局
     */
    private void hideHeaderView() {
        mHeaderView.setPadding(0, -mHeaderMeasuredHeight, 0, 0);
    }

    /**
     * @param onRefreshListener 监听接口
     *                          设置监听接口 实现加载更多与刷新
     */
    public void setRefreshListener(OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }

    public interface OnRefreshListener {
        //刷新listView的回调
        boolean refreshListView();
        //加载更多
        boolean getMoreData();
    }

}

