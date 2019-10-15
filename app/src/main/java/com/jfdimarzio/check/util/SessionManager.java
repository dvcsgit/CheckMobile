package com.jfdimarzio.check.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.jfdimarzio.check.DashboardActivity;
import com.jfdimarzio.check.model.UserSession;

public class SessionManager {
    private static SessionManager mInstance;
    private final SharedPreferences mPreference;
    private final Context mContext;
    private SharedPreferences.Editor mEditor;
    private int PRIVATE_MODE=0;
    private static final String PREF_NAME="SmartPref";
    private static final String IS_LOGIN="IsLoggedIn";
    public static final String KEY_USERID="KEY_USERID";
    public static final String KEY_NAME="KEY_NAME";
    public static String mReturnActivity;

    public SessionManager(final Context context){
        mContext=context;
        mPreference=mContext.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        mEditor=mPreference.edit();
    }

    public static final SessionManager getmInstance(final Context context) {
        if(mInstance==null){
            mInstance=new SessionManager(context.getApplicationContext());
        }
        return mInstance;
    }

    public void createLoginSession(String userID,String name){
        mEditor.putBoolean(IS_LOGIN,true);
        mEditor.putString(KEY_USERID,userID);
        mEditor.putString(KEY_NAME,name);
        mEditor.commit();
    }

    public void checkLogin(){
        if(!this.isLoggedIn()){

        }else {
            Intent intent=new Intent(mContext,DashboardActivity.class);
            //Close all the activities.
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //Add new Flag to start new Activity.
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            return;
        }
    }

    public UserSession getUserSession(){
        String userId=mPreference.getString(KEY_USERID,null);
        String name=mPreference.getString(KEY_NAME,null);
        UserSession user=new UserSession(userId,name);
        return user;
    }

    public void logoutUser(){
        mEditor.clear();
        mEditor.commit();
    }

    public boolean isLoggedIn(){
        return mPreference.getBoolean(IS_LOGIN,false);
    }

    public static String getmReturnActivity(){
        return mReturnActivity;
    }
}
