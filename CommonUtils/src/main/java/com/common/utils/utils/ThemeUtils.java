package com.common.utils.utils;

import android.content.Context;
import android.content.res.TypedArray;

import com.common.utils.R;


/**
 * ThemeUtils
 *
 * @author lzx
 * @date 2018-3-27
 */
public class ThemeUtils {

    private static final int[] APPCOMPAT_CHECK_ATTRS = { R.attr.colorPrimary };

    public static void checkAppCompatTheme(Context context) {
        TypedArray a = context.obtainStyledAttributes(APPCOMPAT_CHECK_ATTRS);
        boolean failed = !a.hasValue(0);
        a.recycle();
        if (failed) {
            throw new IllegalArgumentException("You need to use a Theme.AppCompat theme " + "(or descendant) with the design library.");
        }
    }
}
