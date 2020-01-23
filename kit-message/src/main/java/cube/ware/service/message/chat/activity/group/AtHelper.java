package cube.ware.service.message.chat.activity.group;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import cube.ware.core.CubeCore;
import cube.ware.data.room.model.CubeMessage;
import cube.ware.MessageConstants;
import cube.ware.service.message.R;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * “@”功能帮助类
 *
 * @author Wangxx
 * @date 2017/3/3
 */

public class AtHelper {
    public static String getAtAlertString(String content) {
        return "[有人@你] " + content;
    }

    public static void replaceAtForeground(String value, SpannableString mSpannableString) {
        if (TextUtils.isEmpty(value) || TextUtils.isEmpty(mSpannableString)) {
            return;
        }
        Pattern pattern = Pattern.compile("(\\[有人@你\\])");
        Matcher matcher = pattern.matcher(value);
        while (matcher.find()) {
            int start = matcher.start();
            if (start != 0) {
                continue;
            }
            int end = matcher.end();
            mSpannableString.setSpan(new ForegroundColorSpan(Color.RED), start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }
    }

    public static boolean isAtMessage(CubeMessage message) {
        return !(message == null || !message.isGroupMessage());
    }

    public static ImageSpan getInputAtSpan(String name, float textSize) {
        if (TextUtils.isEmpty(name)) {
            return null;
        }
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        paint.setTextSize(textSize);
        Rect rect = new Rect();

        paint.getTextBounds(name, 0, name.length(), rect);

        // 获取字符串在屏幕上的长度
        int width = (int) (paint.measureText(name));

        final Bitmap bmp = Bitmap.createBitmap(width, rect.height(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);

        canvas.drawText(name, rect.left, rect.height() - rect.bottom, paint);

        return new ImageSpan(CubeCore.getContext(), bmp, ImageSpan.ALIGN_BOTTOM);
    }

    /**
     * 添加@标签span
     *
     * @param context
     * @param spannable
     */
    public static void addAtTagSpan(Context context, Spannable spannable, int textSize) {
        Pattern pattern1 = Pattern.compile(MessageConstants.REGEX.REGEX_AT_MEMBER);
        Matcher matcher1 = pattern1.matcher(spannable);
        while (matcher1.find()) {
            String oldContent = matcher1.group();
            String[] oldContentArray = oldContent.split(":");
            String newContent = oldContentArray[oldContentArray.length - 1];
            newContent = newContent.substring(0, newContent.length() - 1);
            newContent = "@" + newContent + " ";
            TextView textView = createTextView(context, newContent, textSize);
            Drawable drawable = convertViewToDrawable(context, textView);
            int drawableWidth = drawable.getIntrinsicWidth();
            int drawableHeight = drawable.getIntrinsicHeight();
            drawable.setBounds(0, 0, drawableWidth, drawableHeight);
            ImageSpan span = new ImageSpan(drawable, newContent);
            spannable.setSpan(span, matcher1.start(), matcher1.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        Pattern pattern2 = Pattern.compile(MessageConstants.REGEX.REGEX_AT_ALL);
        Matcher matcher2 = pattern2.matcher(spannable);
        while (matcher2.find()) {
            String oldContent = matcher2.group();
            String[] oldContentArray = oldContent.split(":");
            String newContent = oldContentArray[oldContentArray.length - 1];
            newContent = newContent.substring(0, newContent.length() - 1);
            newContent = "@" + newContent + " ";
            TextView textView = createTextView(context, newContent, textSize);
            Drawable drawable = convertViewToDrawable(context, textView);
            int drawableWidth = drawable.getIntrinsicWidth();
            int drawableHeight = drawable.getIntrinsicHeight();
            drawable.setBounds(0, 0, drawableWidth, drawableHeight);
            ImageSpan span = new ImageSpan(drawable, newContent);
            spannable.setSpan(span, matcher2.start(), matcher2.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    /**
     * 替换@标签
     *
     * @param context
     * @param spannable
     */
    public static void replaceAtTag(Context context, SpannableStringBuilder spannable, int color) {
        Pattern pattern1 = Pattern.compile(MessageConstants.REGEX.REGEX_AT_MEMBER);
        Matcher matcher1 = pattern1.matcher(spannable);
        while (matcher1.find()) {
            String oldContent = matcher1.group();
            String[] oldContentArray = oldContent.split(":");
            String newContent = oldContentArray[oldContentArray.length - 1];
            newContent = newContent.substring(0, newContent.length() - 1);
            newContent = "@" + newContent + " ";

            ForegroundColorSpan colorSpan = new ForegroundColorSpan(context.getResources().getColor(color));
            spannable.setSpan(colorSpan, matcher1.start(), matcher1.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable = spannable.replace(matcher1.start(), matcher1.end(), newContent);

            matcher1 = pattern1.matcher(spannable);
        }

        Pattern pattern2 = Pattern.compile(MessageConstants.REGEX.REGEX_AT_ALL);
        Matcher matcher2 = pattern2.matcher(spannable);
        while (matcher2.find()) {
            String oldContent = matcher2.group();
            String[] oldContentArray = oldContent.split(":");
            String newContent = oldContentArray[oldContentArray.length - 1];
            newContent = newContent.substring(0, newContent.length() - 1);
            newContent = "@" + newContent + " ";

            ForegroundColorSpan colorSpan = new ForegroundColorSpan(context.getResources().getColor(color));
            spannable.setSpan(colorSpan, matcher2.start(), matcher2.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable = spannable.replace(matcher2.start(), matcher2.end(), newContent);

            matcher2 = pattern2.matcher(spannable);
        }
    }

    /**
     * 替换@标签为文字
     */
    public static String replaceAtTagToText(String content) {
        Pattern pattern1 = Pattern.compile(MessageConstants.REGEX.REGEX_AT_MEMBER);
        Matcher matcher1 = pattern1.matcher(content);
        while (matcher1.find()) {
            String oldContent = matcher1.group();
            String[] oldContentArray = oldContent.split(":");
            String newContent = oldContentArray[oldContentArray.length - 1];
            newContent = newContent.substring(0, newContent.length() - 1);
            newContent = "@" + newContent + " ";
            content = content.replace(oldContent, newContent);
            matcher1 = pattern1.matcher(content);
        }
        return content.replaceAll(MessageConstants.REGEX.REGEX_AT_ALL, "@全体成员");
    }

    /**
     * 替换@标签
     *
     * @param context
     * @param spannable
     */
    public static String replaceAtTag(Context context, String spannable) {
        Pattern pattern1 = Pattern.compile(MessageConstants.REGEX.REGEX_AT_MEMBER);
        Matcher matcher1 = pattern1.matcher(spannable);
        while (matcher1.find()) {
            String oldContent = matcher1.group();
            String[] oldContentArray = oldContent.split(":");
            String newContent = oldContentArray[oldContentArray.length - 1];
            newContent = newContent.substring(0, newContent.length() - 1);
            newContent = "@" + newContent + " ";
            spannable = spannable.replace(oldContent, newContent);

            matcher1 = pattern1.matcher(spannable);
        }

        Pattern pattern2 = Pattern.compile(MessageConstants.REGEX.REGEX_AT_ALL);
        Matcher matcher2 = pattern2.matcher(spannable);
        while (matcher2.find()) {
            String oldContent = matcher2.group();
            String[] oldContentArray = oldContent.split(":");
            String newContent = oldContentArray[oldContentArray.length - 1];
            newContent = newContent.substring(0, newContent.length() - 1);
            newContent = "@" + newContent + " ";
            spannable = spannable.replace(oldContent, newContent);

            matcher2 = pattern2.matcher(spannable);
        }
        return spannable;
    }

    /**
     * 重新组装at格式的消息
     *
     * @param messageContent 消息内容
     *
     * @return
     */
    public static String rebuildAtMessage(String messageContent) {
        if (TextUtils.isEmpty(messageContent)) {
            return messageContent;
        }
        Pattern pattern1 = Pattern.compile(MessageConstants.REGEX.REGEX_AT_MEMBER);
        Matcher matcher1 = pattern1.matcher(messageContent);
        while (matcher1.find()) {
            String oldContent = matcher1.group();
            String[] oldContentArray = oldContent.split(":");
            String newContent = oldContentArray[oldContentArray.length - 1];
            newContent = newContent.substring(0, newContent.length() - 1);
            newContent = "@" + newContent + " ";
            messageContent = messageContent.replace(oldContent, newContent);
        }

        Pattern pattern2 = Pattern.compile(MessageConstants.REGEX.REGEX_AT_ALL);
        Matcher matcher2 = pattern2.matcher(messageContent);
        while (matcher2.find()) {
            String oldContent = matcher2.group();
            String[] oldContentArray = oldContent.split(":");
            String newContent = oldContentArray[oldContentArray.length - 1];
            newContent = newContent.substring(0, newContent.length() - 1);
            newContent = "@" + newContent + " ";
            messageContent = messageContent.replace(oldContent, newContent);
        }

        return messageContent;
    }

    /**
     * 创建TextView
     *
     * @param context
     * @param text
     * @param textSize
     *
     * @return
     */
    private static TextView createTextView(Context context, String text, int textSize) {
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        textView.setTextColor(context.getResources().getColor(R.color.cube_primary));
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(0, 0, 0, 0);
        return textView;
    }

    /**
     * 将View转换为Drawable
     */
    public static Drawable convertViewToDrawable(Context context, View view) {
        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(spec, spec);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap viewBmp = view.getDrawingCache();
        return new BitmapDrawable(context.getResources(), viewBmp);
    }

    /**
     * 存在At All
     *
     * @param text
     *
     * @return
     */
    public static boolean existAtAll(String text) {
        Pattern pattern2 = Pattern.compile(MessageConstants.REGEX.REGEX_AT_ALL);
        Matcher matcher2 = pattern2.matcher(text);
        return matcher2.find();
    }
}
