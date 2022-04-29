package me.app.coinwallet;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import androidx.multidex.MultiDexApplication;
import androidx.preference.PreferenceManager;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import me.app.coinwallet.workers.BitcoinDownloadWorker;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.utils.Threading;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WalletApplication extends MultiDexApplication {
    private ActivityManager activityManager;
    private Configuration config;

    private static final Logger log = LoggerFactory.getLogger(WalletApplication.class);

    @Override
    public void onCreate() {

        super.onCreate();

        final PackageInfo packageInfo = packageInfo();

        Threading.uncaughtExceptionHandler = (thread, throwable) -> log.info("bitcoinj uncaught exception", throwable);

        activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        final Configuration config = getConfiguration();
        config.updateLastVersionCode(packageInfo.versionCode);
    }

    public synchronized Configuration getConfiguration() {
        if (config == null)
            config = new Configuration(PreferenceManager.getDefaultSharedPreferences(this), getResources(), getFilesDir());
        return config;
    }

    private PackageInfo packageInfo;

    public synchronized PackageInfo packageInfo() {
        if (packageInfo == null) {
            try {
                packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            } catch (final PackageManager.NameNotFoundException x) {
                throw new RuntimeException(x);
            }
        }
        return packageInfo;
    }
}
