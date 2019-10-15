package com.jfdimarzio.check.provider;

public class DBColumn {
    public interface AbnormalReason {

        /* Table name */
        public static final String TABLE_NAME = "AbnormalReason";

        /* 異常識別  */
        public static final String UniqueID = "UniqueID";

        /* 異常原因類別  */
        public static final String ID  = "ID";

        /* 異常描述  */
        public static final String Description = "Description";

    }


    /**
     * 異常原因處理對策對應資料檔
     */
    public interface AbnormalReasonHandlingMethod {

        /* Table name */
        public static final String TABLE_NAME = "AbnormalReasonHandlingMethod";

        /* 異常識別  */
        public static final String AbnormalReasonUniqueID = "AbnormalReasonUniqueID";

        /* 異常原因類別  */
        public static final String HandlingMethodUniqueID  = "HandlingMethodUniqueID";

    }


    /**
     * 檢查項目資料檔
     */
    public interface CheckItem {

        /* Table name */
        public static final String TABLE_NAME = "CheckItem";

        public static final String JobUniqueID = "JobUniqueID";

        /* 異常原因類別  */
        public static final String ControlPointUniqueID = "ControlPointUniqueID";

        public static final String EquipmentUniqueID = "EquipmentUniqueID";

        /* 部位 */
        public static final String EquipmentPartUniqueID = "PartUniqueID";

        /* 異常識別  */
        public static final String CheckItemUniqueID = "CheckItemUniqueID";

        public static final String ID = "ID";

        public static final String Description = "Description";

        public static final String IsFeelItem = "IsFeelItem";

        public static final String LowerLimit = "LowerLimit";

        public static final String UpperLimit = "UpperLimit";

        public static final String LowerAlertLimit = "LowerAlertLimit";

        public static final String UpperAlertLimit = "UpperAlertLimit";

        public static final String Unit = "Unit";

        public static final String Remark = "Remark";

        public static final String Seq = "Seq";

        /* 抄表 類型 */
        public static final String TextValueType = "TextValueType";
    }

    /**
     * 異常原因處理對策對應資料檔
     */
    public interface CheckItemAbnormalReason {

        /* Table name */
        public static final String TABLE_NAME = "CheckItemAbnormalReason";

        /* 異常識別  */
        public static final String CheckItemUniqueID = "CheckItemUniqueID";

        /* 異常原因類別  */
        public static final String AbnormalReasonUniqueID  = "AbnormalReasonUniqueID";

    }


    /**
     * 異常原因處理對策對應資料檔
     */
    public interface CheckItemFeelOption {

        /* Table name */
        public static final String TABLE_NAME = "CheckItemFeelOption";

        /* 異常識別  */
        public static final String UniqueID = "UniqueID";

        /* 異常原因類別  */
        public static final String CheckItemUniqueID  = "CheckItemUniqueID";

        public static final String Description = "Description";

        public static final String IsAbnormal = "IsAbnormal";

        public static final String Seq = "Seq";

    }


    /**
     * 管制點
     */
    public interface ControlPoint {

        /* Table name */
        public static final String TABLE_NAME = "ControlPoint";

        public static final String JobUniqueID = "JobUniqueID";

        public static final String ControlPointUniqueID  = "ControlPointUniqueID";

        public static final String ID = "ID";

        public static final String Description = "Description";

        public static final String IsFeelItemDefaultNormal = "IsFeelItemDefaultNormal";

        public static final String TagID = "TagID";

        public static final String Remark = "Remark";

        public static final String Seq  = "Seq";

    }

    /**
     * 緊急聯絡人
     */
    public interface EmgContact {

        /* Table name */
        public static final String TABLE_NAME = "EmgContact";

        public static final String JobUniqueID = "JobUniqueID";

        public static final String UniqueID  = "UniqueID";

        public static final String Title = "Title";

        public static final String Name = "Name";

    }

    /**
     * 緊急聯絡人明細
     */
    public interface EmgContactTel {

        /* Table name */
        public static final String TABLE_NAME = "EmgContactTel";

        public static final String EmgContactUniqueID = "EmgContactUniqueID";

        public static final String Seq  = "Seq";

        public static final String Tel = "Tel";


    }

