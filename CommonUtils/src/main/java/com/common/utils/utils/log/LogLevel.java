package com.common.utils.utils.log;

import android.util.Log;

/**
 * 日志等级
 *
 * @author LiuFeng
 * @date 2018-9-01
 */
public enum LogLevel {

    /**
     * VERBOSE 等级。
     */
    VERBOSE(Log.VERBOSE),

    /**
     * Debug 等级。
     */
    DEBUG(Log.DEBUG),

    /**
     * Info 等级。
     */
    INFO(Log.INFO),

    /**
     * Warn 等级。
     */
    WARN(Log.WARN),

    /**
     * Error 等级。
     */
    ERROR(Log.ERROR),

    /**
     * ASSERT 等级。
     */
    ASSERT(Log.ASSERT);

    private int code;

    LogLevel(int code) {
        this.code = code;
    }

    /**
     * 解析相应等级
     *
     * @param code
     *
     * @return
     */
    public static LogLevel parse(int code) {
        for (LogLevel level : values()) {
            if (level.getCode() == code) {
                return level;
            }
        }
        throw new IllegalArgumentException("LogLevel code is illegal.");
    }

    public int getCode() {
        return this.code;
    }
}
