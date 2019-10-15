package com.jfdimarzio.check.util;

import android.util.Log;

import com.jfdimarzio.check.model.CheckDateFormatItem;

public class SQLiteLogLibary {
    public static void log(int priority,String tag,String message){
        try{
            String log_level="";
            String login_userID="";
            CheckDateFormatItem checkDateFormatItem=new CheckDateFormatItem();
            String log_time=checkDateFormatItem.getFORMAT_DIS_YYYYMMDD_HHMMSS();
            switch(priority){
                case Log.VERBOSE:
                    log_level="VERBOSE";
                    break;
                case Log.DEBUG:
                    log_level="DEBUG";
                    break;
                case Log.INFO:
                    log_level="INFO";
                    break;
                case Log.WARN:
                    log_level="WARN";
                    break;
                case Log.ERROR:
                    log_level="ERROR";
                    break;
                case Log.ASSERT:
                    log_level="ASSERT";
                    break;
            }
//            LOGDBHelper logdbHelper= AppController.getInstance().getLOGDBHelper();
//            UserSession userSession=AppController.getInstance().getSessionManager().getUserSession();
//
//            if(!TextUtils.isEmpty(userSession.getID())){
//                login_userID=userSession.getID();
//            }
//            logdbHelper.insertLog(log_time,log_level,tag,message,login_userID);
        }
        catch (Exception ex){
            Log.v("FileLogLibary","FileLogLibary Write Error"+ex.getLocalizedMessage());
            ex.printStackTrace();
        }
        finally {

        }
    }
}