    /**
     * 異常原因處理對策對應資料檔
     */
    public interface Equipment {

        /* Table name */
        public static final String TABLE_NAME = "Equipment";

        public static final String JobUniqueID = "JobUniqueID";

        public static final String ControlPointUniqueID  = "ControlPointUniqueID";

        public static final String EquipmentUniqueID = "EquipmentUniqueID";

        public static final String PartUniqueID = "PartUniqueID";

        public static final String ID = "ID";

        public static final String Name  = "Name";

        public static final String PartDescription  = "PartDescription";

        public static final String IsFeelItemDefaultNormal  = "IsFeelItemDefaultNormal";

        public static final String Seq  = "Seq";
    }

    /**
     * 異常原因處理對策對應資料檔
     */
    public interface EquipmentFile {

        /* Table name */
        public static final String TABLE_NAME = "EquipmentFile";

        public static final String UniqueID = "UniqueID";

        public static final String ParentUniqueID  = "ParentUniqueID";

        public static final String Type = "Type";

        public static final String Description = "Description";

        public static final String FileUniqueID  = "FileUniqueID";

        public static final String FileName  = "FileName";

        public static final String Extension  = "Extension";
    }

    /**
     * 異常原因處理對策對應資料檔
     */
    public interface EquipmentMaterial {

        /* Table name */
        public static final String TABLE_NAME = "EquipmentMaterial";

        public static final String EquipmentUniqueID = "EquipmentUniqueID";

        public static final String EquipmentPartUniqueID  = "PartUniqueID";

        public static final String MaterialUniqueID = "MaterialUniqueID";

        public static final String MaterialID = "MaterialID";

        public static final String MaterialName = "MaterialName";

        public static final String QTY = "QTY";

    }

    /**
     * 異常原因處理對策對應資料檔
     */
    public interface EquipmentSpec {

        /* Table name */
        public static final String TABLE_NAME = "EquipmentSpec";

        public static final String EquipmentUniqueID = "EquipmentUniqueID";

        public static final String Spec  = "Spec";

        public static final String Value = "Value";

    }


    /**
     * 異常原因處理對策對應資料檔
     */
    public interface HandlingMethod {

        /* Table name */
        public static final String TABLE_NAME = "HandlingMethod";

        public static final String UniqueID = "UniqueID";

        /* 異常識別  */
        public static final String ID = "ID";

        /* 異常原因類別  */
        public static final String Description  = "Description";

    }

    /**
     * 異常原因處理對策對應資料檔
     */
    public interface Job {

        /* Table name */
        public static final String TABLE_NAME = "Job";

        public static final String UniqueID = "UniqueID";

        public static final String Description  = "Description";

        public static final String Remark = "Remark";

        public static final String BeginTime = "BeginTime";

        public static final String EndTime = "EndTime";

        public static final String IsCheckBySeq = "IsCheckBySeq";

        public static final String IsShowPrevRecord = "IsShowPrevRecord";

        public static final String TimeMode = "TimeMode";

    }

    /**
     * 異常原因處理對策對應資料檔
     */
    public interface LastModifyTime {

        /* Table name */
        public static final String TABLE_NAME = "LastModifyTime";

        public static final String JobUniqueID = "JobUniqueID";

        public static final String VersionTime  = "VersionTime";

    }


    /**
     * 異常原因處理對策對應資料檔
     */
    public interface MaterialSpec {

        /* Table name */
        public static final String TABLE_NAME = "MaterialSpec";

        public static final String MaterialUniqueID = "MaterialUniqueID";

        public static final String Spec  = "Spec";

        public static final String Value  = "Value";

    }


    /**
     * 異常原因處理對策對應資料檔
     */
    public interface OverTimeReason {

        /* Table name */
        public static final String TABLE_NAME = "OverTimeReason";

        public static final String UniqueID = "UniqueID";

        public static final String ID  = "ID";

        public static final String Description  = "Description";

    }

    /**
     * 異常原因處理對策對應資料檔
     */
    public interface OverTimeRecord {

        /* Table name */
        public static final String TABLE_NAME = "OverTimeRecord";

        public static final String JobUniqueID = "JobUniqueID";

        public static final String ShouldDate  = "ShouldDate";

