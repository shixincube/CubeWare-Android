package com.common.utils.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 显示友好的日期时间工具类
 *
 * @author PengZhenjin
 * @date 2016/7/4
 */
public class FriendlyDateUtil {

    // 年+月份+日期+小时+分钟
    private static String defDateTimeFormat = "yyyy/MM/dd";

    // 月份+日期+小时+分钟
    private static String nowDateTimeFormat = "MM月dd日 HH:mm";

    private static String nowDateFormat = "MM月dd日";

    private static String nowDateFormat2 = "MM/dd";

    private static String defDateFormat = "yyyy年MM月dd日";

    // 小时+分钟
    private static String timeFormat = " HH:mm";

    /**
     * 友好的时间格式
     *
     * @param dateTime
     *
     * @return
     */
    public static String friendlyTime(long dateTime) {
        Date date = new Date(dateTime);
        return friendlyTime(date);
    }

    /**
     * 最近消息列表的时间格式
     *
     * @param dateTime
     *
     * @return
     */
    public static String recentTime(long dateTime) {
        Date date = new Date(dateTime);
        return recentTime(date);
    }

    /**
     * 最近消息列表的时间显示
     *
     * @param paramDate 需要转换的日期
     *
     * @return 格式化后的时间字符串
     */
    public static String recentTime(Date paramDate) {
        //boolean is24 = DateUtil.get24HourMode();
        Calendar nowCalendar = Calendar.getInstance();  // 现在时间
        nowCalendar.setTime(new Date());
        int nowYear = nowCalendar.get(Calendar.YEAR);
        int nowMonth = nowCalendar.get(Calendar.MONTH) + 1;
        int nowDay = nowCalendar.get(Calendar.DAY_OF_MONTH);

        Calendar paramCalendar = Calendar.getInstance();  // 参数时间
        paramCalendar.setTime(paramDate);
        int paramYear = paramCalendar.get(Calendar.YEAR);
        int paramMonth = paramCalendar.get(Calendar.MONTH) + 1;
        int paramDay = paramCalendar.get(Calendar.DAY_OF_MONTH);
        int diffDay = nowDay - paramDay;  // 相差的天数
        if (nowYear == paramYear) {
            if (nowMonth == paramMonth) {
                if (diffDay == 0) {
                    //if (is24) {
                        return formatDate(timeFormat, paramCalendar.getTime());
                    //}
                    //return DateUtil.getTodayTimeBucket(paramDate);
                }
                else if (diffDay == 1) {
                    return "昨天";
                }
                else if (diffDay >= 2) {
                    return formatDate(defDateTimeFormat, paramCalendar.getTime());
                }
            }
            else if (nowMonth - paramMonth == 1) {
                if (paramMonth == 2) {
                    if (diffDay == -28 || diffDay == -27) {
                        return "昨天";
                    }
                }
                else {
                    if (diffDay == -30 || diffDay == -29) {
                        return "昨天";
                    }
                }
            }
            return formatDate(defDateTimeFormat, paramCalendar.getTime());
        }
        return formatDate(defDateTimeFormat, paramCalendar.getTime());
    }

