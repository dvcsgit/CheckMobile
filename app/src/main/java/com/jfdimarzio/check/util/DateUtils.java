package com.jfdimarzio.check.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public static final String FORMAT_YYYYMMDD="yyyyMMdd";
    public static final String FORMAT_DIS_YYYYMMDD="yyyy/MM/dd";
    public static final String FORMAT_HHMM="HHmm";

    public static Date converStringToDate(String source,String format){
        Date result=null;
        SimpleDateFormat formatter=new SimpleDateFormat(format);
        try{
            result=formatter.parse(source);
        }catch (ParseException e){
            e.printStackTrace();
        }
        return result;
    }

    public static String format(Date source,String format){
        SimpleDateFormat sdf=new SimpleDateFormat(format);
        return sdf.format(source);
    }

    public static Date addMinsToDate(Date beforeTime,int minutes){
        final long ONE_MINUTE_IN_MILLIS=60000;
        long curTimeInMs=beforeTime.getTime();
        Date afterAddingMins=new Date(curTimeInMs+(minutes*ONE_MINUTE_IN_MILLIS));
        return afterAddingMins;
    }

    public  static Date minusMinsToDate(Date beforeTime,int minutes){
        final long ONE_MINUTE_IN_MILLIS=60000;
        long curTimeInMs=beforeTime.getTime();
        Date afterAddingMins=new Date(curTimeInMs-(minutes*ONE_MINUTE_IN_MILLIS));
        return afterAddingMins;
    }
}