        public static final String ShouldTime  = "ShouldTime";

        public static final String ActualDate  = "ActualDate";

        public static final String ActualTime  = "ActualTime";

        public static final String UserID  = "UserID";

        public static final String Type  = "Type";

        public static final String OverTimeReasonUniqueID  = "OverTimeReasonUniqueID";

        public static final String OverTimeReasonRemark  = "OverTimeReasonRemark";

    }

    /**
     * 異常原因處理對策對應資料檔
     */
    public interface PrevCheckResult {

        /* Table name */
        public static final String TABLE_NAME = "PrevCheckResult";

        public static final String ControlPointUniqueID = "ControlPointUniqueID";

        public static final String EquipmentUniqueID  = "EquipmentUniqueID";

        public static final String CheckItemUniqueID  = "CheckItemUniqueID";

        public static final String LowerLimit  = "LowerLimit";

        public static final String UpperLimit  = "UpperLimit";

        public static final String Unit  = "Unit";

        public static final String CheckDate  = "CheckDate";

        public static final String CheckTime  = "CheckTime";

        public static final String Result  = "Result";

    }



    /**
     * 異常原因處理對策對應資料檔
     */
    public interface UnRFIDReason {

        /* Table name */
        public static final String TABLE_NAME = "UnRFIDReason";

        public static final String UniqueID = "UniqueID";

        public static final String ID  = "ID";

        public static final String Description  = "Description";

    }



    /**
     * 異常原因處理對策對應資料檔
     */
    public interface People {

        /* Table name */
        public static final String TABLE_NAME = "People";

        /* 異常識別  */
        public static final String PersonId = "PersonId";

        public static final String LoginId = "LoginId";

        public static final String Name = "Name";

        public static final String Password  = "Password";

        public static final String Title = "Title";

        public static final String Section = "Section";
    }





//    CREATE TABLE [RepairForm] (
//            [UniqueID] VARCHAR(40),
//    [VHNO] VARCHAR(9),
//    [RepairFormType] NVARCHAR(64),
//    [EquipmentID] NVARCHAR(32),
//    [EquipmentName] NVARCHAR(64),
//    [Description] NVARCHAR(256),
//    [Remark] NVARCHAR(256),
//    [UserID] NVARCHAR(32),
//    [UserName] NVARCHAR(32),
//    [BeginDate] VARCHAR(8),
//    [EndDate] VARCHAR(8));
    /**
     * 異常原因處理對策對應資料檔
     */
    public interface RepairForm {

        /* Table name */
        public static final String TABLE_NAME = "RepairForm";


        public static final String UniqueID = "UniqueID";


        public static final String VHNO  = "VHNO";

        public static final String RepairFormType = "RepairFormType";

        public static final String Equipment  = "Equipment";
        //public static final String EquipmentID  = "EquipmentID";

        //public static final String EquipmentName  = "EquipmentName";

        public static final String Description  = "Description";

        public static final String Subject  = "Subject";

//        public static final String Remark  = "Remark";
//
//        public static final String UserID  = "UserID";
//
//        public static final String UserName  = "UserName";

        public static final String BeginDate  = "BeginDate";

        public static final String EndDate  = "EndDate";
    }

    /**
     * 未巡檢原因
     */
    public interface UnPatrolReason {

        /* Table name */
        public static final String TABLE_NAME = "UnPatrolReason";

        public static final String UniqueID  = "UniqueID";

        public static final String ID  = "ID";

        public static final String Description  = "Description";

        public static final String UserID  = "UserID";
    }



    //endregion

    //region 上傳資料檔
    /**
     * 到位記錄資料檔
     */
    public interface ArriveRecord {

        /* Table name */
        public static final String TABLE_NAME = "ArriveRecord";

        public static final String UniqueID  = "UniqueID";

        public static final String JobUniqueID  = "JobUniqueID";

        public static final String ControlPointUniqueID  = "ControlPointUniqueID";

        public static final String ArriveDate = "ArriveDate";

        public static final String ArriveTime = "ArriveTime";

        public static final String UserID = "UserID";

        public static final String UnRFIDReasonUniqueID = "UnRFIDReasonUniqueID";

        public static final String UnRFIDReasonRemark = "UnRFIDReasonRemark";

    }

