package com.jfdimarzio.check.model;

import java.io.Serializable;

public class ApiFormInput implements Serializable {
    private String UserID;

    private String IMEI;

    private String MacAddress;

    private String AppVersion;

    public ApiFormInput(String userID, String IMEI, String macAddress, String appVersion) {
        UserID = userID;
        this.IMEI = IMEI;
        MacAddress = macAddress;
        AppVersion = appVersion;
    }
}
