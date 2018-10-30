package com.common.utils.utils;

import java.text.SimpleDateFormat;

/**
 * author: kun .
 * date:   On 2018/9/17
 */
public class TimeUtils {

    /**
     * 日期格式字符串转换成时间 2016-08-04 10:34:42
     * @param format 如：yyyy-MM-dd HH:mm:ss
     * @return
     */
   public static String date2TimeStamp(String date_str,String format){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return String.valueOf(sdf.parse(date_str).getTime()/1000);
        } catch (Exception e) {
                e.printStackTrace();
        }
            return "";
         }
}
