package com.jfdimarzio.check.model;


import com.jfdimarzio.check.model.enumtype.MRFormType;

public class DownloadTypeItem {
    private String TagName;
    private MRFormType Type;
    private int Total;
    private int Color;
    private boolean IsSelected;

    public DownloadTypeItem(String tagName,MRFormType type,int color){
        TagName=tagName;
        Type=type;
        Color=color;
        this.IsSelected=true;
    }

    public MRFormType getType() {
        return Type;
    }

    public int getColor(){
        return Color;
    }

    public boolean isSelected(){
        return IsSelected;
    }

    public void setSelected(boolean selected){
        IsSelected=selected;
    }

    public String getTagName(){
        return TagName;
    }

    public int getTotal(){
        return Total;
    }

    public void setTotal(int total){
        Total=total;
    }
}
