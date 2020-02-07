package com.common.utils.utils.log;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * 日志工具类
 *
 * @author LiuFeng
 * @data 2018/9/20 11:46
 */
public final class LogUtil {

    private LogUtil() {}

    /**
     * 打印 DEBUG 级别日志。
     *
     * @param msg 日志内容。
     */
    public static void d(String msg) {
        commonLog(LogLevel.DEBUG, "", msg);
    }

    /**
     * 打印 DEBUG 级别日志。
     *
     * @param tag 该条日志的标签。
     * @param msg 日志内容。
     */
    public static void d(String tag, String msg) {
        commonLog(LogLevel.DEBUG, tag, msg);
    }

    /**
     * 打印 INFO 级别日志。
     *
     * @param msg 日志内容。
     */
    public static void i(String msg) {
        commonLog(LogLevel.INFO, "", msg);
    }

    /**
     * 打印 INFO 级别日志。
     *
     * @param tag 该条日志的标签。
     * @param msg 日志内容。
     */
    public static void i(String tag, String msg) {
        commonLog(LogLevel.INFO, tag, msg);
    }

    /**
     * 打印 INFO 级别日志和其堆栈深度信息
     *
     * @param msg      日志内容。
     * @param maxDepth 堆栈深度
     */
    public static void i(String msg, int maxDepth) {
        commonLog(LogLevel.INFO, "", msg + "\n" + StackTraceUtil.getCroppedStackTraceString(maxDepth));
    }

    /**
     * 打印 INFO 级别日志和其堆栈深度信息
     *
     * @param tag      该条日志的标签。
     * @param msg      日志内容。
     * @param maxDepth 堆栈深度
     */
    public static void i(String tag, String msg, int maxDepth) {
        commonLog(LogLevel.INFO, tag, msg + "\n" + StackTraceUtil.getCroppedStackTraceString(maxDepth));
    }

    /**
     * 打印 WARNING 级别日志。
     *
     * @param msg 日志内容。
     */
    public static void w(String msg) {
        commonLog(LogLevel.WARN, "", msg);
    }

    /**
     * 打印 WARNING 级别日志。
     *
     * @param tag 该条日志的标签。
     * @param msg 日志内容。
     */
    public static void w(String tag, String msg) {
        commonLog(LogLevel.WARN, tag, msg);
    }

    /**
     * 打印 WARNING 级别日志。
     *
     * @param throwable 日志包含的异常。
     */
    public static void w(Throwable throwable) {
        commonLog(LogLevel.WARN, "", getExceptionLog("", throwable));
    }

    /**
     * 打印 WARNING 级别日志。
     *
     * @param tag       该条日志的标签。
     * @param throwable 日志包含的异常。
     */
    public static void w(String tag, Throwable throwable) {
        commonLog(LogLevel.WARN, tag, getExceptionLog("", throwable));
    }

    /**
     * 打印 ERROR 级别日志。
     *
     * @param msg 日志内容。
     */
    public static void e(String msg) {
        commonLog(LogLevel.ERROR, "", msg);
    }

    /**
     * 打印 ERROR 级别日志。
     *
     * @param tag 该条日志的标签。
     * @param msg 日志内容。
     */
    public static void e(String tag, String msg) {
        commonLog(LogLevel.ERROR, tag, msg);
    }

    /**
     * 打印 ERROR 级别日志和其堆栈深度信息
     *
     * @param tag      该条日志的标签。
     * @param msg      日志内容。
     * @param maxDepth 堆栈深度
     */
    public static void e(String tag, String msg, int maxDepth) {
        commonLog(LogLevel.ERROR, tag, msg + "\n" + StackTraceUtil.getCroppedStackTraceString(maxDepth));
    }

    /**
     * 打印 ERROR 级别日志。
     *
     * @param throwable
     */
    public static void e(Throwable throwable) {
        commonLog(LogLevel.ERROR, "", getExceptionLog("", throwable));
    }

    /**
     * 打印 ERROR 级别日志。
     *
     * @param throwable
     */
    public static void e(String tag, Throwable throwable) {
        commonLog(LogLevel.ERROR, tag, getExceptionLog("", throwable));
    }

