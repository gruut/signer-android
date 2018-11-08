package com.gruutnetworks.gruutsigner;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.gruutnetworks.gruutsigner.service.MyJobCreator;
import com.gruutnetworks.gruutsigner.service.PollingSyncJob;

import java.util.concurrent.TimeUnit;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            disableOptimizations(getApplicationContext());
        }

        JobManager.create(this).addJobCreator(new MyJobCreator());
        new JobRequest.Builder(PollingSyncJob.TAG)
                .setPeriodic(TimeUnit.MINUTES.toMillis(15))
                .setUpdateCurrent(true)
                .build()
                .schedule();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void disableOptimizations(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
        String packageName = context.getApplicationContext().getPackageName();
        boolean ignoringOptimizations = powerManager.isIgnoringBatteryOptimizations(packageName);

        if (ignoringOptimizations) {
            return;
        }

        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        intent.setData(Uri.parse("package:" + packageName));

        context.startActivity(intent);
    }
}
