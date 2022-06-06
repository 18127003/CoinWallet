package me.app.coinwallet;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Build;
import androidx.multidex.MultiDexApplication;
import androidx.preference.PreferenceManager;
import me.app.coinwallet.utils.BiometricUtil;
import me.app.coinwallet.utils.LocaleUtil;
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

        createNotificationChannel();
    }

    public synchronized Configuration getConfiguration() {
        if (config == null){
            BiometricUtil biometricUtil = new BiometricUtil(this);
            AssetManager assetManager = getAssets();
            SharedPreferences preferenceManager = PreferenceManager.getDefaultSharedPreferences(this);
            NotificationHandler notificationHandler = new NotificationHandler(this);
            config = new Configuration(preferenceManager, getFilesDir(), biometricUtil, notificationHandler, assetManager);
        }
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

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID_RECEIVE, name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.setLightColor(Color.CYAN);
            channel.enableVibration(true);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtil.onAttach(base, "en"));
    }
}
