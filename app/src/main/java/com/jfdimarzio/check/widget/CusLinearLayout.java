package com.jfdimarzio.check.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class CusLinearLayout extends LinearLayout {
    private static final String TAG = CusLinearLayout.class.getName();

    public CusLinearLayout(Context context) {
        super(context);
    }

    public CusLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public float getXFraction() {
        int width = this.getWidth();
        return width == 0 ? 0.0F : this.getX() / (float)width;
    }

    public void setXFraction(float xFraction) {
        int width = this.getWidth();
        this.setX(width > 0 ? xFraction * (float)width : 0.0F);
    }
}
