package com.jfdimarzio.check.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.jfdimarzio.check.R;
import com.jfdimarzio.check.util.consts.SystemConstants;

import java.io.File;

public class PreferenceUtils {
    private static PreferenceUtils sInstance;
    private final SharedPreferences mPreferences;
    private final Context mContext;

    public PreferenceUtils(final Context context){
        mPreferences= PreferenceManager.getDefaultSharedPreferences(context);
        mContext=context;
    }

    public static final PreferenceUtils getInstance(final Context context) {
        if(sInstance==null){
            sInstance=new PreferenceUtils(context.getApplicationContext());
        }
        return sInstance;
    }

    public final String getBaseUrl(){
        return SystemConstants.HTTP+mPreferences.getString(SystemConstants.KEY_WEB_IP,mContext.getString(R.string.pref_web_ip_default))+"/"+mPreferences.getString(SystemConstants.KEY_WEB_NAME,mContext.getString(R.string.pref_web_name_default))+"/api/";
    }
    public boolean getENABLE_LOG(){
        return mPreferences.getBoolean(SystemConstants.KEY_ENABLE_LOG,false);
    }

    public boolean getIS_DEFAULT_EXCEPT(){
        return mPreferences.getBoolean(SystemConstants.KEY_IS_DEFAULT_EXCEPT,false);
    }

    public boolean getShowCurrentTime(){
        return mPreferences.getBoolean(SystemConstants.KEY_SHOWCURRENTTIME,false);
    }

    public final String getServerTimeUrl(){
        return getBaseUrl()+"Connection";
    }
    public final String getJobListUrl(){return getBaseUrl()+"Job/Get";}
    public final String getDownloadUrl(){return getBaseUrl()+"Download/Post";}
    public final String getUploadUrl() { return getBaseUrl() + "Upload/Post";}

    public final String getFilePath(){
        String key=SystemConstants.KEY_FILE_PATH;
        String sd_path="";
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            File sdFile=android.os.Environment.getExternalStorageDirectory();
            sd_path=sdFile.getPath()+File.separator+DeviceUtils.getPackageName(mContext);
            File dirFile=new File(sd_path);
            if(!dirFile.exists()){
                dirFile.mkdir();
            }
        }
        return mPreferences.getString(key,sd_path);
    }

    public final String getTempPath(){
        String result="";
        String sd_path=getFilePath();
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            result=sd_path+File.separator+"Temp";
            File dirFile=new File(result);
            if(!dirFile.exists()){
                dirFile.mkdir();
            }
        }
        return result;
    }

    public final String getUploadTempPath()
    {
        String result = "";
        String sd_path = getFilePath();
        //確定SD卡可讀寫
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            File sdFile = android.os.Environment.getExternalStorageDirectory();
            result = sd_path + File.separator + "UploadTemp";
            File dirFile = new File(result);
            if(!dirFile.exists()){//如果資料夾不存在
                dirFile.mkdir();//建立資料夾
            }
        }
        return  result;
    }

    public final String getPicPath()
    {
        String result = "";
        String sd_path = getFilePath();
        //確定SD卡可讀寫
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            File sdFile = android.os.Environment.getExternalStorageDirectory();
            result = sd_path + File.separator + "Pic";
            File dirFile = new File(result);
            if(!dirFile.exists()){//如果資料夾不存在
                dirFile.mkdir();//建立資料夾
            }
        }
        return  result;
    }
}
