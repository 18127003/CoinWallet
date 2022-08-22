package me.app.coinwallet.blockchain;

import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.util.Log;

public class StartBlockchainSyncService extends JobService {
    private PowerManager pm;

    public static void schedule(final Application application, String action) {
        final JobScheduler jobScheduler = (JobScheduler) application.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        final JobInfo.Builder jobInfo = new JobInfo.Builder(0, new ComponentName(application,
                StartBlockchainSyncService.class));
        jobInfo.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        jobInfo.setRequiresDeviceIdle(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            jobInfo.setRequiresBatteryNotLow(true);
            jobInfo.setRequiresStorageNotLow(true);
        }
        PersistableBundle bundle = new PersistableBundle();
        bundle.putString("action", action);
        jobInfo.setExtras(bundle);
        jobScheduler.schedule(jobInfo.build());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
    }

    @Override
    public boolean onStartJob(final JobParameters params) {
        final boolean storageLow = registerReceiver(null, new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW)) != null;
        final boolean batteryLow = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_LOW)) != null;
        final boolean powerSaveMode = pm.isPowerSaveMode();
        if (storageLow)
            Log.e("HD","storage low, not starting block chain sync");
        if (batteryLow)
            Log.e("HD","battery low, not starting block chain sync");
        if (powerSaveMode)
            Log.e("HD","power save mode, not starting block chain sync");
        if (!storageLow && !batteryLow && !powerSaveMode){
            String action = params.getExtras().getString("action", BlockchainSyncService.ACTION_START_SYNC);
            Log.e("HD", "Start with action "+action);
            if(BlockchainSyncService.ACTION_START_SYNC.equals(action)){
                BlockchainSyncService.start(this);
            } else if (BlockchainSyncService.ACTION_RESTORE_MNEMONIC.equals(action)){
                BlockchainSyncService.restoreMnemonic(this);
            }
        }
        return false;
    }

    @Override
    public boolean onStopJob(final JobParameters params) {
        return false;
    }
}
