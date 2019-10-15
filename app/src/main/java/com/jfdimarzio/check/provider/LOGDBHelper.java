package com.jfdimarzio.check.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jfdimarzio.check.util.PreferenceUtils;
import com.jfdimarzio.check.util.consts.SystemConstants;

import timber.log.Timber;

public class LOGDBHelper extends SQLiteOpenHelper {
    private final static String DATABASE_NAME= SystemConstants.SQLITE_LOG;
    private final static int DATABASE_VERSION=1;

    private final Context mContext;
    private PreferenceUtils mPreferences;

    private static LOGDBHelper mInstance=null;

    public LOGDBHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        this.mContext=context;
        mPreferences=PreferenceUtils.getInstance(mContext);
    }

    public synchronized static LOGDBHelper getmInstance(Context context){
        if(mInstance==null) {
            mInstance = new LOGDBHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        final String CREATE_LOGColumn="CREATE TABLE "+LOGColumn.LOGInfo.TABLE_NAME+" ("+
                LOGColumn.LOGInfo.LogTime+" NVARCHAR(20), "+
                LOGColumn.LOGInfo.ErrorLevel+" NVARCHAR(20), "+
                LOGColumn.LOGInfo.TagInfo+" NVARCHAR(20), "+
                LOGColumn.LOGInfo.Message+" NVARCHAR(500), "+
                LOGColumn.LOGInfo.LoginUser+" NVARCHAR(40) "+
                ")";
        db.execSQL(CREATE_LOGColumn);

        final String CREATE_SystemInfo="CREATE TABLE "+LOGColumn.SystemInfo.TABLE_NAME+"("+
                LOGColumn.SystemInfo.Key+" NVARCHAR(40), "+
                LOGColumn.SystemInfo.Value+" NVARCHAR(200), "+
                LOGColumn.SystemInfo.Description+" NVARCHAR(100) "+
                ")";
        db.execSQL(CREATE_SystemInfo);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){}

    public boolean insertLog(String log_time,String error_level,String tag_info,String message,String login_user){
        final SQLiteDatabase wDb=getWritableDatabase();
        boolean result=false;
        try{
            final ContentValues values=new ContentValues(5);
            values.put(LOGColumn.LOGInfo.LogTime,log_time);
            values.put(LOGColumn.LOGInfo.ErrorLevel,error_level);
            values.put(LOGColumn.LOGInfo.TagInfo,tag_info);
            values.put(LOGColumn.LOGInfo.Message,message);
            values.put(LOGColumn.LOGInfo.LoginUser,login_user);
            result=true;
        }
        catch (Exception ex){
            Timber.d("InsertLog:%s",ex.getMessage());
        }
        finally {

        }
        return result;
    }

    public void resetTables(){
        try{
            SQLiteDatabase wDb=getWritableDatabase();
            wDb.delete(LOGColumn.LOGInfo.TABLE_NAME,null,null);
            wDb.delete(LOGColumn.LOGInfo.TABLE_NAME,null,null);
            wDb.close();
        }
        catch (Exception ex){
            Timber.d("ResetTables error:%s",ex.getMessage());
        }
    }

}
