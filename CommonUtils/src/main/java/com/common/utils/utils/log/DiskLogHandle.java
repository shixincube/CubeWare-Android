package com.common.utils.utils.log;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 磁盘文件日志操作器--写入文件
 *
 * @author LiuFeng
 * @date 2018-9-01
 */
public class DiskLogHandle extends BaseLogHandle {

    private final static String LOG_FILE_NAME      = "yyyy-MM-dd-HH";     // 日志文件名格式
    private static final int    EMPTY_WHAT         = 100;                 // 空消息what
    private static final int    LOG_WHAT           = 200;                 // 日志消息what
    private static final long   SPACE_TIME         = 1000;                // 间隔1s打印
    private static final long   ONE_DAY_TIME       = 24 * 60 * 60 * 1000; // 一天的时间
    private static final int    MAX_CHAR_NUM       = 500 * 1024 / 2;      // 500kb的汉字数量
    private static final int    MAX_BYTES          = 1024 * 1024;         // 单个日志文件1Mb
    private static final int    SAVE_LOG_MAX_BYTES = 100 * 1024 * 1024;   // 目录保存日志文件最大100Mb
    private static final int    SAVE_OF_DAYS       = 15;                  // 日志保存天数

    private long    lastLogTime;    // 上次打印日志时间戳
    private long    lastDelTime;    // 上次删除文件时间戳
    private Handler handler;
    private Context mContext;
    private String  mFolderPath;

    public DiskLogHandle(Context context, String folderPath) {
        this.mContext = context;
        this.mFolderPath = folderPath;
        HandlerThread thread = new HandlerThread("DiskLogHandle");
        thread.start();
        handler = new WriteHandler(thread.getLooper());
    }

    @Override
    public synchronized void log(LogEvent logEvent) {
        buffer.append(buffer.length() > 0 ? "\n" : "");
        buffer.append(TAG);
        buffer.append(": ");
        buffer.append("[");
        buffer.append(logEvent.level.name());
        buffer.append("] ");
        buffer.append(getDateTime(logEvent.timestamp));
        buffer.append(" ");
        buffer.append(getStackTrace(logEvent.threadName, logEvent.stackTrace, 5));
        buffer.append(" ");
        buffer.append(logEvent.tag);
        buffer.append(" ");
        buffer.append(logEvent.message);
        buffer.append("\n");

        handleLog();
    }

    /**
     * 处理日志数据
     */
    private void handleLog() {
        boolean isMoreThanMax = buffer.length() >= MAX_CHAR_NUM;
        if (!handler.hasMessages(EMPTY_WHAT) || isMoreThanMax) {
            // 超过最大字符数量或距上次打印超过1s，则立即打印和发一个延时消息
            if (isMoreThanMax || System.currentTimeMillis() - lastLogTime > SPACE_TIME) {
                sendLogMessage();
                handler.sendEmptyMessageDelayed(EMPTY_WHAT, SPACE_TIME);
            }
            else {
                handler.sendEmptyMessageDelayed(EMPTY_WHAT, SPACE_TIME);
            }
        }
    }

    /**
     * 发送日志消息
     */
    private synchronized void sendLogMessage() {
        int length = buffer.length();
        if (length > 0) {
            handler.sendMessage(handler.obtainMessage(LOG_WHAT, buffer.toString()));

            // 日志发送后清空buffer
            buffer.delete(0, length);

            // 赋新值
            lastLogTime = System.currentTimeMillis();
        }
    }

    /**
     * 立刻刷入日志到文件
     */
    public void flushLog() {
        sendLogMessage();
    }

    /**
     * 写入日志文件处理者
     */
    class WriteHandler extends Handler {

        WriteHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            // 空消息
            if (msg.what == EMPTY_WHAT) {
                sendLogMessage();
                return;
            }

