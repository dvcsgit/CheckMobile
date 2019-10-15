package com.jfdimarzio.check.model;

public class UserSession {
    private String ID;
    private String Name;

    public UserSession(String ID,String name){
        this.ID=ID;
        this.Name=name;
    }

    public String getID(){
        return ID;
    }

    public String getName(){
        return Name;
    }
}
