package com.common.utils.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * 状态栏工具类
 *
 * @author LiuFeng
 * @date 2017-11-01
 */
public class StatusBarUtil {

    /**
     * 获取状态栏高度
     *
     * @param context
     *
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        return ScreenUtil.getStatusBarHeight(context);
    }

    /**
     * 创建一个和StatusBar大小相同的view
     *
     * @param activity
     * @param statusColor
     *
     * @return
     */
    public static View createStatusBarView(Activity activity, int statusColor) {
        View statusView = new View(activity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity));
        statusView.setLayoutParams(params);
        statusView.setBackgroundColor(statusColor);
        return statusView;
    }

    /**
     * 实现沉浸式状态栏，在状态栏下面添加一个和 状态栏一样高的View
     * (注：当自己的布局根节点为DrawerLayout的时候，需要自己在XML中设置DrawerLayout的FitsSystemWindows属性，否则无效果)
     *
     * @param activity
     * @param color    状态栏颜色
     */
    public static void setStatusBarColor(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 添加一个和状态栏同样大小的view
            View statusView = createStatusBarView(activity, color);
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            decorView.addView(statusView);
            // 设置根布局FitsSystemWindows属性
            ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
            rootView.setFitsSystemWindows(true);    // 防止布局被状态栏遮挡
        }
    }
}
