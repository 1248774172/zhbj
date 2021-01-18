package com.xiaoer.zhbj.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * 权限工具类
 */

public class PermissionsUtil {


    private final int mRequestCode = 100;//权限请求码
    public static boolean showSystemSetting = true;
    List<String> mPermissionList = null;
    private String [] permissions = null;

    private PermissionsUtil() {
    }

    private static PermissionsUtil permissionsUtils;
    private IPermissionsResult mPermissionsResult;

    public static PermissionsUtil getInstance() {
        if (permissionsUtils == null) {
            permissionsUtils = new PermissionsUtil();
        }
        return permissionsUtils;
    }

    public void checkPermissions(Activity context, String[] permissions, @NonNull IPermissionsResult permissionsResult) {
        mPermissionsResult = permissionsResult;
        this.permissions = permissions;

        //创建一个mPermissionList，逐个判断哪些权限未授予，未授予的权限存储到mPerrrmissionList中
        mPermissionList = new ArrayList<>();
        //逐个判断你要的权限是否已经通过
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permission);//添加还未授予的权限
            }
        }

        //申请权限
        if (mPermissionList.size() > 0) {//有权限没有通过，需要申请
            Log.d(TAG, "checkPermissions: -----------------------------申请权限数量："+mPermissionList.size());
            ActivityCompat.requestPermissions(context, permissions, mRequestCode);
        } else {
            //说明权限都已经通过，可以做你想做的事情去
            permissionsResult.passPermissons();
        }


    }

    //请求权限后回调的方法
    //参数： requestCode  是我们自己定义的权限请求码
    //参数： permissions  是我们请求的权限名称数组
    //参数： grantResults 是我们在弹出页面后是否允许权限的标识数组，数组的长度对应的是权限名称数组的长度，数组的数据0表示允许权限，-1表示我们点击了禁止权限

    public void onRequestPermissionsResult(Activity context, int requestCode,
                                           @NonNull int[] grantResults) {
        boolean hasPermissionDismiss = false;//有权限没有通过
        int forbit = 0;
        if (mRequestCode == requestCode) {
            for (int i=0;i < grantResults.length;i++) {
                if (grantResults[i] == -1) {
                    hasPermissionDismiss = true;
                    forbit = i;
                    break;
                }
            }
            //如果有权限没有被允许
            if (hasPermissionDismiss) {
                if (showSystemSetting) {
                    //跳转到系统设置权限页面，或者直接关闭页面，不让他继续访问
                    showSystemPermissionsSettingDialog(context, permissions[forbit]);
                } else {
                    mPermissionsResult.forbitPermissons();
                }
            } else {
                //全部权限通过，可以进行下一步操作。。。
                mPermissionsResult.passPermissons();
            }
        }

    }


    /**
     * 不再提示权限时的展示对话框
     */
    AlertDialog mPermissionDialog;

    private void showSystemPermissionsSettingDialog(final Activity context,String permission) {
        final String mPackName = context.getPackageName();
        if (mPermissionDialog == null) {
            mPermissionDialog = new AlertDialog.Builder(context)
                    .setMessage("已禁用权限，请手动授予"+permission)
                    .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelPermissionDialog();

                            Uri packageURI = Uri.parse("package:" + mPackName);
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                            context.startActivity(intent);
                            context.finish();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //关闭页面或者做其他操作
                            cancelPermissionDialog();
                            //mContext.finish();
                            mPermissionsResult.forbitPermissons();
                        }
                    })
                    .create();
        }
        mPermissionDialog.show();
    }

    //关闭对话框
    private void cancelPermissionDialog() {
        if (mPermissionDialog != null) {
            mPermissionDialog.cancel();
            mPermissionDialog = null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean canShowAlert(Context context){
        return Settings.canDrawOverlays(context);
    }


    public interface IPermissionsResult {
        void passPermissons();

        void forbitPermissons();
    }


}