    /**
     * 需求描述
     * 日期显示规则：
     * 1.今天的显示 时间（24小时制）
     * 2.昨天的显示 昨天+时间
     * 3.前天到一星期内 显示 星期几(+时间 hh:mm)
     * 4.一星期前显示  月份日期(+时间 hh:mm)
     * 5.非今年 显示   年月日(+时间 hh:mm)
     *
     * @param paramDate
     * @param isShowTime 是否显示时间后缀 hh:mm
     *
     * @return
     */
    public static String getRecentDate(Date paramDate, boolean isShowTime) {
        Calendar nowCalendar = Calendar.getInstance();  // 现在时间
        nowCalendar.setTime(new Date());
        int nowYear = nowCalendar.get(Calendar.YEAR);
        int nowMonth = nowCalendar.get(Calendar.MONTH) + 1;
        int nowDay = nowCalendar.get(Calendar.DAY_OF_MONTH);

        Calendar paramCalendar = Calendar.getInstance();  // 参数时间
        paramCalendar.setTime(paramDate);
        int paramYear = paramCalendar.get(Calendar.YEAR);
        int paramMonth = paramCalendar.get(Calendar.MONTH) + 1;
        int paramDay = paramCalendar.get(Calendar.DAY_OF_MONTH);

        if (nowYear == paramYear) {
            if (nowMonth == paramMonth) {
                int diffDay = nowDay - paramDay;  // 相差的天数
                if (diffDay == -1) {
                    return "明天";
                }
                else if (diffDay <= -2) {
                    return formatDate(nowDateTimeFormat, paramCalendar.getTime());
                }
                else if (diffDay == 0) {
                    return formatDate(timeFormat, paramCalendar.getTime());
                }
                else if (diffDay == 1) {
                    return "昨天" + (isShowTime ? formatDate(timeFormat, paramCalendar.getTime()) : "");
                }
                else if (diffDay >= 2 && isSameWeekDates(new Date(), paramDate)) {
                    return getWeek(paramCalendar.getTime()) + (isShowTime ? formatDate(timeFormat, paramCalendar.getTime()) : "");
                }
            }
            return formatDate(nowDateFormat2, paramCalendar.getTime());
        }
        return formatDate(defDateFormat, paramCalendar.getTime());
    }

    public static String formatData(long time, int type){
        Calendar nowCalendar = Calendar.getInstance();  // 现在时间
        nowCalendar.setTime(new Date());
        int nowYear = nowCalendar.get(Calendar.YEAR);
        int nowMonth = nowCalendar.get(Calendar.MONTH) + 1;
        int nowDay = nowCalendar.get(Calendar.DAY_OF_MONTH);

        Calendar paramCalendar = Calendar.getInstance();  // 参数时间
        paramCalendar.setTime(new Date(time));
        int paramYear = paramCalendar.get(Calendar.YEAR);
        int paramMonth = paramCalendar.get(Calendar.MONTH) + 1;
        int paramDay = paramCalendar.get(Calendar.DAY_OF_MONTH);

        if (nowYear == paramYear) {
            if (nowMonth == paramMonth) {
                int diffDay = nowDay - paramDay;  // 相差的天数
                if (diffDay == 0){
                    return formatDate("HH:mm", new Date(time));
                }else if(diffDay == 1){
                    return "昨天 " + formatDate("HH:mm", new Date(time));
                }
                else if(diffDay == 2){
                    return "前天 " + formatDate("HH:mm", new Date(time));
                }
                else if(isSameWeekDates(nowCalendar.getTime(), paramCalendar.getTime())){
                    return getWeek(new Date(time)) + " " + formatDate("HH:mm", new Date(time));
                }
                else{
                    if(type == 1){
                        return formatDate("yyyy年MM月dd日 HH:mm", new Date(time));
                    }else{
                        return formatDate("yyyy-MM-dd HH:mm", new Date(time));
                    }
                }
            }
        }
        if(type == 1){
            return formatDate("yyyy年MM月dd日 HH:mm", new Date(time));
        }else{
            return formatDate("yyyy-MM-dd HH:mm", new Date(time));
        }
    }

