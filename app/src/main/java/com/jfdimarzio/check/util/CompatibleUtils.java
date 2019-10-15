package com.jfdimarzio.check.util;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

public class CompatibleUtils {
    public static void setViewBackground(View btn, Drawable drawable)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            btn.setBackground(drawable);
        }else
        {
            btn.setBackgroundDrawable(drawable);
        }
    }
}
