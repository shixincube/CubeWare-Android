package com.common.utils.log;

import android.util.Log;
import java.util.Locale;

/**
 * 堆栈工具
 *
 * @author LiuFeng
 * @data 2018/11/6 11:15
 */
public class StackTraceUtil {

    /** 堆栈过滤源 **/
    private static final String STACK_TRACE_ORIGIN = LogUtil.class.getName();

    /**
     * 获取堆栈信息
     *
     * @param tr
     *
     * @return
     */
    public static String getStackTraceString(Throwable tr) {
        return Log.getStackTraceString(tr);
    }

    /**
     * 获取堆栈信息
     *
     * @param maxDepth 小于或等于0时将全部展示
     *
     * @return
     */
    public static String getStackTraceString(int maxDepth) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        return subStackTraceString(stackTrace, 0, maxDepth <= 0 ? stackTrace.length : maxDepth);
    }

    /**
     * 获取裁剪后堆栈信息
     * 备注：裁剪调了过滤源前的信息
     *
     * @param maxDepth 小于或等于0时将全部展示
     *
     * @return
     */
    public static String getCroppedStackTraceString(int maxDepth) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        // 取忽略堆栈深度
        int ignoreDepth = 0;
        int total = stackTrace.length;
        for (int i = 0; i < total; i++) {
            String className = stackTrace[i].getClassName();
            if (className.equals(STACK_TRACE_ORIGIN)) {
                ignoreDepth = i + 1;
                break;
            }
        }

        return subStackTraceString(stackTrace, ignoreDepth, maxDepth <= 0 ? total : ignoreDepth + maxDepth);
    }

    /**
     * 获取堆栈信息
     *
     * @param stackTrace
     * @param start
     *
     * @return
     */
    public static String subStackTraceString(StackTraceElement[] stackTrace, int start) {
        return subStackTraceString(stackTrace, start, stackTrace != null ? stackTrace.length : 0);
    }

    /**
     * 截取堆栈信息
     *
     * @param stackTrace
     * @param start
     * @param end
     *
     * @return
     */
    public static String subStackTraceString(StackTraceElement[] stackTrace, int start, int end) {
        if (stackTrace == null || stackTrace.length == 0) {
            return "stackTrace is empty";
        }

        // start和end无效值处理
        int total = stackTrace.length;
        if (start < 0 || start >= total) {
            start = 0;
        }
        if (end <= 0 || end > total) {
            end = total;
        }

        // 拼装堆栈数据
        StringBuilder builder = new StringBuilder();
        for (int i = start; i < end; i++) {
            builder.append(getStackTraceString(stackTrace[i]));
            builder.append("\n");
        }
        return builder.toString();
    }

    /**
     * 获取堆栈信息
     *
     * @param element 堆栈数据
     *
     * @return
     */
    private static String getStackTraceString(StackTraceElement element) {
        String format = "at %s.%s(%s:%d)";
        String className = element.getClassName();
        String fileName = element.getFileName();
        String methodName = element.getMethodName();
        int methodLine = element.getLineNumber();
        return String.format(Locale.CHINESE, format, className, methodName, fileName, methodLine);
    }
}
