package com.jfdimarzio.check.model.response;

import com.jfdimarzio.check.model.enumtype.MRFormType;

public class RequestJobListItem {
    private String JobUniqueID;
    private String JobDescription;
    private String RouteID;
    private String RouteName;
    private String Description;

    private String BeginDate;
    private String EndDate;
    private String ComplateRate;

    private String BeginTime;
    private String EndTime;

    private String RepairFormUniqueID;
    private String RepairFormType;
    private String RepairFormSbject;
    private String VHNO;
    private String EquipmentID;
    private String EquipmentName;
    private String PartDescription;
    private String Equipment;

    private String BeginDateString;
    private String EndDateString;

    private String MaintanenceFormUniqueID;
    private String FormType;
    private String Subject;

    private boolean IsRepairForm;
    private boolean IsSelected;
    private boolean IsExceptChecked;

    private MRFormType MRFormType;

    public String getSubject(){return Subject;}

    public MRFormType getMRFormType(){return MRFormType;}

    public void setMRFormType(MRFormType MRFormType){this.MRFormType=MRFormType;}

    public String getJobUniqueID(){return JobUniqueID;}

    public String getJobDescription(){return JobDescription;}

    public String getRouteID(){return RouteID;}

    public String getRouteName(){return RouteName;}

    public String getDescription(){return Description;}

    public String getBeginDate(){return BeginDate;}

    public String getEndDate(){return EndDate;}

    public String getComplateRate(){return ComplateRate;}

    public String getBeginTime(){return BeginTime;}

    public String getEndTime(){return EndTime;}

    public String getRepairFormUniqueID(){return RepairFormUniqueID;}

    public String getRepairFormType(){return RepairFormType;}

    public String getRepairFormSbject(){return RepairFormSbject;}

    public String getVHNO(){return VHNO;}

    public String getEquipmentID(){return EquipmentID;}

    public String getEquipmentName(){return EquipmentName;}

    public String getPartDescription(){return PartDescription;}

    public String getEquipment(){return Equipment;}

    public String getBeginDateString(){return BeginDateString;}

    public String getEndDateString(){return EndDateString;}

    public String getMaintanenceFormUniqueID(){return MaintanenceFormUniqueID;}

    public String getFormType(){return FormType;}

    public void setIsRepairForm(boolean isRepairForm){IsRepairForm=isRepairForm;}
    public boolean isRepairForm(){return IsRepairForm;}

    public void setIsSelected(boolean isSelected){IsSelected=isSelected;}
    public boolean isSelected(){return IsSelected;}

    public void setIsExceptChecked(boolean isExceptChecked){IsExceptChecked=isExceptChecked;}
    public boolean isExceptChecked(){return IsExceptChecked;}
}
