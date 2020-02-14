package com.common.utils.log;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 日志管理器。
 *
 * @author LiuFeng
 * @data 2018/9/20 11:46
 */
public final class LogManager {

    /** 日志处理器列表。 */
    private ArrayList<BaseLogHandle> handles = new ArrayList<>();

    /** 当前日志等级。 */
    private LogLevel level = LogLevel.DEBUG;

    /** 日志是否可用 */
    private boolean isLoggable = true;

    /** USB是否已连接 */
    private boolean isUsbConnected = true;

    /** 单线程池执行器 */
    private static ExecutorService executor = Executors.newSingleThreadExecutor();

    private static LogManager instance = new LogManager();

    /**
     * 构造函数。
     */
    private LogManager() {
        init();
    }

    /**
     * 初始化默认日志处理
     */
    private void init() {
        BaseLogHandle defaultLogHandle = new DefaultLogHandle();
        this.handles.add(defaultLogHandle);
    }

    /**
     * 获得管理器的单例。
     *
     * @return 返回管理器单例。
     */
    public static LogManager getInstance() {
        return instance;
    }

    /**
     * 设置日志等级。
     *
     * @param level 日志等级 {@link LogLevel} 。
     *
     * @see LogLevel
     */
    public void setLevel(LogLevel level) {
        this.level = level;
    }

    /**
     * 获得日志等级。
     *
     * @return 返回日志等级 {@link LogLevel} 。
     *
     * @see LogLevel
     */
    public LogLevel getLevel() {
        return this.level;
    }

    /**
     * 日志是否可用
     *
     * @return
     */
    public boolean isLoggable() {
        return isLoggable;
    }

    /**
     * 设置日志是否可用
     *
     * @param loggable
     */
    public void setLoggable(boolean loggable) {
        isLoggable = loggable;
    }

    /**
     * usb是否已连接
     *
     * @return
     */
    public boolean isUsbConnected() {
        return isUsbConnected;
    }

    /**
     * 设置usb连接状态
     *
     * @param usbConnected
     */
    public void setUsbConnect(boolean usbConnected) {
        isUsbConnected = usbConnected;
    }

    /**
     * 设置日志的tag
     *
     * @param tag
     */
    public void setLogTag(String tag) {
        for (BaseLogHandle handle : this.handles) {
            handle.setTag(tag);
        }
    }

    /**
     * 记录日志。
     *
     * @param level   指定该日志的记录等级。
     * @param tag     指定日志标签。
     * @param message 指定日志内容。
     */
    public void log(final LogLevel level, final String tag, final String message) {
        if (filterInvalidLog(level)) {
            return;
        }

        // 当前线程名
        final String threadName = Thread.currentThread().getName();
        // 当前线程堆栈信息
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        // 所有打印放入单线程池中，避免主线程阻塞
        executor.execute(new Runnable() {
            @Override
            public void run() {
                LogEvent logEvent = new LogEvent();
                logEvent.tag = tag;
                logEvent.level = level;
                logEvent.message = message;
                logEvent.timestamp = System.currentTimeMillis();
                logEvent.threadName = threadName;
                logEvent.stackTrace = stackTrace;

                for (BaseLogHandle handle : handles) {
                    // 默认日志打印时，非连接USB状态则跳过打印（提升性能）
                    if (handle instanceof DefaultLogHandle && !isUsbConnected) {
                        continue;
                    }

                    handle.log(logEvent);
                }
            }
        });
    }

    /**
     * 立刻刷入日志到文件
     */
    public void flushLog() {
        if (filterInvalidLog(LogLevel.INFO)) {
            return;
        }

        synchronized (this) {
            for (BaseLogHandle handle : this.handles) {
                // 判断是否磁盘日志操作器
                if (handle instanceof DiskLogHandle) {
                    ((DiskLogHandle) handle).flushLog();
                }
            }
        }
    }

    /**
     * 过滤无效日志
     *
     * @param level
     *
     * @return
     */
    private boolean filterInvalidLog(LogLevel level) {
        // 日志不可用
        if (!this.isLoggable) {
            return true;
        }

        // 过滤较低日志等级
        return level.getCode() < this.level.getCode();
    }

    /**
     * 获得指定名称的处理器。
     *
     * @param name 指定处理器名称。
     *
     * @return 返回指定名称的处理器。
     */
    public BaseLogHandle getHandle(String name) {
        synchronized (this) {
            for (BaseLogHandle handle : this.handles) {
                if (handle.getLogHandleName().equals(name)) {
                    return handle;
                }
            }
        }

        return null;
    }

    /**
     * 添加日志内容处理器。
     *
     * @param handle 需添加的日志处理器。
     */
    public void addHandle(BaseLogHandle handle) {
        synchronized (this) {
            if (this.handles.contains(handle)) {
                return;
            }

            for (BaseLogHandle h : this.handles) {
                if (h.getLogHandleName().equals(handle.getLogHandleName())) {
                    return;
                }
            }

            this.handles.add(handle);
        }
    }

    /**
     * 移除日志内容处理器。
     *
     * @param handle 需移除的日志处理器。
     */
    public void removeHandle(BaseLogHandle handle) {
        synchronized (this) {
            this.handles.remove(handle);
        }
    }

    /**
     * 移除所有日志内容处理器。
     */
    public void removeAllHandles() {
        synchronized (this) {
            this.handles.clear();
        }
    }
}
