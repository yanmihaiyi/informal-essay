package com.example.shihao.myapplication.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * @author xiekun
 * @date 2018/07/05.
 *
 * 包工具类
 * 获取包名，版本名，版本号等
 */
public class PackageUtils {

    /**
     * 获取版本号
     * @param context
     * @return
     */
    public static String getAppVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取版本代码
     * @param context
     * @return
     */
    public static int getAppVersionCode(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            int versionCode = info.versionCode;
            return versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取包名
     * @param context
     * @return
     */
    public static String getAppPackageName(Context context) {
        try {
            String packageName = context.getPackageName();
            return packageName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 检查SDK版本
     * @return
     */
    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
}
