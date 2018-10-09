package com.gki.managerment.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * Utils - 日期处理
 *
 * @author Mounate Yan。
 * @version 1.0
 */
public class DateUtils {

    /**
     * 将日期类型转换为字符串
     *
     * @param date    日期
     * @param xFormat 格式
     * @return
     */
    public static String getFormatDate(Date date, String xFormat) {
        date = date == null ? new Date() : date;
        xFormat = StringUtils.isNotEmpty(xFormat) == true ? xFormat : DateFormat.FULL_DATE_FORMAT;
        SimpleDateFormat sdf = new SimpleDateFormat(xFormat);
        return sdf.format(date);
    }

    /**
     * 比较日期大小
     *
     * @param dateX
     * @param dateY
     * @return x < y return [-1];
     * x = y return [0] ;
     * x > y return [1] ;
     */
    public static int compareDate(Date dateX, Date dateY) {
        return dateX.compareTo(dateY);
    }

    /**
     * 将日期字符串转换为日期格式类型
     *
     * @param xDate
     * @param xFormat 为NULL则转换如：2012-06-25
     * @return
     */
    public static Date parseString2Date(String xDate, String xFormat) {
        while (!isNotDate(xDate)) {
            xFormat = StringUtils.isNotEmpty(xFormat) == true ? xFormat : DateFormat.PART_DATE_FORMAT;
            SimpleDateFormat sdf = new SimpleDateFormat(xFormat);
            Date date = null;
            try {
                date = sdf.parse(xDate);
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
            return date;
        }
        return null;
    }

    /**
     * 判断需要转换类型的日期字符串是否符合格式要求
     *
     * @param xDate
     * @param xFormat 可以为NULL
     * @return
     */
    public static boolean isNotDate(String xDate) {
        SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.PART_DATE_FORMAT);
        try {
            if (StringUtils.isEmpty(xDate)) {
                return true;
            }
            sdf.parse(xDate);
            return false;
        } catch (ParseException e) {
            e.printStackTrace();
            return true;
        }
    }

    public static boolean isDate(String xDate) {
        return !isDate(xDate);
    }

    /**
     * 获取俩个日期之间相差天数
     *
     * @param dateX
     * @param dateY
     * @return
     */
    public static int getDiffDays(Date dateX, Date dateY) {
        if ((dateX == null) || (dateY == null)) {
            return 0;
        }

        int dayX = (int) (dateX.getTime() / (60 * 60 * 1000 * 24));
        int dayY = (int) (dateY.getTime() / (60 * 60 * 1000 * 24));

        return dayX > dayY ? dayX - dayY : dayY - dayX;
    }

    public static int getExpectedWeeks(Date dateX, Date dateY) {
        int days = getDiffDays(dateX, dateY);
        int weeks = (280 - days) / 7;
        return (280 - days) % 7 > 0 ? weeks + 1 : weeks;
    }

    public static int getExpectedMonths(Date dateX, Date dateY) {
        int days = getDiffDays(dateX, dateY);
        int months = (280 - days) / 30;
        return months;
    }

    public static String getExpectedWeekString(Date dateX, Date dateY) {
        int days = getDiffDays(dateX, dateY);
        int weeks = (280 - days) / 7;
        return weeks + "周" + (280 - days) % 7 + "天";
    }

