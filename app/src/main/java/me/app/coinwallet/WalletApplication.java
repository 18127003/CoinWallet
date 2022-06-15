package me.app.coinwallet;

import android.content.*;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import androidx.multidex.MultiDexApplication;
import androidx.preference.PreferenceManager;
import me.app.coinwallet.utils.BiometricUtil;
import me.app.coinwallet.utils.LocaleUtil;
import org.bitcoinj.utils.Threading;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WalletApplication extends MultiDexApplication {

    private Configuration config;

    private static final Logger log = LoggerFactory.getLogger(WalletApplication.class);

    @Override
    public void onCreate() {

        super.onCreate();

        final PackageInfo packageInfo = packageInfo();

        Threading.uncaughtExceptionHandler = (thread, throwable) -> log.info("bitcoinj uncaught exception", throwable);

        final Configuration config = getConfiguration();
        config.updateLastVersionCode(packageInfo.versionCode);
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

    @Override
    public void onTerminate() {
        super.onTerminate();

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtil.onAttach(base, "en"));
    }
}
