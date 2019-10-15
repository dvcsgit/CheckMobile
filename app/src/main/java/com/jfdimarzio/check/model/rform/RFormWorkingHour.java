package com.jfdimarzio.check.model.rform;

import java.io.Serializable;

public class RFormWorkingHour implements Serializable {
    private String UserID;
    private String BeginDate;
    private String EndDate;
    private Float WorkingHour;

    public RFormWorkingHour(String userID, String beginDate, String endDate, Float workingHour) {
        UserID = userID;
        BeginDate = beginDate;
        EndDate = endDate;
        WorkingHour = workingHour;
    }

    public String getBeginDate() {
        return BeginDate;
    }

    public void setBeginDate(String beginDate) {
        BeginDate = beginDate;
    }

    public String getEndDate() {
        return EndDate;
    }

    public void setEndDate(String endDate) {
        EndDate = endDate;
    }

    public Float getWorkingHour() {
        return WorkingHour;
    }

    public void setWorkingHour(Float workingHour) {
        WorkingHour = workingHour;
    }

    public String getUserID() {
        return UserID;
    }
}
