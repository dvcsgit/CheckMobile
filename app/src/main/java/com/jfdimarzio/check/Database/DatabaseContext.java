package com.jfdimarzio.check.Database;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;

import com.jfdimarzio.check.util.LogUtils;

import java.io.File;

public class DatabaseContext extends ContextWrapper {
    private static final String TAG= LogUtils.makeLogTag(DatabaseContext.class);
    private String mDatabaseFullPath="";

    public DatabaseContext(Context base, String sqlitFolder){
        super(base);
        this.mDatabaseFullPath=sqlitFolder;
    }

    public File getDatabasePath(String name){
        return new File(this.mDatabaseFullPath,name);
    }

    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler){
        SQLiteDatabase result=SQLiteDatabase.openOrCreateDatabase(this.getDatabasePath(name),null);
//        LogUtils.LOGW(TAG,"openOrCreateDatabase("+name+")="+result.getPath());
        return result;
    }
}
