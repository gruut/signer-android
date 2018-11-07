package com.gruutnetworks.gruutsigner;

import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import com.gruutnetworks.gruutsigner.service.PollingService;

import java.util.concurrent.TimeUnit;

public class MainApplication extends Application {
    private static final int PING_JOB_ID = 50051;

    @Override
    public void onCreate() {
        super.onCreate();

        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(new JobInfo.Builder(PING_JOB_ID,
                new ComponentName(this, PollingService.class))
                .setPeriodic(TimeUnit.MINUTES.toMillis(15))
                .setPersisted(true)
                .build());

    }
}
