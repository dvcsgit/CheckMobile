package com.jfdimarzio.check.util;

public class StringUtils {
    /**
     * 把 8碼 日期格式轉成漂亮一點的 20151124 => 2015/11/24
     * @param source
     * @return
     */
    public static String toBeautyDate(String source)
    {
        String result = source;
        try
        {
            if (source != null && source.length() == 8)
            {
                result = source.substring(0, 4) + "/" + source.substring(4, 6) + "/" + source.substring(6, 8);
            }
        }
        catch (Exception ex)
        {

        }
        return result;
    }

    /**
     * 把 4碼 OR 6碼 日期格式轉成漂亮一點的 160319 => 16:03 ; 1603 => 16:03
     * 忽略秒
     * TODO 目前只支持 6碼
     * @param source
     * @return
     */
    public static String toBeautyTime(String source)
    {
        String result = source;
        try {
            if (source != null && (source.length() == 6 ||source.length() == 4))
            {

                result = source.substring(0,2) +":"+ source.substring(2,4);
            }

            if(source == null)
            {
                result = "N/A";
            }
        } catch (Exception ex) {

        }

        return result;
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < (long)unit) {
            return bytes + " B";
        } else {
            int exp = (int)(Math.log((double)bytes) / Math.log((double)unit));
            String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
            return String.format("%.1f %sB", (double)bytes / Math.pow((double)unit, (double)exp), pre);
        }
    }
}
