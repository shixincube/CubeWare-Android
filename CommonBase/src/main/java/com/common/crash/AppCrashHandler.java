package com.common.crash;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import com.common.utils.AppUtil;
import com.common.utils.DeviceUtil;
import com.common.utils.log.LogUtil;
import com.umeng.analytics.MobclickAgent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 崩溃处理
 *
 * @author LiuFeng
 * @date 2018-6-21
 */
public class AppCrashHandler implements Thread.UncaughtExceptionHandler {

    private static AppCrashHandler mInstance = new AppCrashHandler();

    private static final String FILE_NAME = "crash.log";
    private String mLogPath;

    private Context mContext;

    private AppCrashHandler() {}

    /**
     * 单例
     *
     * @return
     */
    public static AppCrashHandler getInstance() {
        return mInstance;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context, String logPath) {
        this.mContext = context.getApplicationContext();
        this.mLogPath = logPath;
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, final Throwable ex) {
        // 保存错误信息到SDCard
        this.saveExceptionToSDCard(ex, mLogPath);

        // 上传错误信息到服务器
        this.uploadExceptionToServer(ex);

        // 打印错误信息到控制台
        LogUtil.e("AppCrashHandler:" + ex.getMessage(), ex);
    }

    /**
     * 保存错误信息到SDCard
     *
     * @param ex
     */
    private void saveExceptionToSDCard(Throwable ex, String logPath) {
        try {
            if (TextUtils.isEmpty(logPath)) {
                return;
            }

            File logFile = new File(logPath + File.separator + FILE_NAME);
            if (!logFile.exists()) {
                logFile.createNewFile();
            }

            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(logFile)));
            // 打印发生异常的时间
            pw.println();
            pw.println("错误发生时间：" + new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss.SSS", Locale.US).format(new Date()));
            pw.println();

            // 打印手机信息
            pw.println("App版本号：" + AppUtil.getVersionCode(mContext));
            pw.println();
            pw.println("Android系统版本号：" + DeviceUtil.getBuildVersion());
            pw.println("Android手机制造商：" + DeviceUtil.getPhoneManufacturer());
            pw.println("Android手机品牌：" + DeviceUtil.getPhoneBrand());
            pw.println("Android手机型号：" + DeviceUtil.getPhoneModel());
            StringBuilder sb = new StringBuilder();
            sb.append("Android手机CPU：");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                for (String abi : DeviceUtil.getPhoneCPU()) {
                    sb.append(abi);
                    sb.append("，");
                }
            }
            sb.deleteCharAt(sb.length() - 1);
            pw.println(sb.toString());
            pw.println();

            // 打印错误信息
            ex.printStackTrace(pw);
            pw.println();

            pw.close();
        } catch (IOException e) {
            LogUtil.e(e);
        }
    }

    /**
     * 上传错误信息到服务器
     *
     * @param ex
     */
    private void uploadExceptionToServer(Throwable ex) {
        // 上传到友盟
        MobclickAgent.reportError(mContext, ex);
    }
}
