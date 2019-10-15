package com.jfdimarzio.check.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CheckDateFormatItem {
    private String FORMAT_DIS_YYYYMMDD_HHMMSS;
    private String FORMAT_DIS_YYYYMMDD;
    private String FORMAT_YYYYMMDD;
    private String FORMAT_YYYYMMDD_HHMMSS;
    private java.util.Date Date;

    public CheckDateFormatItem(){
        final java.util.Date currentDate=new Date();
        this.Date=currentDate;
        this.FORMAT_DIS_YYYYMMDD_HHMMSS= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(currentDate);
        this.FORMAT_DIS_YYYYMMDD=new SimpleDateFormat("yyyy/MM/dd").format(currentDate);
        this.FORMAT_YYYYMMDD=new SimpleDateFormat("yyyyMMdd").format(currentDate);
        this.FORMAT_YYYYMMDD_HHMMSS=new SimpleDateFormat("yyyyMMdd HHmmss").format(currentDate);
    }

    public String getFORMAT_DIS_YYYYMMDD_HHMMSS(){return FORMAT_DIS_YYYYMMDD_HHMMSS;}

    public String getFORMAT_DIS_YYYYMMDD(){return FORMAT_DIS_YYYYMMDD;}

    public String getFORMAT_YYYYMMDD(){
        return FORMAT_YYYYMMDD;
    }

    public String getFORMAT_YYYYMMDD_HHMMSS(){return FORMAT_YYYYMMDD_HHMMSS;}

    public Date getDate(){return Date;}
}
