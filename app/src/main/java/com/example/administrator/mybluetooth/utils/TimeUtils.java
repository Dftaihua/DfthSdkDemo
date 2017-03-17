package com.example.administrator.mybluetooth.utils;


import com.dfth.sdk.Others.Utils.Logger.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2015/12/11.
 */
public class TimeUtils {
    public static final String STANARD_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String FILE_TEMP_FORMAT = "yyyy_MM_dd_HH_mm_ss";
    public static final String ECG_REPORT_FORMAT = "yyyy.MM.dd HH:mm";

    public static final long MINUTE = 60 * 1000L;
    public static final long MILLIO_TIME = 1;
    public static final long SECOND_TIME = 1000 * MILLIO_TIME;
    public static final long MINUTE_TIME = 60 * SECOND_TIME;
    public static final long HORE_TIME = 60 * MINUTE_TIME;
    public static final long DAY_TIME = 24 * HORE_TIME;

    public static long getTimeByTimeStr(String date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        try {
            Date date1 = dateFormat.parse(date);
            return date1.getTime();
        } catch (ParseException e) {
            Logger.e(e, null);
            return -1;
        }
    }

    public static String getTime(long time, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date(time);
        return dateFormat.format(date);
    }

    public static boolean isToday(long time) {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_YEAR);
        calendar.setTimeInMillis(time);
        return calendar.get(Calendar.DAY_OF_YEAR) == day;
    }

    public static boolean isSameDay(long time, long time1) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(time1);
        return calendar.get(Calendar.YEAR) == calendar1.get(Calendar.YEAR)
                && calendar.get(Calendar.MONTH) == calendar1.get(Calendar.MONTH)
                && calendar.get(Calendar.DAY_OF_MONTH) == calendar1.get(Calendar.DAY_OF_MONTH);
    }

    public static long getECGFileTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2015 - 1900);
        calendar.set(Calendar.MONTH, 11);
        calendar.set(Calendar.DAY_OF_MONTH, 30);
        return calendar.getTimeInMillis();
    }

    public static String getHAMAS(long m) {
        int s = (int) (m / 1000);
        int min = s / 60;
        int hour = min / 60;
        hour %= 24;
        min %= 60;
        s %= 60;
        String ms = min >= 10 ? String.valueOf(min) : "0" + min;
        String ss = s >= 10 ? String.valueOf(s) : "0" + s;
        String hs = hour >= 10 ? String.valueOf(hour) : "0" + hour;
        return hs + ":" + ms + ":" + ss;
    }

    //获取00h00m00s格式的时间
    public static String getHMS(long m) {
        int s = (int) (m / 1000);
        int min = s / 60;
        int hour = min / 60;
        hour %= 24;
        min %= 60;
        s %= 60;
        String ms = String.valueOf(min);
        String ss = String.valueOf(s);
        String hs = String.valueOf(hour);
        if (hs.equals("0") && ms.equals("0")) {
            return ss + "s";
        } else if (hs.equals("0")) {
            return ms + "m " + ss + "s";
        } else {
            return hs + "h " + ms + "m " + ss + "s";
        }
//        return hs + "h " + ms + "m " + ss + "s";
    }

    public static int getAge(long time){
        Calendar calendar = Calendar.getInstance();
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(time);
        return calendar.get(Calendar.YEAR) - calendar1.get(Calendar.YEAR);
    }

    /**
     * 计算年龄
     * @param birth yyyy-MM-dd
     * @return
     */
    public static int calcAge(Date birth){
        Date nowDate= new Date();
        Calendar flightCal= Calendar.getInstance();
        flightCal.setTime(nowDate);
        Calendar birthCal= Calendar.getInstance();
        birthCal.setTime(birth);

        int y= flightCal.get(Calendar.YEAR)-birthCal.get(Calendar.YEAR);
        int m= flightCal.get(Calendar.MONTH)-birthCal.get(Calendar.MONTH);
        int d= flightCal.get(Calendar.DATE)-birthCal.get(Calendar.DATE);
        if(y<=0){
            y=0;
            return y;
        }
        if(m<0){
            y--;
        }
        else if(m<=0&&d<0){
            y--;
        }

        return y;
    }

}