    /**
     * 到位記錄照片資料檔
     */
    public interface ArriveRecordPhoto {

        /* Table name */
        public static final String TABLE_NAME = "ArriveRecordPhoto";

        public static final String ArriveRecordUniqueID  = "ArriveRecordUniqueID";

        public static final String Seq = "Seq";

        public static final String FileName = "FileName";

        public static final String FilePath = "FilePath";

    }

    /**
     * 檢查結果資料檔
     */
    public interface CheckResult {

        /* Table name */
        public static final String TABLE_NAME = "CheckResult";

        public static final String UniqueID  = "UniqueID";

        public static final String ArriveRecordUniqueID  = "ArriveRecordUniqueID";

        public static final String JobUniqueID  = "JobUniqueID";

        public static final String ControlPointUniqueID  = "ControlPointUniqueID";

        public static final String EquipmentUniqueID = "EquipmentUniqueID";

        public static final String EquipmentPartUniqueID = "PartUniqueID";

        public static final String CheckItemUniqueID = "CheckItemUniqueID";

        public static final String CheckDate = "CheckDate";

        public static final String CheckTime = "CheckTime";

        public static final String FeelOptionUniqueID = "FeelOptionUniqueID";

        public static final String OtherMk = "OtherMk";

        public static final String Value = "Value";

        public static final String TextValue = "TextValue";

        public static final String Remark = "Remark";

    }

    /**
     * 檢查結果資料檔
     */
    public interface CheckResultAbnormalReason {

        /* Table name */
        public static final String TABLE_NAME = "CheckResultAbnormalReason";

        public static final String CheckResultUniqueID  = "CheckResultUniqueID";

        public static final String AbnormalReasonUniqueID  = "AbnormalReasonUniqueID";

        public static final String AbnormalReasonRemark  = "AbnormalReasonRemark";

    }

    /**
     * 檢查結果資料檔
     */
    public interface CheckResultHandlingMethod {

        /* Table name */
        public static final String TABLE_NAME = "CheckResultHandlingMethod";

        public static final String CheckResultUniqueID  = "CheckResultUniqueID";

        public static final String AbnormalReasonUniqueID  = "AbnormalReasonUniqueID";

        public static final String HandlingMethodUniqueID  = "HandlingMethodUniqueID";

        public static final String HandlingMethodRemark  = "HandlingMethodRemark";


    }

    /**
     * 檢查結果照片資料檔
     */
    public interface CheckResultPhoto {

        /* Table name */
        public static final String TABLE_NAME = "CheckResultPhoto";

        public static final String CheckResultUniqueID  = "CheckResultUniqueID";

        public static final String Seq = "Seq";

        public static final String FileName = "FileName";

        public static final String FilePath = "FilePath";
    }



    /**
     * 到位時間異常記錄檔　沒有了
     */
    public interface RouteUnFinishedRecord {

        /* Table name */
        public static final String TABLE_NAME = "RouteUnFinishedRecord";

        public static final String RouteUniqueID  = "RouteUniqueID";

        public static final String CheckDate = "CheckDate";

        public static final String UserID = "UserID";

        public static final String CheckItemCount = "CheckItemCount";

        public static final String CheckedItemCount = "CheckedItemCount";

        public static final String AbnormalReasonUniqueID = "AbnormalReasonUniqueID";

        public static final String AbnormalReasonRemark = "AbnormalReasonRemark";

    }

    /**
     * 上傳定義
     */
    public interface UploadDefine {

        /* Table name */
        public static final String TABLE_NAME = "UploadDefine";

        public static final String JobUniqueID  = "JobUniqueID";
    }

    /**
     * 未巡檢記錄
     */
    public interface UnPatrolRecord {

        /* Table name */
        public static final String TABLE_NAME = "UnPatrolRecord";

        public static final String JobUniqueID  = "JobUniqueID";

        public static final String UnPatrolReasonUniqueID  = "UnPatrolReasonUniqueID";

        public static final String UnPatrolReasonRemark  = "UnPatrolReasonRemark";

        public static final String UserID  = "UserID";
    }



    //endregion


    //region 定期保養

