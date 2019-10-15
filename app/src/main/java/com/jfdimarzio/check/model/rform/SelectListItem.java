package com.jfdimarzio.check.model.rform;

import java.io.Serializable;

public class SelectListItem implements Serializable {
    private boolean IsSelected;
    private String Text;
    private String Value;

    public SelectListItem(String text, String value) {
        this.Text = text;
        this.Value = value;
    }

    public String getText() {
        return this.Text;
    }

    public String getValue() {
        return this.Value;
    }

    public boolean isSelected() {
        return this.IsSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.IsSelected = isSelected;
    }

    public String toString() {
        return this.Text;
    }
}
