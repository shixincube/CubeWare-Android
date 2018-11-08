package com.common.utils.utils;

import android.view.View;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * UI点击处理工具类
 *
 * @author LiuFeng
 * @date 2018-8-29
 */
public class ClickUtil {

    private static final int DEFAULT_SPACE_TIME = 500; // 默认间隔时间

    private static final Map<Integer, long[]> timeMap = new LinkedHashMap<>();

    private static long lastClickTime;

    private ClickUtil() {}

    /**
     * 是否正常点击事件
     *
     * @param v
     *
     * @return 非抖动的正常点击事件 返回true
     */
    public static boolean isNormalClick(View v) {
        return isNormalClick(v, DEFAULT_SPACE_TIME);
    }

    /**
     * 是否正常点击事件
     *
     * @param v
     * @param spaceTime 去抖动间隔时间
     *
     * @return 非抖动的正常点击事件 返回true
     */
    public static boolean isNormalClick(View v, long spaceTime) {
        if (timeMap.containsKey(v.getId())) {
            return isNormal(v, timeMap.get(v.getId()), spaceTime);
        }
        else {
            return isNormal(v, new long[2], spaceTime);
        }
    }

    /**
     * 抖动点击中
     *
     * @param v
     *
     * @return
     */
    public static boolean isShaking(View v) {
        return !isNormalClick(v);
    }

    /**
     * 抖动点击中
     *
     * @param v
     * @param spaceTime
     *
     * @return
     */
    public static boolean isShaking(View v, long spaceTime) {
        return !isNormalClick(v, spaceTime);
    }

    /**
     * 是否正常点击
     *
     * @param v
     * @param time
     *
     * @return 最近两次点击间隔时间超过500ms，则表示正常点击 返回true
     */
    private static boolean isNormal(View v, long[] time, long spaceTime) {
        System.arraycopy(time, 1, time, 0, 1);
        time[1] = System.currentTimeMillis();
        timeMap.put(v.getId(), time);
        if (time[1] - time[0] > spaceTime) {
            return true;
        }
        return false;
    }

    /**
     * 清空数据
     */
    public static void clear() {
        timeMap.clear();
    }

    /**
     * 二次点击
     * @return
     */
    public static boolean isFastClick() {
        boolean flag = false;
        long time = System.currentTimeMillis() - lastClickTime;

        if (time > 500) {
            flag = true;
        }
        lastClickTime = System.currentTimeMillis();
        return flag;
    }
    /**
     * 二次点击
     * @return
     */
    public static boolean isFastClick(long blankClickTime) {
        boolean flag = false;
        long time = System.currentTimeMillis() - lastClickTime;

        if (time > blankClickTime) {
            flag = true;
        }
        lastClickTime = System.currentTimeMillis();
        return flag;
    }
}
