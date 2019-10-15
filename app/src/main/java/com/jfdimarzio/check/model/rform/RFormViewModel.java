package com.jfdimarzio.check.model.rform;

import com.jfdimarzio.check.model.JobItem;

import java.io.Serializable;
import java.util.ArrayList;

public class RFormViewModel implements Serializable {

    private  JobItem JobItem;
    private  ArrayList<RFormMaterialItem> RFormMaterialItems;
    private String Remark;
    private String OrgRemark;
    private RFormWorkingHour RFormWorkingHour;
    private RFormWorkingHour OrgRFormWorkingHour;
    private ArrayList<RFormCheckItem> RFormCheckItems;

    public RFormViewModel(JobItem jobItem){
        this.JobItem=jobItem;
        this.RFormMaterialItems=new ArrayList<>();
    }

    public JobItem getJobItem() {
        return JobItem;
    }

    public void setJobItem(JobItem jobItem) {
        JobItem = jobItem;
    }

    public ArrayList<RFormMaterialItem> getRFormMaterialItems() {
        return RFormMaterialItems;
    }

    public void setRFormMaterialItems(ArrayList<RFormMaterialItem> rFormMaterialItems) {
        RFormMaterialItems = rFormMaterialItems;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public String getOrgRemark() {
        return OrgRemark;
    }

    public void setOrgRemark(String orgRemark) {
        OrgRemark = orgRemark;
    }

    public RFormWorkingHour getRFormWorkingHour() {
        return RFormWorkingHour;
    }

    public void setRFormWorkingHour(RFormWorkingHour rFormWorkingHour) {
        this.RFormWorkingHour = rFormWorkingHour;
    }

    public RFormWorkingHour getOrgRFormWorkingHour() {
        return OrgRFormWorkingHour;
    }

    public void setOrgRFormWorkingHour(RFormWorkingHour orgRFormWorkingHour) {
        OrgRFormWorkingHour = orgRFormWorkingHour;
    }

    public ArrayList<RFormCheckItem> getRFormCheckItems() {
        return RFormCheckItems;
    }

    public void setRFormCheckItems(ArrayList<RFormCheckItem> RFormCheckItems) {
        this.RFormCheckItems = RFormCheckItems;
    }
}
