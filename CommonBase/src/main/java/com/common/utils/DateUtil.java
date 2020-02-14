package com.common.utils;

import android.text.TextUtils;
import com.common.utils.log.LogUtil;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 日期、时间工具类
 *
 * @author PengZhenjin
 * @date 2016/6/2
 */
public class DateUtil {

    public static final String TAG = "DateUtil";

    /**
     * 将String类型转换为dateTime类型
     *
     * @param dateStr    时间字符串
     * @param dateFormat 日期格式，默认：yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static Date stringToDate(String dateStr, String dateFormat) {
        try {
            if (TextUtils.isEmpty(dateFormat)) {
                dateFormat = "yyyy-MM-dd HH:mm:ss";
            }
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将dateTime类型转换为String类型
     *
     * @param dateTime   日期
     * @param dateFormat 日期格式，默认：yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String dateToString(Date dateTime, String dateFormat) {
        if (TextUtils.isEmpty(dateFormat)) {
            dateFormat = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(dateTime);
    }

    /**
     * 将dateTime类型转换为String类型
     *
     * @param dateTime   日期
     * @param dateFormat 日期格式，默认：yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String dateToString(long dateTime, String dateFormat) {
        if (TextUtils.isEmpty(dateFormat)) {
            dateFormat = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(new Date(dateTime));
    }

    /**
     * 将Date类型转换为String，类型：yyyy-MM-dd
     *
     * @param dateTime
     *
     * @return
     */
    public static String dateToString(long dateTime) {
        Date date = new Date(dateTime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    /**
     * 将Date类型转换为String，类型：yyyy-MM-dd
     *
     * @param date
     *
     * @return
     */
    public static String dateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    // date要转换的date类型的时间
    public static long dateToLong(Date date) {
        return date.getTime();
    }

    /**
     * 根据时间戳得到年
     *
     * @param dateTime
     *
     * @return
     */
    public static int getYear(long dateTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateTime);
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 根据时间戳得到月
     *
     * @param dateTime
     *
     * @return
     */
    public static int getMonth(long dateTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateTime);
        return calendar.get(Calendar.MONTH);
    }

    /**
     * 根据时间戳得到日
     *
     * @param dateTime
     *
     * @return
     */
    public static int getDay(long dateTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateTime);
        return calendar.get(Calendar.DATE);
    }

    /**
     * 根据时间戳得到日
     *
     * @param dateTime
     *
     * @return
     */
    public static long getDayStamp(long dateTime) {
        return stringToDate(getYear(dateTime) + "-" + getMonth(dateTime) + "-" + getDay(dateTime)).getTime();
    }

