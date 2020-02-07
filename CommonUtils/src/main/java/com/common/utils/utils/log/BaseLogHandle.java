package com.common.utils.utils.log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 日志操作器基类
 *
 * @author LiuFeng
 * @data 2018/9/20 11:45
 */
public abstract class BaseLogHandle {

    protected static String TAG = "DEFAULT_TAG";

    protected final StringBuilder buffer = new StringBuilder();

    private SimpleDateFormat timeFormat = new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.UK);

    private Date date = new Date();

    /**
     * 获得日志句柄名称（默认类名）
     *
     * @return 返回日志句柄名称。
     */
    public String getLogHandleName() {
        return this.getClass().getName();
    }

    /**
     * 日志打印
     *
     * @param logEvent 日志事件
     */
    public abstract void log(LogEvent logEvent);

    /**
     * 获取tag
     *
     * @return 全局日志标签。
     */
    public String getTag() {
        return TAG;
    }

    /**
     * 设置tag
     *
     * @param tag 全局日志标签。
     */
    public void setTag(String tag) {
        TAG = tag;
    }

    /**
     * 获取格式化时间
     *
     * @return
     */
    public String getDateTime(long timestamp) {
        date.setTime(timestamp);
        return timeFormat.format(date);
    }

    /**
     * 获取堆栈信息
     *
     * @param currentThread 当前线程
     * @param stackTraceArr 堆栈数组数据
     * @param deep          取堆栈数据深度(下标)
     *
     * @return
     */
    public String getStackTrace(String currentThread, StackTraceElement[] stackTraceArr, int deep) {
        if (stackTraceArr == null) {
            return "";
        }

        if (deep >= stackTraceArr.length) {
            return "Index Out of Bounds! deep:" + deep + " length:" + stackTraceArr.length;
        }

        StackTraceElement stackTrace = stackTraceArr[deep];
        String format = "[(%s:%d)# %s -> %s]";
        String fileName = stackTrace.getFileName();
        int methodLine = stackTrace.getLineNumber();
        String methodName = stackTrace.getMethodName();
        return String.format(Locale.CHINESE, format, fileName, methodLine, methodName, currentThread);
    }
}
