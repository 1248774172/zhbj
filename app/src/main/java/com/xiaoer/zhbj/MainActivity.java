package com.xiaoer.zhbj;

import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.xiaoer.zhbj.entity.GlobalPath;
import com.xiaoer.zhbj.fragment.ContentFragment;
import com.xiaoer.zhbj.fragment.LeftMenuFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private static final String CONTENT_FRAGMENT = "content_fragment";
    private static final String LEFTMENU_FRAGMENT = "leftmenu_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        copyFilesFromAssets(this, "zhbj", GlobalPath.assetsPath);

        initFragment();
    }

    public void copyFilesFromAssets(Context context, String oldPath, String newPath) {
        try {
            String[] fileNames = context.getAssets().list(oldPath);//获取assets目录下的所有文件及目录名
            assert fileNames != null;
            if (fileNames.length > 0) {//如果是目录
                File file = new File(newPath);
                file.mkdirs();
                for (String fileName : fileNames) {
                    copyFilesFromAssets(context, oldPath + "/" + fileName, newPath + "/" + fileName);
                }
            } else {//如果是文件
                InputStream is = context.getAssets().open(oldPath);
                FileOutputStream fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount;
                while ((byteCount = is.read(buffer)) != -1) {//循环从输入流读取 buffer字节
                    fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
                }
                fos.flush();//刷新缓冲区
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    void copyFileFromAssets(Context context, String folder){
//        String filesDir = context.getFilesDir().getPath();
//        filesDir = filesDir + "/assets/" + folder;
//        copyFilesFromAssets(context,folder,filesDir);
//    }

    /**
     * 初始化主界面fragment和侧边栏fragment
     */
    public void initFragment() {
        ContentFragment contentFragment = new ContentFragment();
        LeftMenuFragment leftMenuFragment = new LeftMenuFragment();
        //开启事务用fragment替换frameLayout
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fl_contain, contentFragment, CONTENT_FRAGMENT);
        fragmentTransaction.replace(R.id.fl_left_menu, leftMenuFragment, LEFTMENU_FRAGMENT);
        fragmentTransaction.commit();
    }

    public LeftMenuFragment getLeftMenuFragment() {
        return (LeftMenuFragment) getSupportFragmentManager().findFragmentByTag(LEFTMENU_FRAGMENT);
    }

    public ContentFragment getContentFragment() {
        return (ContentFragment) getSupportFragmentManager().findFragmentByTag(CONTENT_FRAGMENT);
    }

    private void initView() {
        //侧边栏占屏幕1/3
        NavigationView nv_left = findViewById(R.id.nv_left);
        ViewGroup.LayoutParams layoutParams = nv_left.getLayoutParams();
        layoutParams.width = getResources().getDisplayMetrics().widthPixels / 3;
        nv_left.setLayoutParams(layoutParams);
    }
}