    /**
     * 将DateStr类型转换为Date，类型：yyyy-MM-dd
     *
     * @param dateStr
     *
     * @return
     */
    public static Date stringToDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从时间(毫秒)中提取出时间(分:秒)
     * 时间格式:  分:秒
     *
     * @param millisecond
     *
     * @return
     */
    public static String getTimeFromMillisecond(Long millisecond) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        Date date = new Date(millisecond);
        String timeStr = simpleDateFormat.format(date);
        if(timeStr.contains("00:00")){
            return "00:01";
        }
        return timeStr;
    }

    /**
     * 获取系统当前时间戳
     *
     * @return
     */
    public static long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }

    /**
     * 获取当前年
     *
     * @return
     */
    public static int getCurrentYear() {
        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTime(new Date());
        return nowCalendar.get(Calendar.YEAR);
    }

    /**
     * 获取当前月
     *
     * @return
     */
    public static int getCurrentMonth() {
        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTime(new Date());
        return nowCalendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取当前日
     *
     * @return
     */
    public static int getCurrentDay() {
        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTime(new Date());
        return nowCalendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取当前时
     *
     * @return
     */
    public static int getCurrentHour() {
        Calendar nowCalendar = Calendar.getInstance();  // 现在时间
        nowCalendar.setTime(new Date());
        return nowCalendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取当前分
     *
     * @return
     */
    public static int getCurrentMinute() {
        Calendar nowCalendar = Calendar.getInstance();  // 现在时间
        nowCalendar.setTime(new Date());
        return nowCalendar.get(Calendar.MINUTE);
    }

    /**
     * 获取当前秒
     *
     * @return
     */
    public static int getCurrentSecond() {
        Calendar nowCalendar = Calendar.getInstance();  // 现在时间
        nowCalendar.setTime(new Date());
        return nowCalendar.get(Calendar.SECOND);
    }

    /**
     * 根据指定的日期获取年
     *
     * @param date
     *
     * @return
     */
    public static int getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 根据指定的日期获取月
     *
     * @param date
     *
     * @return
     */
    public static int getMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 根据指定的日期获取星期
     *
     * @param date
     *
     * @return
     */
    public static String getWeekOfDate(Date date) {
        String[] weekArray = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int weekIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (weekIndex < 0) {
            weekIndex = 0;
        }
        return weekArray[weekIndex];
    }

    /**
     * 根据指定的日期获取日
     *
     * @param date
     *
     * @return
     */
    public static int getDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 根据指定的日期获取时
     *
     * @return
     */
    public static int getHour(Date date) {
        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTime(date);
        return nowCalendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 根据指定的日期获取分
     *
     * @return
     */
    public static int getMinute(Date date) {
        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTime(date);
        return nowCalendar.get(Calendar.MINUTE);
    }

    /**
     * 获取本年的起始时间
     *
     * @return
     */
    public static long getTimeOfYearStart() {
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.HOUR_OF_DAY, 0);
        ca.clear(Calendar.MINUTE);
        ca.clear(Calendar.SECOND);
        ca.clear(Calendar.MILLISECOND);
        ca.set(Calendar.DAY_OF_YEAR, 1);
        return ca.getTimeInMillis();
    }

    /**
     * 获取前n天日期、后n天日期
     *
     * @param distanceDay 前几天 如获取前7天日期则传-7即可；如果后7天则传7
     *
     * @return
     */
    public static String getOldDate(int distanceDay) {
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date beginDate = new Date();
        Calendar date = Calendar.getInstance();
        date.setTime(beginDate);
        date.set(Calendar.DATE, date.get(Calendar.DATE) + distanceDay);
        Date endDate = null;
        try {
            endDate = dft.parse(dft.format(date.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dft.format(endDate);
    }

    /**
     * 根据指定时间转换年月日
     *
     * @param date
     *
     * @return
     */
    public static String getYearTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        return format.format(date);
    }

    public static String getYearTimeWithWeekInfo(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 EEEE HH:mm");
        return format.format(date);
    }

    public static String getTimeWithWeekInfo(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("MM月dd日 HH:mm");
        return format.format(date);
    }

    public static String getMonthTimeWeekInfo(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("MM月dd日 HH:mm");
        return format.format(date);
    }

    /**
     * 根据指定时间获取时分
     *
     * @param date
     *
     * @return
     */
    public static String getHourTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(date);
    }

    /**
     * 截止上一天的23:59:59秒
     *
     * @return
     */
    public static long getStartTimeYesDay() {
        Calendar calendar = Calendar.getInstance();
        int mCurrentYear = calendar.get(Calendar.YEAR);
        int mCurrentMonth = calendar.get(Calendar.MONTH);
        int mCurrentDay = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(mCurrentYear, mCurrentMonth, mCurrentDay - 1, 23, 59, 59);
        Date endDate = calendar.getTime();
        long endTime = endDate.getTime();
        return endTime;
    }

    /**
     * 获取秒转化分
     *
     * @param milliseconds
     *
     * @return
     */
    public static long getSecondsByMilliseconds(long milliseconds) {
        long seconds = new BigDecimal(((float) milliseconds / (float) 1000)).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
        return seconds;
    }

    public static boolean get24HourMode() {
        return android.text.format.DateFormat.is24HourFormat(CommonUtils.getContext());
    }

    /**
     * 获取时间戳的友好显示
     *
     * @param milliseconds
     *
     * @return
     */
    public static String getTimeShowString(long milliseconds) {
        boolean is24 = get24HourMode();
        String dataString;
        String timeStringBy24;

        Date currentTime = new Date(milliseconds);
        Date today = new Date();
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        Date todayBegin = todayStart.getTime();
        Date yesterdayBegin = new Date(todayBegin.getTime() - 3600 * 24 * 1000);

        if (!currentTime.before(todayBegin)) {
            dataString = "";
        }
        else if (!currentTime.before(yesterdayBegin)) {
            dataString = "昨天";
        }
        else if (isSameWeekDates(currentTime, today)) {
            dataString = getWeekOfDate(currentTime);
        }
        else {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
            dataString = dateFormatter.format(currentTime);
        }

        SimpleDateFormat timeFormatter24 = new SimpleDateFormat("HH:mm", Locale.getDefault());
        timeStringBy24 = timeFormatter24.format(currentTime);

        if (is24) {
            return dataString + " " + timeStringBy24;
        }
        else {
            if (!currentTime.before(todayBegin)) {
                return getTodayTimeBucket(currentTime);
            }
            else {
                return dataString + " " + getTodayTimeBucket(currentTime);
            }
        }
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

    /**
     * 根据不同时间段，显示不同时间
     *
     * @param date
     *
     * @return
     */
    public static String getTodayTimeBucket(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        SimpleDateFormat timeFormatter1to12 = new SimpleDateFormat("h:mm", Locale.getDefault());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour >= 0 && hour < 12) {
            return "上午 " + timeFormatter1to12.format(date);
        }
        else if (hour >= 12 && hour < 24) {
            return "下午 " + timeFormatter1to12.format(date);
        }
        return "";
    }

    /**
     * 判断起止日期是否在一个时间段
     *
     * @return
     */
    public static boolean isOutOfDateTime(long startTime, long endTime) {
        long nowTime = getCurrentTimeMillis();
        return nowTime >= startTime && nowTime <= endTime;
    }

    /**
     * 根据指定时间获取时或者分
     *
     * @param date
     *
     * @return
     */
    public static String getHourOrMinute(long date) {
        String str1 = dateToString(date, "yyyy-MM-dd HH:mm:ss");
        String str2 = dateToString(getCurrentTimeMillis(), "yyyy-MM-dd HH:mm:ss");
        return getDistanceTime(str1, str2);
    }

    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     *
     * @param str1 时间参数 1 格式：1990-01-01 12:00:00
     * @param str2 时间参数 2 格式：2009-01-01 12:00:00
     *
     * @return String 返回值为：xx天xx小时xx分
     */
    public static String getDistanceTime(String str1, String str2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date one;
        Date two;
        long day = 0;
        long hour = 0;
        long min = 0;
        String time = null;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff;
            if (time1 < time2) {
                diff = time2 - time1;
            }
            else {
                diff = time1 - time2;
            }
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (day >= 1) {
            time = String.valueOf(day);
        }
        if (day < 1 && hour >= 1) {
            time = hour + "小时前";
        }
        else {
            time = min + "分钟前";
        }
        return time;
    }

    /**
     * 判断指定时间和当前时间是否超过1天
     *
     * @param date
     *
     * @return
     */
    public static boolean isDay(long date) {
        String str1 = dateToString(date, "yyyy-MM-dd HH:mm:ss");
        String str2 = dateToString(getCurrentTimeMillis(), "yyyy-MM-dd HH:mm:ss");
        return getContrastTime(str1, str2);
    }

    public static boolean getContrastTime(String str1, String str2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date one;
        Date two;
        long day = 0;
        boolean isDay;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff;
            if (time1 < time2) {
                diff = time2 - time1;
            }
            else {
                diff = time1 - time2;
            }
            day = diff / (24 * 60 * 60 * 1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (day >= 1) {
            isDay = true;
        }
        else {
            isDay = false;
        }
        return isDay;
    }

    /**
     * 判断是否为今天(效率比较高)
     *
     * @param day 传入的 时间  "2016-06-28 10:10:30" "2016-06-28" 都可以
     *
     * @return true今天 false不是
     *
     * @throws ParseException
     */
    public static boolean IsToday(String day) throws ParseException {

        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);

        Calendar cal = Calendar.getInstance();
        Date date = getDateFormat().parse(day);
        cal.setTime(date);

        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR) - pre.get(Calendar.DAY_OF_YEAR);

            if (diffDay == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否为昨天(效率比较高)
     *
     * @param day 传入的 时间  "2016-06-28 10:10:30" "2016-06-28" 都可以
     *
     * @return true是 false不是
     *
     * @throws ParseException
     */
    public static boolean IsYesterday(String day) throws ParseException {

        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);

        Calendar cal = Calendar.getInstance();
        Date date = getDateFormat().parse(day);
        cal.setTime(date);

        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR) - pre.get(Calendar.DAY_OF_YEAR);

            if (diffDay == -1) {
                return true;
            }
        }
        return false;
    }

    private static ThreadLocal<SimpleDateFormat> DateLocal = new ThreadLocal<SimpleDateFormat>();

    public static SimpleDateFormat getDateFormat() {
        if (null == DateLocal.get()) {
            DateLocal.set(new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA));
        }
        return DateLocal.get();
    }

    /**
     * 判断时间是否在时间段内
     *
     * @param nowTime
     * @param beginTime
     * @param endTime
     *
     * @return
     */
    public static boolean belongCalendar(Date nowTime, Date beginTime, Date endTime) {
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(beginTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 两个时间相隔多少天
     *
     * @param date1
     * @param date2
     *
     * @return
     */
    public static int differentDays(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if (year1 != year2)   //不同一年
        {
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0)    //闰年
                {
                    timeDistance += 366;
                }
                else    //不是闰年
                {
                    timeDistance += 365;
                }
            }

            return timeDistance + (day2 - day1);
        }
        else    //同年
        {
            return day2 - day1;
        }
    }

    /**
     * 是否同年
     *
     * @param date1
     * @param date2
     *
     * @return
     */
    public static boolean isYear(Date date1, Date date2) {

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if (year1 != year2) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * 是否同月
     *
     * @param date1
     * @param date2
     *
     * @return
     */
    public static boolean isMonth(Date date1, Date date2) {

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        int month1 = cal1.get(Calendar.MONTH);
        int month2 = cal2.get(Calendar.MONTH);
        if (month1 != month2) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * 判断两个日期是否为同一天
     *
     * @param date1
     * @param date2
     *
     * @return
     */
    public static boolean isSameDate(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        boolean isSameYear = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
        boolean isSameMonth = isSameYear && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
        boolean isSameDate = isSameMonth && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);

        return isSameDate;
    }

    // 根据年月日计算年龄,birthTimeString:"1994-11-14"
    public static int getAgeFromBirthTime(String birthTimeString) {
        // 先截取到字符串中的年、月、日
        String strs[] = birthTimeString.trim().split("-");
        int selectYear = Integer.parseInt(strs[0]);
        int selectMonth = Integer.parseInt(strs[1]);
        int selectDay = Integer.parseInt(strs[2]);
        // 得到当前时间的年、月、日
        Calendar cal = Calendar.getInstance();
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH) + 1;
        int dayNow = cal.get(Calendar.DATE);

        // 用当前年月日减去生日年月日
        int yearMinus = yearNow - selectYear;
        int monthMinus = monthNow - selectMonth;
        int dayMinus = dayNow - selectDay;

        int age = yearMinus;// 先大致赋值
        if (yearMinus < 0) {// 选了未来的年份
            age = 0;
        }
        else if (yearMinus == 0) {// 同年的，要么为1，要么为0
            if (monthMinus < 0) {// 选了未来的月份
                age = 0;
            }
            else if (monthMinus == 0) {// 同月份的
                if (dayMinus < 0) {// 选了未来的日期
                    age = 0;
                }
                else if (dayMinus >= 0) {
                    age = 1;
                }
            }
            else if (monthMinus > 0) {
                age = 1;
            }
        }
        else if (yearMinus > 0) {
            if (monthMinus < 0) {// 当前月>生日月
            }
            else if (monthMinus == 0) {// 同月份的，再根据日期计算年龄
                if (dayMinus < 0) {
                }
                else if (dayMinus >= 0) {
                    age = age + 1;
                }
            }
            else if (monthMinus > 0) {
                age = age + 1;
            }
        }
        return age;
    }

    // 根据时间戳计算年龄
    public static int getAgeFromBirthTime(long birthTimeLong) {
        Date date = new Date(birthTimeLong);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String birthTimeString = format.format(date);
        return getAgeFromBirthTime(birthTimeString);
    }

    /**
     * 获取指定时间的时间戳，精确到最后一秒，
     *
     * @param year
     * @param month
     * @param day
     *
     * @return
     */
    public static long getCustomizationTime(int year, int month, int day) {
        long time = 0;
        try {
            time = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(day + "/" + (month) + "/" + year + " 23:59:59").getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    /**
     * 获取当前日期
     *
     * @return
     */
    public static String getDateToday() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        return str;
    }

    public static String formatTimestamp(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");
        return simpleDateFormat.format(time);
    }

    /**
     * 计算时间差(天)
     *
     * @param endDate
     * @param nowDate
     *
     * @return
     */
    public static long getDatePoorDay(Date endDate, Date nowDate) {
        long nd = 1000 * 24 * 60 * 60;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        return diff / nd;
    }

    /**
     * 计算时间差(小时)
     *
     * @param endDate
     * @param nowDate
     *
     * @return
     */
    public static long getDatePoorHour(Date endDate, Date nowDate) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        return diff % nd / nh;
    }

    /**
     * 计算时间差(分钟)
     *
     * @param endDate
     * @param nowDate
     *
     * @return
     */
    public static long getDatePoorMin(Date endDate, Date nowDate) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少分钟
        return diff % nd % nh / nm;
    }

    /**
     * 计算时间差(秒)
     *
     * @param endDate
     * @param nowDate
     *
     * @return
     */
    public static long getDatePoorSec(Date endDate, Date nowDate) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少秒
        return diff % nd % nh % nm / ns;
    }
}
