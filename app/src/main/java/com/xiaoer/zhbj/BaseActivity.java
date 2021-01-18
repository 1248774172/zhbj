package com.xiaoer.zhbj;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;


public class BaseActivity extends Activity {
    private PermissionListener mlistener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 权限申请
     *
     * @param permissions 待申请的权限集合
     * @param listener    申请结果监听事件
     */
    protected void requestRunTimePermission(String[] permissions,
                                            PermissionListener listener) {
        this.mlistener = listener;

        // 用于存放为授权的权限
        List<String> permissionList = new ArrayList<>();
        for (String permission : permissions) {
            // 判断是否已经授权，未授权，则加入待授权的权限集合中
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }

        // 判断集合
        if (!permissionList.isEmpty()) { // 如果集合不为空，则需要去授权
            ActivityCompat.requestPermissions(this,
                    permissionList.toArray(new String[0]),
                    1);
        } else { // 为空，则已经全部授权
            listener.onGranted();
        }
    }

    /**
     * 权限申请结果
     *
     * @param requestCode  请求码
     * @param permissions  所有的权限集合
     * @param grantResults 授权结果集合
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0) {
                // 被用户拒绝的权限集合
                List<String> deniedPermissions = new ArrayList<>();
                // 用户通过的权限集合
                List<String> grantedPermissions = new ArrayList<>();
                for (int i = 0; i < grantResults.length; i++) {
                    // 获取授权结果，这是一个int类型的值
                    int grantResult = grantResults[i];

                    String permission = permissions[i];
                    if (grantResult != PackageManager.PERMISSION_GRANTED) { // 用户拒绝授权的权限
                        deniedPermissions.add(permission);
                    } else { // 用户同意的权限
                        grantedPermissions.add(permission);
                    }
                }

                if (deniedPermissions.isEmpty()) { // 用户拒绝权限为空
                    mlistener.onGranted();
                } else { // 不为空
                    Toast.makeText(this, "应用缺少必要的权限！请点击\"权限\"，打开所需要的权限。", Toast.LENGTH_LONG).show();
                    // 回调授权成功的接口
                    mlistener.onDenied(deniedPermissions);
                    // 回调授权失败的接口
                    mlistener.onGranted(grantedPermissions);
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                }
            }
        }
    }
}
