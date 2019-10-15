package com.jfdimarzio.check.model.response;

import java.util.ArrayList;

public class DownloadFormModel {

    private String CheckDate;
    private ArrayList<DownloadParameter> Parameters;
    public DownloadFormModel(String checkDate,ArrayList<DownloadParameter> parameters){
        CheckDate=checkDate;
        Parameters=parameters;
    }
}