            // 判断写入权限
            if (checkPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                String content = (String) msg.obj;

                // 处理文件:过期或过大
                handleFile();

                // 通过时间戳生成文件名
                String fileName = String.valueOf(DateFormat.format(LOG_FILE_NAME, System.currentTimeMillis()));
                File logFile = getLogFile(mFolderPath, fileName);
                // 写入日志内容到文件
                writeLogToFile(logFile, content);
            }
        }
    }

    /**
     * 权限检测
     *
     * @param permission
     *
     * @return
     */
    private boolean checkPermission(Context context, String permission) {
        if (context == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 处理文件:删除过期或过大文件
     */
    private void handleFile() {
        long currentTime = System.currentTimeMillis();
        // 判断上次执行删除是否在一天以前
        if ((currentTime - lastDelTime) > ONE_DAY_TIME) {
            List<File> fileList = listFilesInDir(new File(mFolderPath));
            if (fileList != null && !fileList.isEmpty()) {
                try {
                    // 按修改时间降序排序
                    Collections.sort(fileList, new Comparator<File>() {
                        @Override
                        public int compare(File o1, File o2) {
                            long diff = o2.lastModified() - o1.lastModified();
                            return diff > 0 ? 1 : diff == 0 ? 0 : -1;
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "sort log files: ", e);
                }

                long totalLength = 0;
                // 过期限定时间
                long limitTime = currentTime - SAVE_OF_DAYS * ONE_DAY_TIME;
                for (File file : fileList) {
                    totalLength += file.length();
                    // 根据过期或文件总大小判断，执行删除
                    if (file.lastModified() < limitTime || totalLength > SAVE_LOG_MAX_BYTES) {
                        file.delete();
                    }
                }
            }

            // 保存删除时间
            this.lastDelTime = currentTime;
        }
    }

    /**
     * 流出文件目录下所有文件
     *
     * @param dir
     *
     * @return
     */
    private List<File> listFilesInDir(File dir) {
        if (!isDir(dir)) {
            return null;
        }

        List<File> list = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            list.addAll(Arrays.asList(files));
        }
        return list;
    }

    /**
     * 判断是否是目录
     *
     * @param file 文件
     *
     * @return {@code true}: 是<br>{@code false}: 否
     */
    private boolean isDir(final File file) {
        return file != null && file.exists() && file.isDirectory();
    }

    /**
     * 获取日志文件--文件大小超过指定大小时新建一个文件
     *
     * @param folderName
     * @param fileName
     *
     * @return
     */
    private static File getLogFile(String folderName, String fileName) {
        File folder = new File(folderName);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        int newFileCount = 0;
        File newFile;
        File existingFile = null;

        newFile = new File(folder, String.format("%s.txt", fileName));
        while (newFile.exists()) {
            existingFile = newFile;
            newFileCount++;
            newFile = new File(folder, String.format("%s(%s).txt", fileName, newFileCount));
        }

        if (existingFile != null) {
            if (existingFile.length() >= MAX_BYTES) {
                return newFile;
            }
            return existingFile;
        }

        return newFile;
    }

    /**
     * 获取包含错误文件名的日志文件--文件大小超过指定大小时新建一个文件
     *
     * @param folderName
     * @param fileName
     * @param level
     *
     * @return
     */
    private static File getErrorLogFile(String folderName, String fileName, LogLevel level) {
        File folder = new File(folderName);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        int newFileCount = 0;
        File newFile;
        File existingFile = null;

        String normalFormat = "%s%s.txt";
        String warnFormat = LogLevel.WARN.name() + "_%s%s.txt";
        String errorFormat = LogLevel.ERROR.name() + "_%s%s.txt";
        while (true) {
            // 后缀名
            String postfix = newFileCount == 0 ? "" : "(" + newFileCount + ")";

            // 普通文件
            File normalFile = new File(folder, String.format(normalFormat, fileName, postfix));
            if (normalFile.exists()) {
                existingFile = normalFile;
                newFileCount++;
                continue;
            }

            // 错误文件
            File errorFile = new File(folder, String.format(errorFormat, fileName, postfix));
            if (errorFile.exists()) {
                existingFile = errorFile;
                newFileCount++;
                continue;
            }

            // 警告文件
            File warnFile = new File(folder, String.format(warnFormat, fileName, postfix));
            if (warnFile.exists()) {
                existingFile = warnFile;
                newFileCount++;
                continue;
            }

            // 新文件级别
            switch (level) {
                case ERROR:
                    newFile = errorFile;
                    break;

                case WARN:
                    newFile = warnFile;
                    break;

                default:
                    newFile = normalFile;
                    break;
            }
            break;
        }

        if (existingFile != null) {
            // 超过指定文件大小则启用新文件
            if (existingFile.length() >= MAX_BYTES) {
                return newFile;
            }

            String name = existingFile.getName();
            boolean containLevel = false;
            for (LogLevel logLevel : LogLevel.values()) {
                // 判断文件前缀日志等级
                if (name.startsWith(logLevel.name())) {
                    containLevel = true;
                    // 当前将打印日志等级大于文件原先等级
                    if (level.getCode() > logLevel.getCode()) {
                        File dest = new File(existingFile.getParent() + File.separator + name.replace(logLevel.name(), level.name()));
                        // 修改文件前缀日志等级
                        if (existingFile.renameTo(dest)) {
                            return dest;
                        }
                    }
                    break;
                }
            }

            // 不包含日志等级文件添加前缀日志等级
            if (!containLevel && level.getCode() >= LogLevel.WARN.getCode()) {
                File dest = new File(existingFile.getParent() + File.separator + level.name() + "_" + name);
                if (existingFile.renameTo(dest)) {
                    return dest;
                }
            }
            return existingFile;
        }

        return newFile;
    }

    /**
     * 写入日志到文件
     *
     * @param logFile
     * @param content
     */
    private static void writeLogToFile(File logFile, String content) {
        if (logFile == null || TextUtils.isEmpty(content)) {
            return;
        }

        // 不存在时先创建文件
        if (!logFile.exists()) {
            try {
                if (!logFile.createNewFile()) {
                    return;
                }
            } catch (IOException e) {
                Log.e(TAG, "createNewFile: ", e);
                return;
            }
        }

        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(logFile, true));
            bw.write(content);
            bw.newLine();
            bw.flush();
        } catch (IOException e) {
            Log.e(TAG, "writeLogToFile: ", e);
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    Log.e(TAG, "writeLogToFile: ", e);
                }
            }
        }
    }
}
