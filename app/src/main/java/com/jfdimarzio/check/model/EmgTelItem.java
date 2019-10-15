package com.jfdimarzio.check.model;

import java.util.ArrayList;

public class EmgTelItem {
    private String Title;
    private String Name;
    private ArrayList<String> TelList;

    public EmgTelItem(String title, String name) {
        Title = title;
        Name = name;
        this.TelList = new ArrayList<>();
    }

    public void setTelList(ArrayList<String> telList) {
        TelList = telList;
    }

    public String getTitle() {
        return Title;
    }

    public String getName() {
        return Name;
    }

    public ArrayList<String> getTelList() {
        return TelList;
    }
}
