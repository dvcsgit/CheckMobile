package com.jfdimarzio.check.util;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.jfdimarzio.check.R;

import java.util.HashMap;

public class AppUtils {

    public static HashMap<Integer,Integer> getColorMap(Context context){
        HashMap<Integer,Integer> colorMap=new HashMap<>();
        colorMap.put(0, ContextCompat.getColor(context, R.color.tag_0));
        colorMap.put(1,ContextCompat.getColor(context,R.color.tag_1));
        colorMap.put(2,ContextCompat.getColor(context,R.color.tag_2));
        return colorMap;
    }

    public static boolean convertStringToBool(String source) {
        boolean result = false;
        if (source != null && source.equals("Y"))
        {
            result = true;
        }

        return result;
    }
}
