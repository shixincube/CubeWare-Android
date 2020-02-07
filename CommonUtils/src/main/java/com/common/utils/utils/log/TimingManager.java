package com.common.utils.utils.log;

import android.os.SystemClock;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 一个实用程序类，用来帮助记录在方法调用中的耗时。
 * 典型的用法是:
 *
 * <pre>
 *     TimingManager.getInstance().addRecord(label, "work");
 *     // ... do some work A ...
 *     TimingManager.getInstance().addRecord(label, "work A");
 *     // ... do some work B ...
 *     TimingManager.getInstance().addRecord(label, "work B");
 *     // ... do some work C ...
 *     TimingManager.getInstance().addRecord(label, "work C");
 *     TimingManager.getInstance().toLogTime(priority, label);
 * </pre>
 *
 * <p>toLog调用会将以下内容添加到日志中:</p>
 *
 * <pre>
 *     D/TAG     ( 3459): methodA: begin
 *     D/TAG     ( 3459): methodA:      9 ms, work A
 *     D/TAG     ( 3459): methodA:      1 ms, work B
 *     D/TAG     ( 3459): methodA:      6 ms, work C
 *     D/TAG     ( 3459): methodA: end, 16 ms
 * </pre>
 *
 * @author LiuFeng
 * @date 2018-8-30
 */
public class TimingManager {
    private static String TAG = "DEFAULT_TAG";

    private boolean isToLog      = false;       // 是否打印到控制台
    private int     recordMaxNum = 1000;        // 单个标签可以记录的最大数量

    private final StringBuilder                 buffer    = new StringBuilder();    // 字符串处理
    private       Map<String, List<RecordTime>> recordMap = new HashMap<>();        // 记录容器

    private static TimingManager instance = new TimingManager();

    /**
     * 单例
     *
     * @return
     */
    public static TimingManager getInstance() {
        return instance;
    }

    /**
     * 添加记录
     *
     * @param label   记录的标签
     * @param message 记录的日志内容。
     */
    public synchronized void addRecord(String label, String message) {
        // 时间记录
        long now = SystemClock.elapsedRealtime();
        RecordTime recordTime = new RecordTime();
        recordTime.message = message;
        recordTime.time = now;

        // 存入记录时间容器
        List<RecordTime> recordTimeList = handleRecordTimes(label);
        recordTimeList.add(recordTime);
    }

    /**
     * 处理记录数据集合（数据过多时释放）
     *
     * @param label
     *
     * @return
     */
    private List<RecordTime> handleRecordTimes(String label) {
        // 不包含此标签时，先创建集合
        if (!recordMap.containsKey(label)) {
            recordMap.put(label, new ArrayList<RecordTime>());
        }

        // 时间记录
        List<RecordTime> recordTimeList = recordMap.get(label);

        // 非空，且大于最大等于记录数
        if (recordTimeList.size() > 0 && recordTimeList.size() >= recordMaxNum) {
            String desc = "Add too many records! Please toLogTime. record num:" + recordTimeList.size();
            LogUtil.e(desc);

            // 执行打印记录，并清除标签
            LogUtil.toLogTime(label, desc);

            // 取出记录列表中第一条数据，存入新集合
            RecordTime firstRecordTime = recordTimeList.get(0);
            recordTimeList.clear();
            recordTimeList.add(firstRecordTime);
            recordMap.put(label, recordTimeList);
        }

        return recordTimeList;
    }

    /**
     * 获取记录数据
     *
     * @param label
     *
     * @return
     */
    private synchronized String getRecord(String label) {
        // 记录容器
        List<RecordTime> recordTimeList = recordMap.get(label);

        if (recordTimeList == null || recordTimeList.isEmpty()) {
            return label + ": 没有记录";
        }

        // 组装数据
        buffer.append("\n");
        buffer.append("标签: ");
        buffer.append(label);
        buffer.append(": 开始");
        buffer.append("\n");
        long first = recordTimeList.get(0).time;
        long now = first;
        int total = recordTimeList.size();
        long last = recordTimeList.get(total - 1).time;
        int maxSpaceLength = String.valueOf(last - first).length();

        // 取出数据计算差值并拼接
        for (int i = 0; i < total; i++) {
            now = recordTimeList.get(i).time;
            long prev = i == 0 ? first : recordTimeList.get(i - 1).time;
            long spaceTime = now - prev;
            int currentSpaceLength = String.valueOf(spaceTime).length();

            // 拼接
            buffer.append(getBlank(14 + (maxSpaceLength - currentSpaceLength)));
            buffer.append(spaceTime);
            buffer.append(" ms");
            buffer.append("  message: ");
            buffer.append(recordTimeList.get(i).message);
            buffer.append("\n");
        }

        buffer.append("结束: 总耗时: ");
        buffer.append(now - first);
        buffer.append(" ms");
        buffer.append("  记录次数: ");
        buffer.append(total);
        buffer.append(" 次");

        String record = buffer.toString();

        // 清空buffer下次使用
        buffer.delete(0, buffer.length());

        return record;
    }

    /**
     * 获取指定数量空格
     *
     * @param spaces
     *
     * @return
     */
    private static String getBlank(int spaces) {
        String number = spaces <= 0 ? "" : String.valueOf(spaces);
        return String.format("%" + number + "s", "");
    }

    /**
     * 打印记录时间
     *
     * @param priority
     * @param label
     *
     * @return
     */
    public synchronized String toLogTime(int priority, String label) {
        return toLogTime(priority, label, "");
    }

    /**
     * 打印记录时间
     *
     * @param priority
     * @param label
     * @param msg
     *
     * @return
     */
    public synchronized String toLogTime(int priority, String label, String msg) {
        // 记录时间容器
        List<RecordTime> recordTimeList = recordMap.get(label);

        // 判断数据集合小于最大值时，可追加一条表示结束的记录
        // 如果不判断就追加数据，当超过最大值时将产生死循环
        if (recordTimeList == null || recordTimeList.size() < recordMaxNum) {
            addRecord(label, "to log time. " + msg);
        }

        // 调取记录
        String record = getRecord(label);

        // 打印到控制台
        if (isToLog) {
            Log.println(priority, TAG, record);
        }

        // 调取打印后清除此标记数据
        remove(label);
        return record;
    }

    /**
     * 删除键
     *
     * @param label
     */
    public synchronized void remove(String label) {
        recordMap.remove(label);
    }

    /**
     * 清空全部数据
     */
    public synchronized void clearAll() {
        recordMap.clear();
    }
}
