package com.common.alive;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;

/**
 * Created by dth
 * Des: Android 5.0+ 使用的 JobScheduler 运行在 :watch 子进程中.
 * Date: 2018/10/19.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class JobSchedulerService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        if (!DaemonEnv.sInitialized) return false;
        DaemonEnv.startServiceMayBind(DaemonEnv.sServiceClass);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
