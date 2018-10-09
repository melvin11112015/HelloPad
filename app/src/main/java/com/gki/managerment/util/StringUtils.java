package com.gki.managerment.util;


import java.text.DecimalFormat;

public class StringUtils {
    /**
     * 判断字符串是否有值，如果为null或者是空字符串或者只有空格或者为"null"字符串，则返回true，否则则返回false
     */
    static DecimalFormat formatNumber = new DecimalFormat("0.00");
    public static boolean isEmpty(String value) {
        if (value != null && !"".equalsIgnoreCase(value.trim())
                && !"null".equalsIgnoreCase(value.trim())) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isNotEmpty(String value) {
        if (value != null && !"".equalsIgnoreCase(value.trim())
                && !"null".equalsIgnoreCase(value.trim())) {
            return true;
        } else {
            return false;
        }
    }

    public static float ToFloat(String value) {
        float result = 0;
        try {
            result = Float.parseFloat(value);
        } catch (NumberFormatException e) {
            //...
        }
        return result;
    }

    public static String FormatNumber(float aNumber)
    {
        return formatNumber.format(aNumber);
    }
}