    /**
     *
     *  定保單
     CREATE TABLE [MForm] (
     [UniqueID] VARCHAR(40) CONSTRAINT [PK_MForm] UNIQUE,
     [VHNO] VARCHAR(10),
     [Description] NVARCHAR(256),
     [Remark] NVARCHAR(256),
     [EquipmentID] NVARCHAR(32),
     [EquipmentName] NVARCHAR(64),
     [PartDescription] NVARCHAR(64));

     */
    public interface MForm {

        /* Table name */
        public static final String TABLE_NAME = "MForm";

        public static final String UniqueID = "UniqueID";

        public static final String VHNO  = "VHNO";

        public static final String Description = "Description";

        /**
         * UI will not show
         */
        public static final String Remark  = "Remark";

        public static final String EquipmentID  = "EquipmentID";

        public static final String EquipmentName  = "EquipmentName";

        /**
         * Optional
         */
        public static final String PartDescription  = "PartDescription";

        public static final String BeginDate  = "BeginDate";

        public static final String EndDate  = "EndDate";

    }

    /**
     * MFormMaterial From for fill qty
     */
    public interface MFormMaterial {

        /* Table name */
        public static final String TABLE_NAME = "MFormMaterial";

        public static final String MFormUniqueID = "MFormUniqueID";

        public static final String MaterialUniqueID = "MaterialUniqueID";

        public static final String MaterialID  = "MaterialID";

        public static final String MaterialName = "MaterialName";

        public static final String Quantity = "Quantity";
    }

    public interface MFormMaterialResult {

        /* Table name */
        public static final String TABLE_NAME = "MFormMaterialResult";

        public static final String MFormUniqueID = "MFormUniqueID";

        public static final String MaterialUniqueID  = "MaterialUniqueID";

        public static final String Quantity = "Quantity";

        public static final String UserID = "UserID";

    }


    public interface MFormResult {

        /* Table name */
        public static final String TABLE_NAME = "MFormResult";

        public static final String MFormUniqueID = "MFormUniqueID";

        public static final String Remark  = "Remark";

        public static final String UserID = "UserID";

        public static final String MDate = "MDate";

        public static final String MTime = "MTime";

        public static final String IsNeedVerify = "IsNeedVerify";


    }



    /**
     *  檢查項目資料檔
     */
    public interface Standard {

        /* Table name */
        public static final String TABLE_NAME = "Standard";

        public static final String MFormUniqueID = "MFormUniqueID";

        /* 異常識別  */
        public static final String StandardUniqueID = "StandardUniqueID";

        public static final String ID = "ID";

        public static final String Description = "Description";

        public static final String IsFeelItem = "IsFeelItem";

        public static final String LowerLimit = "LowerLimit";

        public static final String UpperLimit = "UpperLimit";

        public static final String LowerAlertLimit = "LowerAlertLimit";

        public static final String UpperAlertLimit = "UpperAlertLimit";

        public static final String Unit = "Unit";

        public static final String Remark = "Remark";

        public static final String Seq = "Seq";
    }


    public interface StandardAbnormalReason {

        /* Table name */
        public static final String TABLE_NAME = "StandardAbnormalReason";

        /* 異常識別  */
        public static final String StandardUniqueID = "StandardUniqueID";

        /* 異常原因類別  */
        public static final String AbnormalReasonUniqueID  = "AbnormalReasonUniqueID";

    }

    /**
     * 定保 檢查結果資料檔
     */
    public interface MFormStandardResult {

        /* Table name */
        public static final String TABLE_NAME = "MFormStandardResult";

        public static final String MFormUniqueID  = "MFormUniqueID";

        public static final String StandardUniqueID = "StandardUniqueID";

        public static final String MDate = "MDate";

        public static final String MTime = "MTime";

        public static final String FeelOptionUniqueID = "FeelOptionUniqueID";

        public static final String Value = "Value";

        /* 2018/01/04 add */
        public static final String Remark = "Remark";

    }

    /**
     * 異常原因處理對策對應資料檔
     */
    public interface StandardFeelOption {

        /* Table name */
        public static final String TABLE_NAME = "StandardFeelOption";

        /* 異常識別  */
        public static final String UniqueID = "UniqueID";

        /* 異常原因類別  */
        public static final String StandardUniqueID  = "StandardUniqueID";

