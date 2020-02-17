package com.common.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.common.base.R;

/**
 * 吐丝提示工具类
 *
 * @author LiuFeng
 * @date 2017-11-01
 */
public class ToastUtil {

    // 上下文
    private static Context context;

    // 吐司布局Padding
    private static int left;
    private static int top;
    private static int right;
    private static int bottom;

    // 类加载初始化
    static {
        context = CommonUtils.getContext();
        left = DeviceUtil.dp2px(context, 12);
        top = DeviceUtil.dp2px(context, 10);
        right = DeviceUtil.dp2px(context, 12);
        bottom = DeviceUtil.dp2px(context, 10);
    }

    /**
     * 自定义Toast样式
     *
     * @param resId
     * @param text
     * @param duration
     *
     * @description
     */
    private static Toast makeText(int resId, CharSequence text, int duration) {
        Toast result = new Toast(context);

        // 由layout文件创建一个View对象
        LinearLayout layout = new LinearLayout(context);
        layout.setBackgroundResource(R.drawable.toast_default_bg);
        layout.setAlpha(0.8f);
        layout.setPadding(left, top, right, bottom);
        layout.setGravity(Gravity.CENTER);

        // 可显示带图的toast
        if (resId != 0) {
            // 实例化ImageView对象
            ImageView imageView = new ImageView(context);
            imageView.setImageResource(resId);
            layout.addView(imageView);
        }

        // 实例化TextView对象
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextSize(14);
        textView.setTextColor(Color.WHITE);
        textView.setGravity(Gravity.CENTER);
        layout.addView(textView);

        result.setView(layout);
        result.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        result.setDuration(duration);

        return result;
    }

    /**
     * @param imageResId 图片Id
     * @param message    消息
     *                   默认3秒
     */
    public static void showToast(int imageResId, String message) {
        Toast mToast = makeText(imageResId, message, 3000);
        mToast.show();
    }

    /**
     * @param message 消息
     */
    public static void showToast(String message) {
        Toast mToast = makeText(0, message, 3000);
        mToast.show();
    }

    public static void showToast(int imageResId, int messageResId) {
        Toast mToast = makeText(imageResId, context.getResources().getString(messageResId), 3000);
        mToast.show();
    }

    /**
     * @param messageResId 消息String ID
     */
    public static void showToast(int messageResId) {
        Toast mToast = makeText(0, context.getResources().getString(messageResId), 3000);
        mToast.show();
    }

    /**
     * @param messageResId 消息String ID
     */
    public static void showToastTime(int messageResId, int duration) {
        Toast mToast = makeText(0, context.getResources().getString(messageResId), duration);
        mToast.show();
    }
}
