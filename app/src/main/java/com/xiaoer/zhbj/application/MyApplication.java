package com.xiaoer.zhbj.application;

import android.app.Application;

import com.miui.zeus.mimo.sdk.MimoSdk;
import com.tamsiree.rxkit.RxTool;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RxTool.init(this);

        MimoSdk.init(this, "2882303761518833882");
//        MimoSdk.init(this, "2882303761517518052");
        MimoSdk.setDebugOn(false); // sdk debug
        MimoSdk.setStagingOn(false); // sdk

    }
}
