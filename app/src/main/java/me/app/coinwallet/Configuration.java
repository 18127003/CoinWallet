package me.app.coinwallet;

import android.content.SharedPreferences;
import android.content.res.Resources;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Configuration {
    public final int lastVersionCode;
    public final File directory;
    public final NetworkParameters parameters = TestNet3Params.get();

    private final SharedPreferences prefs;
    private final Resources res;
    private static final String PREFS_KEY_LAST_VERSION = "last_version";

    private static final Logger log = LoggerFactory.getLogger(Configuration.class);

    public Configuration(final SharedPreferences prefs, final Resources res, final File directory) {
        this.prefs = prefs;
        this.res = res;
        this.directory = directory;
        this.lastVersionCode = prefs.getInt(PREFS_KEY_LAST_VERSION, 0);
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
