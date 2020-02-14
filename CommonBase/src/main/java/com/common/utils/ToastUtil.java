package com.common.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 吐丝提示工具类
 *
 * @author LiuFeng
 * @date 2017-11-01
 */
public class ToastUtil extends Toast {

    public ToastUtil(Context context) {
        super(context);
    }

    /**
     * 自定义Toast样式
     *
     * @param context
     * @param resId
     * @param text
     * @param duration
     *
     * @description
     */
    private static Toast makeText(Context context, int resId, CharSequence text, int duration) {
        Toast result = new Toast(context);

        // 由layout文件创建一个View对象
        LinearLayout layout = new LinearLayout(context);
        layout.setBackgroundResource(android.R.drawable.toast_frame);

        // 实例化ImageView和TextView对象
        ImageView imageView = new ImageView(context);
        TextView textView = new TextView(context);
        layout.addView(imageView);
        layout.addView(textView);

        //这里我为了给大家展示就使用这个方面既能显示无图也能显示带图的toast
        //不用显示图片直接写0，显示图片加载ID即可
        if (resId == 0) {
            imageView.setVisibility(View.GONE);
        }
        else {
            imageView.setImageResource(resId);
        }

        textView.setText(text);
        textView.setTextColor(Color.WHITE);

        result.setView(layout);
        result.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        result.setDuration(duration);

        return result;
    }

    /**
     *
     * @param context  上下文
     * @param imageResId 图片Id
     * @param message  消息
     *     默认3秒
     */
    public static void showToast(Context context, int imageResId, String message) {
        Toast mToast = ToastUtil.makeText(context, imageResId, message, 3000);
        mToast.show();
    }

    /**
     *
     * @param context 上下文
     * @param message  消息
     *                 没图片 0
     */
    public static void showToast(Context context, String message) {
        Toast mToast = ToastUtil.makeText(context, 0, message, 3000);
        mToast.show();
    }

    public static void showToast(Context context, int imageResId, int messageResId) {
        Toast mToast = ToastUtil.makeText(context, imageResId, context.getResources().getString(messageResId), 3000);
        mToast.show();
    }

    /**
     *
     * @param context
     * @param messageResId 消息String ID
     */
    public static void showToast(Context context, int messageResId) {
        Toast mToast = ToastUtil.makeText(context, 0, context.getResources().getString(messageResId), 3000);
        mToast.show();
    }


    /**
     * @param messageResId 消息String ID
     */
    public static void showToastTime(Context context, int messageResId, int duration) {
        Toast mToast = ToastUtil.makeText(context, 0, context.getResources().getString(messageResId), duration);
        mToast.show();
    }
}
