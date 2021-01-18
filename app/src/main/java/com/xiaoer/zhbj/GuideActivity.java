package com.xiaoer.zhbj;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.xiaoer.zhbj.Utils.SpKey;
import com.xiaoer.zhbj.Utils.SpUtil;

import java.util.ArrayList;

public class GuideActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager vp_guide;
    private LinearLayout ll_dots;
    private ImageView iv_redPoint;
    /**
     * 立即体验
     */
    private Button bt_start;
    private int [] mGuideImagesId = new int []{R.drawable.guide1,R.drawable.guide2,R.drawable.guide3};
    private ArrayList<ImageView> mImageViews;
    private int mPointDis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_guide);

        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        initView();
        initData();

//        AlertDialog.Builder builder = new AlertDialog.Builder(this);

//        builder.setView(inflate);
//        final AlertDialog alertDialog = builder.create();


    }

    private void initView() {
        vp_guide = findViewById(R.id.vp_guide);
        ll_dots = findViewById(R.id.ll_dots);
        bt_start = findViewById(R.id.bt_start);
        iv_redPoint = findViewById(R.id.iv_redPoint);
        bt_start.setOnClickListener(this);

        bt_start.setVisibility(View.INVISIBLE);

        iv_redPoint.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                iv_redPoint.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mPointDis = ll_dots.getChildAt(1).getLeft() - ll_dots.getChildAt(0).getLeft();
            }
        });


                vp_guide.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // 更新小红点距离
                int leftMargin = (int) (mPointDis * positionOffset) + position
                        * mPointDis;// 计算小红点当前的左边距
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv_redPoint
                        .getLayoutParams();
                params.leftMargin = leftMargin;// 修改左边距

                // 重新设置布局参数
                iv_redPoint.setLayoutParams(params);

            }

            @Override
            public void onPageSelected(int position) {
                if(position == mImageViews.size()-1){
                    bt_start.setVisibility(View.VISIBLE);
                }else {
                    bt_start.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    private void initData() {
        mImageViews = new ArrayList<>();
        for(int i = 0; i< mGuideImagesId.length; i++) {
            ImageView view = new ImageView(this);
            view.setBackgroundResource(mGuideImagesId[i]);
            mImageViews.add(view);

            ImageView point = new ImageView(this);
            point.setBackgroundResource(R.drawable.shape_point_gray);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            if(i>0)
                layoutParams.leftMargin = 20;
            point.setLayoutParams(layoutParams);
            ll_dots.addView(point);
        }

        vp_guide.setAdapter(new myAdapter());
    }

    class myAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mImageViews.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ImageView imageView = mImageViews.get(position);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("first_enter",true);
        startActivity(intent);
        SpUtil.putBoolean(this, SpKey.IS_FIRST_ENTER,false);
        finish();
    }
}