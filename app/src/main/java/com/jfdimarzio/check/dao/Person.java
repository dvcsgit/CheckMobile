package com.jfdimarzio.check.dao;

public class Person {
    private String PersonId;
    private String Title;
    private String Name;
    private String Password;
    private String PId;
    private String Shift;

    public Person(String PersonId, String title, String name, String password, String PId,String Shift){
        this.PersonId=PersonId;
        this.Title=title;
        this.Name= name;
        this.Password=password;
        this.PId=PId;
        this.Shift=Shift;
    }

    public String getPersonId(){return PersonId;}

    public String getTitle(){return Title;}

    public String getName(){return Name;}

    public String getPassword(){return Password;}

    public String getPId(){return PId;}

    public String getShift() {
        return Shift;
    }
}
