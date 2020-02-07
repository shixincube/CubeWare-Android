package cube.ware.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import com.common.utils.utils.FileUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import cube.ware.core.CubeCore;
import cube.ware.common.MessageConstants;
import java.io.File;
import java.util.Map;

/**
 * SharedPreferences工具类
 *
 * @author LiuFeng
 * @date 2018-8-14
 */
public class SpUtil {

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
     * 获取App资源路径
     *
     * @return
     */
    public static String getResourcePath() {
        String path = FileUtil.getFilePath(CubeCore.getContext(), MessageConstants.Sp.PATH_APP);
        initFileDir(path);
        return path;
    }

    public static String getFilePath() {
        return getResourcePath() + File.separator + MessageConstants.Sp.PATH_FILE;
    }

    public static String getImagePath() {
        return getResourcePath() + File.separator + MessageConstants.Sp.PATH_IMAGE;
    }

    public static String getThumbPath() {
        return getResourcePath() + File.separator + MessageConstants.Sp.PATH_THUMB;
    }

    public static String getLogPath() {
        return getResourcePath() + File.separator + MessageConstants.Sp.PATH_LOG;
    }

    /**
     * 初始化应用文件目录
     *
     * @param appResourcePath
     */
    private static void initFileDir(String appResourcePath) {
        File appFile = new File(appResourcePath);
        if (!appFile.exists()) {
            appFile.mkdirs();
        }

        File log = new File(appFile, MessageConstants.Sp.PATH_LOG);
        if (!log.exists()) {
            log.mkdirs();
        }

        File image = new File(appFile, MessageConstants.Sp.PATH_IMAGE);
        if (!image.exists()) {
            image.mkdirs();
        }

        File file = new File(appFile, MessageConstants.Sp.PATH_FILE);
        if (!file.exists()) {
            file.mkdirs();
        }

        File thumb = new File(appFile, MessageConstants.Sp.PATH_THUMB);
        if (!thumb.exists()) {
            thumb.mkdirs();
        }
    }

    /**
     * 保存token
     *
     * @param cubeToken
     */
    public static void setCubeToken(String cubeToken) {
        setString(MessageConstants.Sp.CUBE_TOKEN, cubeToken);
    }

    public static String getCubeToken() {
        return getString(MessageConstants.Sp.CUBE_TOKEN, "");
    }

    /**
     * 保存Id
     *
     * @param cubeId
     */
    public static void setCubeId(String cubeId) {
        setString(MessageConstants.Sp.USER_CUBEID, cubeId);
    }

    public static String getCubeId() {
        return getString(MessageConstants.Sp.USER_CUBEID, "");
    }

    /**
     * 保存名字
     *
     * @param name
     */
    public static void setUserName(String name) {
        setString(MessageConstants.Sp.CUBE_NAME, name);
    }

    public static String getUserName() {
        return getString(MessageConstants.Sp.CUBE_NAME, "");
    }

    /**
     * 保存头像
     *
     * @param avator
     */
    public static void setUserAvator(String avator) {
        setString(MessageConstants.Sp.USER_AVATOR, avator);
    }

    public static String getUserAvator() {
        return getString(MessageConstants.Sp.USER_AVATOR, "");
    }

    /**
     * 保存user
     *
     * @param userJson
     */
    public static void setUserJson(String userJson) {
        setString(MessageConstants.Sp.USER_JSON, userJson);
    }

    public static String getUserJson() {
        return getString(MessageConstants.Sp.USER_JSON, "");
    }

    /**
     * 设置@全体成员
     *
     * @param groupCube 群组cube号
     * @param value     @全体成员的值map（key:2017-9-8 value:1）
     */
    public static void setAtAll(String groupCube, Map<String, Integer> value) {
        String json = new Gson().toJson(value, new TypeToken<Map<String, Integer>>() {}.getType());
        SpUtil.setString(MessageConstants.Sp.SP_CUBE_AT_ALL + getCubeId() + groupCube, json);
    }

    /**
     * 接收到的@All消息
     *
     * @param groupCube
     * @param isAtAll
     */
    public static void setReceiveAtAll(String groupCube, boolean isAtAll) {
        SpUtil.setBoolean(groupCube, isAtAll);
    }

    public static void getReceiveAtAll(String groupCube, boolean isAtAll) {
        SpUtil.getBoolean(groupCube, isAtAll);
    }

    /**
     * 接收到的@消息
     *
     * @param groupCube
     * @param isAtAll
     */
    public static void setReceiveAt(String groupCube, boolean isAtAll) {
        SpUtil.setBoolean(groupCube, isAtAll);
    }

    public static void getReceiveAt(String groupCube, boolean isAtAll) {
        SpUtil.getBoolean(groupCube, isAtAll);
    }

    public static void setDraftMessage(String chatId, String content) {
        setString(MessageConstants.Sp.MESSAGE_DRAFT + chatId, content);
    }

    public static String getDraftMessage(String chatId) {
        return getString(MessageConstants.Sp.MESSAGE_DRAFT + chatId, "");
    }

    /**
     * 获取@全体成员
     *
     * @param groupCube
     *
     * @return
     */
    public static Map<String, Integer> getAtAll(String groupCube) {
        String json = SpUtil.getString(MessageConstants.Sp.SP_CUBE_AT_ALL + getCubeId() + groupCube, "");
        return new Gson().fromJson(json, new TypeToken<Map<String, Integer>>() {}.getType());
    }

    /**
     * 清空sp
     */
    public static void clear() {
        clearAll();
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
