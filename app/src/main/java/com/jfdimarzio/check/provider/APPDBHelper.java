package com.jfdimarzio.check.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.jfdimarzio.check.Database.DatabaseContext;
import com.jfdimarzio.check.util.PreferenceUtils;
import com.jfdimarzio.check.util.consts.SystemConstants;

import java.util.ArrayList;

import timber.log.Timber;

public class APPDBHelper extends SQLiteOpenHelper {
    private final static String DATABASE_NAME=SystemConstants.SQLITE_APP;
    private final static int DATABASE_VERSION=2;
    private final Context mContext;
    private PreferenceUtils mPreferences;
    private String mDatabaseFullPath;

    private static APPDBHelper mInstance=null;

    private APPDBHelper(Context context){
        super(new DatabaseContext(context,PreferenceUtils.getInstance(context).getFilePath()),DATABASE_NAME,null,DATABASE_VERSION);

        this.mContext=context;
        mPreferences=PreferenceUtils.getInstance(mContext);
        mDatabaseFullPath=mPreferences.getFilePath();
    }

    public synchronized static APPDBHelper getInstance(Context context){
        if(mInstance==null){
            mInstance=new APPDBHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        final String CREATE_RECORD_HISTORY="CREATE TABLE "+APPColumn.CheckResultSign.TABLE_NAME+" ("+
                APPColumn.CheckResultSign.UniqueID+" NVARCHAR(40), "+
                APPColumn.CheckResultSign.ArriveRecordUniqueID+" NVARCHAR(40), "+
                APPColumn.CheckResultSign.JobUniqueID+" NVARCHAR(40), "+
                APPColumn.CheckResultSign.ControlPointUniqueid+" NVARCHAR(40), "+
                APPColumn.CheckResultSign.ABNote+" NVARCHAR(500), "+
                APPColumn.CheckResultSign.ABHand+" NVARCHAR(500), "+
                APPColumn.CheckResultSign.SingPath+" NVARCHAR(200)"+
                ")";
        db.execSQL(CREATE_RECORD_HISTORY);

        final String CREATE_SUGGEST_USER="CREATE TABLE "+APPColumn.SuggestUser.TABLE_NAME+" ("+
                APPColumn.SuggestUser.UserID+" NVARCHAR(40),"+
                APPColumn.SuggestUser.QueryDateTime+" NVARCHAR(14)"+
                ")";
        db.execSQL(CREATE_SUGGEST_USER);

        final String CREATE_RECORD_HISTORY_INDEX="CREATE INDEX IDX_SUGGEST_USER ON "+APPColumn.SuggestUser.TABLE_NAME+" ("+
                APPColumn.SuggestUser.UserID+" ASC);";
        db.execSQL(CREATE_RECORD_HISTORY_INDEX);

        version2(db);
    }

    private void version2(SQLiteDatabase db){
//        Timber.d("version2...start");
        final String CREATE_VERSION_TABLE="CREATE TABLE "+APPColumn.Version.TABLE_NAME+" ("+
                APPColumn.Version.Id+" INT,"+
                APPColumn.Version.AppName+" NVARCHAR(100),"+
                APPColumn.Version.ApkName+" NVARCHAR(100),"+
                APPColumn.Version.VerName+" NVARCHAR(100),"+
                APPColumn.Version.VerCode+" INT,"+
                APPColumn.Version.ReleaseNote+" NVARCHAR(1000),"+
                APPColumn.Version.IsForceUpdate+" INTEGER DEFAULT 0,"+
                APPColumn.Version.DateReleased+" NVARCHAR(32),"+
                "UNIQUE(["+APPColumn.Version.ApkName+"])"+
                ")";
        db.execSQL(CREATE_VERSION_TABLE);

//        Timber.d("version2...done");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
        switch (oldVersion){
            case 1:
//                Timber.d("upgrade logic from version 1 to 2");
                version2(db);
            case 2:
//                Timber.d("upgrade logic from version 2 to 3");
            case 3:
//                Timber.d("upgrade logic from version 3 to 4");
                break;
                default:
//                    Timber.d("onUpgrade() with unknown oldVersion:%s",oldVersion);
        }
    }

    public String getLastUserID(){
        String result=null;
        final SQLiteDatabase rDb=getReadableDatabase();
        Cursor cursor=null;
        try{
            String query="SELECT * FROM "+APPColumn.SuggestUser.TABLE_NAME+" "+
                    "\n ORDER BY "+APPColumn.SuggestUser.QueryDateTime+" DESC"+
                    "\n LIMIT 1";
            cursor=rDb.rawQuery(query,null);
            if(cursor!=null&&cursor.moveToFirst()){
                cursor.moveToFirst();
                result=cursor.getString(cursor.getColumnIndexOrThrow(APPColumn.SuggestUser.UserID));
            }
        }catch (Exception ex){
            Timber.e("getLastUserID Error:%s",ex.getMessage());
        }finally {
            if(cursor!=null){
                cursor.close();
            }
        }
        return result;
    }

    public ArrayList<String> getUserIDArrayList(){
        final SQLiteDatabase rDb=getReadableDatabase();
        ArrayList<String> result=new ArrayList<>();
        Cursor cursor=null;
        try{
            final String[] fetch_columns=new String[]{
                    APPColumn.SuggestUser.UserID
            };
            cursor=rDb.query(APPColumn.SuggestUser.TABLE_NAME,fetch_columns,null,null,null,null,null);
            if(cursor.moveToFirst()){
                do{
                    result.add(cursor.getString(cursor.getColumnIndexOrThrow(APPColumn.SuggestUser.UserID)));
                }while(cursor.moveToNext());
            }
        }catch (Exception ex){
//            Timber.e("getUserIDArrayList Error:%s",ex.getMessage());
        }finally {
            if(cursor!=null){
                cursor.close();
            }
        }
        return result;
    }

    public boolean insertOrUpdateUserID(String userID,String query_date_time){
        boolean result=false;
        final SQLiteDatabase wDb=getWritableDatabase();
        Cursor cursor=null;
        try{
            wDb.beginTransaction();
            String selection=APPColumn.SuggestUser.UserID+"=? ";
            String[] selectionArgs=new String[]{userID};
            final String[] fetch_columns=new String[]{
                    APPColumn.SuggestUser.UserID,
                    APPColumn.SuggestUser.QueryDateTime
            };

            String tempUserID="";
            cursor=wDb.query(APPColumn.SuggestUser.TABLE_NAME,fetch_columns,selection,selectionArgs,null,null,null);
            if(cursor!=null&&cursor.moveToFirst()){
                cursor.moveToFirst();
                tempUserID=cursor.getString(cursor.getColumnIndexOrThrow(APPColumn.SuggestUser.UserID));
            }
            if(TextUtils.isEmpty(tempUserID)){
                //insert
                final ContentValues suggest_values=new ContentValues(2);
                suggest_values.put(APPColumn.SuggestUser.UserID,userID);
                suggest_values.put(APPColumn.SuggestUser.QueryDateTime,query_date_time);
                long insert_row_id=wDb.insert(APPColumn.SuggestUser.TABLE_NAME,null,suggest_values);
//                Timber.d("Insert successfully.insert_row_id:%s",insert_row_id);
            }else {
                final ContentValues suggest_values=new ContentValues(1);
                suggest_values.put(APPColumn.SuggestUser.QueryDateTime,query_date_time);
                long updatep_row_id=wDb.update(APPColumn.SuggestUser.TABLE_NAME,suggest_values,selection,selectionArgs);
//                Timber.d("Update successfully.update_row_id:%s",updatep_row_id);
            }
            wDb.setTransactionSuccessful();
            result=true;
        }catch (Exception ex){
//            Timber.e("insertOrUpdateUserID Error:%s",ex.getMessage());
        }finally {
            if(cursor!=null){
                cursor.close();
            }
            wDb.endTransaction();
        }
        return result;
    }

//    public boolean updateLastedApp(VersionItem model){
//
//    }

//    public VersionItem getLastVersion(){
//
//    }
}
