package com.jfdimarzio.check.model;

public class DoneTotalItem {
    private int Total;
    private int Done;

    public DoneTotalItem(int total, int done) {
        Total = total;
        Done = done;
    }

    public int getTotal() {
        return Total;
    }

    public int getDone() {
        return Done;
    }
}
