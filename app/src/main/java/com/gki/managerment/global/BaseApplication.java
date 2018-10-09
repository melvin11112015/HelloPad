package com.gki.managerment.global;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

/**
 * 自定义Application
 * <p/>
 * 注意:需要在清单文件中声明使用当前类
 * <p/>
 * <application
 * android:name="com.itcast.googleplay01.global.BaseApplication"
 * >
 *
 * @author Kevin
 * @date 2015-9-24
 */
public class BaseApplication extends Application {

    private static Context context;
    private static Handler handler;
    private static int mainThreadId;

    public static Context getContext() {
        return context;
    }

    public static Handler getHandler() {
        return handler;
    }

    public static int getMainThreadId() {
        return mainThreadId;
    }

    // 当应用创建的时候,调用此方法
    @Override
    public void onCreate() {
        super.onCreate();

        //1. 获取Context
        context = getApplicationContext();

        //2. 创建handler
        handler = new Handler();

        //3. 获取主线程id
        mainThreadId = android.os.Process.myTid();
    }

}