    /**
     * 获取传值日期之后几天的日期并转换为字符串类型
     *
     * @param date    需要转换的日期 date 可以为NULL 此条件下则获取当前日期
     * @param after   天数
     * @param xFormat 转换字符串类型 (可以为NULL)
     * @return
     */
    public static String getAfterCountDate(Date date, int after, String xFormat) {
        date = date == null ? new Date() : date;
        xFormat = StringUtils.isNotEmpty(xFormat) == true ? xFormat : DateFormat.PART_DATE_FORMAT;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, after);
        return getFormatDate(calendar.getTime(), xFormat);
    }

    /**
     * 获取日期的参数 如：年 , 月 , 日 , 星期几
     *
     * @param xDate   日期 可以为日期格式,可以是字符串格式; 为NULL或者其他格式时都判定为当前日期
     * @param xFormat 年 yyyy 月 MM 日 dd 星期 week ;其他条件下都返回0
     */
    public static int getDateTimeParam(Object xDate, String xFormat) {
        xDate = xDate == null ? new Date() : xDate;
        Date date = null;
        if (xDate instanceof String) {
            date = parseString2Date(xDate.toString(), null);
        } else if (xDate instanceof Date) {
            date = (Date) xDate;
        } else {
            date = new Date();
        }
        date = date == null ? new Date() : date;
        if (StringUtils.isNotEmpty(xFormat)
                && (xFormat.equals(DateFormat.YEAR_DATE_FORMAT)
                || xFormat.equals(DateFormat.MONTH_DATE_FORMAT)
                || xFormat.equals(DateFormat.DAY_DATE_FORMAT))) {
            return Integer.parseInt(getFormatDate(date, xFormat));
        } else if (StringUtils.isNotEmpty(xFormat)
                && (DateFormat.WEEK_DATE_FORMAT.equals(xFormat))) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int week = cal.get(java.util.Calendar.DAY_OF_WEEK) - 1 == 0 ?
                    7 : cal.get(java.util.Calendar.DAY_OF_WEEK) - 1;
            return week;
        } else {
            return 0;
        }
    }

    /**
     * 获取星期字符串
     *
     * @param xDate
     * @return
     */
    public static String getWeekString(Object xDate) {
        int week = getDateTimeParam(xDate, DateFormat.WEEK_DATE_FORMAT);
        switch (week) {
            case 1:
                return "星期一";
            case 2:
                return "星期二";
            case 3:
                return "星期三";
            case 4:
                return "星期四";
            case 5:
                return "星期五";
            case 6:
                return "星期六";
            case 7:
                return "星期日";
            default:
                return "";
        }
    }

    /**
     * 当前日历，这里用中国时间表示
     *
     * @return 以当地时区表示的系统当前日历
     */
    public static Calendar getCalendar() {
        return Calendar.getInstance();
    }

    /**
     * 指定毫秒数表示的日历
     *
     * @param millis 毫秒数
     * @return 指定毫秒数表示的日历
     */
    public static Calendar getCalendar(long millis) {
        Calendar cal = Calendar.getInstance();
        // --------------------cal.setTimeInMillis(millis);
        cal.setTime(new Date(millis));
        return cal;
    }

    /**
     * 获取时间字符串
     */
    public static String getDataString(SimpleDateFormat formatstr) {
        return formatstr.format(getCalendar().getTime());
    }

    public static String getDataString(String formatStr) {
        SimpleDateFormat sdFormat = new SimpleDateFormat(formatStr);
        return getDataString(sdFormat);
    }

    public static String getFormatDate1(Date date, String xFormat) {
        if (date == null)
            return "";
        xFormat = StringUtils.isNotEmpty(xFormat) == true ? xFormat : DateFormat.FULL_DATE_FORMAT;
        SimpleDateFormat sdf = new SimpleDateFormat(xFormat);
        return sdf.format(date);
    }

    /**
     * 把2013-12-16转成 16Mon-December, 2013
     *
     * @param date
     * @param xFormat
     * @return
     */
    public static String getFormatDateEn(String date, String xFormat) {
        if (date == null || "".equals(date))
            return "";
        xFormat = StringUtils.isNotEmpty(xFormat) == true ? xFormat : DateFormat.PART_DATE_FORMAT;
        Date parseString2Date = parseString2Date(date, xFormat);
        SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.PART_DATE_FORMAT_EN, Locale.ENGLISH);
        return sdf.format(parseString2Date);
    }

    /**
     * 根据用户生日计算年龄
     */
    public static int getAgeByBirthday(Date birthday) {
        Calendar cal = Calendar.getInstance();

        if (cal.before(birthday)) {
            throw new IllegalArgumentException(
                    "The birthDay is before Now.It's unbelievable!");
        }

        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH) + 1;
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);

        cal.setTime(birthday);
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH) + 1;
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

        int age = yearNow - yearBirth;

        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                // monthNow==monthBirth
                if (dayOfMonthNow < dayOfMonthBirth) {
                    age--;
                }
            } else {
                // monthNow>monthBirth
                age--;
            }
        }
        return age;
    }

    public class DateFormat {
        public static final String FULL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
        public static final String TIME_FORMAT = "HH:mm:ss";
        public static final String FULL_DATE_FORMAT_CN = "yyyy年MM月dd日 HH时mm分ss秒";
        public static final String PART_DATE_FORMAT = "yyyy-MM-dd";
        public static final String PART_DATE_FORMAT_SHORT = "yyyyMMdd";
        public static final String PART_DATE_FORMAT_LONG = "yyyyMMddHHmmss";
        public static final String PART_DATE_FORMAT_CN = "yyyy年MM月dd日";
        public static final String SHORT_DATE_FORMAT_CN = "yyyy年MM月";
        public static final String PART_DATE_FORMAT_EN = "ddE-MMMMM, yyyy";
        public static final String YEAR_DATE_FORMAT = "yyyy";
        public static final String MONTH_DATE_FORMAT = "MM";
        public static final String DAY_DATE_FORMAT = "dd";
        public static final String WEEK_DATE_FORMAT = "week";
        public static final String FULL_DATE_FORMAT_SS = "yyyyMMddHHmmssSS";

    }

    public static float GetDiffMinutes(Date dtBegin, Date dtEnd) {
        long aDiff = dtEnd.getTime() - dtBegin.getTime();
        float fTime = (float)(aDiff/60000);
        return fTime;
    }
}
