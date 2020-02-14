package com.common.utils.log;

import android.util.Log;

/**
 * 默认日志处理器--打印到控制台
 *
 * @author LiuFeng
 * @data 2018/9/20 11:43
 */
public class DefaultLogHandle extends BaseLogHandle {

    @Override
    public void log(LogEvent logEvent) {
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

        print(logEvent.level.getCode(), TAG, buffer.toString());

        buffer.delete(0, buffer.length());
    }

    /**
     * 超4000字符时分段打印
     *
     * @param priority
     * @param tag
     * @param content
     */
    private void print(int priority, String tag, String content) {
        if (content == null) {
            content = "";
        }

        // 不超过4000时正常打印
        if (content.length() <= 4000) {
            Log.println(priority, tag, content);
            return;
        }

        int count = 1;
        // 超过4000时分段打印
        for (int i = 0; i < content.length(); i += 4000) {
            String desc = String.format("分段打印(%s):", count);
            String splitContent = i + 4000 < content.length() ? content.substring(i, i + 4000) : content.substring(i, content.length());
            Log.println(priority, tag, desc + splitContent);
            count++;
        }
    }
}
