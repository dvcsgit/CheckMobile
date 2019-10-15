package com.jfdimarzio.check.model.rform;

import java.io.Serializable;

public class RFormCheckItem implements Serializable {
    private String RFormUniqueID;
    private String ColumnUniqueID;
    private String ColumnDescription;
    private String ColumnOptionUniqueID;

    private int Seq;

    //region 紀錄抄表

    private String ResultValue;
    private String ResultColumnOptionUniqueID;
    private boolean IsChanged;//資料是否有異動過

    //endregion

    private String UIOptionResult;
    private boolean IsOption;


    public RFormCheckItem(String RFormUniqueID, String columnUniqueID, String columnDescription, String columnOptionUniqueID, int seq, String resultValue, String resultColumnOptionUniqueID) {
        this.RFormUniqueID = RFormUniqueID;
        ColumnUniqueID = columnUniqueID;
        ColumnDescription = columnDescription;
        ColumnOptionUniqueID = columnOptionUniqueID;
        Seq = seq;
        ResultValue = resultValue;
        ResultColumnOptionUniqueID = resultColumnOptionUniqueID;
    }

    public boolean isChanged() {
        return IsChanged;
    }

    public void setChanged(boolean changed) {
        IsChanged = changed;
    }

    public String getRFormUniqueID() {
        return RFormUniqueID;
    }

    public String getColumnUniqueID() {
        return ColumnUniqueID;
    }

    public String getColumnDescription() {
        return ColumnDescription;
    }

    public String getColumnOptionUniqueID() {
        return ColumnOptionUniqueID;
    }

    public int getSeq() {
        return Seq;
    }

    public String getResultValue() {
        return ResultValue;
    }

    public String getResultColumnOptionUniqueID() {
        return ResultColumnOptionUniqueID;
    }

    public String getUIOptionResult() {
        return UIOptionResult;
    }

    public boolean isOption() {
        return IsOption;
    }

    public void setOption(boolean option) {
        IsOption = option;
    }

    public void setResultValue(String resultValue) {
        ResultValue = resultValue;
    }

    public void setResultColumnOptionUniqueID(String resultColumnOptionUniqueID) {
        ResultColumnOptionUniqueID = resultColumnOptionUniqueID;
    }

    public void setUIOptionResult(String UIOptionResult) {
        this.UIOptionResult = UIOptionResult;
    }
}
