package com.xiaoer.zhbj;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.tamsiree.rxkit.RxWebViewTool;
import com.tamsiree.rxkit.interfaces.OnWebViewLoad;
import com.zackratos.ultimatebarx.library.UltimateBarX;

public class ShowNewsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        Toolbar tb_main = findViewById(R.id.tb_main);
        setSupportActionBar(tb_main);
        tb_main.setTitle("");

        UltimateBarX.with(this)
                .colorRes(R.color.defaultToolbarColor)
                .fitWindow(true)
                .applyStatusBar();

        String url = getIntent().getStringExtra("url");
        WebView wv_web = findViewById(R.id.wv_web);
        wv_web.loadUrl(url);
        RxWebViewTool.initWebView(this, wv_web, new OnWebViewLoad() {
            @Override
            public void onPageStarted() {
            }

            @Override
            public void onReceivedTitle(@NonNull String s) {
            }

            @Override
            public void onProgressChanged(int i) {
            }

            @Override
            public void shouldOverrideUrlLoading() {
            }

            @Override
            public void onPageFinished() {
            }
        });

    }
}