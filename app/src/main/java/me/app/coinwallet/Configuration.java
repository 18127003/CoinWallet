package me.app.coinwallet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import me.app.coinwallet.utils.BiometricUtil;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Configuration {
    public final int lastVersionCode;
    public final File directory;
    public final NetworkParameters parameters;
    public final ExecutorService executorService;
    public final BiometricUtil biometricUtil;
    public final NotificationHandler notificationHandler;
    public final AssetManager assetManager;

    private final SharedPreferences prefs;
    private static final String PREFS_KEY_LAST_VERSION = "last_version";
    private static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    private final static String FINGERPRINT_ENABLED = "fingerprint_enabled";
    private final static String BLOCKCHAIN_CHECKPOINTS = "org.bitcoin.test.checkpoints.txt";

    private static final Logger log = LoggerFactory.getLogger(Configuration.class);

    public Configuration(final SharedPreferences prefs, final File directory, final BiometricUtil biometricUtil,
                         NotificationHandler notificationHandler, AssetManager assetManager) {
        this.prefs = prefs;
        this.directory = directory;
        this.lastVersionCode = prefs.getInt(PREFS_KEY_LAST_VERSION, 0);
        this.notificationHandler = notificationHandler;
        this.parameters = TestNet3Params.get();
        this.executorService = Executors.newFixedThreadPool(NUMBER_OF_CORES);
        this.biometricUtil = biometricUtil;
        this.assetManager = assetManager;
    }

    public boolean isFingerprintEnabled(){
        return prefs.getBoolean(FINGERPRINT_ENABLED, false);
    }

    public void setFingerprintEnabled(boolean enabled){
        prefs.edit().putBoolean(FINGERPRINT_ENABLED, enabled).apply();
    }

    public InputStream getBlockchainCheckpointFile() {
        try {
            return assetManager.open(BLOCKCHAIN_CHECKPOINTS, AssetManager.ACCESS_STREAMING);
        } catch (IOException ioException) {
            return null;
        }
    }

    public void updateLastVersionCode(final int currentVersionCode) {
        prefs.edit().putInt(PREFS_KEY_LAST_VERSION, currentVersionCode).apply();

        if (currentVersionCode > lastVersionCode)
            log.info("detected app upgrade: " + lastVersionCode + " -> " + currentVersionCode);
        else if (currentVersionCode < lastVersionCode)
            log.warn("detected app downgrade: " + lastVersionCode + " -> " + currentVersionCode);
    }

    public void registerOnSharedPreferenceChangeListener(final SharedPreferences.OnSharedPreferenceChangeListener listener) {
        prefs.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterOnSharedPreferenceChangeListener(final SharedPreferences.OnSharedPreferenceChangeListener listener) {
        prefs.unregisterOnSharedPreferenceChangeListener(listener);
    }
}
