package com.common.utils;

/**
 * 版本控制工具类
 *
 * @author liufeng
 * @date 2017-11-13
 */
public class VersionUtil {

    /**
     * 比较版本号的大小,前者大则返回一个正数,后者大返回一个负数,相等则返回0
     *
     * @param versionNew
     * @param versionOld
     */
    public static int compareVersion(String versionNew, String versionOld) {
        if (EmptyUtil.isNotEmpty(versionNew) && EmptyUtil.isNotEmpty(versionOld)) {
            //注意此处为正则匹配，不能用"."
            String[] versionArrayNew = versionNew.split("\\.");
            String[] versionArrayOld = versionOld.split("\\.");
            int oldLength = versionArrayOld.length;
            int newLength = versionArrayNew.length;
            //取最小长度值
            int minLength = Math.min(oldLength, newLength);
            for (int i = 0; i < minLength; i++) {
                if (Integer.parseInt(versionArrayNew[i]) > Integer.parseInt(versionArrayOld[i])) {
                    return 1;
                }
            }
            if (newLength > oldLength) {
                return 1;
            }
            return 0;
        }
        else if (EmptyUtil.isEmpty(versionNew) && EmptyUtil.isEmpty(versionOld)) {
            return 0;
        }
        else if (EmptyUtil.isNotEmpty(versionNew)) {
            return 1;
        }
        else {
            return -1;
        }
    }
}
