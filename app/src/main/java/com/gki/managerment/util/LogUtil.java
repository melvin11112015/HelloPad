package com.gki.managerment.util;

import android.util.Log;

/**
 * 日志输出的工具类
 *
 * @author Administrator
 */
public class LogUtil {
    private static boolean is_debug = true;  //开发阶段为true ，发布该为false

    public static void i(String tag, String msg) {
        if (is_debug) {
            Log.i(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (is_debug) {
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (is_debug) {
            Log.d(tag, msg);
        }
    }

}
