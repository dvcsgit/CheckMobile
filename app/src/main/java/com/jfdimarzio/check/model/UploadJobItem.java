package com.jfdimarzio.check.model;

public class UploadJobItem extends JobItem {
    //Extend
    private boolean IsSelected;

    public UploadJobItem(String uniqueID, String description, String remark, String beginTime, String endTime, boolean isCheckBySeq, boolean isShowPrevRecord, boolean isRepairForm, boolean isSelected) {
        super(uniqueID, description, remark, beginTime, endTime, isCheckBySeq, isShowPrevRecord, isRepairForm);
        IsSelected = isSelected;
    }

    public boolean isSelected() {
        return IsSelected;
    }

    public void setIsSelected(boolean isSelected) {
        IsSelected = isSelected;
    }
}
