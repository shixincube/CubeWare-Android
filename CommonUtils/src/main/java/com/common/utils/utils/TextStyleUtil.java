package com.common.utils.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

/**
 * 颜色处理工具类
 *
 * @author LiuFeng
 * @date 2017-11-01
 */
public class TextStyleUtil {
    /**
     * 在一段内容中处理部分内容的颜色
     *
     * @param text      内容
     * @param colorText 需要染色的内容
     * @param RgbColor  需要处理的内容的颜色
     *
     * @return
     */
    public static SpannableStringBuilder DyeText(String text, String colorText, int RgbColor) {
        SpannableStringBuilder style = new SpannableStringBuilder(text);
        int start = 0;
        int end = 0;
        if (text != null && colorText != null) {
            start = text.indexOf(colorText);
            end = start + colorText.length();
        }
        if (start < 0) {
            start = end = 0;
        }
        style.setSpan(new ForegroundColorSpan(RgbColor), start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        return style;
    }

    /**
     * 在一段内容中处理部分内容的颜色
     *
     * @param text      内容
     * @param colorText 需要染色的内容
     * @param colorId   需要处理的内容的颜色id
     *
     * @return
     */
    public static SpannableStringBuilder DyeText(Context context, String text, String colorText, int colorId) {
        SpannableStringBuilder style = new SpannableStringBuilder(text);
        int start = 0;
        int end = 0;
        if (text != null && colorText != null) {
            start = text.indexOf(colorText);
            end = start + colorText.length();
        }
        if (start < 0) {
            start = end = 0;
        }
        style.setSpan(new ForegroundColorSpan(context.getResources().getColor(colorId)), start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        return style;
    }

    /**
     * 加粗部分文本
     *
     * @param text
     * @param handleText
     *
     * @return
     */
    public static SpannableStringBuilder changeTextStyle(String text, String handleText) {
        SpannableStringBuilder style = new SpannableStringBuilder();
        if (text.contains(handleText)) {
            int start = text.indexOf(handleText);
            int end = start + handleText.length();
            style.append(text.substring(0, start));
            style.append(getBoldSpannable(handleText));// 加粗文字
            style.append(text.substring(end));
        }
        else {
            style.append(text);
        }
        return style;
    }

    /**
     * 处理加粗文本
     *
     * @param handleText
     *
     * @return
     */
    public static SpannableStringBuilder getBoldSpannable(String handleText) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(handleText);
        ssb.setSpan(new TextStyleSpan(Typeface.NORMAL), 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ssb;
    }

    @SuppressLint("ParcelCreator")
    public static class TextStyleSpan extends StyleSpan {
        public TextStyleSpan(int style) {
            super(style);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setFakeBoldText(true);
            //FIXME 这里还可以做其他差异性设置（修改文字大小等）
            super.updateDrawState(ds);
        }

        @Override
        public void updateMeasureState(TextPaint paint) {
            paint.setFakeBoldText(true);
            super.updateMeasureState(paint);
        }
    }
}