    /**
     * 打印 ERROR 级别日志。
     *
     * @param tag
     * @param msg
     * @param throwable
     */
    public static void e(String tag, String msg, Throwable throwable) {
        commonLog(LogLevel.ERROR, tag, getExceptionLog(msg, throwable));
    }

    /**
     * 打印格式化json信息
     *
     * @param json
     */
    public static void json(String json) {
        commonLog(LogLevel.INFO, "", getFormatJson(json));
    }

    /**
     * 打印格式化json信息
     *
     * @param jsonObject
     */
    public static void json(JSONObject jsonObject) {
        if (jsonObject == null) {
            commonLog(LogLevel.INFO, "", "Empty/Null JSONObject");
            return;
        }

        commonLog(LogLevel.INFO, "", getFormatJson(jsonObject.toString()));
    }

    /**
     * 打印格式化json信息
     *
     * @param tag
     * @param json
     */
    public static void json(String tag, String json) {
        commonLog(LogLevel.INFO, tag, getFormatJson(json));
    }

    /**
     * 打印格式化json信息
     *
     * @param tag
     * @param jsonObject
     */
    public static void json(String tag, JSONObject jsonObject) {
        if (jsonObject == null) {
            commonLog(LogLevel.INFO, tag, "Empty/Null JSONObject");
            return;
        }

        commonLog(LogLevel.INFO, tag, getFormatJson(jsonObject.toString()));
    }

    /**
     * 打印格式化json信息
     *
     * @param jsonObject
     */
    public static void json(LogLevel level, String tag, JSONObject jsonObject) {
        if (jsonObject == null) {
            commonLog(level, tag, getFormatJson("Empty/Null JSONObject"));
            return;
        }

        commonLog(level, tag, getFormatJson(jsonObject.toString()));
    }

    /**
     * 打印格式化json信息
     *
     * @param level
     * @param tag
     * @param json
     */
    public static void json(LogLevel level, String tag, String json) {
        commonLog(level, tag, getFormatJson(json));
    }

    /**
     * 获取格式化json
     *
     * @param jsonObject
     *
     * @return
     */
    public static String getFormatJson(JSONObject jsonObject) {
        if (jsonObject == null) {
            return "Empty/Null JSONObject";
        }

        return getFormatJson(jsonObject.toString());
    }

