package com.jfdimarzio.check.util;

import android.content.Context;
import android.content.pm.PackageManager;

public class DeviceUtils {
//    private static final String TAG=LogUtils.makeLogTag(DeviceUtils.class);

    public DeviceUtils(){}

    public static String getPackageName(Context context){
        String result="";
        try{
            result=context.getPackageName().toString();
        }catch (Exception var3){
//            LogUtils.LOGE(TAG,var3.getMessage());
        }
        return result;
    }

    public static String getVerName(Context context) {
        String verName = "";

        try {
            verName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException var3) {
//            LogUtils.LOGE(TAG, var3.getMessage());
        }

        return verName;
    }
}
