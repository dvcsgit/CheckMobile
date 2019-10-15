package com.jfdimarzio.check.model;

import com.jfdimarzio.check.util.consts.StringConst;

import java.io.Serializable;

public class UnPatrolRecordItem implements Serializable {
    private String JobUniqueID;
    private String UnPatrolReasonUniqueID;
    private String UnPatrolReasonRemark;
    private String UserID;

    private String UIUnPatrolReason;

    public UnPatrolRecordItem(String jobUniqueID, String unPatrolReasonUniqueID, String unPatrolReasonRemark, String userID, String UIUnPatrolReason) {
        JobUniqueID = jobUniqueID;
        UnPatrolReasonUniqueID = unPatrolReasonUniqueID;
        UnPatrolReasonRemark = unPatrolReasonRemark;
        UserID = userID;
        this.UIUnPatrolReason = UIUnPatrolReason;
    }

    public String getJobUniqueID() {
        return JobUniqueID;
    }

    public String getUnPatrolReasonUniqueID() {
        return UnPatrolReasonUniqueID;
    }

    public String getUnPatrolReasonRemark() {
        return UnPatrolReasonRemark;
    }

    public String getUserID() {
        return UserID;
    }

    public String getUIUnPatrolReason() {
        return UIUnPatrolReason;
    }

    /**
     * 顯示方法
     * @return
     */
    public String getDisplayReason()
    {
        String result = this.UIUnPatrolReason;

        if(this.UnPatrolReasonUniqueID.equals(StringConst.OTHER))
        {
            result = result + " - " + this.getUnPatrolReasonRemark();
        }

        return result;
    }
}
