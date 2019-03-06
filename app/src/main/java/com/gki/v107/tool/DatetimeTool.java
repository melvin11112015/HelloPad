package com.gki.v107.tool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DatetimeTool {

    public static final int TYPE_DATE = 1;
    public static final int TYPE_TIME = 2;
    public static final int TYPE_DATETIME = 5;
    public static final int TYPE_ODATA = 3;

    public static final boolean DEFAULT_ADJUST_TIMEZONE = false;

    /**
     * 时区调整和时间格式化
     *
     * @param odataDatetimeStr  从接口接收的时间
     * @param requestType       转换类型
     * @param canAdjustTimeZone 是否调整时区
     * @return 根据转换类型返回的格式化时间
     */
    public static String convertOdataTimezone(String odataDatetimeStr, int requestType, boolean canAdjustTimeZone){

        odataDatetimeStr = odataDatetimeStr.replace("T", " ");
        odataDatetimeStr = odataDatetimeStr.replace("Z", "");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone(canAdjustTimeZone?"GMT+00:00":"GMT+08:00"));
        Date date = null;
        try {
            date = sdf.parse(odataDatetimeStr);
        } catch (ParseException e) {
            e.printStackTrace();
            date = new Date();
        }

        SimpleDateFormat sdfDat = new SimpleDateFormat("yyyy-MM-dd");
        sdfDat.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        String datStr = sdfDat.format(date);

        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
        sdfTime.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        String timeStr = sdfTime.format(date);

        sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        String normalDatetime = sdf.format(date);

        String resultDatetimeStr = datStr + 'T' + timeStr;
        switch (requestType) {
            case TYPE_DATE:
                return datStr;
            case TYPE_TIME:
                return timeStr;
            default:
            case TYPE_DATETIME:
                return normalDatetime;
            case TYPE_ODATA:
                return resultDatetimeStr;
        }

    }

    public static String getCurrentOdataDatetime(){
        Date date = new Date();
        SimpleDateFormat sdfDat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
        return sdfDat.format(date)+"T"+sdfTime.format(date);
    }

    public static String getCurrentOdataDate() {
        Date date = new Date();
        SimpleDateFormat sdfDat = new SimpleDateFormat("yyyy-MM-dd");
        return sdfDat.format(date);
    }
}