    /**
     * 获取格式化json
     *
     * @param json
     *
     * @return
     */
    public static String getFormatJson(String json) {
        if (TextUtils.isEmpty(json)) {
            return "Empty/Null json";
        }

        String content = json.trim();
        JSONException exception = null;
        try {
            Object object = new JSONTokener(content).nextValue();
            if (object instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) object;
                content = jsonObject.toString(2);
            }
            else if (object instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) object;
                content = jsonArray.toString(2);
            }
            else {
                content = "Invalid Json: " + json;
            }
        } catch (JSONException e) {
            exception = e;
        }

        if (exception != null) {
            commonLog(LogLevel.ERROR, "", getExceptionLog(json, exception));
        }

        return content;
    }

    /**
     * 打印格式化xml信息
     *
     * @param xml
     */
    public static void xml(String xml) {
        commonLog(LogLevel.INFO, "", getFormatXml("", xml));
    }

    /**
     * 打印格式化xml信息
     *
     * @param tag
     * @param xml
     */
    public static void xml(String tag, String xml) {
        commonLog(LogLevel.INFO, tag, getFormatXml(tag, xml));
    }

    /**
     * 打印格式化xml信息
     *
     * @param level
     * @param tag
     * @param xml
     */
    public static void xml(LogLevel level, String tag, String xml) {
        commonLog(level, tag, getFormatXml(tag, xml));
    }

    /**
     * 获取格式化xml
     *
     * @param tag
     * @param xml
     *
     * @return
     */
    public static String getFormatXml(String tag, String xml) {
        if (TextUtils.isEmpty(xml)) {
            return "Empty/Null xml content";
        }

        String content = xml.trim();
        TransformerException exception = null;
        try {
            Source xmlInput = new StreamSource(new StringReader(content));
            StreamResult xmlOutput = new StreamResult(new StringWriter());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(xmlInput, xmlOutput);
            content = xmlOutput.getWriter().toString().replaceFirst(">", ">\n");
        } catch (TransformerException e) {
            exception = e;
        }

        if (exception != null) {
            commonLog(LogLevel.ERROR, tag, getExceptionLog(xml, exception));
        }

        return content;
    }

    /**
     * 添加记录
     *
     * 备注：此方法用于常见的需打印方法执行前后耗时的场景
     * 典型的用法是:
     *
     * <pre>
     *     LogUtil.addRecord(label, "work");
     *     // ... do some work A ...
     *     LogUtil.addRecord(label, "work A");
     *     // ... do some work B ...
     *     LogUtil.addRecord(label, "work B");
     *     // ... do some work C ...
     *     LogUtil.addRecord(label, "work C");
     *     LogUtil.toLogTime(label);
     * </pre>
     *
     * <p>toTimingLog调用会将以下内容添加到日志中:</p>
     *
     * <pre>
     *     D/TAG     ( 3459): methodA: begin
     *     D/TAG     ( 3459): methodA:      9 ms, work A
     *     D/TAG     ( 3459): methodA:      1 ms, work B
     *     D/TAG     ( 3459): methodA:      6 ms, work C
     *     D/TAG     ( 3459): methodA: end, 16 ms
     * </pre>
     *
     * @param label   记录的标签
     * @param message 记录的日志内容。
     */
    public static void addRecord(String label, String message) {
        TimingManager.getInstance().addRecord(label, message);
    }

    /**
     * 执行时间记录打印
     *
     * 备注：此方法用于常见的需打印方法执行前后耗时的场景
     *
     * @param label
     */
    public static void toLogTime(String label) {
        commonLog(LogLevel.INFO, "", TimingManager.getInstance().toLogTime(Log.INFO, label));
    }

    /**
     * 执行时间记录打印
     *
     * @param label
     * @param msg
     */
    public static void toLogTime(String label, String msg) {
        commonLog(LogLevel.INFO, "", TimingManager.getInstance().toLogTime(Log.INFO, label, msg));
    }

    /**
     * 获取异常信息的log
     *
     * @param msg
     * @param throwable
     *
     * @return
     */
    private static String getExceptionLog(String msg, Throwable throwable) {
        return msg + "\nBe Caught exception: " + StackTraceUtil.getStackTraceString(throwable);
    }

    /**
     * 公用日志打印方法
     *
     * @param level
     * @param tag
     * @param msg
     */
    private static void commonLog(LogLevel level, String tag, String msg) {
        LogManager.getInstance().log(level, tag, msg);
    }

    /**
     * 日志是否可用
     *
     * @return
     */
    public static boolean isLoggable() {
        return LogManager.getInstance().isLoggable();
    }

    /**
     * 设置日志是否可用
     *
     * @param loggable
     */
    public static void setLoggable(boolean loggable) {
        LogManager.getInstance().setLoggable(loggable);
    }

    /**
     * 设置usb连接状态
     *
     * @param usbConnected
     */
    public static void setUsbConnect(boolean usbConnected) {
        LogManager.getInstance().setUsbConnect(usbConnected);
    }

    /**
     * 设置全局tag
     *
     * @param tag
     */
    public static void setLogTag(String tag) {
        LogManager.getInstance().setLogTag(tag);
    }

    /**
     * 添加普通日志
     */
    public static void addCommonLogHandle() {
        BaseLogHandle defaultLogHandle = new DefaultLogHandle();
        LogManager.getInstance().addHandle(defaultLogHandle);
    }

    /**
     * 添加文件日志
     */
    public static void addDiskLogHandle(Context context, String folderPath) {
        BaseLogHandle diskLogHandle = new DiskLogHandle(context, folderPath);
        LogManager.getInstance().addHandle(diskLogHandle);
    }

    /**
     * 删除所有日志处理
     */
    public static void removeAllHandles() {
        LogManager.getInstance().removeAllHandles();
    }

    /**
     * 立刻刷入日志到文件
     */
    public static void flushLog() {
        LogManager.getInstance().flushLog();
    }
}
