package com.jfdimarzio.check;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.jfdimarzio.check.provider.APPDBHelper;
import com.jfdimarzio.check.provider.EquipCheckHelper;
import com.jfdimarzio.check.provider.LOGDBHelper;
import com.jfdimarzio.check.util.PreferenceUtils;
import com.jfdimarzio.check.util.SQLiteLogLibary;

import timber.log.Timber;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import static com.jfdimarzio.check.util.LogUtils.makeLogTag;

public class AppController extends Application {
    private static final String TAG=makeLogTag(AppController.class);
    private static AppController mInstance;
    private PreferenceUtils mPreferenceUtils;
    private RequestQueue mRequestQueue;

    private APPDBHelper mAPPDBHelper;
    private EquipCheckHelper mEquipCheckHelper;

    private Context mContext;
    private LOGDBHelper mLOGDBHelper;

    @Override
    public void onCreate(){
        super.onCreate();
        mInstance=this;

        if(BuildConfig.DEBUG){
            Timber.plant(new Timber.DebugTree());
        }else {
            Timber.plant(new CrashReportingTree());
        }
    }

    private static class CrashReportingTree extends Timber.Tree{
        @Override
        protected void log(int priority,String tag,String message,Throwable t){
            if(priority== Log.VERBOSE||priority==Log.DEBUG){
                return;
            }
            if(AppController.getInstance().getPreferenceUtils().getENABLE_LOG()){
                SQLiteLogLibary.log(priority,tag,message);
            }
        }
    }
    public static synchronized AppController getInstance(){
        return mInstance;
    }

    public PreferenceUtils getPreferenceUtils(){
        if(mPreferenceUtils==null){
            mPreferenceUtils=PreferenceUtils.getInstance(getApplicationContext());
        }
        return mPreferenceUtils;
    }

    public <T> void addToRequestQueue(Request<T> req,String tag){
        req.setTag(TextUtils.isEmpty(tag)?TAG:tag);
        getRequestQueue().add(req);
    }

    public RequestQueue getRequestQueue(){
        if(mRequestQueue==null){
            mRequestQueue=Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public void cancelPendingRequests(Object tag){
        if(mRequestQueue!=null){
            mRequestQueue.cancelAll(tag);
        }
    }

    public APPDBHelper getAPPDBHelper() {
        if(mAPPDBHelper==null){
            mAPPDBHelper=APPDBHelper.getInstance(getApplicationContext());
        }
        return mAPPDBHelper;
    }

    public EquipCheckHelper getEquipCheckHelper(){
        if(mEquipCheckHelper==null){
            mEquipCheckHelper=EquipCheckHelper.getmInstance(getApplicationContext());
        }
        return mEquipCheckHelper;
    }

    public LOGDBHelper getLOGDBHelper()
    {
        if(mLOGDBHelper == null)
        {
            mLOGDBHelper = LOGDBHelper.getmInstance(getApplicationContext());
        }
        return mLOGDBHelper;
    }

    public Context getmContext() {
        mContext=getApplicationContext();
        return mContext;
    }
}
