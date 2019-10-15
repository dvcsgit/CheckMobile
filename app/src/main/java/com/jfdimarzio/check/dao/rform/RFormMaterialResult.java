package com.jfdimarzio.check.dao.rform;

public class RFormMaterialResult {
    private String RFormUniqueID;
    private String MaterialUniqueID;
    private int Quantity;

    public RFormMaterialResult(String RFormUniqueID,String MaterialUniqueID,int Quantity){
        this.RFormUniqueID=RFormUniqueID;
        this.MaterialUniqueID=MaterialUniqueID;
        this.Quantity=Quantity;
    }

    public String getRFormUniqueID(){
        return RFormUniqueID;
    }

    public String getMaterialUniqueID(){
        return MaterialUniqueID;
    }

    public int getQuantity(){
        return Quantity;
    }
}
