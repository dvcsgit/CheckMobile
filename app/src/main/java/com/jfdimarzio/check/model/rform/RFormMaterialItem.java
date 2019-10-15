package com.jfdimarzio.check.model.rform;

import java.io.Serializable;

public class RFormMaterialItem implements Serializable {

    private  String RFormUniqueID;
    private  String MaterialUniqueID;
    private  String MaterialID;
    private  int Quantity;
    private  int ResultQuantity;

    private String MaterialName;
    private boolean IsChanged;

    public RFormMaterialItem(String RFormUniqueID, String materialUniqueID, String materialID, String materialName, int quantity){
        this.RFormUniqueID=RFormUniqueID;
        MaterialUniqueID=materialUniqueID;
        MaterialID=materialID;
        Quantity=quantity;
        ResultQuantity=-1;
    }

    public String getRFormUniqueID() {
        return RFormUniqueID;
    }

    public String getMaterialUniqueID() {
        return MaterialUniqueID;
    }

    public String getMaterialID() {
        return MaterialID;
    }

    public String getMaterialName() {
        return MaterialName;
    }

    public int getQuantity() {
        return Quantity;
    }

    public int getResultQuantity() {
        return ResultQuantity;
    }

    public void setResultQuantity(int resultQuantity) {
        ResultQuantity = resultQuantity;
    }

    public boolean isChanged() {
        return IsChanged;
    }

    public void setChanged(boolean changed) {
        IsChanged = changed;
    }
}
