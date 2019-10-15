package com.jfdimarzio.check.provider;

public class LOGColumn {
    public interface LOGInfo{
        String TABLE_NAME="LOGInfo";
        String LogTime="LogTime";
        String ErrorLevel="ErrorLevel";
        String TagInfo="TagInfo";
        String Message="Message";
        String LoginUser="LoginUser";
    }
    public interface SystemInfo{
        String TABLE_NAME="SystemInfo";
        String Key="Key";
        String Value="Value";
        String Description="Description";
    }
}
