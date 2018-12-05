package com.gki.managerment.http;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class AppContants {
    public static String NameSpace = "http://tempuri.org/";                    //http://192.168.0.101:9876/services
    public final static String Version = "1.09";                                 //当前程序的版本号
    public final static String DEFAULT_SERVICE_PATH = "http://10.1.1.65:8080/Services/";
    public static String DEFAULT_IMAGE_PATH = "http://10.1.1.65:8080/UploadFiles/";
    public static String ServicePath = DEFAULT_SERVICE_PATH;    //http://120.25.75.151:8888/UserSV.asmx
    public static String ImagePath = DEFAULT_IMAGE_PATH;

    public static String packageVersionName(Context context) {
        PackageManager manager = context.getPackageManager();
        String name = null;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return name;
    }
}