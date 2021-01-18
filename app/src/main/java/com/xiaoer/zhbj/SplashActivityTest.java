package com.xiaoer.zhbj;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.miui.zeus.mimo.sdk.SplashAd;
import com.xiaoer.zhbj.Utils.SpKey;
import com.xiaoer.zhbj.Utils.SpUtil;

import java.util.List;

import static android.content.ContentValues.TAG;


public class SplashActivityTest extends BaseActivity{
    public String[] permissions = null;
    private SplashAd mSplashAd;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_test);
        //在调用SDK之前，如果您的App的targetSDKVersion >= 23，那么一定要把"READ_PHONE_STATE"、"WRITE_EXTERNAL_STORAGE"这几个权限申请到
        permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE};

        requestRunTimePermission(permissions, new PermissionListener() {

            @Override
            public void onGranted(List<String> grantedPermission) {
                this.onGranted();
            }

            @Override
            public void onGranted() {
                showAd();
            }

            @Override
            public void onDenied(List<String> deniedPermission) {
//                    Toast.makeText(SplashActivityTest.this, deniedPermission.get(0) + "权限被拒绝了", Toast.LENGTH_SHORT).show();
                Toast.makeText(SplashActivityTest.this, "请打开应用必要的权限", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAd() {
        FrameLayout splash_container = findViewById(R.id.splash_container);
        mSplashAd = new SplashAd(SplashActivityTest.this);
        mSplashAd.loadAndShow(splash_container, "003d1bd3be5fee0a93d7eac3dd81b913", new SplashAd.SplashAdListener() {
//        mSplashAd.loadAndShow(splash_container, "94f4805a2d50ba6e853340f9035fda18", new SplashAd.SplashAdListener() {
            @Override
            public void onAdShow() {
                Log.i("TAG", "onAdRenderFailed: ---------------------1");

            }

            @Override
            public void onAdClick() {
                Log.i("TAG", "onAdRenderFailed: ---------------------2");

            }

            @Override
            public void onAdDismissed() {
                Log.i("TAG", "onAdRenderFailed: ---------------------3");

                jump();
            }

            @Override
            public void onAdLoadFailed(int i, String s) {
                Log.i("TAG", "onAdRenderFailed: ---------------------4"+s+":"+i);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        jump();
                    }
                },2000);
            }
            @Override
            public void onAdLoaded() {
                Log.i("TAG", "onAdRenderFailed: ---------------------5");

            }

            @Override
            public void onAdRenderFailed() {
                Log.i("TAG", "onAdRenderFailed: ---------------------6");
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        jump();
                    }
                },2000);
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            return false;
        return super.onKeyDown(keyCode, event);
    }

    private void jump() {
        Log.i(TAG, "-------------------广告页展示完毕，进入应用");
        boolean is_first_enter = SpUtil.getBoolean(this, SpKey.IS_FIRST_ENTER, true);
        Intent intent;
        if(is_first_enter)
            intent = new Intent(this,GuideActivity.class);
        else
            intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mSplashAd!=null)
        mSplashAd.destroy();
    }
}