    public static String formatDataToMail(long time){
        Calendar nowCalendar = Calendar.getInstance();  // 现在时间
        nowCalendar.setTime(new Date());
        int nowYear = nowCalendar.get(Calendar.YEAR);
        int nowMonth = nowCalendar.get(Calendar.MONTH) + 1;
        int nowDay = nowCalendar.get(Calendar.DAY_OF_MONTH);

        Calendar paramCalendar = Calendar.getInstance();  // 参数时间
        paramCalendar.setTime(new Date(time));
        int paramYear = paramCalendar.get(Calendar.YEAR);
        int paramMonth = paramCalendar.get(Calendar.MONTH) + 1;
        int paramDay = paramCalendar.get(Calendar.DAY_OF_MONTH);

        if (nowYear == paramYear) {
            if (nowMonth == paramMonth) {
                int diffDay = nowDay - paramDay;  // 相差的天数
                if (diffDay == 0){
                    return formatDate("HH:mm", new Date(time));
                }else if(diffDay == 1){
                    return "昨天 " + formatDate("HH:mm", new Date(time));
                }
                else if(isSameWeekDates(nowCalendar.getTime(), paramCalendar.getTime())){
                    return getWeek(new Date(time)) + " " + formatDate("HH:mm", new Date(time));
                }
                else{
                    return formatDate("MM月dd日 HH:mm", new Date(time));
                }
            }
        }
        return formatDate("yyyy年MM月dd日 HH:mm", new Date(time));
    }

    /**
     * 友好的时间显示
     *
     * @param paramDate 需要转换的日期
     *
     * @return 格式化后的时间字符串
     */
    public static String friendlyTime(Date paramDate) {
        Calendar nowCalendar = Calendar.getInstance();  // 现在时间
        nowCalendar.setTime(new Date());
        int nowYear = nowCalendar.get(Calendar.YEAR);
        int nowMonth = nowCalendar.get(Calendar.MONTH) + 1;
        int nowDay = nowCalendar.get(Calendar.DAY_OF_MONTH);

        Calendar paramCalendar = Calendar.getInstance();  // 参数时间
        paramCalendar.setTime(paramDate);
        int paramYear = paramCalendar.get(Calendar.YEAR);
        int paramMonth = paramCalendar.get(Calendar.MONTH) + 1;
        int paramDay = paramCalendar.get(Calendar.DAY_OF_MONTH);

        if (nowYear == paramYear) {
            if (nowMonth == paramMonth) {
                int diffDay = nowDay - paramDay;  // 相差的天数
                if (diffDay == -1) {
                    return "明天" + formatDate(timeFormat, paramCalendar.getTime());
                }
                else if (diffDay <= -2) {
                    return formatDate(nowDateTimeFormat, paramCalendar.getTime());
                }
                else if (diffDay == 0) {
                    return formatDate(timeFormat, paramCalendar.getTime());
                }
                else if (diffDay == 1) {
                    return "昨天" + formatDate(timeFormat, paramCalendar.getTime());
                }
                else if (diffDay == 2) {
                    return getWeek(paramCalendar.getTime()) + formatDate(timeFormat, paramCalendar.getTime());
                }
                else if (diffDay == 3) {
                    return getWeek(paramCalendar.getTime()) + formatDate(timeFormat, paramCalendar.getTime());
                }
                else if (diffDay >= 4) {
                    return formatDate(nowDateTimeFormat, paramCalendar.getTime());
                }
            }
            return formatDate(nowDateTimeFormat, paramCalendar.getTime());
        }
        return formatDate(defDateTimeFormat, paramCalendar.getTime());
    }

    /**
     * 格式化时间
     *
     * @param pattern 格式化表达式
     * @param date    日期
     *
     * @return 格式化后时间字符串
     */
    public static String formatDate(String pattern, Date date) {
        return new SimpleDateFormat(pattern).format(date);
    }

    /**
     * 根据日期获得星期
     *
     * @param date
     *
     * @return
     */
    public static String getWeek(Date date) {
        String[] weekDaysName = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int w = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return weekDaysName[w];
    }

    /**
     * 判断两个日期是否在同一周
     *
     * @param date1
     * @param date2
     *
     * @return
     */
    public static boolean isSameWeekDates(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        int subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
        if (0 == subYear) {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR)) {
                return true;
            }
        }
        else if (1 == subYear && 11 == cal2.get(Calendar.MONTH)) {
            // 如果12月的最后一周横跨来年第一周的话则最后一周即算做来年的第一周
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR)) {
                return true;
            }
        }
        else if (-1 == subYear && 11 == cal1.get(Calendar.MONTH)) {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR)) {
                return true;
            }
        }
        return false;
    }
}

