package com.jfdimarzio.check.model.response;

import java.util.ArrayList;

public class ServerGenericsResponse<T> extends ServerResponse {
    private ArrayList<T> Data;

    public ArrayList<T> getData() {
        return Data;
    }

    public void setData(ArrayList<T> data){Data=data;}
}
