package cube.ware.service.message.chat.panel.input;

/**
 * @author CloudZhang
 * @date 2018/1/29 11:13
 */

import android.content.Context;
import android.util.AttributeSet;

/**
 * 超过一定行数显示固定行数时末尾添加 "..."的TextView(适配图文混排)
 */
public class EllipsizedTextView extends android.support.v7.widget.AppCompatTextView {

    private int mMaxLines;

    public EllipsizedTextView(Context context) {
        this(context, null);
    }

    public EllipsizedTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EllipsizedTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mMaxLines = 1;
    }

    public void setMyText(CharSequence text, int measuredWidth) {
        setSingleLine();//默认单行
        //sp2px(getContext(), textsize) 单个中文的宽度，sp转换成px适应不同手机
        int textWidth = sp2px(getContext(), 14) * (getChineseNums(text.toString()) + (getNoChineseNums(text.toString()) + 1) / 2);
        int tempWidth = 0;
        int n = measuredWidth / sp2px(getContext(), 14);
        char[] chars = text.toString().toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char aChar = chars[i];
            tempWidth += sp2px(getContext(), 7) * (isChinese(aChar) ? 2 : 1);
            if (tempWidth > measuredWidth) {
                n = i - 2;
                break;
            }
        }
        if (textWidth > measuredWidth) {
            if (n - 1 < text.length()) {
                setText(text.toString().substring(0, n - 1) + "...");
            }
            else {
                setText(text);
            }
        }
        else {
            setText(text);
        }
    }

    public String getAdaptString(String text, int measuredWidth){
        //sp2px(getContext(), textsize) 单个中文的宽度，sp转换成px适应不同手机
        int textWidth = sp2px(getContext(), 14) * (getChineseNums(text.toString()) + (getNoChineseNums(text.toString()) + 1) / 2);
        int tempWidth = 0;
        int n = measuredWidth / sp2px(getContext(), 14);
        char[] chars = text.toString().toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char aChar = chars[i];
            tempWidth += sp2px(getContext(), 7) * (isChinese(aChar) ? 2 : 1);
            if (tempWidth > measuredWidth) {
                n = i - 2;
                break;
            }
        }
        if (textWidth > measuredWidth) {
            if (n - 1 < text.length()) {
                return text.toString().substring(0, n - 1) + "...";
            }
        }
        return text.toString();
    }

    public static boolean isChinese(char c) {
        return c >= 0x4E00 && c <= 0x9FA5;// 根据字节码判断
    }

    /**
     * 字符串中，中文的字数
     *
     * @param str
     *
     * @return
     */
    private int getChineseNums(String str) {
        int byteLength = str.getBytes().length;
        int strLength = str.length();
        return (byteLength - strLength) / 2;
    }

    /**
     * 字符串中，非中文的字数
     *
     * @param str
     *
     * @return
     */
    private int getNoChineseNums(String str) {
        int byteLength = str.getBytes().length;
        int strLength = str.length();
        return strLength - (byteLength - strLength) / 2;
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     *
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    @Override
    public int getMaxLines() {
        return mMaxLines;
    }

    @Override
    public void setMaxLines(int maxLines) {
        super.setMaxLines(maxLines);
        mMaxLines = maxLines;
    }
}
