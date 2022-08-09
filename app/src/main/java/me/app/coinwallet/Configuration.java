package me.app.coinwallet;

import android.content.SharedPreferences;
import android.content.res.AssetManager;
import androidx.appcompat.app.AppCompatDelegate;
import me.app.coinwallet.data.configuration.ConfigurationOption;
import me.app.coinwallet.utils.BiometricUtil;
import me.app.coinwallet.utils.NotificationHandler;
import me.app.coinwallet.utils.ToastUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Configuration {
    public final int lastVersionCode;
    public final File directory;
    public final ExecutorService executorService;
    public final BiometricUtil biometricUtil;
    public final NotificationHandler notificationHandler;
    public final AssetManager assetManager;
    public final ToastUtil toastUtil;
    public final SharedPreferences prefs;
    private static final String PREFS_KEY_LAST_VERSION = "last_version";
    private static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    private final static String FINGERPRINT_ENABLED = "fingerprint_enabled";
    private final static String BLOCKCHAIN_CHECKPOINTS = "org.bitcoin.test.checkpoints.txt";
    private final static String UI_MODE = "Selected.Theme";
    private static final String LANGUAGE = "Locale.Helper.Selected.Language";
    public int uiMode;

    private static Configuration instance;

    public static synchronized Configuration get() {
        if(instance == null){
            throw new IllegalStateException("Configuration not initialized by application");
        }
        return instance;
    }

    private final List<ConfigurationOption<Integer>> themes = Stream.of(
            new ConfigurationOption<>("Light", AppCompatDelegate.MODE_NIGHT_NO),
            new ConfigurationOption<>("Dark", AppCompatDelegate.MODE_NIGHT_YES),
            new ConfigurationOption<>("System default", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    ).collect(Collectors.toList());

    private final List<ConfigurationOption<String>> languages = Stream.of(
            new ConfigurationOption<>("English", "en"),
            new ConfigurationOption<>("Vietnam", "vi")
    ).collect(Collectors.toList());

    private static final Logger log = LoggerFactory.getLogger(Configuration.class);

    private Configuration(final SharedPreferences prefs, final File directory, final BiometricUtil biometricUtil,
                         NotificationHandler notificationHandler, AssetManager assetManager, ToastUtil toastUtil) {
        this.prefs = prefs;
        this.directory = directory;
        this.lastVersionCode = prefs.getInt(PREFS_KEY_LAST_VERSION, 0);
        this.notificationHandler = notificationHandler;
        this.executorService = Executors.newFixedThreadPool(NUMBER_OF_CORES);
        this.biometricUtil = biometricUtil;
        this.assetManager = assetManager;
        this.toastUtil = toastUtil;
        this.uiMode = prefs.getInt(UI_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    public boolean isFingerprintEnabled(){
        return prefs.getBoolean(FINGERPRINT_ENABLED, false);
    }

    public void setFingerprintEnabled(boolean enabled){
        prefs.edit().putBoolean(FINGERPRINT_ENABLED, enabled).apply();
    }

    public List<ConfigurationOption<String>> getLanguages(){ return languages; }

    public List<ConfigurationOption<Integer>> getThemes(){ return themes; }

    public void changeTheme(int uiMode){
        this.uiMode = uiMode;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(UI_MODE, uiMode);
        editor.apply();
    }

    public void changeLanguage(String language){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(LANGUAGE, language);
        editor.apply();
    }

    public String getSelectedLanguage(){
        return prefs.getString(LANGUAGE, Locale.getDefault().getLanguage());
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

    public static class Builder{
        private File directory;
        public BiometricUtil biometricUtil;
        public NotificationHandler notificationHandler;
        public AssetManager assetManager;
        public ToastUtil toastUtil;
        public SharedPreferences prefs;

        public Builder directory(File directory){
            this.directory = directory;
            return this;
        }

        public Builder biometric(BiometricUtil biometricUtil){
            this.biometricUtil = biometricUtil;
            return this;
        }

        public Builder notification(NotificationHandler notificationHandler){
            this.notificationHandler = notificationHandler;
            return this;
        }

        public Builder asset(AssetManager assetManager){
            this.assetManager = assetManager;
            return this;
        }

        public Builder toast(ToastUtil toastUtil){
            this.toastUtil = toastUtil;
            return this;
        }

        public Builder prefs(SharedPreferences preferences){
            this.prefs = preferences;
            return this;
        }

        public void build(){
            if(instance == null){
                instance = new Configuration(prefs, directory, biometricUtil, notificationHandler, assetManager, toastUtil);
            }
        }
    }
}
