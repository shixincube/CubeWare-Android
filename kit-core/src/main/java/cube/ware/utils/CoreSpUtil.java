package cube.ware.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import cube.ware.core.CubeCore;

/**
 * SharedPreferences工具类
 *
 * @author LiuFeng
 * @date 2018-8-14
 */
public class CoreSpUtil {

    private static SharedPreferences sp;

    static {
        if (CubeCore.getContext() != null) {
            sp = CubeCore.getContext().getSharedPreferences("cube_ware", Context.MODE_PRIVATE);
        }
        else {
            throw new NullPointerException("Context is null, Initialize Context before using the SpUtil");
        }
    }

    /**
     * 设置DBName
     *
     * @param dbName
     */
    public static void setDBName(@NonNull String dbName) {
        setString("db_name", dbName);
    }

    /**
     * 获取DBName
     *
     * @return
     */
    public static String getDBName() {
        return getString("db_name", null);
    }

    //==============================sp基础方法，用于封装具体方法=============================//

    /**
     * 获取String--基础方法
     */
    private static String getString(@NonNull String key, String defValue) {
        return sp.getString(key, defValue);
    }

    /**
     * 设置String--基础方法
     */
    private static void setString(@NonNull String key, String value) {
        sp.edit().putString(key, value).apply();
    }

    /**
     * 获取Boolean--基础方法
     */
    private static boolean getBoolean(@NonNull String key, boolean defValue) {
        return sp.getBoolean(key, defValue);
    }

    /**
     * 设置Boolean--基础方法
     */
    private static void setBoolean(@NonNull String key, boolean value) {
        sp.edit().putBoolean(key, value).apply();
    }

    /**
     * 获取Int--基础方法
     */
    private static int getInt(@NonNull String key, int defValue) {
        return sp.getInt(key, defValue);
    }

    /**
     * 设置Int--基础方法
     */
    private static void setInt(@NonNull String key, int value) {
        sp.edit().putInt(key, value).apply();
    }

    /**
     * 获取Long--基础方法
     */
    private static long getLong(@NonNull String key, long defValue) {
        return sp.getLong(key, defValue);
    }

    /**
     * 设置Long--基础方法
     */
    private static void setLong(@NonNull String key, long value) {
        sp.edit().putLong(key, value).apply();
    }

    /**
     * 包含key键--基础方法
     */
    private static boolean contains(@NonNull String key) {
        return sp.contains(key);
    }

    /**
     * 移除指定key--基础方法
     */
    private static void remove(@NonNull String key) {
        sp.edit().remove(key).apply();
    }

    /**
     * 清除全部--基础方法
     */
    private static void clearAll() {
        sp.edit().clear().apply();
    }
}
