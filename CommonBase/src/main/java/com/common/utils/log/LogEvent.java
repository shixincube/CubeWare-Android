package com.common.utils.log;

/**
 * 日志事件
 *
 * @author LiuFeng
 * @data 2019/3/12 10:49
 */
public class LogEvent {
    public String              tag;
    public LogLevel            level;
    public String              message;
    public Long                timestamp;
    public String              threadName;
    public Integer             stackIndex;
    public StackTraceElement[] stackTrace;
}
