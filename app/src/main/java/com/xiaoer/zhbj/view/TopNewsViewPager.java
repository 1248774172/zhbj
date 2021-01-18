package com.xiaoer.zhbj.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import java.util.Objects;

public class TopNewsViewPager extends ViewPager {

    private float mStartX = -1;
    private float mStartY = -1;

    public TopNewsViewPager(@NonNull Context context) {
        super(context, null);
    }

    public TopNewsViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = ev.getX();
                mStartY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:

                float transX = ev.getX() - mStartX;
                float transY = ev.getY() - mStartY;

                if (Math.abs(transY) > Math.abs(transX)) {
                    //上下滑动  不拦截
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else {
                    int currentItem = getCurrentItem();
                    if (transX > 0) {
                        //向右滑
                        if (currentItem == 0) {
                            //当前在第一页 不拦截
                            getParent().requestDisallowInterceptTouchEvent(false);
                        } else {
                            getParent().requestDisallowInterceptTouchEvent(true);
                        }
                    }else {
                        int count = Objects.requireNonNull(getAdapter()).getCount();
                        //向左滑
                        if (currentItem == count - 1) {
                            //当前在最后一页 不拦截
                            getParent().requestDisallowInterceptTouchEvent(false);
                        } else {
                            getParent().requestDisallowInterceptTouchEvent(true);
                        }
                    }
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