        public static final String Description = "Description";

        public static final String IsAbnormal = "IsAbnormal";

        public static final String Seq = "Seq";

    }

    /**
     * 檢查結果資料檔
     */
    public interface MFormStandardResultAbnormalReason {

        /* Table name */
        public static final String TABLE_NAME = "MFormStandardResultAbnormalReason";

        public static final String MFormUniqueID  = "MFormUniqueID";

        public static final String StandardUniqueID  = "StandardUniqueID";

        public static final String AbnormalReasonUniqueID  = "AbnormalReasonUniqueID";

        public static final String AbnormalReasonRemark  = "AbnormalReasonRemark";

    }

    /**
     * 檢查結果資料檔
     */
    public interface MFormStandardResultHandlingMethod {

        /* Table name */
        public static final String TABLE_NAME = "MFormStandardResultHandlingMethod";

        public static final String MFormUniqueID  = "MFormUniqueID";

        public static final String StandardUniqueID  = "StandardUniqueID";

        public static final String AbnormalReasonUniqueID  = "AbnormalReasonUniqueID";

        public static final String HandlingMethodUniqueID  = "HandlingMethodUniqueID";

        public static final String HandlingMethodRemark  = "HandlingMethodRemark";


    }

    /**
     * 檢查結果照片資料檔
     */
    public interface MFormStandardResultPhoto {

        /* Table name */
        public static final String TABLE_NAME = "MFormStandardResultPhoto";

        public static final String MFormUniqueID  = "MFormUniqueID";

        public static final String StandardUniqueID  = "StandardUniqueID";

        public static final String Seq = "Seq";

        public static final String FileName = "FileName";

        public static final String FilePath = "FilePath";
    }

    public interface MFormWorkingHour {

        /* Table name */
        public static final String TABLE_NAME = "MFormWorkingHour";

        public static final String MFormUniqueID = "MFormUniqueID";

        public static final String BeginDate  = "BeginDate";

        public static final String EndDate = "EndDate";

        public static final String WorkingHour = "WorkingHour";


    }


    //endregion

    //region RForm

    public interface RFormColumnOption {

        /* Table name */
        public static final String TABLE_NAME = "RFormColumnOption";

        public static final String UniqueID = "UniqueID";

        public static final String ColumnUniqueID = "ColumnUniqueID";

        public static final String Description  = "Description";

        public static final String Seq = "Seq";

    }

    public interface RFormColumnValue {

        /* Table name */
        public static final String TABLE_NAME = "RFormColumnValue";

        public static final String RFormUniqueID = "RFormUniqueID";

        public static final String ColumnUniqueID = "ColumnUniqueID";

        public static final String ColumnDescription  = "ColumnDescription";

        public static final String Seq = "Seq";

        public static final String ColumnOptionUniqueID = "ColumnOptionUniqueID";

        public static final String Value = "Value";

    }


    public interface RFormWorkingHour {

        /* Table name */
        public static final String TABLE_NAME = "RFormWorkingHour";

        public static final String RFormUniqueID = "RFormUniqueID";

        public static final String UserID = "UserID";

        public static final String BeginDate  = "BeginDate";

        public static final String EndDate = "EndDate";

        public static final String Hour = "Hour";
    }

    /**
     *     PRIMARY KEY([RFormUniqueID], [MaterialUniqueID]),
     CONSTRAINT [PK_RFormMaterial] UNIQUE([RFormUniqueID], [MaterialUniqueID]));
     */
    public interface RFormMaterial {

        /* Table name */
        public static final String TABLE_NAME = "RFormMaterial";

        public static final String RFormUniqueID = "RFormUniqueID";

        public static final String MaterialUniqueID  = "MaterialUniqueID";

        public static final String MaterialID  = "MaterialID";

        public static final String MaterialName = "MaterialName";

        public static final String Quantity = "Quantity";
    }

    public interface RFormMaterialResult {

        /* Table name */
        public static final String TABLE_NAME = "RFormMaterialResult";

        public static final String RFormUniqueID = "RFormUniqueID";

        public static final String MaterialUniqueID  = "MaterialUniqueID";

        public static final String Quantity = "Quantity";
    }


    //endregion
}
