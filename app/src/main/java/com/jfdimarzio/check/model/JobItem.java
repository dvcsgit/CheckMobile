package com.jfdimarzio.check.model;

import java.io.Serializable;
import com.jfdimarzio.check.model.enumtype.MRFormType;

public class JobItem implements Serializable {
    private String UniqueID;//
    private String Description;//
    private String Remark;//
    private String BeginTime;
    private String EndTime;
    private String ArriveTime;
    private String CompleteTime;
    private String TimeMode;

    private boolean IsCheckBySeq;
    private boolean IsShowPrevRecord;
    private boolean IsHaveEmgTel;

    private int UITotalCount;
    private int UIDoneCount;

    public void setMRFormType(MRFormType MRFormType) {
        this.MRFormType = MRFormType;
    }

    private MRFormType MRFormType;


    //region 修復單
    private boolean IsRepairForm;
    private String FormType;
    private String VHNO;
    private String Subject;
    private String Equipment;
    private String BegDate;
    private String EndDate;
    //endregion

    /**
     * 定保單
     */
    private String EquipmentID;
    private String EquipmentName;
    private String PartDescription;

    //未巡檢原因 完整資訊
    private UnPatrolRecordItem UnPatrolRecordItem;

    private String SystemType;

    public String getSystemType() {
        return SystemType;
    }

    public void setSystemType(String systemType) {
        SystemType = systemType;
    }

    /**
     * 一般單子
     * @param uniqueID
     * @param description
     * @param remark
     * @param beginTime
     * @param endTime
     * @param isCheckBySeq
     * @param isShowPrevRecord
     * @param isRepairForm
     */
    public JobItem(String uniqueID, String description, String remark, String beginTime, String endTime, boolean isCheckBySeq, boolean isShowPrevRecord, boolean isRepairForm) {
        this.MRFormType = MRFormType.None;
        UniqueID = uniqueID;
        Description = description;
        Remark = remark;
        BeginTime = beginTime;
        EndTime = endTime;
        IsCheckBySeq = isCheckBySeq;
        IsShowPrevRecord = isShowPrevRecord;
        IsRepairForm = isRepairForm;
        this.TimeMode = "0";
        this.SystemType = "巡檢";
    }

    /**
     * 修復單
     * @param uniqueID
     * @param VHNO
     * @param equipment
     * @param subject
     * @param description
     * @param formType
     * @param begDate
     * @param endDate
     */
    public JobItem(String uniqueID,String VHNO,String equipment ,String subject, String description,String formType , String begDate, String endDate) {
        this.IsRepairForm = true;
        this.MRFormType = MRFormType.Repair;
        this.UniqueID = uniqueID;//
        this.VHNO = VHNO;//
        this.Equipment = equipment;
        this.Subject = subject;//
        this.Description = description;
        this.FormType = formType;
        this.BegDate = begDate;
        this.EndDate = endDate;
        this.TimeMode = "0";
        this.SystemType = "修復單";
    }


    /**
     * 定保單
     * @param uniqueID
     * @param VHNO
     * @param description
     * @param remark
     * @param equipmentID
     * @param equipmentName
     * @param partDescription
     * @param begDate
     * @param endDate
     */
    public JobItem(String uniqueID,String VHNO, String description,String remark,String equipmentID , String equipmentName,String partDescription,String begDate,String endDate) {
        this.IsRepairForm = true;
        this.MRFormType = MRFormType.MForm;
        this.UniqueID = uniqueID;
        this.VHNO = VHNO;
        this.Description = description;
        this.Remark = remark;
        this.EquipmentID = equipmentID;
        this.EquipmentName = equipmentName;
        this.PartDescription = partDescription;
        this.SystemType = "定保單";
        this.BegDate = begDate;
        this.EndDate = endDate;
    }


    public int getUITotalCount() {
        return UITotalCount;
    }

    public void setUITotalCount(int UITotalCount) {
        this.UITotalCount = UITotalCount;
    }

    public int getUIDoneCount() {
        return UIDoneCount;
    }

    public void setUIDoneCount(int UIDoneCount) {
        this.UIDoneCount = UIDoneCount;
    }


    public int getProgress() {
        Integer result = 0 ;
        if(this.getUITotalCount()>0)
        {
            result = (int)(this.getUIDoneCount()*100)/this.getUITotalCount();
        }
        return result;
    }

    public String getUniqueID() {
        return UniqueID;
    }

    public String getDescription() {
        return Description;
    }

    public String getRemark() {
        return Remark;
    }

    public String getBeginTime() {
        return BeginTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public boolean isCheckBySeq() {
        return IsCheckBySeq;
    }

    public boolean isShowPrevRecord() {
        return IsShowPrevRecord;
    }

    public boolean isRepairForm() {
        return IsRepairForm;
    }

    public String getFormType() {
        return FormType;
    }

    public String getVHNO() {
        return VHNO;
    }

    public String getSubject() {
        return Subject;
    }

    public String getEquipment() {
        return Equipment;
    }

    public String getBegDate() {
        return BegDate;
    }

    public String getEndDate() {
        return EndDate;
    }

    public String getArriveTime() {
        return ArriveTime;
    }

    public void setArriveTime(String arriveTime) {
        ArriveTime = arriveTime;
    }

    public String getCompleteTime() {
        return CompleteTime;
    }

    public void setCompleteTime(String completeTime) {
        CompleteTime = completeTime;
    }

    public boolean isHaveEmgTel() {
        return IsHaveEmgTel;
    }

    public void setIsHaveEmgTel(boolean isHaveEmgTel) {
        IsHaveEmgTel = isHaveEmgTel;
    }

    public void setTimeMode(String timeMode) {
        TimeMode = timeMode;
    }

    public String getTimeMode() {
        return TimeMode;
    }

    public UnPatrolRecordItem getUnPatrolRecordItem() {
        return UnPatrolRecordItem;
    }

    public void setUnPatrolRecordItem(UnPatrolRecordItem unPatrolRecordItem) {
        UnPatrolRecordItem = unPatrolRecordItem;
    }

    public MRFormType getMRFormType() {
        return MRFormType;
    }

    public String getEquipmentID() {
        return EquipmentID;
    }

    public String getEquipmentName() {
        return EquipmentName;
    }

    public String getPartDescription() {
        return PartDescription;
    }

    public void setPartDescription(String partDescription) {
        PartDescription = partDescription;
    }

    public void setEquipmentName(String equipmentName) {
        EquipmentName = equipmentName;
    }

    public void setEquipmentID(String equipmentID) {
        EquipmentID = equipmentID;
    }

    public void setEndDate(String endDate) {
        EndDate = endDate;
    }

    public void setBegDate(String begDate) {
        BegDate = begDate;
    }

    public void setEquipment(String equipment) {
        Equipment = equipment;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }

    public void setVHNO(String VHNO) {
        this.VHNO = VHNO;
    }

    public void setFormType(String formType) {
        FormType = formType;
    }
}
