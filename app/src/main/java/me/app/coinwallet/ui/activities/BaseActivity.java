package me.app.coinwallet.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.IdRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import me.app.coinwallet.Configuration;
import me.app.coinwallet.R;
import me.app.coinwallet.WalletApplication;
import me.app.coinwallet.ui.fragments.HomeFragment;
import me.app.coinwallet.utils.LocaleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

public abstract class BaseActivity extends AppCompatActivity {
    public Configuration configuration = Configuration.get();

    protected static final Logger log = LoggerFactory.getLogger(BaseActivity.class);

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(configuration.uiMode);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtil.onAttach(base,configuration.getSelectedLanguage()));
    }

    /***
     * load fragment with default frame id = R.id.frame_container
     */
    public void loadFragment(Class<? extends Fragment> fragment){
        loadFragment(fragment, R.id.frame_container);
    }

    protected void loadFragment(Class<? extends Fragment> fragment, int frameId){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction =manager.beginTransaction();
        boolean fragmentPopped = manager.popBackStackImmediate (fragment.getSimpleName(), 0);
        Fragment current = manager.findFragmentByTag(fragment.getSimpleName());
        if(!fragmentPopped && current == null){
            try {
                current = (Fragment) fragment.getMethod("newInstance").invoke(null);
            } catch (NoSuchMethodException| IllegalAccessException| IllegalArgumentException| InvocationTargetException e){
                // Ignore
            }
        }
        transaction.replace(frameId, current, fragment.getSimpleName());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void loadFragmentOut(Class<? extends Fragment> fragment, String label){
        Intent intent = SingleFragmentActivity.newActivity(this, fragment, label);
        startActivity(intent);
    }

    public void loadFragmentOut(Class<? extends Fragment> fragment, @StringRes int label){
        Intent intent = SingleFragmentActivity.newActivity(this, fragment, label);
        startActivity(intent);
    }
}
